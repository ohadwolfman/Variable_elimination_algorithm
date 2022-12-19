import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Ex1 {

    public static void main(String[] args) throws FileNotFoundException {
        String path = "C:\\Users\\ohad1\\OneDrive\\Documents\\";
        String inputFileName = "input.txt";
        String inputDirectory = path+inputFileName;

        File inputFile = new File(inputDirectory);
        Scanner input = new Scanner(inputFile);
        String fileName = input.nextLine();
        String xmlDirectory = path+fileName;
        Xml_reader.readXml(xmlDirectory);

        while (input.hasNextLine()) {
            String line = input.nextLine();
            int queryLen = line.length();
            String query = line.substring(2, queryLen - 3);
            System.out.println(query);

            String functionNumber = line.substring(queryLen - 1);
            System.out.println(extractEvidences(query));

            if (functionNumber.charAt(0) == '1') {
                ArrayList<String> evidences = extractEvidences(query);
                //System.out.println(SimpleInference(query));
                ;
            }
            if (functionNumber.charAt(0) == '2') {
                ArrayList<String> evidences = extractEvidences(query);
                //System.out.println(VariableElimination(query));
                ;
            }
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

    public static String[][] createCpt(List<Variable> Variables) {
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
        String[][] CPT = new String[rows][Variables.size() + 1];

        for (int i = 0; i < rows; i++) {
            String[] row = allPermutations.get(i).split(",");
            CPT[i][0] = String.valueOf(i + 1);
            for (int j=1; j<=Variables.size(); j++){
                CPT[i][j] = row[j-1];
            }
        }
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

    public static void printCpt(ArrayList<Variable> variables) {
        String[][] cpt = createCpt(variables);
        for (int i = 0; i < cpt.length; i++) {
            for (int j = 0; j < variables.size() + 1; j++) {
                System.out.print(cpt[i][j] + ",");
            }
            System.out.println();
        }
    }
}
