package FinalProject;

import FinalProject.analysis.*;
import FinalProject.node.*;
import java.util.*;

class TranslateTree extends DepthFirstAdapter
{
	private SymbolTable symbolTable;
  private ArrayList<String> stringConstantList;
  // For counting through the stringConstantList
  private int stringCounter = 0;

  private int ifCounter = 0;
  private int loopCounter = 0;
  private int switchCounter = 0;

  RegisterFile registerFile = new RegisterFile();

  //private String currentClassName = "";
  private String currentMethodName = "";

  // Attributes for inheriting and synthesizing
  private int inhAttrInt;
  private int synthAttrInt;
  //private float inhAttrReal;
  private float synthAttrReal;
  private String inhAttrStr;
  private String synthAttrStr;
  //private SMethod inhAttrMethod;
  //private SMethod synthAttrMethod;
  private Register inhAttrReg;
  private Register synthAttrReg;

  // Sizes in bytes
  private final int STR_SIZE = 256;
  private final int VAR_SIZE = 4;
  private final int WORD_SIZE = 4;

 	public TranslateTree(SymbolTable inSymbolTable, ArrayList<String> inStringConstantList)
  {
		symbolTable = inSymbolTable;
    stringConstantList = inStringConstantList;
	}

  /////////////////////////////////////////////////////////////////////////////////////

	// Helper methods

  private String getMipsDataDeclaration(SVariable var)
  {
    switch (var.getType()) {
      case "INT":
        return ".word 0";
      case "REAL":
        return ".float 0.0";
      case "STRING":
        return ".space " + STR_SIZE;
      case "BOOLEAN":
        return ".word 0";
      default:
        return "";
    }
  }

  private void createDataSection()
  {
    System.out.println("\t.data");

    Map<String, SVariable> globalVars = symbolTable.getVars();
    
    for (Map.Entry<String, SVariable> globalVar : globalVars.entrySet())
    {
      System.out.println(globalVar.getKey() + ":");
      System.out.println("\t" + getMipsDataDeclaration(globalVar.getValue()));
    }
    
    int stringNum = 0;

    for (String str : stringConstantList)
    {
      // Underscores are allowed in MIPS labels but not in our language's id's
      System.out.println("_stringConstant" + stringNum + ":");
      System.out.println("\t.align 2");
      System.out.println("\t.asciiz " + str);
      stringNum++;
    }
  }

  // Print instruction line
  private void printI(String instruction)
  {
    System.out.println("\t" + instruction);
  }

  // Print label line
  private void printL(String labelName)
  {
    System.out.println(labelName + ":");
  }

  private SVariable getVar(String varName)
  {
    try
    {
 
      if (symbolTable.containsVar(varName)) 
      {
        return symbolTable.getVar(varName);
      }
      else
      {
        return symbolTable.getMethod(currentMethodName).getVar(varName);
      }
    }
    catch (Exception e)
    {
      System.out.println("Something went very wrong. No variable?");
      return null;
    }
  }

  private String convertFloatToHexString(Float num)
  {
    return String.format("0x%08X", Float.floatToRawIntBits(num));
  }
  
	/////////////////////////////////////////////////////////////////////////////////////

	// Token cases

	public void caseTString(TString node)
  {
    synthAttrStr = node.getText();
  }

  public void caseTId(TId node)
  {
    synthAttrStr = node.getText();
  }

  public void caseTInt(TInt node)
  {
    synthAttrInt = Integer.parseInt(node.getText());
  }

  public void caseTReal(TReal node)
  {
    synthAttrReal = Float.parseFloat(node.getText());
  }

	/////////////////////////////////////////////////////////////////////////////////////

	// Production cases
	
  public void caseAProg(AProg node)
  {
    createDataSection();
    System.out.println("\t.text");

    node.getClassmethodstmts().apply(this);
  }

  public void caseAFullproductionClassmethodstmts(AFullproductionClassmethodstmts node)
  {
    node.getClassmethodstmts().apply(this);
    node.getClassmethodstmt().apply(this);
  }

  public void caseAEmptyproductionClassmethodstmts(AEmptyproductionClassmethodstmts node)
  {

  }

  public void caseAClassdeclClassmethodstmt(AClassdeclClassmethodstmt node)
  {
    // TO DO
    
    //node.getId().apply(this);
    //node.getMethodstmtseqs().apply(this);
  }

  public void caseAMethoddeclClassmethodstmt(AMethoddeclClassmethodstmt node)
  {
    node.getType().apply(this);

    node.getId().apply(this);
    String methodName = synthAttrStr;

    currentMethodName = methodName;

    // Figure out me later
    node.getVarlist().apply(this);

    printL(methodName);

    node.getStmtseq().apply(this);

    if (methodName.equals("MAIN"))
    {
      printI("li $v0, 10");
      printI("syscall");
    }

    currentMethodName = "";
  }

  public void caseAFielddeclClassmethodstmt(AFielddeclClassmethodstmt node)
  {
    // Taken care of in Prog case

    // node.getType().apply(this);
    // node.getId().apply(this);

    // List<PAdditionalidlist> starList = new ArrayList<PAdditionalidlist>(node.getAdditionalidlist());
    // for(PAdditionalidlist starItem : starList)
    // {
    //   starItem.apply(this);
    // }
  }

  public void caseAFullproductionMethodstmtseqs(AFullproductionMethodstmtseqs node)
  {
    // TO DO

    //node.getMethodstmtseqs().apply(this);
    //node.getMethodstmtseq().apply(this);
  }

  public void caseAEmptyproductionMethodstmtseqs(AEmptyproductionMethodstmtseqs node)
  {
    // TO DO
  }

  public void caseAMethoddeclMethodstmtseq(AMethoddeclMethodstmtseq node)
  {
    // TO DO

    //node.getType().apply(this);
    //node.getId().apply(this);
    //node.getVarlist().apply(this);
    //node.getStmtseq().apply(this);
  }

