import java.util.ArrayList;
import java.util.List;

public class Variable {

    private String name;
    private ArrayList<String> outcomeList;
    private ArrayList<String> parents;
    private ArrayList<Double> cpt;

    public Variable (){
    }

    public Variable (String Name){
        this.name = Name;
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

    public ArrayList<String> getOutcomeList() {
        return outcomeList;
    }

    public void setOutcomeList(String outcome) {
        this.outcomeList.add(outcome);
    }

    @Override
    public String toString() {
        return "Variable{\n" +
                "name='" + name + '\'' +
                ",\noutcomeList=" + outcomeList +
                '}';
    }

    public ArrayList<String> getParents() {
        return parents;
    }

    public void setParents(String parent) {
        this.parents.add(parent);
    }

    public ArrayList<Double> getCpt() {
        return cpt;
    }

    public void setCpt(ArrayList<Double> cpt) {
        this.cpt = cpt;
    }
}
