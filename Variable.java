import java.util.ArrayList;
import java.util.List;

public class Variable {

    private String name;
    private List<String> outcomeList;
    private List<String> parents;
    private List<Double> cpt;

    public Variable (String Name){
        this.name = Name;
        this.outcomeList = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.cpt = new ArrayList<>();

    }

    /*public Variable (String name, ArrayList<String> outcomes, ArrayList<String> parents, ArrayList<Double> cpt){
        this.name=name;
        this.outcomeList=outcomes;
        this.parents=parents;
        this.cpt=cpt;
    }*/

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
        this.parents.add(parent);
    }

    public List<Double> getCpt() {
        return cpt;
    }

    public void setCpt(ArrayList<Double> cpt) {
        this.cpt = cpt;
    }
}
