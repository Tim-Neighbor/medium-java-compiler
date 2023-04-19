package FinalProject;

public class TupleOfTwo
{
    private int x;
    private int y;

    public TupleOfTwo(int inX, int inY)
    {
        x = inX;
        y = inY;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public String toString()
    {
        return "[" + x + ", " + y + "]";
    }

    public boolean equals(Object that) 
    {
        if (that instanceof TupleOfTwo)
        {
            TupleOfTwo thatTuple = (TupleOfTwo)that;
            if (x == thatTuple.getX() && y == thatTuple.getY())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return x * y - x;
    }
}