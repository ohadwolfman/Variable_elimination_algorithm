import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Ex1 {
    public static String final_answer;

    public static void main(String[] args) throws FileNotFoundException {
        File inputFile = new File("src/input.txt");
        Scanner input = new Scanner(inputFile);
        String fileName = input.nextLine();
        Network net  = new Network();
        Xml_reader.readXml(fileName,net);

        ArrayList<String> varEvidence = new ArrayList<>();
        ArrayList<String> varHidden = new ArrayList<>();
        ArrayList<String> varNames = new ArrayList<>();


        //System.out.println("varNames"+varNames);
        while (input.hasNextLine()) {
            for (int i = 0; i < net.getDef().size(); i++) {
                varNames.add(net.getDef().get(i).getForField());
            }
            String line = input.nextLine();
            int queryLen = line.length();
            String query = line.substring(2, queryLen - 3);
            String [] vars = query.split("\\|");
            varEvidence.add(vars[0]);
            //System.out.println(Arrays.deepToString(vars));
            String queryVarName = vars[0].substring(0,vars[0].indexOf("="));
            String queryVarOutCome = vars[0].substring(vars[0].indexOf("=")+1);
            String [] otherVars = vars[1].split(",");
            varEvidence.addAll(Arrays.asList(otherVars));

            ArrayList<String> currentVars = Parser.evidenceWithoutOutcomes(varEvidence);
            for (int i = 0; i <net.getVars().size() ; i++) {
                String varName = net.getVars().get(i).getName();
                if(!currentVars.contains(varName))
                    varHidden.add(varName);
            }
            String functionNumber = line.substring(queryLen - 1);
            if (functionNumber.charAt(0) == '1') {
                ArrayList<String> evidences = Parser.extractEvidences(query);
                double denominator = 0;
                double numerter = 0;
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
                        numerter+=Parser.computeRow(toCompute[i],varEvidence,net,numerter);
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
                String ans = (String.format("%.5f", (numerter    / (denominator + numerter))));

                ans+=Parser.addAndMull(varHidden,net,queryVarName);
                //System.out.println(ans);
                final_answer+=ans+"\n";

            }
            if (functionNumber.charAt(0) == '2') {
                ArrayList<String> evidences = Parser.extractEvidences(query);
                Parser.variableElimination(varHidden,varEvidence,varNames,currentVars,net,queryVarOutCome);

                //System.out.println(VariableElimination(query));
            }

            if (functionNumber.charAt(0) == '3') {
                //System.out.println("0,0,0");
                final_answer+="0,0,0\n";
            }
            varEvidence.clear();
            varHidden.clear();
            varNames.clear();
        }
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
