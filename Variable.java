import java.util.ArrayList;
import java.util.List;

public class Variable {

    private String name;
    private List<String> outcomeList;
    private List<String> parents;
    private String[][] cpt;

    public Variable (String Name){
        this.name = Name;
        this.outcomeList = new ArrayList<>();
        this.parents = new ArrayList<>();
    }

    public Variable (String Name, ArrayList<String>outcomeList){
        this.name = Name;
        this.outcomeList = outcomeList;
        this.parents = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getOutcomeList() {
        return outcomeList;
    }

    public void setOutcomeList(String outcome) {
        this.outcomeList.add(outcome);
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ",outcomeList=" + outcomeList +
                '}';
    }

    public List<String> getParents() {
        return parents;
    }

    public void setParents(String parent) {
//        this.parents.add(parent);
        this.parents.add(0,parent);
    }

    public String[][] getCpt() {
        return cpt;
    }

    public void setCpt(String[][] cpt) {
        this.cpt = cpt;
    }
}
