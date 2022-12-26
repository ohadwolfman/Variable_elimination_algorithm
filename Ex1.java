import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Ex1 {
    public static void main(String[] args) throws FileNotFoundException {
        String path = "C:\\Users\\ohad1\\OneDrive\\Documents\\";
//        String path = "C:\\Users\\yarin\\IdeaProjects\\OhadCode\\src\\";
        String inputFileName = "input.txt";
        String inputDirectory = path+inputFileName;
        //ParseInput(String inputDirectory);

        File inputFile = new File(inputDirectory);
        Scanner input = new Scanner(inputFile);
        String fileName = input.nextLine();
        String xmlDirectory = path+fileName;
        Network net  = new Network();
        Xml_reader.readXml(xmlDirectory,net);

        ArrayList<String> varEvidence = new ArrayList<>();
        ArrayList<String> varHidden = new ArrayList<>();
        ArrayList<String> varNames = new ArrayList<>();


        System.out.println("varNames"+varNames);
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

                    if(!flag)Parser.computeRow(toCompute[i],true,varEvidence,net);
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

                    if(!flag)
                        Parser.computeRow(toCompute[i],false,varEvidence,net);

                }
                String ans = (String.format("%.5f", (Parser.numerter / (Parser.denominator + Parser.numerter))));

                ans+=Parser.addAndMull(varHidden,net,queryVarName);
                System.out.println(ans);
                Parser.numerter = 0;
                Parser.denominator = 0;

            }
            if (functionNumber.charAt(0) == '2') {
                ArrayList<String> evidences = Parser.extractEvidences(query);
                Parser.variableElimination(varHidden,varEvidence,varNames,currentVars,net);
                Parser.copyFactors(net,currentVars,varEvidence);


                Parser.p = false;
                //System.out.println(VariableElimination(query));
            }
            varEvidence.clear();
            varHidden.clear();
            varNames.clear();
        }


    }


}
