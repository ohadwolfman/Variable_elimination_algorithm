import java.util.*;

public class Parser {

    public static String isQueryAlreadyExists(Network net,String queryVarName,ArrayList<String> queryAndEvidencesWithOutcome){
        String ans = "-1";
        for (int i = 0; i < net.getVarbyName(queryVarName).getCpt().length; i++) {
            ArrayList<String> row = new ArrayList<>();
            for (String element : net.getVarbyName(queryVarName).getCpt()[i]){
                row.add(element);
            }
            boolean first = row.subList(0,row.size()-1).containsAll(queryAndEvidencesWithOutcome);
            boolean second = queryAndEvidencesWithOutcome.containsAll(row.subList(0,row.size()-1));
            if(first&&second) {
                ans = row.get(row.size() - 1);
                break;
            }
        }
        return ans;
    }

    /**
     * This method calculate the probability of some outcome of specific Variable given other evidences
     * The method use a simple inference as Sum of multiplications the probabilities of the query and evidences,
     *      and all the permutations of the hidden variables
     * The function accept the following parameters:
     * @param net - object from type Network that present Bayesian Network
     * @param varNames - the names of the variables in the network
     * @param currentVars - the variables which isn't hidden - the query variable and the evidence variables
     * @param varEvidence - the given evidences in the query line
     * @param queryVarName - the query variable that we need to calculate
     * @return array with 2 cells - numerate and denominator for the right equation
     */
    public static double[] simpleInference(Network net,ArrayList<String> varNames,ArrayList<String> currentVars,ArrayList<String> varEvidence,String queryVarName){
        double denominator = 0;
        double numerator = 0;
        String [][] toCompute = Parser.createCpt(net.getVars(),varNames);

        for (int i = 0; i < toCompute.length; i++) {
            boolean flag = false;
            for (int j = 0; j < toCompute[i].length; j++) {
                String varWithOutCome = toCompute[i][j];
                String var = toCompute[i][j].substring(0,toCompute[i][j].indexOf("="));
                if(currentVars.contains(var)){
                    if(!varEvidence.contains(varWithOutCome))
                        flag = true;
                }
            }
            if(!flag){
                numerator+=Parser.computeRow(toCompute[i],varEvidence,net,numerator);
            }
        }
        for (int i = 0; i < toCompute.length; i++) {
            boolean flag = false;
            for (int j = 0; j < toCompute[i].length; j++) {
                String varWithOutCome = toCompute[i][j];
                String var = toCompute[i][j].substring(0,toCompute[i][j].indexOf("="));
                if(currentVars.contains(var)){
                    if(var.equals(queryVarName)){
                        if(varEvidence.contains(varWithOutCome))flag = true;
                    }else{
                        if(!varEvidence.contains(varWithOutCome))
                            flag = true;
                    }
                }
            }
            if(!flag){
                denominator+=Parser.computeRow(toCompute[i],varEvidence,net,denominator);
            }
        }
        double[] num_and_den = {numerator, denominator};
        return num_and_den;
    }

