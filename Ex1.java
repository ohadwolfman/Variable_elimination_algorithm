import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Ex1 {

    public static double numerter = 0;
    public static double denominator = 0;


    public static void main(String[] args) throws FileNotFoundException {
        String path = "C:\\Users\\ohad1\\OneDrive\\Documents\\";
//        String path = "C:\\Users\\yarin\\IdeaProjects\\OhadCode\\src\\";
        String inputFileName = "input.txt";
        String inputDirectory = path+inputFileName;
        String ans = "";
        File inputFile = new File(inputDirectory);
        Scanner input = new Scanner(inputFile);
        String fileName = input.nextLine();
        String xmlDirectory = path+fileName;
        Network net  = new Network();
        Xml_reader.readXml(xmlDirectory,net);
        ArrayList<String> varEvidence = new ArrayList<>();
        ArrayList<String> varHidden = new ArrayList<>();
        ArrayList<String> varNames = new ArrayList<>();
        for (int i = 0; i < net.getDef().size(); i++) {
            varNames.add(net.getDef().get(i).getForField());
        }
        System.out.println(varNames);
        while (input.hasNextLine()) {
            String line = input.nextLine();
            int queryLen = line.length();
            String query = line.substring(2, queryLen - 3);
            String [] vars = query.split("\\|");
            varEvidence.add(vars[0]);
            System.out.println(Arrays.deepToString(vars));
            String queryVarName = vars[0].substring(0,vars[0].indexOf("="));
            String queryVarOutCome = vars[0].substring(vars[0].indexOf("=")+1);
            String [] otherVars = vars[1].split(",");
            for (int i = 0; i <otherVars.length ; i++) {
                varEvidence.add(otherVars[i]);
            }
            ArrayList<String> currentVars = evidenceWithoutOutcomes(varEvidence);
            for (int i = 0; i <net.getVars().size() ; i++) {
                String varName = net.getVars().get(i).getName();
                if(!currentVars.contains(varName))varHidden.add(varName);
            }
            String functionNumber = line.substring(queryLen - 1);

            if (functionNumber.charAt(0) == '1') {
                ArrayList<String> evidences = extractEvidences(query);

                String [][] toCompute = createCpt(net.getVars(),varNames);
                for (int i = 0; i < toCompute.length; i++) {
                    boolean flag = false;
                    for (int j = 0; j < toCompute[i].length; j++) {
                        String varWithOutCome = toCompute[i][j];
                        String var = toCompute[i][j].substring(0,toCompute[i][j].indexOf("="));
                        if(currentVars.contains(var)){
                            if(!varEvidence.contains(varWithOutCome))flag = true;
                        }
                    }

                    if(!flag)computeRow(toCompute[i],true,varEvidence,net);
                }
                System.out.println(numerter);

//                for (int i = 0; i < toCompute.length; i++) {
//                    computeRow(toCompute[i],false,varEvidence,net);
//                }

//                printCpt((ArrayList<Variable>) net.getVars(),varNames);

            }
            if (functionNumber.charAt(0) == '2') {
                ArrayList<String> evidences = extractEvidences(query);
                //System.out.println(VariableElimination(query));
                ;
            }
        }
    }

    public static ArrayList<String> evidenceWithoutOutcomes(ArrayList<String> evi){
        ArrayList<String> ans = new ArrayList<>();

        for (int i = 0; i <evi.size() ; i++) {
            String var = evi.get(i).substring(0,evi.get(i).indexOf("="));
            ans.add(var);

        }
        return  ans;
    }

    public static void computeRow(String [] row,boolean flag,ArrayList<String> evidence,Network net){
        double sum = 1;

        for (int i = 0; i <row.length ; i++) {
            String currVar = row[i].substring(0,row[i].indexOf("="));
            String [] arr = new String [net.getVarbyName(currVar).getParents().size()+1];
            arr[0] = row[i];
            for (int j = 0; j < net.getVarbyName(currVar).getParents().size(); j++) {
                String currParnets = net.getVarbyName(currVar).getParents().get(j);
                for (int k = 0; k < row.length; k++) {
                    String checkParent = row[k].substring(0,row[i].indexOf("="));
                    if(currParnets.equals(checkParent))
                        arr[j+1]=row[k];
                }
            }
            String[][] cptCurrVar = net.getVarbyName(currVar).getCpt();
            for (int j = 0; j <cptCurrVar.length ; j++) {
                if(Arrays.equals(arr,Arrays.copyOfRange(cptCurrVar[j],0,cptCurrVar[j].length-1))){
                    sum*=Double.parseDouble(cptCurrVar[j][cptCurrVar[j].length-1]);
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

    public static String[][] createCpt(List<Variable> Variables,ArrayList<String> varName) {
        List<List<String>> listsInput = new ArrayList<>();
        System.out.println(Variables.size());
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