  public void caseAFielddeclMethodstmtseq(AFielddeclMethodstmtseq node)
  {
    // TO DO

    // node.getId().apply(this);

    // List<PAdditionalidlist> starList = new ArrayList<PAdditionalidlist>(node.getAdditionalidlist());
    // for (PAdditionalidlist starItem : starList)
    // {
    //   starItem.apply(this);
    // }

    // node.getType().apply(this);
  }

  public void caseAFullproductionStmtseq(AFullproductionStmtseq node)
  {
    node.getStmt().apply(this);
    node.getStmtseq().apply(this);
  }

  public void caseAEmptyproductionStmtseq(AEmptyproductionStmtseq node)
  {

  }

  public void caseAAssignexprStmt(AAssignexprStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    SVariable var = getVar(varName);
    String varType = var.getType();
    
    node.getExpr().apply(this);
    Register argRegister = synthAttrReg;

    if (symbolTable.containsVar(varName))
    {
      // global var
      
      if (varType.equals("REAL"))
      {
        if (!argRegister.isFloatRegister())
        {
          Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
          printI("mtc1 " + argRegister + ", " + tempFloatRegister);
          printI("cvt.s.w " + tempFloatRegister + ", " + tempFloatRegister);
          argRegister.setIsInUse(false);
          argRegister = tempFloatRegister;
        }
        printI("s.s " + argRegister + ", " + varName);
      }
      else
      {
        printI("sw " + argRegister + ", " + varName);
      }
    }
    else
    {
      // local var

      int offset = var.getOffset();
    
      if (var.isArray())
      {
        node.getBracketedint().apply(this);
        int arrayIndex = synthAttrInt;
        offset += (VAR_SIZE * arrayIndex);
      }
  
      if (varType.equals("REAL"))
      
      {
        if (!argRegister.isFloatRegister())
        {
          Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
          printI("mtc1 " + argRegister + ", " + tempFloatRegister);
          printI("cvt.s.w " + tempFloatRegister + ", " + tempFloatRegister);
          argRegister.setIsInUse(false);
          argRegister = tempFloatRegister;
        }
        printI("s.s " + argRegister + ", " + offset + "($sp)");
      }
      else
      {
        printI("sw " + argRegister + ", " + offset + "($sp)");
      }
    }

    argRegister.setIsInUse(false);
  }

  public void caseAAssignstringStmt(AAssignstringStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    SVariable var = getVar(varName);
    
    node.getString().apply(this);

    String stringLabel = "_stringConstant" + stringCounter;

    Register tempRegister = registerFile.getFirstOpenRegularRegister();
    tempRegister.setIsInUse(true);
    Register tempRegister2 = registerFile.getFirstOpenRegularRegister();

    if (symbolTable.containsVar(varName))
    {
      // global var
      // Transfer String word by word
      for (int i = 0; i < STR_SIZE; i += WORD_SIZE)
      {
        printI("lw " + tempRegister + ", " + stringLabel + "+" + i);
        printI("la " + tempRegister2 + ", " + varName);
        printI("sw " + tempRegister + ", " + i + "(" + tempRegister2 + ")");
      }
    }
    else
    {
      int offset = var.getOffset();
    
      if (var.isArray())
      {
        node.getBracketedint().apply(this);
        int arrayIndex = synthAttrInt;
        offset += (STR_SIZE * arrayIndex);
      }
      
      for (int i = 0; i < STR_SIZE; i += WORD_SIZE)
      {
        printI("lw " + tempRegister + ", " + stringLabel + "+" + i);
        int finalOffset = offset + i;
        printI("sw " + tempRegister + ", " + finalOffset + "($sp)");
      }
    }

    tempRegister.setIsInUse(false);

    stringCounter++;
  }

  public void caseAVardeclStmt(AVardeclStmt node)
  {
    node.getType().apply(this);
    
    node.getId().apply(this);
    String varName = synthAttrStr;
    SVariable var = getVar(varName);
    
    int size;

    if (var.getType().equals("STRING"))
    {
      size = STR_SIZE;
    }
    else
    {
      size = VAR_SIZE;
    }

    if (var.isArray())
    {
      node.getBracketedint().apply(this);
      int arraySize = synthAttrInt;
      size = size * arraySize;
    }
    
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    int methodOffset = currentMethod.getOffset();

    var.setOffset(methodOffset);
    currentMethod.setOffset(size + methodOffset);

    // Set the initial value for the variable

    Register tempRegister = registerFile.getFirstOpenRegularRegister();
    int offset = var.getOffset();
    
    if (var.isArray())
    {
      int varSize;
      if (var.getType().equals("STRING"))
      {
        varSize = STR_SIZE;
      }
      else
      {
        varSize = VAR_SIZE;
      }
      int arraySize = var.getArraySize();
      
      switch (var.getType())
      {
        case "STRING": // For STRING, 0 is the null char '\0'
        case "INT":
        case "BOOLEAN":
          for (int i = 0; i < arraySize; i++)
          {
            printI("li " + tempRegister + ", 0");
            printI("sw " + tempRegister + ", " + (offset + (i * varSize)) + "($sp)");
          }
          break;
        case "REAL":
          for (int i = 0; i < arraySize; i++)
          {
            Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
            printI("li " + tempRegister + ", 0");
            printI("mtc1 " + tempRegister + ", " + tempFloatRegister);
            printI("s.s " + tempFloatRegister + ", " + (offset + (i * varSize)) + "($sp)");
          }
      }
    }
    else
    {
      switch (var.getType())
      {
        case "STRING": // For STRING, 0 is the null char '\0'
        case "INT":
        case "BOOLEAN":
          printI("li " + tempRegister + ", 0");
          printI("sw " + tempRegister + ", " + offset + "($sp)");
          break;
        case "REAL":
          Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
          printI("li " + tempRegister + ", 0");
          printI("mtc1 " + tempRegister + ", " + tempFloatRegister);
          printI("s.s " + tempFloatRegister + ", " + offset + "($sp)");
      }
    }

    // Get additional id's to declare

    List<PAdditionalidlist> starList = new ArrayList<PAdditionalidlist>(node.getAdditionalidlist());
    for (PAdditionalidlist starItem : starList)
    {
      starItem.apply(this);
      varName = synthAttrStr;
      var = getVar(varName);
    
      if (var.getType().equals("STRING"))
      {
        size = STR_SIZE;
      }
      else
      {
        size = VAR_SIZE;
      }

      if (var.isArray())
      {
        node.getBracketedint().apply(this);
        int arraySize = synthAttrInt;
        size = size * arraySize;
      }
      
      currentMethod = symbolTable.getMethod(currentMethodName);
      methodOffset = currentMethod.getOffset();

      var.setOffset(methodOffset);
      currentMethod.setOffset(size + methodOffset);

      // Set the initial value for the variable

      tempRegister = registerFile.getFirstOpenRegularRegister();
      offset = var.getOffset();
      
      if (var.isArray())
      {
        int varSize;
        if (var.getType().equals("STRING"))
        {
          varSize = STR_SIZE;
        }
        else
        {
          varSize = VAR_SIZE;
        }
        int arraySize = var.getArraySize();
        
        switch (var.getType())
        {
          case "STRING": // For STRING, 0 is the null char '\0'
          case "INT":
          case "BOOLEAN":
            for (int i = 0; i < arraySize; i++)
            {
              printI("li " + tempRegister + ", 0");
              printI("sw " + tempRegister + ", " + (offset + (i * varSize)) + "($sp)");
            }
            break;
          case "REAL":
            for (int i = 0; i < arraySize; i++)
            {
              Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
              printI("li " + tempRegister + ", 0");
              printI("mtc1 " + tempRegister + ", " + tempFloatRegister);
              printI("s.s " + tempFloatRegister + ", " + (offset + (i * varSize)) + "($sp)");
            }
        }
      }
      else
      {
        switch (var.getType())
        {
          case "STRING": // For STRING, 0 is the null char '\0'
          case "INT":
          case "BOOLEAN":
            printI("li " + tempRegister + ", 0");
            printI("sw " + tempRegister + ", " + offset + "($sp)");
            break;
          case "REAL":
            Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
            printI("li " + tempRegister + ", 0");
            printI("mtc1 " + tempRegister + ", " + tempFloatRegister);
            printI("s.s " + tempFloatRegister + ", " + offset + "($sp)");
        }
      }
    }
  }

