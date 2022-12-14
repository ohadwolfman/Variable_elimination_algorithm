import java.util.List;

public class Definition {

    private String forField;
    private List<String> givenList;
    private List<Double> tableList;

    public Definition(String def) {
        this.forField=def;
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

    public void setTableList(List<Double> table) {
        this.tableList = (table);
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
