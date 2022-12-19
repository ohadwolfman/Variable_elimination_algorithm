import java.util.List;
import java.util.ArrayList;

public class Definition {

    private String forField;
    private List<String> givenList;
    private List<Double> tableList;

    public Definition(String def) {
        this.forField=def;
        this.givenList= new ArrayList<>();
        this.tableList= new ArrayList<>();
    }

    public String getForField() {
        return forField;
    }

    public void setForField(String forField) {
        this.forField = forField;
    }

    public List<String> getGivenList() {
        return givenList;
    }

    public void setGivenList(String given) {
        this.givenList.add(given);
    }

    public List<Double> getTableList() {
        return tableList;
    }

    public void setTableList(String table) {
        List<Double> result = convertStringToList(table);
        this.tableList = result;
    }

    public static List<Double> convertStringToList(String input) {
        List<Double> result = new ArrayList<>();
        // Split the input string on any number of whitespace characters
        String[] parts = input.split("\s+");

        for (String part : parts) {
            result.add(Double.parseDouble(part));
        }
        return result;
    }

    public static String[][] createCPT(Definition def){
        int numOfVarInCpt = def.givenList.size()+1;
        int numOfRows = (int) Math.pow(2,numOfVarInCpt);

        String[][] cpt = new String[numOfRows][numOfVarInCpt+1];
        int jumps = numOfRows/2;
        for (int i=0; i<numOfVarInCpt; i++){
            for (int j=0; j<jumps; j++){
                cpt[j][i] = "T";
                }
            for (int j=0; j<jumps; j++){
                cpt[j][i] = "F";
                }
            }

        return cpt;
    }

    @Override
    public String toString() {
        return "Definition{" +
                "forField='" + forField + '\'' +
                ", givenList=" + givenList +
                ", tableList=" + tableList +
                '}';
    }
}