    /**
     * This function built for variable elimination
     * @param net - object from type Network that present Bayesian Network
     * @param currentVars - the variables which isn't hidden - the query variable and the evidence variables
     * @param varEvidence - the given evidences in the query line
     * @return Array list of factors for the whole net
     *         'factors' is list of factors, such that every factor contains list of rows
     *         and every row will contain the permutation, and it's probability
     */
    //
    public static ArrayList<ArrayList<ArrayList<String>>>  copyFactors(Network net,ArrayList<String> currentVars,ArrayList<String> varEvidence){
        ArrayList<ArrayList<ArrayList<String>>> factors = new ArrayList<>();

        // the for loop takes every variable from the net and create his given cpt(from the xml file)
        for (int i = 0; i < net.getVars().size() ; i++) {
            String varName = net.getVars().get(i).getName();

            // newCpt will contain list of rows such that every row will contain the permutation and it's probability
            ArrayList<ArrayList<String>> newCpt = new ArrayList<>();
            for (int j = 0; j < net.getVarbyName(varName).getCpt().length; j++) {
                boolean flag = false;
                for (int k = 1; k < currentVars.size(); k++) {
                    if(Arrays.toString(net.getVarbyName(varName).getCpt()[j]).contains(currentVars.get(k)+"=")){
                        if(!Arrays.toString(net.getVarbyName(varName).getCpt()[j]).contains(varEvidence.get(k))){
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) continue;
                ArrayList<String> beforefiltered = new ArrayList<>();

                for (int k = 0; k < net.getVarbyName(varName).getCpt()[j].length; k++) {
                    if(k<net.getVarbyName(varName).getCpt()[j].length-1) {
                        int index = net.getVarbyName(varName).getCpt()[j][k].indexOf("=");
                        String varNametoFilter = net.getVarbyName(varName).getCpt()[j][k].substring(0, index);
                        if (!varNametoFilter.equals(currentVars.get(0)) && currentVars.contains(varNametoFilter))
                            continue;

                    }
                    if(k<net.getVarbyName(varName).getCpt()[j].length-1||beforefiltered.size()>0)
                        beforefiltered.add(net.getVarbyName(varName).getCpt()[j][k]);
                }
                if(beforefiltered.size()>0){
                    newCpt.add(beforefiltered);
                }
            }
            factors.add(newCpt);
        }

        return factors;
    }

    public static void deletEptyFactors( ArrayList<ArrayList<ArrayList<String>>> factors){

        for (int i = factors.size()-1; i >= 0; i--) {
            if(factors.get(i).size()==0)
                factors.remove(i);
        }

    }


    public static String variableElimination(ArrayList<String> varHidden,ArrayList<String> varEvidence,ArrayList<String> varNames,ArrayList<String> currentVars,Network net,String queryVarOutCome){
        double mul = 0;
        double add = 0;
        // currentVars evidence without outcomes

        ArrayList<ArrayList<ArrayList<String>>> factors = copyFactors(net,currentVars,varEvidence);
        deletEptyFactors(factors);
        ArrayList<String> unRelevanteVars = new ArrayList<>();
        boolean flag = false;
        for (int i = 0; i < varHidden.size(); i++) {
            flag = false;
            for (int j = 0; j < currentVars.size(); j++) {
                Variable son = net.getVarbyName(currentVars.get(j));
                Variable parent = net.getVarbyName(varHidden.get(i));
                if(isParent2(parent,son,net)){
                    flag = true;
                    break;
                }
            }
            if (!flag)unRelevanteVars.add(varHidden.get(i));

        }

        for (int i = unRelevanteVars.size()-1; i >=0; i--) {
            if(varHidden.contains(unRelevanteVars.get(i)))
                varHidden.remove(unRelevanteVars.get(i));
        }


        //remove unrealevent factors from lsit fo factors
        for (int i = factors.size()-1; i >= 0; i--) {
            ArrayList<String> factorVariableContains = getVarNamesFromFactor(factors.get(i));
            for (int j = 0; j < unRelevanteVars.size(); j++) {
                if(factorVariableContains.contains(unRelevanteVars.get(j))){
                    factors.remove(i);
                    break;
                }
            }

        }
//        System.out.println(unRelevanteVars);
        Collections.sort(varHidden);
//        for (int i = 0; i < factors.size(); i++) {
//            System.out.println(factors.get(i));
//        }
        for (int i = 0; i < varHidden.size(); i++) {
            String currVar = varHidden.get(i);
            int numberOfInstacesinFactors = numberOfInstacesinFactors(factors,currVar);
            int index = indexOfFirstFactorContain(factors,currVar);

            //if there are 2 or more factors containing the same variable - we will join and eliminate one of them
            while(numberOfInstacesinFactors>1){
                int[] indexofFactors = twoSmallestFactorIndex(factors,currVar);
                int indexFirst = indexofFactors[0];
                int indexSecond = indexofFactors[1];
                ArrayList<ArrayList<String>> firstFactor = new ArrayList<>(factors.get(indexFirst));
                ArrayList<ArrayList<String>> secondFactor = new ArrayList<>(factors.get(indexSecond));
                if(indexFirst<indexSecond){
                    int tempIndex = indexFirst;
                    indexFirst = indexSecond;
                    indexSecond = tempIndex;
                }

                factors.remove(indexFirst);
                factors.remove(indexSecond);
                factors.add(join(firstFactor,secondFactor));
                index = factors.size()-1;
                //shoul check
                numberOfInstacesinFactors--;
            }
            ArrayList<ArrayList<String>> newFactor = eliminate(factors.get(index),currVar);
            factors.set(index,newFactor);
            deletEptyFactors(factors);
        }
        int numberOfInstacesinFactors = numberOfInstacesinFactors(factors,currentVars.get(0));
        int index = indexOfFirstFactorContain(factors,currentVars.get(0));
        while(numberOfInstacesinFactors>1){
            int[] indexofFactors = twoSmallestFactorIndex(factors,currentVars.get(0));
            int indexFirst = indexofFactors[0];
            int indexSecond = indexofFactors[1];
            ArrayList<ArrayList<String>> firstFactor = new ArrayList<>(factors.get(indexFirst));
            ArrayList<ArrayList<String>> secondFactor = new ArrayList<>(factors.get(indexSecond));
            if(indexFirst<indexSecond){
                int tempIndex = indexFirst;
                indexFirst = indexSecond;
                indexSecond = tempIndex;
            }
            factors.remove(indexFirst);
            factors.remove(indexSecond);
            factors.add(join(firstFactor,secondFactor));
            index = factors.size()-1;
            //shoul check
            numberOfInstacesinFactors--;
        }
        double normalize = normalize(factors.get(0),queryVarOutCome);
        String s = String.format("%.5f",normalize);
        return s;
    }

    public static double normalize(ArrayList<ArrayList<String>> factor,String queryWithOutCome){
        double sum = 0;
        int index = 0;
        for (int i = 0; i < factor.size(); i++) {
            if(factor.get(i).contains(queryWithOutCome)){
                index = i;
            }
            sum+=Double.parseDouble(factor.get(i).get(1));
        }
        double valQuery = Double.parseDouble(factor.get(index).get(1));
        return valQuery/sum;
    }

    public static ArrayList<ArrayList<String>> eliminate( ArrayList<ArrayList<String>> factor,String varHidden){
        ArrayList<ArrayList<String>> newFactor = new ArrayList<>();
        ArrayList<Integer> rowsTaken = new ArrayList<>();
        for (int i = 0; i < factor.size(); i++) {
            boolean entered = false;
            if(rowsTaken.contains(i))
                continue;
            rowsTaken.add(i);
            ArrayList<String> temp = rowWithoutOutComes(factor.get(i));

            int index = temp.indexOf(varHidden);
            if(index!=-1) {
                factor.get(i).remove(index);
            }
            double sum = Double.parseDouble(factor.get(i).get(factor.get(i).size()-1));
            for (int j = i+1; j < factor.size(); j++) {
                //should check

                ArrayList<String> temp2 = rowWithoutOutComes(factor.get(j));
                int index2 = temp.indexOf(varHidden);
                if(index2!=-1) {
                    factor.get(j).remove(index2);
                }

                if(factor.get(i).subList(0,factor.get(i).size()-1).equals(factor.get(j).subList(0,factor.get(j).size()-1))){
                    sum+= Double.parseDouble(factor.get(j).get(factor.get(j).size()-1));
                    Ex1.addAndMullVariableElimination[0]++;
                    rowsTaken.add(j);
                    entered = true;
                }

            }

            if(entered){
                factor.get(i).set(factor.get(i).size()-1,sum+"");
                if(factor.get(i).size()>1)
                    newFactor.add(factor.get(i));
            }
        }
        return newFactor;
    }


    public static ArrayList<String> rowWithoutOutComes(ArrayList<String> row){
        ArrayList<String> ans = new ArrayList<>();
        for (int i = 0; i < row.size()-1; i++) {
            int index = row.get(i).indexOf("=");
            ans.add(row.get(i).substring(0,index));
        }
        return ans;

    }

    /**
     * Check if two factors contain some common variable such that we can join the two factors
     * every row in factors is permutation of the same variables, so we will take the first row from every factor
     * @param firstFactorRow
     * @param secondFactorRow
     * @return true if the factors have a common variable, and false otherwise
     * */
    public static boolean shouldJoin(ArrayList<String> firstFactorRow,ArrayList<String> secondFactorRow){
        ArrayList<String> rowWithOut = rowWithoutOutComes(secondFactorRow);

        for (int i = 0; i < firstFactorRow.size()-1; i++) {
            int index = firstFactorRow.get(i).indexOf("=");
            String varWithoutOutComes = firstFactorRow.get(i).substring(0,index);
            if(rowWithOut.contains(varWithoutOutComes)){
                if(!secondFactorRow.contains(firstFactorRow.get(i))){
                    return false;
                }
            }
        }
        return true;
    }

    public static ArrayList<String> sameVarsInFactors(ArrayList<String> firstFactorRow,ArrayList<String> secondFactorRow){
        ArrayList<String> sameVarsInfactors = new ArrayList<>();
        ArrayList<String> newSecond = rowWithoutOutComes(secondFactorRow);
        for (int i = 0; i < firstFactorRow.size()-1; i++) {
            int index = firstFactorRow.get(i).indexOf("=");
            String varWithoutOutComes = firstFactorRow.get(i).substring(0,index);

            if(newSecond.contains(varWithoutOutComes)){
                if(secondFactorRow.contains(firstFactorRow.get(i))){
                    sameVarsInfactors.add(firstFactorRow.get(i));
                }

            }
        }
        return  sameVarsInfactors;
    }


    public static ArrayList<ArrayList<String>> join(ArrayList<ArrayList<String>> firstFactor,ArrayList<ArrayList<String>> secondFactor) {
//        System.out.println(firstFactor+"first");
//        System.out.println(secondFactor+"second");
        ArrayList<ArrayList<String>> multiplyFactor = new ArrayList<>();

        for (int i = 0; i < firstFactor.size(); i++) {
            for (int j = 0; j < secondFactor.size(); j++) {
                if (shouldJoin(firstFactor.get(i), secondFactor.get(j))) {
                    ///should check
                    ArrayList<String> toRow = sameVarsInFactors(firstFactor.get(i), secondFactor.get(j));
                    for (int k = 0; k < firstFactor.get(i).size() - 1; k++) {
                        if (!toRow.contains(firstFactor.get(i).get(k))) {
                            toRow.add(firstFactor.get(i).get(k));
                        }
                    }
                    for (int k = 0; k < secondFactor.get(j).size() - 1; k++) {
                        if (!toRow.contains(secondFactor.get(j).get(k))) {
                            toRow.add(secondFactor.get(j).get(k));
                        }
                    }
                    int sizeFirst = firstFactor.get(i).size() - 1;
                    int sizeSecond = secondFactor.get(i).size() - 1;
                    double valInerst = Double.parseDouble(firstFactor.get(i).get(sizeFirst)) * Double.parseDouble(secondFactor.get(j).get(sizeSecond));
                    Ex1.addAndMullVariableElimination[1]++;
                    toRow.add(valInerst + "");

                    multiplyFactor.add(toRow);
                }
            }
        }
        return multiplyFactor;
    }

    public static int[] twoSmallestFactorIndex(ArrayList<ArrayList<ArrayList<String>>> factors,String varHidden){
        int[] smalletFactors = new int[2];

        int indexSmallest_1 = smallestFactorIndex(factors,varHidden,-1);
        int indexSmallest_2 =  smallestFactorIndex(factors,varHidden,indexSmallest_1);

        int minSizeFirst = factors.get(indexSmallest_1).size();
        int minSizeSecond = factors.get(indexSmallest_2).size();

        if(minSizeFirst<minSizeSecond){
            smalletFactors[0] = indexSmallest_1;
            smalletFactors[1] = lowerAscii(factors,varHidden,minSizeSecond,-1);
        }else{
            smalletFactors[0] = lowerAscii(factors,varHidden,minSizeFirst,-1);
            smalletFactors[1] = lowerAscii(factors,varHidden,minSizeFirst, smalletFactors[0]);
        }
        return smalletFactors;
    }

    public static int smallestFactorIndex(ArrayList<ArrayList<ArrayList<String>>> factors,String varHidden,int taken){
        int min = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < factors.size(); i++) {
            ArrayList<String> varsInFactor = getVarNamesFromFactor(factors.get(i));
            if(varsInFactor.contains(varHidden)&&i!=taken){
                if(min>factors.get(i).size()){
                    min = factors.get(i).size();
                    index = i;
                }
            }
        }
        return index;
    }

    public static int lowerAscii(ArrayList<ArrayList<ArrayList<String>>> factors,String varHidden,int size,int indexTaken){
        int min = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i <factors.size(); i++) {
            ArrayList<String> varsInFactor = getVarNamesFromFactor(factors.get(i));
            int sum = 0;
            if(varsInFactor.contains(varHidden)&&factors.get(i).size()==size&&i!=indexTaken){
                for (int j = 0; j <varsInFactor.size() ; j++) {
                    for( char c : varsInFactor.get(j).toCharArray())
                        sum+= c;
                }
                if(min>sum){
                    min = sum;
                    index = i;
                }
            }
        }
        return index;
    }

