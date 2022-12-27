import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Ex1 {
    // The final_answer is a String that collect all the relevant answers for the queries
    // eventually final_answer will be exported to output file
    // addAndMullVariableElimination will contain the number of adding actions and the number of multiplication actions
    public static String final_answer="";
    public static int[] addAndMullVariableElimination= {1,0};

    public static void main(String[] args) throws FileNotFoundException {
        File inputFile = new File("input.txt");
        Scanner input = new Scanner(inputFile);
        String fileName = input.nextLine();
        Network net  = new Network();
        Xml_reader.readXml(fileName,net);

        ArrayList<String> queryAndEvidencesWithOutcome = new ArrayList<>();
        ArrayList<String> varHidden = new ArrayList<>();
        ArrayList<String> varNames = new ArrayList<>();

        while (input.hasNextLine()) {
            for (int i = 0; i < net.getDef().size(); i++) {
                varNames.add(net.getDef().get(i).getForField());
            }
            String line = input.nextLine();
            int queryLen = line.length();
            // the query is P(...|...),X. we want to hold only the query
            String query = line.substring(2, queryLen - 3);
            String [] vars = query.split("\\|");

            //we will split the line to query ([0]) and evidences ([1])
            queryAndEvidencesWithOutcome.add(vars[0]);
            //System.out.println(Arrays.deepToString(vars));
            String queryVarName = vars[0].substring(0,vars[0].indexOf("="));
            String queryVarOutCome = vars[0].substring(vars[0].indexOf("=")+1);
            String [] otherVars = vars[1].split(",");
            queryAndEvidencesWithOutcome.addAll(Arrays.asList(otherVars));

            //currentVars is all the variables in the query and the evidences variables - without the outcomes
            ArrayList<String> currentVars = Parser.evidenceWithoutOutcomes(queryAndEvidencesWithOutcome);
            for (int i = 0; i <net.getVars().size() ; i++) {
                String varName = net.getVars().get(i).getName();
                if(!currentVars.contains(varName))
                    varHidden.add(varName);
            }

            if(!Parser.isQueryAlreadyExists(net,queryVarName,queryAndEvidencesWithOutcome).equals("-1")) {
                final_answer += Parser.isQueryAlreadyExists(net, queryVarName, queryAndEvidencesWithOutcome)+",0,0";
                continue;
            }
            String functionNumber = line.substring(queryLen - 1);

            if (functionNumber.charAt(0) == '1') {
                double[] num_and_den = Parser.simpleInference(net,varNames,currentVars,queryAndEvidencesWithOutcome,queryVarName);
                String ans = (String.format("%.5f", (num_and_den[0] / (num_and_den[0] + num_and_den[1]))));
                ans+=Parser.addAndMullSimpleInference(varHidden,net,queryVarName);
                //System.out.println(ans);
                final_answer+=ans+"\n";
            }

            if (functionNumber.charAt(0) == '2') {
                String ans = Parser.variableElimination(varHidden,queryAndEvidencesWithOutcome,varNames,currentVars,net,queryVarName+"="+queryVarOutCome);
                ans+=","+addAndMullVariableElimination[0]+","+addAndMullVariableElimination[1];
                final_answer+=ans+"\n";

                addAndMullVariableElimination[0]=1;
                addAndMullVariableElimination[1]=0;
            }

            if (functionNumber.charAt(0) == '3') {
                //System.out.println("0,0,0");
                final_answer+="0,0,0\n";
            }

            queryAndEvidencesWithOutcome.clear();
            varHidden.clear();
            varNames.clear();
        }

        //export the prints to an output file
        try{
            FileWriter writeToOutput = new FileWriter("output.txt");
            writeToOutput.write(final_answer);
            writeToOutput.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
