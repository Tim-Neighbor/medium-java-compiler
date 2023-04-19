package FinalProject;

import java.util.*;

public class RegisterFile
{
    ArrayList<Register> regularRegisters = new ArrayList<Register>();
    ArrayList<Register> floatRegisters = new ArrayList<Register>();
    
    public RegisterFile()
    {
      for (int i = 0; i <= 9; i++)
      {
        regularRegisters.add(new Register("$t" + i));
      }
  
      for (int i = 0; i <= 7; i++)
      {
        regularRegisters.add(new Register("$s" + i));
      }

      // skip $f0 because it is needed for syscalls

      for (int i = 1; i <= 11; i++)
      {
        floatRegisters.add(new Register("$f" + i));
      }

      // skip $f12 because it is needed for syscalls

      for (int i = 13; i <= 31; i++)
      {
        floatRegisters.add(new Register("$f" + i));
      }
    }
  
    public Register getFirstOpenRegularRegister()
    {
      for (Register reg : regularRegisters)
      {
        if (!reg.getIsInUse())
        {
          return reg;
        }
      }

      System.out.println("ERROR: No more remaining registers to allocate. Please break up expression into multiple statements.");

      return null;
    }

    public Register getFirstOpenFloatRegister()
    {
      for (Register reg : floatRegisters)
      {
        if (!reg.getIsInUse())
        {
          return reg;
        }
      }

      System.out.println("ERROR: No more remaining float registers to allocate. Please break up expression into multiple statements.");
  
      return null;
    }
}
