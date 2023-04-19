package FinalProject;

import FinalProject.analysis.*;
import FinalProject.node.*;
import java.util.*;

class CheckTreeSemantics extends DepthFirstAdapter
{
	private SymbolTable symbolTable;
  private ArrayList<String> stringConstantList;

  // Accessible by other classes to check for semantic errors
  public boolean hadSemanticError = false;

  //private String currentClassName = "";
  private String currentMethodName = "";

  private int numDuplicateMethods = 0;

  // Attributes for inheriting and synthesizing
  //private int inhAttrInt;
  private int synthAttrInt;
  //private float inhAttrReal;
  //private float synthAttrReal;
  //private String inhAttrStr;
  private String synthAttrStr;
  private SMethod inhAttrMethod;
  //private SMethod synthAttrMethod;

 	public CheckTreeSemantics(SymbolTable inSymbolTable, ArrayList<String> inStringConstantList)
  {
		symbolTable = inSymbolTable;
    stringConstantList = inStringConstantList;
	}
  
  /////////////////////////////////////////////////////////////////////////////////////
  
  // Helper methods (used in many places)

  private void printErrorMessage(Exception e)
  {
    System.out.println(e.getMessage());
    hadSemanticError = true;
  }

  /*
  private void checkForVar(SVariable var, String varName) 
  {
    if (var == null)
    {
      // Variable was not in method, so check above
      try
      {
        // Check global variables
        var = symbolTable.getVar(varName);
      }
      catch (Exception e)
      {
        // If it's not here (or anywhere previously), then it's not anywhere
        printErrorMessage(e);
      }
    }
  } 
  */

  private void checkForArrayErrors(SVariable var, PBracketedint bracketedInt)
  {
    if (bracketedInt != null && var != null)
    {
      try
      {
        if (!var.isArray())
        {
          throw new Exception("ERROR: variable \"" + var +
            "\" is not an array but is being accessed as an array");
        }

        bracketedInt.apply(this);
        int arrayAccessLocation = synthAttrInt;

        if (arrayAccessLocation >= var.getArraySize())
        {
          throw new Exception("ERROR: Array index out of bounds: accessing slot " +
            arrayAccessLocation + " of variable \"" + var +
            "\" with size " + var.getArraySize());
        }
      }
      catch (Exception e)
      {
        printErrorMessage(e);
      }
    }
  }

  private void compareTypes(String lhsType, String rhsType, String varName) throws Exception
  {
    switch (lhsType) {
      case "INT":
        if (!(lhsType.equals(rhsType) || rhsType.equals("BOOLEAN")))
        {
          throw new Exception("ERROR: Trying to assign a " + rhsType +
           " value to \"" + varName + "\", which is a " + lhsType + ".");
        }
        break;
      case "REAL":
        if (!(lhsType.equals(rhsType) || rhsType.equals("INT") || rhsType.equals("BOOLEAN")))
        {
          throw new Exception("ERROR: Trying to assign a " + rhsType +
          " value to \"" + varName + "\", which is a " + lhsType + ".");
        }
        break;
      case "STRING":
      case "BOOLEAN":
      default:
        if (!lhsType.equals(rhsType)) 
        {
          throw new Exception("ERROR: Trying to assign a " + rhsType +
          " value to \"" + varName + "\", which is a " + lhsType + ".");
        }
    }
  }

	/////////////////////////////////////////////////////////////////////////////////////

	// Token cases

	public void caseTString(TString node)
  {
    //synthAttrStr = node.getText();
    stringConstantList.add(node.getText());
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
    //synthAttrReal = Float.parseFloat(node.getText());
  }

	/////////////////////////////////////////////////////////////////////////////////////

	// Production cases
	
  public void caseAProg(AProg node)
  {
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
    String methodType = synthAttrStr;
    node.getId().apply(this);
    String methodName = synthAttrStr;

    SMethod method = new SMethod(methodName, methodType);

    try 
    {
      symbolTable.addMethod(method);
    } 
    catch (Exception e)
    {
      // Add the method under an alternate name and continue running
      
      printErrorMessage(e);
      String duplicateMethodName = "duplicate" + numDuplicateMethods + "$" + methodName;
      numDuplicateMethods++;
      method = new SMethod(duplicateMethodName, methodType);
    }

    inhAttrMethod = method;
    node.getVarlist().apply(this);

    currentMethodName = methodName;
    node.getStmtseq().apply(this);
    method.finishMethod();
    currentMethodName = "";
  }