  public void caseAIfStmt(AIfStmt node)
  {
    node.getBoolean().apply(this);
    Register boolRegister = synthAttrReg;

    String endLabel = "_if" + ifCounter + "End";
    ifCounter++;

    printI("bne " + boolRegister + ", 1, " + endLabel);

    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    
    currentMethod.goToInnerScope();
    node.getStmtseq().apply(this);
    currentMethod.goToOuterScope();
    
    printL(endLabel);
    
    boolRegister.setIsInUse(false);
  }

  public void caseAIfelseStmt(AIfelseStmt node)
  {
    node.getBoolean().apply(this);
    Register boolRegister = synthAttrReg;

    String elseLabel = "_if" + ifCounter + "Else";
    String endLabel = "_if" + ifCounter + "End";
    ifCounter++;

    printI("bne " + boolRegister + ", 1, " + elseLabel);

    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    currentMethod.goToInnerScope();
    node.getThenstmtseq().apply(this);
    currentMethod.goToOuterScope();
    
    printI("j " + endLabel);
    
    printL(elseLabel);
    
    currentMethod.goToInnerScope();
    node.getElsestmtseq().apply(this);
    currentMethod.goToOuterScope();
    
    printL(endLabel);

    boolRegister.setIsInUse(false);
  }

  public void caseAWhileStmt(AWhileStmt node)
  {
    String startLabel = "_loop" + loopCounter + "WhileStart";
    String endLabel = "_loop" + loopCounter + "WhileEnd";
    loopCounter++;
    
    printL(startLabel);

    node.getBoolean().apply(this);
    Register boolRegister = synthAttrReg;
    
    printI("bne " + boolRegister + ", 1," + endLabel);

    //System.out.println(currentMethodName);
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    //System.out.println(currentMethod);

    currentMethod.goToInnerScope();
    node.getStmtseq().apply(this);
    currentMethod.goToOuterScope();
    
    printI("j " + startLabel);

    printL(endLabel);
    
    boolRegister.setIsInUse(false);
  }

  public void caseAForStmt(AForStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    node.getExpr().apply(this);
    Register exprRegister = synthAttrReg;
    
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    currentMethod.goToInnerScope();

    // Assignment part of FOR

    SVariable var = getVar(varName);

    if (node.getType() != null)
    {
      // Declaring new variable
      
      node.getType().apply(this);
    
      int methodOffset = currentMethod.getOffset();

      var.setOffset(methodOffset);
      currentMethod.setOffset(VAR_SIZE + methodOffset);

      // Set the value for the variable

      int offset = var.getOffset();
      
      switch (var.getType())
      {
        case "INT":
        case "BOOLEAN":
          printI("sw " + exprRegister + ", " + offset + "($sp)");
          break;
        case "REAL":
          if (!exprRegister.isFloatRegister())
          {
            Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
            printI("mtc1 " + exprRegister + ", " + tempFloatRegister);
            printI("cvt.s.w " + tempFloatRegister + ", " + tempFloatRegister);
            exprRegister.setIsInUse(false);
            exprRegister = tempFloatRegister;
          }
          printI("s.s " + exprRegister + ", " + offset + "($sp)");
      }
    }
    else
    {
      // Not a new var
      
      if (symbolTable.containsVar(varName))
      {
        // using global var
        
        switch (var.getType())
        {
          case "INT":
          case "BOOLEAN":
            printI("sw " + exprRegister + ", " + varName);
            break;
          case "REAL":
            if (!exprRegister.isFloatRegister())
            {
              Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
              printI("mtc1 " + exprRegister + ", " + tempFloatRegister);
              printI("cvt.s.w " + tempFloatRegister + ", " + tempFloatRegister);
              exprRegister.setIsInUse(false);
              exprRegister = tempFloatRegister;
            }
            printI("s.s " + exprRegister + ", " + varName);
        }
      }
      else
      {
        // using local var

        switch (var.getType())
        {
          case "INT":
          case "BOOLEAN":
            printI("sw " + exprRegister + ", " + var.getOffset() + "($sp)");
            break;
          case "REAL":
            if (!exprRegister.isFloatRegister())
            {
              Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
              printI("mtc1 " + exprRegister + ", " + tempFloatRegister);
              printI("cvt.s.w " + tempFloatRegister + ", " + tempFloatRegister);
              exprRegister.setIsInUse(false);
              exprRegister = tempFloatRegister;
            }
            printI("s.s " + exprRegister + ", " + var.getOffset() + "($sp)");
        }
      }
    }

    // Rest of FOR

    exprRegister.setIsInUse(false);
    
    String startLabel = "_loop" + loopCounter + "ForStart";
    String endLabel = "_loop" + loopCounter + "ForEnd";
    loopCounter++;
    
    printL(startLabel);

    node.getBoolean().apply(this);
    Register boolRegister = synthAttrReg;

    printI("bne " + boolRegister + ", 1," + endLabel);

    currentMethod.goToInnerScope();
    node.getStmtseq().apply(this);
    currentMethod.goToOuterScope();

    node.getForincrementer().apply(this);

    printI("j " + startLabel);

    printL(endLabel);
    
    currentMethod.goToOuterScope();
    boolRegister.setIsInUse(false);
  }