    public static int numberOfInstacesinFactors( ArrayList<ArrayList<ArrayList<String>>> factors,String varHidden){
        int counter = 0;
        for (int i = 0; i < factors.size(); i++) {
            ArrayList<String> varsInFactor = getVarNamesFromFactor(factors.get(i));
            if(varsInFactor.contains(varHidden))
                counter++;
        }
        return counter;
    }
    public static int indexOfFirstFactorContain( ArrayList<ArrayList<ArrayList<String>>> factors,String varHidden){
        for (int i = 0; i < factors.size(); i++) {
            ArrayList<String> varsInFactor = getVarNamesFromFactor(factors.get(i));
            if(varsInFactor.contains(varHidden))
                return i;
        }
        return -1;
    }


    public static ArrayList<String> getVarNamesFromFactor( ArrayList<ArrayList<String>> currFactor){
        ArrayList<String> varNamesWithout = new ArrayList<>();
        for (int i = 0; i < currFactor.get(0).size()-1; i++) {
            int index =  currFactor.get(0).get(i).indexOf("=");
            String varName = currFactor.get(0).get(i).substring(0,index);
            varNamesWithout.add(varName);
        }
        return  varNamesWithout;

    }

    /**
     * Check recursively if one variable is ancestor of other variable
     * @param parent - the variable we want to check is ancestor
     * @param var - the required variable
     * @param net - the Bayesian Network we read from the xml
     * @return
     */
    public static boolean isParent2(Variable parent,Variable var,Network net){
        if(var.getName().equals(parent.getName()))
            return true;
        for (int i = 0; i < var.getParents().size(); i++) {
            if(isParent2(parent,net.getVarbyName(var.getParents().get(i)),net))
                return true;
        }
        return false;
    }