  public void caseAFielddeclClassmethodstmt(AFielddeclClassmethodstmt node)
  {
    node.getType().apply(this);
    String fieldType = synthAttrStr;
    
    node.getId().apply(this);
    String fieldName = synthAttrStr;

    SVariable var = new SVariable(fieldName, fieldType);
    try
    {
      symbolTable.addVar(var);
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }

    List<PAdditionalidlist> starList = new ArrayList<PAdditionalidlist>(node.getAdditionalidlist());
    for(PAdditionalidlist starItem : starList)
    {
      starItem.apply(this);
      fieldName = synthAttrStr;

      var = new SVariable(fieldName, fieldType);
      try
      {
        symbolTable.addVar(var);
      }
      catch (Exception e)
      {
        // Don't add the variable, and continue running
        printErrorMessage(e);
      }
    }
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

    // node.getType().apply(this);
    // node.getId().apply(this);
    // node.getVarlist().apply(this);
    // node.getStmtseq().apply(this);
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
    node.getExpr().apply(this);
    String exprType = synthAttrStr;

    try
    {
      if (exprType.startsWith("$ERROR"))
      {
        // exprType will contain the error message that should be returned
        throw new Exception(exprType.substring(1));
      }
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
    
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    SVariable var = currentMethod.getVar(varName);

    try
    {
      if (var == null)
      {
        var = symbolTable.getVar(varName);
      }
      PBracketedint bracketedInt = node.getBracketedint();
      checkForArrayErrors(var, bracketedInt);

      String varType = var.getType();
      if (!exprType.startsWith("$ERROR"))
      {
        if (var.isArray())
        {
          varName += "[]";
        }

        compareTypes(varType, exprType, varName);
      }      
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
  }

  public void caseAAssignstringStmt(AAssignstringStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;

    node.getString().apply(this);
    
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    SVariable var = currentMethod.getVar(varName);

    try
    {
      if (var == null)
      {
        var = symbolTable.getVar(varName);
      }
      PBracketedint bracketedInt = node.getBracketedint();
      checkForArrayErrors(var, bracketedInt);

      String varType = var.getType();

      if (var.isArray())
      {
        varName += "[]";
      }

      compareTypes(varType, "STRING", varName);
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
  }

  public void caseAVardeclStmt(AVardeclStmt node)
  {
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    node.getType().apply(this);
    String varType = synthAttrStr;
    
    node.getId().apply(this);
    String varName = synthAttrStr;

    SVariable var;

    if (node.getBracketedint() != null)
    {
      node.getBracketedint().apply(this);
      int arraySize = synthAttrInt;
      var = new SVariable(varName, varType, arraySize);
    }
    else 
    {
      var = new SVariable(varName, varType);
    }

    try
    {
      currentMethod.addVar(var);
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }

    List<PAdditionalidlist> starList = new ArrayList<PAdditionalidlist>(node.getAdditionalidlist());
    for(PAdditionalidlist starItem : starList)
    {
      starItem.apply(this);
      varName = synthAttrStr;

      if (node.getBracketedint() != null)
      {
        node.getBracketedint().apply(this);
        int arraySize = synthAttrInt;
        var = new SVariable(varName, varType, arraySize);
      }
      else 
      {
        var = new SVariable(varName, varType);
      }

      try
      {
        currentMethod.addVar(var);
      }
      catch (Exception e)
      {
        printErrorMessage(e);
      }
    }
  }

  public void caseAIfStmt(AIfStmt node)
  {
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    node.getBoolean().apply(this);
    
    currentMethod.addAndGoToInnerScope();
    node.getStmtseq().apply(this);
    currentMethod.goToOuterScope();
  }

  public void caseAIfelseStmt(AIfelseStmt node)
  {
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    node.getBoolean().apply(this);

    currentMethod.addAndGoToInnerScope();
    node.getThenstmtseq().apply(this);
    currentMethod.goToOuterScope();

    currentMethod.addAndGoToInnerScope();
    node.getElsestmtseq().apply(this);
    currentMethod.goToOuterScope();
  }

  public void caseAWhileStmt(AWhileStmt node)
  {
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    node.getBoolean().apply(this);

    currentMethod.addAndGoToInnerScope();
    node.getStmtseq().apply(this);
    currentMethod.goToOuterScope();
  }

  public void caseAForStmt(AForStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    node.getExpr().apply(this);
    String exprType = synthAttrStr;

    try
    {
      if (exprType.startsWith("$ERROR"))
      {
        // exprType will contain the error message that should be thrown
        throw new Exception(exprType.substring(1));
      }
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }

    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    currentMethod.addAndGoToInnerScope();

    if (node.getType() != null)
    {
      // Declaring new variable
      // need to add the var to the current method

      node.getType().apply(this);
      String newVarType = synthAttrStr;  

      try 
      {
        if (!exprType.startsWith("$ERROR"))
        {
          compareTypes(newVarType, exprType, varName);
          SVariable var = new SVariable(varName, newVarType);
          currentMethod.addVar(var);
        }
      }
      catch (Exception e)
      {
        printErrorMessage(e);
      }
    }
    else
    {
      // Using existing variable
      
      SVariable var = currentMethod.getVar(varName);
      try 
      {
        if (var == null) 
        {
          var = symbolTable.getVar(varName);
        }
        
        // var exists, check types
        String varType = var.getType();
        
        if (!exprType.startsWith("$ERROR"))
        {
          compareTypes(varType, exprType, varName);
        }
      }
      catch (Exception e) 
      {
        printErrorMessage(e);
      }
    }

    node.getBoolean().apply(this);

    currentMethod.addAndGoToInnerScope();
    node.getStmtseq().apply(this);
    currentMethod.goToOuterScope();

    node.getForincrementer().apply(this);

    currentMethod.goToOuterScope();
  }

  public void caseAGetStmt(AGetStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    SVariable var = currentMethod.getVar(varName);

    try
    {
      if (var == null)
      {
        var = symbolTable.getVar(varName);
      }
      
      PBracketedint bracketedInt = node.getBracketedint();
      checkForArrayErrors(var, bracketedInt);

      //String varType = var.getType();
      //compareTypes(varType, "STRING", varName);
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
  }
  
  public void caseAPutStmt(APutStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    SVariable var = currentMethod.getVar(varName);

    try
    {
      if (var == null)
      {
        var = symbolTable.getVar(varName);
      }
      
      PBracketedint bracketedInt = node.getBracketedint();
      checkForArrayErrors(var, bracketedInt);

      //String varType = var.getType();
      //compareTypes(varType, "STRING", varName);
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
  }

  public void caseAIncrStmt(AIncrStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;

    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    SVariable var = currentMethod.getVar(varName);

    try
    {
      if (var == null)
      {
        var = symbolTable.getVar(varName);
      }
      
      PBracketedint bracketedInt = node.getBracketedint();
      checkForArrayErrors(var, bracketedInt);

      if (var.isArray())
      {
        varName += "[]";
      }

      String varType = var.getType();
      compareTypes(varType, "INT", varName);
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
  }

  public void caseADecrStmt(ADecrStmt node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;

    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    SVariable var = currentMethod.getVar(varName);

    try
    {
      if (var == null)
      {
        var = symbolTable.getVar(varName);
      }
      
      PBracketedint bracketedInt = node.getBracketedint();
      checkForArrayErrors(var, bracketedInt);

      if (var.isArray())
      {
        varName += "[]";
      }

      String varType = var.getType();
      compareTypes(varType, "INT", varName);
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
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
    try
    {
      node.getExpr().apply(this);
      String exprType = synthAttrStr;

      if (exprType.startsWith("$ERROR"))
      {
        // exprType will contain the error message that should be returned
        throw new Exception(exprType.substring(1));
      }
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
    
    List<PCasestmt> starList = new ArrayList<PCasestmt>(node.getCasestmt());
    //node.getInt().apply(this);

    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    currentMethod.addAndGoToInnerScope();
    node.getFirststmtseq().apply(this);
    currentMethod.goToOuterScope();

    // if (node.getBreakstmt() != null)
    // {
    //   node.getBreakstmt().apply(this);
    // }

    
    for (PCasestmt starItem : starList)
    {
      starItem.apply(this);
    }
  
    currentMethod.addAndGoToInnerScope();
    node.getSecondstmtseq().apply(this);
    currentMethod.goToOuterScope();
  }

  public void caseAAdditionalmethodcall(AAdditionalmethodcall node)
  {
    // TO DO

    // node.getId().apply(this);
    // node.getVarlisttwo().apply(this);
  }

  private void incrementAndDecrementHelper(String varName)
  {
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);

    SVariable var = currentMethod.getVar(varName);

    try
    {
      if (var == null)
      {
        var = symbolTable.getVar(varName);
      }

      String varType = var.getType();
      compareTypes(varType, "INT", varName);
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
  }
  
  public void caseAIncrForincrementer(AIncrForincrementer node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    
    incrementAndDecrementHelper(varName);
  }

  public void caseADecrForincrementer(ADecrForincrementer node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    
    incrementAndDecrementHelper(varName);
  }

  public void caseAAssignForincrementer(AAssignForincrementer node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    node.getExpr().apply(this);
    String exprType = synthAttrStr;

    try
    {
      if (exprType.startsWith("$ERROR"))
      {
        // exprType will contain the error message that should be returned
        throw new Exception(exprType.substring(1));
      }
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
    
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    SVariable var = currentMethod.getVar(varName);

    try
    {
      if (var == null)
      {
        var = symbolTable.getVar(varName);
      }
      
      String varType = var.getType();
      if (!exprType.startsWith("$ERROR"))
      {
        compareTypes(varType, exprType, varName);
      }      
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
  }

  public void caseABreakstmt(ABreakstmt node)
  {
    
  }

  public void caseACasestmt(ACasestmt node)
  {
    //node.getInt().apply(this);
    
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    
    currentMethod.addAndGoToInnerScope();
    node.getStmtseq().apply(this);
    currentMethod.goToOuterScope();

    // if (node.getBreakstmt() != null)
    // {
    //   node.getBreakstmt().apply(this);
    // }
  }

  public void caseAVarlist(AVarlist node)
  {
    if (node.getVarlisthelper() != null)
    {
      // inhAttrMethod = inhAttrMethod;
      node.getVarlisthelper().apply(this);
    }
  }

  public void caseAVarlisthelper(AVarlisthelper node)
  {
    SMethod method = inhAttrMethod;

    node.getId().apply(this);
    String paramName = synthAttrStr;
    node.getType().apply(this);
    String paramType = synthAttrStr;
    
    SVariable var;

    if (node.getBracketedint() != null)
    {
      node.getBracketedint().apply(this);
      int arraySize = synthAttrInt;
      var = new SVariable(paramName, paramType, arraySize);
    }
    else
    {
      var = new SVariable(paramName, paramType);
    }

    try 
    {
      method.addParam(var);
    }
    catch (Exception e) 
    {
      // Don't add the parameter, and continue running
      printErrorMessage(e);
    }
    
    List<PAdditionalvarlist> starList = new ArrayList<PAdditionalvarlist>(node.getAdditionalvarlist());
    for (PAdditionalvarlist starItem : starList)
    {
      // inhAttrMethod = inhAttrMethod;
      starItem.apply(this);
    }
  }

  public void caseAAdditionalvarlist(AAdditionalvarlist node)
  {
    SMethod method = inhAttrMethod;

    node.getId().apply(this);
    String paramName = synthAttrStr;
    node.getType().apply(this);
    String paramType = synthAttrStr;
    
    SVariable var;

    if (node.getBracketedint() != null)
    {
      node.getBracketedint().apply(this);
      int arraySize = synthAttrInt;
      var = new SVariable(paramName, paramType, arraySize);
    }
    else
    {
      var = new SVariable(paramName, paramType);
    }

    try 
    {
      method.addParam(var);
    }
    catch (Exception e)
    {
      // Don't add the parameter, and continue running
      printErrorMessage(e);
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
    String exprType = synthAttrStr;

    //node.getCond().apply(this);

    node.getOperand().apply(this);
    String operandType = synthAttrStr;
    
    if (exprType.startsWith("$ERROR"))
    {
      // Send the error upwards
      synthAttrStr = exprType;
    }
    else if (operandType.startsWith("$ERROR"))
    {
      // Send the error upwards
      synthAttrStr = operandType;
    }
    else if (exprType.equals(operandType))
    {
      // The type of the total expression will be BOOLEAN
      synthAttrStr = "BOOLEAN";
    }
    else // mismatch here
    {
      synthAttrStr = "$ERROR: Type mismatch: " + exprType + " does not match with " + operandType;
    }
  }

  public void caseAOperandExpr(AOperandExpr node)
  {
    node.getOperand().apply(this);
    // Pass the type upwards
    // synthAttrStr = synthAttrStr
  }

  public void caseAOperationOperand(AOperationOperand node)
  {
    node.getOperand().apply(this);
    String operandType = synthAttrStr;

    //node.getAddop().apply(this);

    node.getTerm().apply(this);
    String termType = synthAttrStr;
    
    if (operandType.startsWith("$ERROR"))
    {
      // Send the error upwards
      synthAttrStr = operandType;
    }
    else if (termType.startsWith("$ERROR"))
    {
      // Send the error upwards
      synthAttrStr = termType;
    }
    else if (operandType.equals(termType))
    {
      // Send this type up as the type of the total expression
      synthAttrStr = operandType;
    }
    else // mismatch here
    {
      synthAttrStr = "$ERROR: Type mismatch: " + operandType + " does not match with " + termType;
    }
  }

  public void caseATermOperand(ATermOperand node)
  {
    node.getTerm().apply(this);
    // Pass the type upwards
    // synthAttrStr = synthAttrStr
  }

  public void caseAOperationTerm(AOperationTerm node)
  {
    node.getTerm().apply(this);
    String termType = synthAttrStr;

    //node.getMultop().apply(this);

    node.getFactor().apply(this);
    String factorType = synthAttrStr;
    
    if (termType.startsWith("$ERROR"))
    {
      // Send the error upwards
      synthAttrStr = termType;
    }
    else if (factorType.startsWith("$ERROR"))
    {
      // Send the error upwards
      synthAttrStr = factorType;
    }
    else if (termType.equals(factorType))
    {
      // Send this type up as the type of the total expression
      synthAttrStr = termType;
    }
    else // mismatch here
    {
      synthAttrStr = "$ERROR: Type mismatch: " + termType + " does not match with " + factorType;
    }
  }

  public void caseAFactorTerm(AFactorTerm node)
  {
    node.getFactor().apply(this);
    // Pass the type upwards
    // synthAttrStr = synthAttrStr
  }

  public void caseAParenFactor(AParenFactor node)
  {
    node.getExpr().apply(this);
    // Pass the type upwards
    // synthAttrStr = synthAttrStr
  }

  public void caseANegateFactor(ANegateFactor node)
  {
    node.getFactor().apply(this);
    // Pass the type upwards
    // synthAttrStr = synthAttrStr
  }

  public void caseAIntFactor(AIntFactor node)
  {
    //node.getInt().apply(this);
    synthAttrStr = "INT";
  }

  public void caseARealFactor(ARealFactor node)
  {
    //node.getReal().apply(this);
    synthAttrStr = "REAL";
  }

  public void caseABooleanFactor(ABooleanFactor node)
  {
    //node.getBooleanliteral().apply(this);
    synthAttrStr = "BOOLEAN";
  }

  public void caseAIdFactor(AIdFactor node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;
    
    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    SVariable var = currentMethod.getVar(varName);

    try
    {
      if (var == null)
      {
        var = symbolTable.getVar(varName);
      }
      
      PBracketedint bracketedInt = node.getBracketedint();
      checkForArrayErrors(var, bracketedInt);
      
      String varType = var.getType();
      synthAttrStr = varType;
    }
    catch (Exception e)
    {
      synthAttrStr = "$" + e.getMessage();
    }
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
    //node.getBooleanliteral().apply(this);
  }

  public void caseACondBoolean(ACondBoolean node)
  {
    try
    {
      node.getExpr().apply(this);
      String exprType = synthAttrStr;
  
      //node.getCond().apply(this);
  
      node.getOperand().apply(this);
      String operandType = synthAttrStr;

      if (exprType.startsWith("$ERROR"))
      {
        throw new Exception(exprType.substring(1));
      }
      else if (operandType.startsWith("$ERROR"))
      {
        throw new Exception(operandType.substring(1));
      }
      else if (!exprType.equals(operandType))
      {
        throw new Exception("ERROR: Type mismatch: " + exprType + " does not match with " + operandType);
      }
    }
    catch (Exception e)
    {
      printErrorMessage(e);
    }
  }

  public void caseAIdBoolean(AIdBoolean node)
  {
    node.getId().apply(this);
    String varName = synthAttrStr;

    SMethod currentMethod = symbolTable.getMethod(currentMethodName);
    SVariable var = currentMethod.getVar(varName);

    // Variable was not in method, so check above
      try
      {
        // Check global variables
        if (var == null)
        {
          var = symbolTable.getVar(varName);
        }
        
        if (!var.getType().equals("BOOLEAN"))
        {
          throw new Exception("ERROR: \"" + varName + "\" is not of type BOOLEAN");
        }
      }
      catch (Exception e)
      {
        // If it's not here (or anywhere previously), then it's not anywhere
        printErrorMessage(e);
      }
  }

  public void caseATrueBooleanliteral(ATrueBooleanliteral node)
  {
    
  }

  public void caseAFalseBooleanliteral(AFalseBooleanliteral node)
  {
    
  }

  public void caseAEqualsCond(AEqualsCond node)
  {

  }

  public void caseANotequalsCond(ANotequalsCond node)
  {

  }

  public void caseAGreaterorequalsCond(AGreaterorequalsCond node)
  {

  }

  public void caseALessorequalsCond(ALessorequalsCond node)
  {

  }

  public void caseAGreaterthanCond(AGreaterthanCond node)
  {

  }

  public void caseALessthanCond(ALessthanCond node)
  {

  }

  public void caseAPlusAddop(APlusAddop node)
  {

  }

  public void caseAMinusAddop(AMinusAddop node)
  {

  }

  public void caseATimesMultop(ATimesMultop node)
  {

  }

  public void caseADivideMultop(ADivideMultop node)
  {

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