  public void caseAGetStmt(AGetStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    SVariable var = getVar(varName);
    String varType = var.getType();
    
    if (varType.equals("STRING"))
    {
      if (symbolTable.containsVar(varName))
      {
        // global var
        printI("la $a0, " + varName);
      }
      else
      {
        // local variable
        if (node.getBracketedint() != null)
        {
          // array access

          node.getBracketedint().apply(this);
          int arrayIndex = synthAttrInt;

          int totalOffset = STR_SIZE * arrayIndex + var.getOffset();
          printI("addi $a0, $sp, " + totalOffset);
        }
        else
        {
          // Not an array access
          printI("addi $a0, $sp, " + var.getOffset());
          
        }
      }
      printI("li $a1, " + STR_SIZE);
    }
  
    int syscallNum;

    if (varType.equals("STRING"))
    {
      syscallNum = 8;
    }
    else if (varType.equals("REAL"))
    {
      syscallNum = 6;
    }
    else
    {
      syscallNum = 5;
    }

    printI("li $v0, " + syscallNum);
    printI("syscall");

    if (!varType.equals("STRING"))
    {
      if (symbolTable.containsVar(varName))
      {
        // global var
        if (varType.equals("REAL"))
        {
          printI("s.s $f0, " + varName);
        }
        else if (varType.equals("BOOLEAN"))
        {
          printI("sne $v0, $zero, $v0");
          printI("sw $v0, " + varName);
        }
        else
        {
          printI("sw $v0, " + varName);
        }
      }
      else
      {
        // local variable
        if (node.getBracketedint() != null)
        {
          // array access

          node.getBracketedint().apply(this);
          int arrayIndex = synthAttrInt;
          int totalOffset = VAR_SIZE * arrayIndex + var.getOffset();

          if (varType.equals("REAL"))
          {
            printI("s.s $f0, " + totalOffset + "($sp)");
          }
          else if (varType.equals("BOOLEAN"))
          {
            printI("sne $v0, $zero, $v0");
            printI("sw $v0, " + totalOffset + "($sp)");
          }  
          else
          {
            printI("sw $v0, " + totalOffset + "($sp)");
          }
        }
        else
        {
          // Not an array access
          if (varType.equals("REAL"))
          {
            printI("s.s $f0, " + var.getOffset() + "($sp)");
          }
          else if (varType.equals("BOOLEAN"))
          {
            printI("sne $v0, $zero, $v0");
            printI("sw $v0, " + var.getOffset() + "($sp)");
          }  
          else
          {
            printI("sw $v0, " + var.getOffset() + "($sp)");
          }
        }
      }
    }
  }

  public void caseAPutStmt(APutStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    SVariable var = getVar(varName);
    String varType = var.getType();
    
    if (symbolTable.containsVar(varName))
    {
      // global var
      if (varType.equals("STRING"))
      {
        printI("la $a0, " + varName);
      }
      else if (varType.equals("REAL"))
      {
        printI("l.s $f12, " + varName);
      }
      else
      {
        printI("lw $a0, " + varName);
      }
    }
    else
    {
      // local variable
      if (node.getBracketedint() != null)
      {
        // array access

        node.getBracketedint().apply(this);
        int arrayIndex = synthAttrInt;

        if (varType.equals("STRING"))
        {
          int totalOffset = STR_SIZE * arrayIndex + var.getOffset();
          printI("addi $a0, $sp, " + totalOffset);
        }
        else if (varType.equals("REAL"))
        {
          int totalOffset = VAR_SIZE * arrayIndex + var.getOffset();
          printI("l.s $f12, " + totalOffset + "($sp)");
        }
        else
        {
          int totalOffset = VAR_SIZE * arrayIndex + var.getOffset();
          printI("lw $a0, " + totalOffset + "($sp)");
        }
      }
      else
      {
        // Not an array access
        if (varType.equals("STRING"))
        {
          printI("addi $a0, $sp, " + var.getOffset());
        }
        else if (varType.equals("REAL"))
        {
          printI("l.s $f12, " + var.getOffset() + "($sp)");
        }
        else
        {
          printI("lw $a0, " + var.getOffset() + "($sp)");
        }
      }
    }

    int syscallNum;

    if (varType.equals("STRING"))
    {
      syscallNum = 4;
    }
    else if (varType.equals("REAL"))
    {
      syscallNum = 2;
    }
    else
    {
      syscallNum = 1;
    }

    printI("li $v0, " + syscallNum);
    printI("syscall");
  }

