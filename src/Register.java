package FinalProject;

public class Register
{
    private String regName;
    private boolean isInUse;

    public Register(String inRegName)
    {
        regName = inRegName;
        isInUse = false;
    }

    public void setIsInUse(boolean inIsInUse)
    {
        isInUse = inIsInUse;
    }

    public boolean getIsInUse()
    {
        return isInUse;
    }

    public String getRegName()
    {
        return regName;
    }

    public boolean isFloatRegister()
    {
        return regName.startsWith("$f");
    }

    public String toString()
    {
        return regName;
    }

    public boolean equals(Object that)
    {
        if (that instanceof Register)
        {

            return regName.equals(((Register) that).toString());
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return regName.hashCode();
    }
}