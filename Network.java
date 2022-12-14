import java.util.ArrayList;
import java.util.List;

public class Network {
    private ArrayList<Variable> varibaleList;
    private ArrayList<Definition> definitionList;

    public Network() {
    }

    public void addVar(Variable v) {
        varibaleList.add(v);
    }

    public void addDef(Definition def) {
        definitionList.add(def);
    }

    public void deleteVar(Variable v) {
        varibaleList.remove(v);
    }

    public boolean isEmpty(){
        if(varibaleList.isEmpty())
            return true;
        return false;
    }

    public boolean isVariableExists(Variable v){
        return (varibaleList.contains(v))? true: false;
    }




}