  private void incrementDecrementHelper(String varName, int numToAdd, int arrayIndex)
  {
    SVariable var = getVar(varName);
    
    Register tempRegister = registerFile.getFirstOpenRegularRegister();

    if (symbolTable.containsVar(varName))
    {
      // global var
      
      printI("lw " + tempRegister + ", " + varName);
      printI("addi " + tempRegister + ", " + tempRegister + ", " + numToAdd);
      printI("sw " + tempRegister + ", " + varName);
    }
    else
    {
      // local var

      int offset = var.getOffset();
    
      if (var.isArray())
      {
        offset += (VAR_SIZE * arrayIndex);
      }
  
      printI("lw " + tempRegister + ", " + offset + "($sp)");
      printI("addi " + tempRegister + ", " + tempRegister + ", " + numToAdd);
      printI("sw " + tempRegister + ", " + offset + "($sp)");
    }
  }

  public void caseAIncrStmt(AIncrStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;

    int arrayIndex = 0;

    if (node.getBracketedint() != null)
    {
      node.getBracketedint().apply(this);
      arrayIndex = synthAttrInt;
    }
    
    incrementDecrementHelper(varName, 1, arrayIndex);
  }

  public void caseADecrStmt(ADecrStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;

    int arrayIndex = 0;

    if (node.getBracketedint() != null)
    {
      node.getBracketedint().apply(this);
      arrayIndex = synthAttrInt;
    }
    
    incrementDecrementHelper(varName, -1, arrayIndex);
  }

  public void caseANewStmt(ANewStmt node)
  {
    // TO DO

    // node.getFirstid().apply(this);
    // if (node.getBracketedint() != null)
    // {
    //   node.getBracketedint().apply(this);
    // }
    // node.getSecondid().apply(this);
  }

  public void caseAMethodcallStmt(AMethodcallStmt node)
  {
    // TO DO

    // node.getId().apply(this);
    // node.getVarlisttwo().apply(this);
  }

  public void caseAMethodcallonsomethingStmt(AMethodcallonsomethingStmt node)
  {
    // TO DO

    // node.getFirstid().apply(this);
    // if (node.getBracketedint() != null)
    // {
    //   node.getBracketedint().apply(this);
    // }
    // node.getSecondid().apply(this);
    // node.getVarlisttwo().apply(this);
  
    // List<PAdditionalmethodcall> starList = new ArrayList<PAdditionalmethodcall>(node.getAdditionalmethodcall());
    // for (PAdditionalmethodcall starItem : starList)
    // {
    //   starItem.apply(this);
    // }
  }

  public void caseAReturnStmt(AReturnStmt node)
  {
    // TO DO

    // node.getExpr().apply(this);
  }

  public void caseASwitchStmt(ASwitchStmt node)
  {
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    node.getExpr().apply(this);
    Register exprRegister = synthAttrReg;
    
    List<PCasestmt> starList = new ArrayList<PCasestmt>(node.getCasestmt());
    
    int numCases = starList.size() + 1;

    List<String> caseLabelList = new ArrayList<String>();
    for (int i = 1; i < numCases; i++)
    {
      caseLabelList.add("_Switch" + switchCounter + "Case" + i);
    }
    List<String> fallthroughLabelList = new ArrayList<String>();
    for (int i = 1; i < numCases; i++)
    {
      fallthroughLabelList.add("_Switch" + switchCounter + "Fallthrough" + i);
    }
    String defaultLabel = "_Switch" + switchCounter + "Default";
    String endLabel = "_Switch" + switchCounter + "End";
    switchCounter++;
    
    int casesVisited = 0;

    node.getInt().apply(this);
    int thisCaseInt = synthAttrInt;

    if (numCases - casesVisited == 1)
    {
      printI("bne " + exprRegister + ", " + thisCaseInt + ", " + defaultLabel);
    }
    else
    {
      printI("bne " + exprRegister + ", " + thisCaseInt + ", " + caseLabelList.get(casesVisited));
    }

    currentMethod.goToInnerScope();
    node.getFirststmtseq().apply(this); // First CASE
    currentMethod.goToOuterScope();

    if (node.getBreakstmt() != null)
    {
      node.getBreakstmt().apply(this);
      printI("j " + endLabel);
    }
    else
    {
      printI("j " + fallthroughLabelList.get(casesVisited));
    }
    
    for (PCasestmt starItem : starList)
    {
      switchCounter--; // So it'll be the same inside the case
      printL(caseLabelList.get(casesVisited));
      casesVisited++;
      inhAttrInt = casesVisited;

      if (numCases - casesVisited == 1)
      {
        inhAttrStr = defaultLabel;
      }
      else
      {
        inhAttrStr = caseLabelList.get(casesVisited);
      }

      inhAttrReg = exprRegister;

      starItem.apply(this); // Additional CASEs
    }
  
    printL(defaultLabel);
    
    currentMethod.goToInnerScope();
    node.getSecondstmtseq().apply(this); // DEFAULT
    currentMethod.goToOuterScope();
    
    printL(endLabel);
  }

  public void caseAAdditionalmethodcall(AAdditionalmethodcall node)
  {
    // TO DO

    // node.getId().apply(this);
    // node.getVarlisttwo().apply(this);
  }

  private void forIncrementDecrementHelper(String varName, int numToAdd)
  {
    SVariable var = getVar(varName);
    
    Register tempRegister = registerFile.getFirstOpenRegularRegister();

    if (symbolTable.containsVar(varName))
    {
      // global var
      
      printI("lw " + tempRegister + ", " + varName);
      printI("addi " + tempRegister + ", " + tempRegister + ", " + numToAdd);
      printI("sw " + tempRegister + ", " + varName);
    }
    else
    {
      // local var

      int offset = var.getOffset();
    
      printI("lw " + tempRegister + ", " + offset + "($sp)");
      printI("addi " + tempRegister + ", " + tempRegister + ", " + numToAdd);
      printI("sw " + tempRegister + ", " + offset + "($sp)");
    }
  }

  public void caseAIncrForincrementer(AIncrForincrementer node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;

    forIncrementDecrementHelper(varName, 1);
  }

  public void caseADecrForincrementer(ADecrForincrementer node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;

    forIncrementDecrementHelper(varName, -1);
  }

