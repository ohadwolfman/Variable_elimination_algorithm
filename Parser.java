import java.util.*;

public class Parser {

    // function for variable elimination
    /**
     * This method
     *
     * @param
     * @param
     * @param
     * @param
     * @return
     */
    public static ArrayList<ArrayList<ArrayList<String>>>  copyFactors(Network net,ArrayList<String> currentVars,ArrayList<String> varEvidence){
        ArrayList<ArrayList<ArrayList<String>>> factors = new ArrayList<>();
        for (int i = 0; i < net.getVars().size() ; i++) {
            String varName = net.getVars().get(i).getName();
            ArrayList<ArrayList<String>> newCpt = new ArrayList<>();
            for (int j = 0; j < net.getVarbyName(varName).getCpt().length; j++) {
                boolean flag = false;
                for (int k = 1; k < currentVars.size(); k++) {

                    if(Arrays.toString(net.getVarbyName(varName).getCpt()[j]).contains(currentVars.get(k))){
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

    /**
     *
     * @param factors
     * @return
     */
    public static void deletEptyFactors( ArrayList<ArrayList<ArrayList<String>>> factors){

        for (int i = factors.size()-1; i >= 0; i--) {
            if(factors.get(i).size()==0)
                factors.remove(i);
        }

    }

    /**
     *
     * @param varHidden
     * @param varEvidence
     * @param varNames
     * @param currentVars
     * @param net
     * @param queryVarOutCome
     * @return
     */
    public static void variableElimination(ArrayList<String> varHidden,ArrayList<String> varEvidence,ArrayList<String> varNames,ArrayList<String> currentVars,Network net,String queryVarOutCome){
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
            if(varHidden.contains(unRelevanteVars.get(i)))varHidden.remove(unRelevanteVars.get(i));
        }


        //remove unrealevent factors from lsit fo factors
        for (int i = factors.size()-1; i > 0; i--) {
            ArrayList<String> factorVariableContains = getVarNamesFromFactor(factors.get(i));
            for (int j = 0; j < unRelevanteVars.size(); j++) {
                if(factorVariableContains.contains(unRelevanteVars.get(j))){
                    factors.remove(i);
                    break;
                }
            }

        }

        Collections.sort(varHidden);
        for (int i = 0; i < varHidden.size(); i++) {
            String currVar = varHidden.get(i);
            int numberOfInstacesinFactors = numberOfInstacesinFactors(factors,currVar);
            int index = indexOfFirstFactorContain(factors,currVar);


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

        double ans = normalize(factors.get(0),queryVarOutCome);
        String s = String.format("%.5f",ans);
        //System.out.println(s);
        Ex1.final_answer+=s+"\n";
    }

    /**
     *
     * @param factor
     * @param queryWithOutCome
     * @return
     */
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

    /**
     *
     * @param factor
     * @param varHidden
     * @return
     */
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

    /**
     *
     * @param row
     * @return
     */
    public static ArrayList<String> rowWithoutOutComes(ArrayList<String> row){

        ArrayList<String> ans = new ArrayList<>();
        for (int i = 0; i < row.size()-1; i++) {
            int index = row.get(i).indexOf("=");
            ans.add(row.get(i).substring(0,index));

        }
        return ans;

    }

    /**
     *
     * @param firstFactorRow
     * @param secondFactorRow
     * @return
     */
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

    /**
     *
     * @param firstFactorRow
     * @param secondFactorRow
     * @return
     */
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

    /**
     *
     * @param firstFactor
     * @param secondFactor
     * @return
     */
    public static ArrayList<ArrayList<String>> join(ArrayList<ArrayList<String>> firstFactor,ArrayList<ArrayList<String>> secondFactor) {

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
                    toRow.add(valInerst + "");

                    multiplyFactor.add(toRow);


                }
            }


        }
        return multiplyFactor;
    }

    /**
     *
     * @param factors
     * @param varHidden
     * @return
     */
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

    /**
     *
     * @param factors
     * @param varHidden
     * @param taken
     * @return
     */
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

    /**
     *
     * @param factors
     * @param varHidden
     * @param size
     * @param indexTaken
     * @return
     */
    public static int lowerAscii(ArrayList<ArrayList<ArrayList<String>>> factors,String varHidden,int size,int indexTaken){
        int min = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i <factors.size(); i++) {
            ArrayList<String> varsInFactor = getVarNamesFromFactor(factors.get(i));
            int sum = 0;
            if(varsInFactor.contains(varHidden) && (factors.get(i).size()==size) && (i!=indexTaken)){
                for (String s : varsInFactor) {
                    for (char c : s.toCharArray())
                        sum += c;
                }
                if(min>sum){
                    min = sum;
                    index = i;
                }
            }

        }
        return index;
    }

    /**
     *
     * @param factors
     * @param varHidden
     * @return
     */
    public static int numberOfInstacesinFactors( ArrayList<ArrayList<ArrayList<String>>> factors,String varHidden){
        int counter = 0;
        for (int i = 0; i < factors.size(); i++) {
            ArrayList<String> varsInFactor = getVarNamesFromFactor(factors.get(i));
            if(varsInFactor.contains(varHidden))
                counter++;
        }
        return counter;
    }

    /**
     *
     * @param factors
     * @param varHidden
     * @return
     */
    public static int indexOfFirstFactorContain( ArrayList<ArrayList<ArrayList<String>>> factors,String varHidden){
        for (int i = 0; i < factors.size(); i++) {
            ArrayList<String> varsInFactor = getVarNamesFromFactor(factors.get(i));
            if(varsInFactor.contains(varHidden))
                return i;
        }
        return -1;
    }

    /**
     *
     * @param currFactor
     * @return
     */
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
     *
     * @param parent
     * @param var
     * @param net
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

    /**
     *
     * @param evi
     * @return
     */
    public static ArrayList<String> evidenceWithoutOutcomes(ArrayList<String> evi){
        ArrayList<String> ans = new ArrayList<>();

        for (String s : evi) {
            String var = s.substring(0, s.indexOf("="));
            ans.add(var);
        }
        return  ans;
    }

    /**
     *
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

    /**
     *
     * @param hidden
     * @param net
     * @param queryVarName
     * @return
     */
    public static String addAndMull(ArrayList<String> hidden, Network net, String queryVarName){
        int plus =0 ;
        int mUl = 0;
        int sum  = 1;
        for (int i = 0; i < hidden.size(); i++) {
            sum*= net.getVarbyName(hidden.get(i)).getOutcomeList().size();
        }

        plus = sum-1;
        double beforeplus =  plus*(net.getVarbyName(queryVarName).getOutcomeList().size()-1);
        int beforeMul =   sum*(net.getVarbyName(queryVarName).getOutcomeList().size()-1);
        plus+= beforeplus;
        mUl = (sum+beforeMul)*(net.getVars().size()-1);
        plus+= net.getVarbyName(queryVarName).getOutcomeList().size()-1;
        return ","+plus+","+mUl;
    }

    /**
     *
     * @param query
     * @return
     */
    public static ArrayList<String> extractEvidences(String query) {
        ArrayList<String> result = new ArrayList<>();
        int pointer=0;
        while(query.charAt(pointer) != '|'){
            pointer++;
        }
        pointer++;

        String ev = query.substring(pointer);
        ev = ev.replace("=",",");
        String[] arr = ev.split(",");
        for(int i=0; i< arr.length; i++){
            result.add(arr[i]);

        }

        for(int i=0; i<result.size(); i++){
            if(Objects.equals(result.get(i), "F")){
                String newValue = result.get(i-1).toLowerCase();
                result.set(i-1, newValue);
            }
        }

        for(int i=1; i<result.size(); i+=2){
            result.remove(i);
        }

        return result;
    }

    /**
     *
     * @param Variables
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
     *
     * @param lists
     * @param result
     * @param depth
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
     *
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
