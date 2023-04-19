package FinalProject;

import java.util.*;

public class SymbolTable
{
    private Map<String, SVariable> variables = new LinkedHashMap<String, SVariable>();
    private Map<String, SMethod> methods = new HashMap<String, SMethod>();
    //private Map<String, SClass> classes = new HashMap<String, SClass>();

    public SymbolTable() {}
    
    // Was boolean in slides
    public void addMethod(SMethod method) throws Exception
    {
        if (containsMethod(method.getName())) 
        {
            throw new Exception("ERROR: The given global method \"" + method +
                "\" already exists");
        }
        else
        {
            methods.put(method.getName(), method);
        }
    }

    public boolean containsMethod(String id)
    {
        return methods.containsKey(id);
    }

    public SMethod getMethod(String id)
    {
        return methods.get(id);
    }

    // Was boolean in slides
    public void addVar(SVariable var) throws Exception
    {
        if (containsVar(var.getName())) 
        {
            throw new Exception("ERROR: The given global variable \"" + var +
                "\" already exists");
        }
        else
        {
            variables.put(var.getName(), var);
        }
    }

    public boolean containsVar(String id)
    {
        return variables.containsKey(id);
    }

    public SVariable getVar(String id) throws Exception
    {
        SVariable toReturn = variables.get(id);
        if (toReturn == null)
        {
            throw new Exception("ERROR: The given variable \"" + id +
                "\" has not been declared");
        }
        else
        {
            return variables.get(id);
        }
    }

    public Map<String, SVariable> getVars()
    {
        return variables;
    }
}