  public void caseAAssignForincrementer(AAssignForincrementer node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    SVariable var = getVar(varName);
    String varType = var.getType();
    
    node.getExpr().apply(this);
    Register argRegister = synthAttrReg;

    if (symbolTable.containsVar(varName))
    {
      // global var
      
      if (varType.equals("REAL"))
      {
        if (!argRegister.isFloatRegister())
          {
            Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
            printI("mtc1 " + argRegister + ", " + tempFloatRegister);
            printI("cvt.s.w " + tempFloatRegister + ", " + tempFloatRegister);
            argRegister.setIsInUse(false);
            argRegister = tempFloatRegister;
          }
        printI("s.s " + argRegister + ", " + varName);
      }
      else
      {
        printI("sw " + argRegister + ", " + varName);
      }
    }
    else
    {
      // local var

      int offset = var.getOffset();
  
      if (varType.equals("REAL"))
      {
        if (!argRegister.isFloatRegister())
          {
            Register tempFloatRegister = registerFile.getFirstOpenFloatRegister();
            printI("mtc1 " + argRegister + ", " + tempFloatRegister);
            printI("cvt.s.w " + tempFloatRegister + ", " + tempFloatRegister);
            argRegister.setIsInUse(false);
            argRegister = tempFloatRegister;
          }
        printI("s.s " + argRegister + ", " + offset + "($sp)");
      }
      else
      {
        printI("sw " + argRegister + ", " + offset + "($sp)");
      }
    }

    argRegister.setIsInUse(false);
  }

  public void caseABreakstmt(ABreakstmt node)
  {
    
  }

  public void caseACasestmt(ACasestmt node)
  {
    Register exprRegister = inhAttrReg;
    String nextLabel = inhAttrStr;
    int fallThroughNum = inhAttrInt;

    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    String endLabel = "_Switch" + switchCounter + "End";
    String currFallthroughLabel = "_Switch" + switchCounter + "Fallthrough" + fallThroughNum;
    String nextFallthroughLabel = "_Switch" + switchCounter + "Fallthrough" + (fallThroughNum + 1);
    switchCounter++;

    node.getInt().apply(this);
    int thisCaseInt = synthAttrInt;
    
    printI("bne " + exprRegister + ", " + thisCaseInt + ", " + nextLabel);

    printL(currFallthroughLabel);

    currentMethod.goToInnerScope();
    node.getStmtseq().apply(this);
    currentMethod.goToOuterScope();
    
    if (node.getBreakstmt() != null)
    {
      node.getBreakstmt().apply(this);
      printI("j " + endLabel);
    }
    else
    {
      printI("j " + nextFallthroughLabel);
    }
  }

  public void caseAVarlist(AVarlist node)
  {
    // TO DO

    if (node.getVarlisthelper() != null)
    {
      node.getVarlisthelper().apply(this);
    }
  }

  public void caseAVarlisthelper(AVarlisthelper node)
  {
    // TO DO

    node.getId().apply(this);
    node.getType().apply(this);
    if (node.getBracketedint() != null)
    {
      node.getBracketedint().apply(this);
    }

    List<PAdditionalvarlist> starList = new ArrayList<PAdditionalvarlist>(node.getAdditionalvarlist());
    for (PAdditionalvarlist starItem : starList)
    {
      starItem.apply(this);
    }
  }

  public void caseAAdditionalvarlist(AAdditionalvarlist node)
  {
    // TO DO

    node.getId().apply(this);
    node.getType().apply(this);
    if (node.getBracketedint() != null)
    {
      node.getBracketedint().apply(this);
    }
  }

  public void caseAVarlisttwo(AVarlisttwo node)
  {
    // TO DO

    // if (node.getVarlisttwohelper() != null)
    // {
    //   node.getVarlisttwohelper().apply(this);
    // }
  }

  public void caseAVarlisttwohelper(AVarlisttwohelper node)
  {
    // TO DO

    // node.getExpr().apply(this);
    
    // List<PAdditionalexprlist> starList = new ArrayList<PAdditionalexprlist>(node.getAdditionalexprlist());
    // for (PAdditionalexprlist starItem : starList)
    // {
    //   starItem.apply(this);
    // }
  }

  public void caseAAdditionalexprlist(AAdditionalexprlist node)
  {
    // TO DO
    
    // node.getExpr().apply(this);
  }

