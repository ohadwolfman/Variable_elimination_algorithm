import java.util.*;

public class Parser {
    public static double numerter = 0;
    public static double denominator = 0;
    public static  boolean p = false;

    // function for variable elimination
    public static ArrayList<ArrayList<String[]>>  copyFactors(Network net,ArrayList<String> currentVars,ArrayList<String> varEvidence){
        ArrayList<ArrayList<String[]>> factors = new ArrayList<>();
        for (int i = 0; i < net.getVars().size() ; i++) {
            String varName = net.getVars().get(i).getName();
            ArrayList<String []> newCpt = new ArrayList<>();
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
                if (flag)
                    continue;
                ArrayList<String> beforefiltered = new ArrayList<>();

                for (int k = 0; k < net.getVarbyName(varName).getCpt()[j].length; k++) {
                    if(k<net.getVarbyName(varName).getCpt()[j].length-1) {
                        int index = net.getVarbyName(varName).getCpt()[j][k].indexOf("=");
//                    System.out.println(""+index+"dsadas");
//                    System.out.println( net.getVarbyName(varName).getCpt()[j][k].contains("="));
                        String varNametoFilter = net.getVarbyName(varName).getCpt()[j][k].substring(0, index);
                        if (!varNametoFilter.equals(currentVars.get(0)) && currentVars.contains(varNametoFilter))
                            continue;

                    }

                    beforefiltered.add(net.getVarbyName(varName).getCpt()[j][k]);

                }

                String[] insert = new String[beforefiltered.size()];
                for (int l = 0; l < insert.length; l++) {
                    insert[l] = beforefiltered.get(l);
                }
//                System.out.println(beforefiltered);
                newCpt.add(insert);
            }


            factors.add(newCpt);
        }
        for (int i = 0; i < factors.size(); i++) {
            System.out.println("factor number dsadsadas :"+i);
            for (int j = 0; j < factors.get(i).size(); j++) {
                System.out.println(Arrays.toString(factors.get(i).get(j)));
            }

        }
        return factors;
    }

    public static void deletEptyFactors( ArrayList<ArrayList<String[]>> factors){

        for (int i = factors.size()-1; i > 0; i--) {
            if(factors.get(i).size()==0)factors.remove(i);
        }
    }


    public static String variableElimination(ArrayList<String> varHidden,ArrayList<String> varEvidence,ArrayList<String> varNames,ArrayList<String> currentVars,Network net){

        // currentVars evidence without outcomes
        ArrayList<ArrayList<String []>> factors = copyFactors(net,currentVars,varEvidence);
        ArrayList<String> unRelevanteVars = new ArrayList<>();
        System.out.println(unRelevanteVars+" is unRelevanteVars");

        for (int i = 0; i < varHidden.size(); i++) {
            for (int j = 0; j < currentVars.size(); j++) {
                Variable parent = net.getVarbyName(currentVars.get(j));
                Variable son = net.getVarbyName(varHidden.get(i));
                isParent(parent,son,net);
                if(p){
                    break;
                }

            }
            if (!p){
                unRelevanteVars.add(varHidden.get(i));
            }else{
                p = false;
            }

        }


        for (int i = unRelevanteVars.size()-1; i >0; i--) {
            if(varHidden.contains(unRelevanteVars.get(i)))varHidden.remove(unRelevanteVars.get(i));
        }

        Collections.sort(varHidden);

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




        }

        System.out.println("-----------factors after---------");
        return "dsadsa";

    }

    public static ArrayList<String> getVarNamesFromFactor( ArrayList<String []> currFactor){
        ArrayList<String> varNamesWithout = new ArrayList<>();
        for (int i = 0; i < currFactor.get(0).length-1; i++) {
            int index =  currFactor.get(0)[i].indexOf("=");
            String varName = currFactor.get(0)[i].substring(0,index);
            varNamesWithout.add(varName);
        }
        return  varNamesWithout;

    }

    public static void isParent(Variable parent,Variable var,Network net){
        if(var==null){
            return ;
        }
        if(var.getName().equals(parent.getName()))
            p = true;
        for (int i = 0; i < var.getParents().size(); i++) {
            isParent(parent,net.getVarbyName(var.getParents().get(i)),net);
        }
    }


    public static ArrayList<String> evidenceWithoutOutcomes(ArrayList<String> evi){
        ArrayList<String> ans = new ArrayList<>();

        for (String s : evi) {
            String var = s.substring(0, s.indexOf("="));
            ans.add(var);

        }
        return  ans;
    }

    public static void computeRow(String [] row,boolean flag,ArrayList<String> evidence,Network net){
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
        if(flag){
            numerter+=sum;
        }else{
            denominator+=sum;
        }
    }

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
        System.out.println(Arrays.deepToString(CPT));
        return CPT;
    }

    public static void generatePermutations(List<List<String>> lists, List<String> result, int depth, String current) {
        if (depth == lists.size()) {
            result.add(current.substring(0, current.length()-1));

            return;
        }

        for (int i = 0; i < lists.get(depth).size(); i++) {
            generatePermutations(lists, result, depth + 1, current + lists.get(depth).get(i) + ",");
        }
    }

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