    public static ArrayList<String> evidenceWithoutOutcomes(ArrayList<String> evi){
        ArrayList<String> ans = new ArrayList<>();
        for (String s : evi) {
            String var = s.substring(0, s.indexOf("="));
            ans.add(var);
        }
        return  ans;
    }

    /**
     * This function helps to SimpleInference function
     * @param row
     * @param evidence
     * @param net
     * @param valueReturn
     * @return
     */
    public static double computeRow(String [] row,ArrayList<String> evidence,Network net,double valueReturn){
        double sum = 1;

        for (String s : row) {
            String currVar = s.substring(0, s.indexOf("="));
            String[] arr = new String[net.getVarbyName(currVar).getParents().size() + 1];
            arr[0] = s;
            for (int j = 0; j < net.getVarbyName(currVar).getParents().size(); j++) {
                String currParnets = net.getVarbyName(currVar).getParents().get(j);
                for (String value : row) {
                    String checkParent = value.substring(0, s.indexOf("="));
                    if (currParnets.equals(checkParent))
                        arr[j + 1] = value;
                }
            }
            String[][] cptCurrVar = net.getVarbyName(currVar).getCpt();
            for (int j = 0; j < cptCurrVar.length; j++) {
                if (Arrays.equals(arr, Arrays.copyOfRange(cptCurrVar[j], 0, cptCurrVar[j].length - 1))) {
                    sum *= Double.parseDouble(cptCurrVar[j][cptCurrVar[j].length - 1]);
                    break;
                }
            }
        }
        return sum;
    }

