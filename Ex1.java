import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

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

        /*
        private static String SimpleInference (String query){
            ArrayList<Double> answer = new ArrayList<>();
        }

        private static void VariableElimination (String query){
        }
*/
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
        return result;
    }
}
