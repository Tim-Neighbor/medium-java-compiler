package FinalProject;

public class SVariable
{
    private String name;
    private String type;
    private boolean isArray;
    private int arraySize;

    // Offset from the stack pointer
    private int offset;
    
    public SVariable(String inName, String inType)
    {
        name = inName;
        type = inType;
        isArray = false;
        arraySize = 0;
    }
    
    public SVariable(String inName, String inType, int inArraySize)
    {
        name = inName;
        type = inType;
        isArray = true;
        arraySize = inArraySize;
    }


    public String getName()
    {
        return name;
    }

    public String getType() 
    {
        return type;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int inOffset)
    {
        offset = inOffset;
    }

    public boolean isArray()
    {
        return isArray;
    }

    public int getArraySize()
    {
        return arraySize;
    }

    public boolean equals(Object that) 
    {
        if (that instanceof SVariable)
        {
            return name.equals(((SVariable) that).name);         
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public String toString()
    {
        if (isArray)
        {
            return name + ":" + type + "[" + arraySize + "]";
        }
        else
        {
            return name + ":" + type;
        }
    }
}