    public static String addAndMullSimpleInference(ArrayList<String> hidden, Network net, String queryVarName){
        int plus =0 ;
        int mul = 0;

        int sum  = 1;
        for (int i = 0; i < hidden.size(); i++) {
            sum*= net.getVarbyName(hidden.get(i)).getOutcomeList().size();
        }
        plus = sum-1;
        double beforeplus =  plus*(net.getVarbyName(queryVarName).getOutcomeList().size()-1);
        int beforeMul =   sum*(net.getVarbyName(queryVarName).getOutcomeList().size()-1);
        plus+= beforeplus;
        mul = (sum+beforeMul)*(net.getVars().size()-1);
        plus+= net.getVarbyName(queryVarName).getOutcomeList().size()-1;
        return ","+plus+","+mul;
    }

    /**
     * The function create 2d array that contains all the optional permutation of all the variables with the outcomes
     * the amount of rows will be multiplication of number of outcomes for every variable
     * @param Variables - list of all the Variables in the network
     * @param varName
     * @return
     */
    public static String[][] createCpt(List<Variable> Variables, ArrayList<String> varName) {
        List<List<String>> listsInput = new ArrayList<>();
        for (Variable v : Variables) {
            listsInput.add(v.getOutcomeList());
        }
        List<String> allPermutations = new ArrayList<>();
        generatePermutations(listsInput, allPermutations, 0, "");

        int rows = 1; //the rows is a multiplication of the number of outcomes of every variable
        for (Variable v : Variables) {
            rows *= v.getOutcomeList().size();
        }
        // Initialize the 2D array with the appropriate size
        String[][] CPT = new String[rows][Variables.size()];

        for (int i = 0; i < rows; i++) {
            String[] row = allPermutations.get(i).split(",");
//            CPT[i][0] = String.valueOf(i + 1);
            for (int j=0; j<=Variables.size()-1; j++){
                CPT[i][j] = varName.get(j)+"="+row[j];
            }
        }
        return CPT;
    }

    /**
     * This recursive method generate all the permutations of the outcomes
     * afterward the method createCpt will join all the permutations as the values of the variables
     * @param lists - list that contain lists of the outcomes of the variables we want to create the CPT for
     * @param result - list of all the optional permutation
     * @param depth - indicator for the recursion
     * @param current
     */
    public static void generatePermutations(List<List<String>> lists, List<String> result, int depth, String current) {
        if (depth == lists.size()) {
            result.add(current.substring(0, current.length()-1));
            return;
        }
        for (int i = 0; i < lists.get(depth).size(); i++) {
            generatePermutations(lists, result, depth + 1, current + lists.get(depth).get(i) + ",");
        }
    }

    /**
     * This function helped during the building process
     * the function print the cpt table
     * @param variables
     * @param varName
     */
    public static void printCpt(ArrayList<Variable> variables,ArrayList<String> varName) {
        String[][] cpt = createCpt(variables,varName);
        for (int i = 0; i < cpt.length; i++) {
            for (int j = 0; j < variables.size() + 1; j++) {
                System.out.print(cpt[i][j] + ",");
            }
            System.out.println();
        }
    }
}
