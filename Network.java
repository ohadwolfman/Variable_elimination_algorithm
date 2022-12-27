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
        for (Variable variable : varibaleList) {
            if (varName.equals(variable.getName())) return variable;
        }
        return null;
    }

    public void addDef(Definition def) {
        definitionList.add(def);
    }

    public List<Definition> getDef(){
        return this.definitionList;
    }
    public List<Variable> getVars(){
        return this.varibaleList;
    }
}
