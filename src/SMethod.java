package FinalProject;

import java.util.*;

public class SMethod
{
    private String name;
    private String returnType;
    private ArrayList<SVariable> params = new ArrayList<SVariable>();
    private Map<ArrayList<Integer>, SScope> allScopes = new HashMap<ArrayList<Integer>, SScope>();
    private SScope curScope;

    // How much offset has been done in this method so far
    private int offset;
    
    public SMethod(String inName, String inReturnType)
    {
        name = inName;
        returnType = inReturnType;
        
        SScope topLevelScope = new SScope();
        allScopes.put(topLevelScope.getId(), topLevelScope);
        curScope = topLevelScope;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return returnType;
    }
    
    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int inOffset)
    {
        offset = inOffset;
    }

    public String toString()
    {
        String paramList = "";

        for (SVariable var : params)
        {
            if (!paramList.equals(""))
            {
                paramList = paramList + ", ";
            }
            paramList = paramList + var;
        }

        return returnType + " " + name + "(" + paramList + ")";
    }

    // PARAMS

    public void addParam(SVariable var) throws Exception
    {
        if (containsParam(var.getName()))
        {
            throw new Exception("ERROR: The given parameter \"" + var +
                "\" already exists in the method \"" + this + "\"");
        }
        else
        {
            params.add(var);
            addVar(var);
        }
    }

    public boolean containsParam(String id)
    {
        for (SVariable var : params)
        {
            if (id.equals(var.getName()))
            {
                return true;
            }
        }

        return false;
    }

    public ArrayList<SVariable> getParams()
    {
        return params;
    }

    /*public SVariable getParam(String id)
    {
        return null;
    }*/

    // VARS

    public void addVar(SVariable var) throws Exception
    {
        if (curScope.containsVar(var.getName()))
        {
            throw new Exception("ERROR: The given variable \"" + var +
                "\" already exists in the current scope within the method \"" +
                this + "\"");
        }

        curScope.addVar(var);
    }

    public boolean containsVar(String id)
    {
        SScope scopeToSearch = curScope;

        while (scopeToSearch != null)
        {
            if (scopeToSearch.containsVar(id))
            {
                return true;
            }
            else
            {
                scopeToSearch = scopeToSearch.getParent();
            }
        }
        return false;
    }
    
    public SVariable getVar(String id)
    {
        SScope scopeToSearch = curScope;

        while (scopeToSearch != null)
        {
            //System.out.println("New Scope [Level: " + scopeToSearch.getLevel() + ", Index: " + scopeToSearch.getIndex() + "]");
            //scopeToSearch.getVars().forEach((key, value) -> System.out.println(value));
                
            if (scopeToSearch.containsVar(id))
            {
                return scopeToSearch.getVar(id);
            }
            else
            {
                scopeToSearch = scopeToSearch.getParent();
            }
        }
        
        //System.out.println("COULDNT FIND VARIABLE: " + id + " IN THE METHOD: " + this);

        return null;
    }

    // SCOPE

    public void addAndGoToInnerScope()
    {
        ArrayList<Integer> newScopeId = new ArrayList<Integer>();
        
        for (Integer num : curScope.getId())
        {
            newScopeId.add(num);
        }
        newScopeId.add(curScope.getNextAvailableChildIndex());
        curScope.incrementNextAvailableChildIndex();

        SScope newScope = new SScope(newScopeId, curScope);
        allScopes.put(newScopeId, newScope);

        curScope = newScope;
    }

    public void goToInnerScope()
    {
        ArrayList<Integer> newScopeId = new ArrayList<Integer>();
        
        for (Integer num : curScope.getId())
        {
            newScopeId.add(num);
        }
        newScopeId.add(curScope.getNextAvailableChildIndex());
        curScope.incrementNextAvailableChildIndex();

        //TupleOfTwo tuple = new TupleOfTwo(newScopeLevel, newScopeIndex);
        //System.out.println("Trying to find Tuple of Two: " + tuple);
        //allScopes.forEach((key,value) -> System.out.println(key));
        curScope = allScopes.get(newScopeId);
        //System.out.println("curScope is " + curScope);
    }

    public void goToOuterScope()
    {
        if (curScope.getParent() != null)
        {
            curScope.resetNextAvailableChildIndex();
            curScope = curScope.getParent();
        }
        else
        {
            System.out.println("Error: Can't go any higher in scope");
        }
    }

    public void finishMethod()
    {
        curScope.resetNextAvailableChildIndex();
    }
}