  public void caseAOperationExpr(AOperationExpr node)
  {
    node.getExpr().apply(this);
    Register exprRegister = synthAttrReg;
    node.getCond().apply(this);
    String op = synthAttrStr;
    node.getOperand().apply(this);
    Register operandRegister = synthAttrReg;

    Register destRegister = registerFile.getFirstOpenRegularRegister();

    if (exprRegister.isFloatRegister())
    {
      //destRegister = registerFile.getFirstOpenFloatRegister();

      switch (op)
      {
        case "==":
          printI("li " + destRegister + ", " + 1);
          printI("c.eq.s " + exprRegister + ", " + operandRegister);
          printI("movt " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case "!=":
          printI("li " + destRegister + ", " + 1);
          printI("c.eq.s " + exprRegister + ", " + operandRegister);
          printI("movf " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case ">=":
          printI("li " + destRegister + ", " + 1);
          printI("c.lt.s " + exprRegister + ", " + operandRegister);
          printI("movf " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case "<=":
          printI("li " + destRegister + ", " + 1);
          printI("c.le.s " + exprRegister + ", " + operandRegister);
          printI("movt " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case ">":
          printI("li " + destRegister + ", " + 1);
          printI("c.le.s " + exprRegister + ", " + operandRegister);
          printI("movf " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case "<":
          printI("li " + destRegister + ", " + 1);
          printI("c.lt.s " + exprRegister + ", " + operandRegister);
          printI("movt " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
      }
    }
    else
    {
      //destRegister = registerFile.getFirstOpenRegularRegister();

      switch (op)
      {
        case "==":
          printI("seq " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case "!=":
          printI("sne " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case ">=":
          printI("sge " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case "<=":
          printI("sle " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case ">":
          printI("sgt " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case "<":
          printI("slt " + destRegister + ", " + exprRegister + ", " + operandRegister);
      }
    }

    exprRegister.setIsInUse(false);
    operandRegister.setIsInUse(false);
    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
  }

  public void caseAOperandExpr(AOperandExpr node)
  {
    node.getOperand().apply(this);
    // synthAttrReg = synthAttrReg;
  }

  public void caseAOperationOperand(AOperationOperand node)
  {
    node.getOperand().apply(this);
    Register operandRegister = synthAttrReg;
    node.getAddop().apply(this);
    String op = synthAttrStr;
    node.getTerm().apply(this);
    Register termRegister = synthAttrReg;

    Register destRegister;

    if (operandRegister.isFloatRegister())
    {
      destRegister = registerFile.getFirstOpenFloatRegister();
      
      switch (op)
      {
        case "+":
          printI("add.s " + destRegister + ", " + operandRegister + ", " + termRegister);
          break;
        case "-":
          printI("sub.s " + destRegister + ", " + operandRegister + ", " + termRegister);
      }    
    }
    else
    {
      destRegister = registerFile.getFirstOpenRegularRegister();

      switch (op)
      {
        case "+":
          printI("add " + destRegister + ", " + operandRegister + ", " + termRegister);
          break;
        case "-":
          printI("sub " + destRegister + ", " + operandRegister + ", " + termRegister);
      }
    }

    operandRegister.setIsInUse(false);
    termRegister.setIsInUse(false);
    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
  }

  public void caseATermOperand(ATermOperand node)
  {
    node.getTerm().apply(this);
    // synthAttrReg = synthAttrReg;
  }

  public void caseAOperationTerm(AOperationTerm node)
  {
    node.getTerm().apply(this);
    Register termRegister = synthAttrReg;
    node.getMultop().apply(this);
    String op = synthAttrStr;
    node.getFactor().apply(this);
    Register factorRegister = synthAttrReg;

    Register destRegister;

    if (termRegister.isFloatRegister())
    {
      destRegister = registerFile.getFirstOpenFloatRegister();

      switch (op)
      {
        case "*":
          printI("mul.s " + destRegister + ", " + termRegister + ", " + factorRegister);
          break;
        case "/":
          printI("div.s " + destRegister + ", " + termRegister + ", " + factorRegister);
      }
    }
    else
    {
      destRegister = registerFile.getFirstOpenRegularRegister();

      switch (op)
      {
        case "*":
          printI("mul " + destRegister + ", " + termRegister + ", " + factorRegister);
          break;
        case "/":
          printI("div " + destRegister + ", " + termRegister + ", " + factorRegister);
      }
    }

    termRegister.setIsInUse(false);
    factorRegister.setIsInUse(false);
    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
  }

  public void caseAFactorTerm(AFactorTerm node)
  {
    node.getFactor().apply(this);
    // synthAttrReg = synthAttrReg;
  }

  public void caseAParenFactor(AParenFactor node)
  {
    node.getExpr().apply(this);
    // synthAttrReg = synthAttrReg;
  }

  public void caseANegateFactor(ANegateFactor node)
  {
    node.getFactor().apply(this);
    Register argRegister = synthAttrReg;
    Register destRegister;

    if (argRegister.isFloatRegister())
    {
      destRegister = registerFile.getFirstOpenFloatRegister();
      printI("neg.s " + destRegister + ", " + argRegister);
    }
    else
    {
      destRegister = registerFile.getFirstOpenRegularRegister();
      printI("neg " +  destRegister + ", " + argRegister);
    }

    destRegister.setIsInUse(true);
    argRegister.setIsInUse(false);
    synthAttrReg = destRegister;
  }

  public void caseAIntFactor(AIntFactor node)
  {
    node.getInt().apply(this);
    int num = synthAttrInt;
    
    Register destRegister = registerFile.getFirstOpenRegularRegister();
    printI("li " + destRegister + ", " + num);
    
    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
  }

  public void caseARealFactor(ARealFactor node)
  {
    node.getReal().apply(this);
    float num = synthAttrReal;
    String numHex = convertFloatToHexString(num);
    
    Register tempRegister = registerFile.getFirstOpenRegularRegister();
    Register destRegister = registerFile.getFirstOpenFloatRegister();
    
    printI("li " + tempRegister + ", " + numHex);
    printI("mtc1 " + tempRegister + ", " + destRegister);

    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
  }

  public void caseABooleanFactor(ABooleanFactor node)
  {
    node.getBooleanliteral().apply(this);
    int boolNum = synthAttrInt;

    Register destRegister = registerFile.getFirstOpenRegularRegister();
    printI("li " + destRegister + ", " + boolNum);
    
    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
    // synthAttrReg = synthAttrReg;
  }

  // Load the value of the id
  // If a string, load the address
  public void caseAIdFactor(AIdFactor node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    SVariable var = getVar(varName);
    Register destRegister;

    if (var.getType().equals("REAL"))
    {
      destRegister = registerFile.getFirstOpenFloatRegister();
    }
    else
    {
      destRegister = registerFile.getFirstOpenRegularRegister();
    }

    if (symbolTable.containsVar(varName))
    {
      // global var
      if (var.getType().equals("STRING"))
      {
        printI("la " + destRegister + ", " + varName);
      }
      else if (var.getType().equals("REAL"))
      {
        printI("l.s " + destRegister + ", " + varName);
      }
      else
      {
        printI("lw " + destRegister + ", " + varName);
      }
    }
    else
    {
      // local variable
      if (node.getBracketedint() != null)
      {
        // array access

        node.getBracketedint().apply(this);
        int arrayIndex = synthAttrInt;

        if (var.getType().equals("STRING"))
        {
          int totalOffset = STR_SIZE * arrayIndex + var.getOffset();
          printI("addi " + destRegister + ", $sp, " + totalOffset);
        }
        else if (var.getType().equals("REAL"))
        {
          int totalOffset = VAR_SIZE * arrayIndex + var.getOffset();
          printI("l.s " + destRegister + ", " + totalOffset + "($sp)");
        }
        else
        {
          int totalOffset = VAR_SIZE * arrayIndex + var.getOffset();
          printI("lw " + destRegister + ", " + totalOffset + "($sp)");
        }
      }
      else
      {
        // Not an array access
        if (var.getType().equals("STRING"))
        {
          printI("addi " + destRegister + ", $sp, " + var.getOffset());
        }
        else if (var.getType().equals("REAL"))
        {
          printI("l.s " + destRegister + ", " + var.getOffset() + "($sp)");
        }
        else
        {
          printI("lw " + destRegister + ", " + var.getOffset() + "($sp)");
        }
      }
    }

    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
  }

  public void caseAMethodcallFactor(AMethodcallFactor node)
  {
    // TO DO

    //node.getId().apply(this);
    //node.getVarlisttwo().apply(this);
  }

  public void caseAMethodcallonsomethingFactor(AMethodcallonsomethingFactor node)
  {
    // TO DO

    // node.getFirstid().apply(this);
    // if (node.getBracketedint() != null)
    // {
    //   node.getBracketedint().apply(this);
    // }
    // node.getSecondid().apply(this);
    // node.getVarlisttwo().apply(this);
  }

  public void caseALiteralBoolean(ALiteralBoolean node)
  {
    node.getBooleanliteral().apply(this);
    int boolNum = synthAttrInt;

    Register destRegister = registerFile.getFirstOpenRegularRegister();
    printI("li " + destRegister + ", " + boolNum);
    
    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
  }

  public void caseACondBoolean(ACondBoolean node)
  {
    node.getExpr().apply(this);
    Register exprRegister = synthAttrReg;
    node.getCond().apply(this);
    String op = synthAttrStr;
    node.getOperand().apply(this);
    Register operandRegister = synthAttrReg;

    Register destRegister;

    if (exprRegister.isFloatRegister())
    {
      destRegister = registerFile.getFirstOpenFloatRegister();

      switch (op)
      {
        case "==":
          printI("li " + destRegister + ", " + 1);
          printI("c.eq.s " + exprRegister + ", " + operandRegister);
          printI("movt " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case "!=":
          printI("li " + destRegister + ", " + 1);
          printI("c.eq.s " + exprRegister + ", " + operandRegister);
          printI("movf " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case ">=":
          printI("li " + destRegister + ", " + 1);
          printI("c.lt.s " + exprRegister + ", " + operandRegister);
          printI("movf " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case "<=":
          printI("li " + destRegister + ", " + 1);
          printI("c.le.s " + exprRegister + ", " + operandRegister);
          printI("movt " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case ">":
          printI("li " + destRegister + ", " + 1);
          printI("c.le.s " + exprRegister + ", " + operandRegister);
          printI("movf " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
          break;
        case "<":
          printI("li " + destRegister + ", " + 1);
          printI("c.lt.s " + exprRegister + ", " + operandRegister);
          printI("movt " + destRegister + ", $zero");
          printI("seq " + destRegister + ", " + destRegister + ", $zero");
      }
    }
    else
    {
      destRegister = registerFile.getFirstOpenRegularRegister();

      switch (op)
      {
        case "==":
          printI("seq " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case "!=":
          printI("sne " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case ">=":
          printI("sge " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case "<=":
          printI("sle " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case ">":
          printI("sgt " + destRegister + ", " + exprRegister + ", " + operandRegister);
          break;
        case "<":
          printI("slt " + destRegister + ", " + exprRegister + ", " + operandRegister);
      }
    }

    exprRegister.setIsInUse(false);
    operandRegister.setIsInUse(false);
    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
  }

  public void caseAIdBoolean(AIdBoolean node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    SVariable var = getVar(varName);
    Register destRegister = registerFile.getFirstOpenRegularRegister();

    if (symbolTable.containsVar(varName))
    {
      // global var
      printI("lw " + destRegister + ", " + varName);
    }
    else
    {
      // local variable
      printI("lw " + destRegister + ", " + var.getOffset() + "($sp)");
    }

    destRegister.setIsInUse(true);
    synthAttrReg = destRegister;
  }

  public void caseATrueBooleanliteral(ATrueBooleanliteral node)
  {
    synthAttrInt = 1;
  }

  public void caseAFalseBooleanliteral(AFalseBooleanliteral node)
  {
    synthAttrInt = 0;
  }

  public void caseAEqualsCond(AEqualsCond node)
  {
    synthAttrStr = "==";
  }

  public void caseANotequalsCond(ANotequalsCond node)
  {
    synthAttrStr = "!=";
  }

  public void caseAGreaterorequalsCond(AGreaterorequalsCond node)
  {
    synthAttrStr = ">=";
  }

  public void caseALessorequalsCond(ALessorequalsCond node)
  {
    synthAttrStr = "<=";
  }

  public void caseAGreaterthanCond(AGreaterthanCond node)
  {
    synthAttrStr = ">";
  }

  public void caseALessthanCond(ALessthanCond node)
  {
    synthAttrStr = "<";
  }

  public void caseAPlusAddop(APlusAddop node)
  {
    synthAttrStr = "+";
  }

  public void caseAMinusAddop(AMinusAddop node)
  {
    synthAttrStr = "-";
  }

  public void caseATimesMultop(ATimesMultop node)
  {
    synthAttrStr = "*";
  }

  public void caseADivideMultop(ADivideMultop node)
  {
    synthAttrStr = "/";
  }

  public void caseAIntType(AIntType node)
  {
    synthAttrStr = "INT";
  }

  public void caseARealType(ARealType node)
  {
    synthAttrStr = "REAL";
  }

  public void caseAStringType(AStringType node)
  {
    synthAttrStr = "STRING";
  }

  public void caseABooleanType(ABooleanType node)
  {
    synthAttrStr = "BOOLEAN";
  }

  public void caseAVoidType(AVoidType node)
  {
    synthAttrStr = "VOID";
  }

  public void caseAIdType(AIdType node)
  {
    node.getId().apply(this);
    // synthAttrStr = synthAttrStr;
  }

  public void caseAAdditionalidlist(AAdditionalidlist node)
  {
    node.getId().apply(this);
    // synthAttrStr = synthAttrStr;
  }

  public void caseABracketedint(ABracketedint node)
  {
    node.getInt().apply(this);
    // synthAttrInt = synthAttrInt;
  }
}
