import java.util.ArrayList;
import java.util.List;

public class Network {
    private List<Variable> varibaleList;
    private List<Definition> definitionList;

    public Network() {
        this.varibaleList = new ArrayList<>();
        this.definitionList = new ArrayList<>();
    }

    public void addVar(Variable v) {
        varibaleList.add(v);
    }

    public Variable getVarbyName(String varName ){;
        for (int i = 0; i <varibaleList.size() ; i++) {
            if(varName.equals(varibaleList.get(i).getName()))return varibaleList.get(i);
        }
        return null;
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
    public List<Definition> getDef(){
        return this.definitionList;
    }
    public List<Variable> getVars(){
        return this.varibaleList;
    }


    public boolean isVariableExists(Variable v){
        return (varibaleList.contains(v))? true: false;
    }




}
