import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Parser {
    public static double numerter = 0;
    public static double denominator = 0;

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
