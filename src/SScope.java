package FinalProject;

import java.util.*;

public class SScope
{
    private ArrayList<Integer> id;
    private SScope parent;
    private Map<String, SVariable> vars = new HashMap<String, SVariable>();
    private int nextAvailableChildIndex = 0;

    public SScope()
    {
        id = new ArrayList<Integer>();
        id.add(0);
        parent = null;
    }

    public SScope(ArrayList<Integer> inId, SScope inParent)
    {
        id = inId;
        parent = inParent;
    }

    public ArrayList<Integer> getId()
    {
        return id;
    }

    public SScope getParent()
    {
        return parent;
    }

    public int getNextAvailableChildIndex()
    {
        return nextAvailableChildIndex;
    }

    public void resetNextAvailableChildIndex()
    {
        nextAvailableChildIndex = 0;
    }

    public void incrementNextAvailableChildIndex()
    {
        nextAvailableChildIndex++;
    }

    // VARS

    // Caller should call SScope.containsVar() first
    public void addVar(SVariable var)
    {
        vars.put(var.getName(), var);
    }

    public boolean containsVar(String id)
    {
        return vars.containsKey(id);
    }

    public SVariable getVar(String id)
    {
        return vars.get(id);
    }

    public Map<String, SVariable> getVars()
    {
        return vars;
    }
} 