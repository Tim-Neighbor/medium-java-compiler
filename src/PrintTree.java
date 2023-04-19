package FinalProject;

import FinalProject.analysis.*;
import FinalProject.node.*;
import java.util.*;

class PrintTree extends DepthFirstAdapter
{
 	public PrintTree()
    {
        System.out.println("Start of printing the AST");
	}

	private void printClassName(Node node)
    {
		System.out.println("Got a " + node.getClass().getSimpleName());
	}

	/////////////////////////////////////////////////////////////////////////////////////

	// Token cases

	public void caseTString(TString node)
    {
        System.out.println("Got a TString: " + node.getText());
    }

    public void caseTId(TId node)
    {
        System.out.println("Got a TId: " + node.getText());
    }

    public void caseTInt(TInt node)
    {
        System.out.println("Got a TInt: " + node.getText());
    }

    public void caseTReal(TReal node)
    {
        System.out.println("Got a TReal: " + node.getText());
    }

	/////////////////////////////////////////////////////////////////////////////////////

	// Production cases
	
    public void caseAProg(AProg node)
    {
        printClassName(node);
		node.getClassmethodstmts().apply(this);
    }

    public void caseAFullproductionClassmethodstmts(AFullproductionClassmethodstmts node)
    {
        printClassName(node);
		node.getClassmethodstmts().apply(this);
		node.getClassmethodstmt().apply(this);
    }

    public void caseAEmptyproductionClassmethodstmts(AEmptyproductionClassmethodstmts node)
    {
        printClassName(node);
    }

    public void caseAClassdeclClassmethodstmt(AClassdeclClassmethodstmt node)
    {
        printClassName(node);
		node.getId().apply(this);
		node.getMethodstmtseqs().apply(this);
    }

    public void caseAMethoddeclClassmethodstmt(AMethoddeclClassmethodstmt node)
    {
        printClassName(node);
		node.getType().apply(this);
		node.getId().apply(this);
		node.getVarlist().apply(this);
		node.getStmtseq().apply(this);
    }

    public void caseAFielddeclClassmethodstmt(AFielddeclClassmethodstmt node)
    {
        printClassName(node);
		node.getId().apply(this);

		List<PAdditionalidlist> starList = new ArrayList<PAdditionalidlist>(node.getAdditionalidlist());
        for(PAdditionalidlist starItem : starList)
        {
            starItem.apply(this);
        }

		node.getType().apply(this);
    }

    public void caseAFullproductionMethodstmtseqs(AFullproductionMethodstmtseqs node)
    {
        printClassName(node);
		node.getMethodstmtseqs().apply(this);
		node.getMethodstmtseq().apply(this);
    }

    public void caseAEmptyproductionMethodstmtseqs(AEmptyproductionMethodstmtseqs node)
    {
        printClassName(node);
    }

    public void caseAMethoddeclMethodstmtseq(AMethoddeclMethodstmtseq node)
    {
        printClassName(node);
		node.getType().apply(this);
		node.getId().apply(this);
		node.getVarlist().apply(this);
		node.getStmtseq().apply(this);
    }

    public void caseAFielddeclMethodstmtseq(AFielddeclMethodstmtseq node)
    {
        printClassName(node);
		node.getId().apply(this);

		List<PAdditionalidlist> starList = new ArrayList<PAdditionalidlist>(node.getAdditionalidlist());
        for (PAdditionalidlist starItem : starList)
        {
            starItem.apply(this);
        }

		node.getType().apply(this);
    }

    public void caseAFullproductionStmtseq(AFullproductionStmtseq node)
    {
        printClassName(node);
		node.getStmt().apply(this);
		node.getStmtseq().apply(this);
    }

    public void caseAEmptyproductionStmtseq(AEmptyproductionStmtseq node)
    {
        printClassName(node);
    }

    public void caseAAssignexprStmt(AAssignexprStmt node)
    {
        printClassName(node);
		node.getId().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
		node.getExpr().apply(this);
    }

    public void caseAAssignstringStmt(AAssignstringStmt node)
    {
        printClassName(node);
		node.getId().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
		node.getString().apply(this);
    }

    public void caseAVardeclStmt(AVardeclStmt node)
    {
        printClassName(node);
		node.getId().apply(this);

		List<PAdditionalidlist> starList = new ArrayList<PAdditionalidlist>(node.getAdditionalidlist());
        for (PAdditionalidlist starItem : starList)
        {
            starItem.apply(this);
        }

		node.getType().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
    }

    public void caseAIfStmt(AIfStmt node)
    {
        printClassName(node);
		node.getBoolean().apply(this);
		node.getStmtseq().apply(this);
    }

    public void caseAIfelseStmt(AIfelseStmt node)
    {
        printClassName(node);
		node.getBoolean().apply(this);
		node.getThenstmtseq().apply(this);
		node.getElsestmtseq().apply(this);
    }

    public void caseAWhileStmt(AWhileStmt node)
    {
        printClassName(node);
		node.getBoolean().apply(this);
		node.getStmtseq().apply(this);
    }

    public void caseAForStmt(AForStmt node)
    {
        printClassName(node);
		if (node.getType() != null)
		{
			node.getType().apply(this);
		}
		node.getId().apply(this);
		node.getExpr().apply(this);
		node.getBoolean().apply(this);
		node.getForincrementer().apply(this);
		node.getStmtseq().apply(this);
    }

    public void caseAGetStmt(AGetStmt node)
    {
        printClassName(node);
		node.getId().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
    }

    public void caseAPutStmt(APutStmt node)
    {
        printClassName(node);
		node.getId().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
    }

    public void caseAIncrStmt(AIncrStmt node)
    {
        printClassName(node);
		node.getId().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
    }

    public void caseADecrStmt(ADecrStmt node)
    {
        printClassName(node);
		node.getId().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
    }

    public void caseANewStmt(ANewStmt node)
    {
        printClassName(node);
		node.getFirstid().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
		node.getSecondid().apply(this);
    }

    public void caseAMethodcallStmt(AMethodcallStmt node)
    {
        printClassName(node);
		node.getId().apply(this);
		node.getVarlisttwo().apply(this);
    }

    public void caseAMethodcallonsomethingStmt(AMethodcallonsomethingStmt node)
    {
        printClassName(node);
		node.getFirstid().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
		node.getSecondid().apply(this);
		node.getVarlisttwo().apply(this);
		
		List<PAdditionalmethodcall> starList = new ArrayList<PAdditionalmethodcall>(node.getAdditionalmethodcall());
        for (PAdditionalmethodcall starItem : starList)
        {
            starItem.apply(this);
        }
    }

    public void caseAReturnStmt(AReturnStmt node)
    {
        printClassName(node);
		node.getExpr().apply(this);
    }

    public void caseASwitchStmt(ASwitchStmt node)
    {
        printClassName(node);
		node.getExpr().apply(this);
		node.getInt().apply(this);
		node.getFirststmtseq().apply(this);
		if (node.getBreakstmt() != null)
		{
			node.getBreakstmt().apply(this);
		}

		List<PCasestmt> starList = new ArrayList<PCasestmt>(node.getCasestmt());
        for (PCasestmt starItem : starList)
        {
            starItem.apply(this);
        }
		
		node.getSecondstmtseq().apply(this);
    }

    public void caseAAdditionalmethodcall(AAdditionalmethodcall node)
    {
        printClassName(node);
		node.getId().apply(this);
		node.getVarlisttwo().apply(this);
    }

    public void caseAIncrForincrementer(AIncrForincrementer node)
    {
        printClassName(node);
		node.getId().apply(this);
    }

    public void caseADecrForincrementer(ADecrForincrementer node)
    {
        printClassName(node);
		node.getId().apply(this);
    }

    public void caseAAssignForincrementer(AAssignForincrementer node)
    {
        printClassName(node);
		node.getId().apply(this);
		node.getExpr().apply(this);
    }

    public void caseABreakstmt(ABreakstmt node)
    {
        printClassName(node);
    }

    public void caseACasestmt(ACasestmt node)
    {
        printClassName(node);
		node.getInt().apply(this);
		node.getStmtseq().apply(this);
		if (node.getBreakstmt() != null)
		{
			node.getBreakstmt().apply(this);
		}
    }

    public void caseAVarlist(AVarlist node)
    {
        printClassName(node);
		if (node.getVarlisthelper() != null)
		{
			node.getVarlisthelper().apply(this);
		}
    }

    public void caseAVarlisthelper(AVarlisthelper node)
    {
        printClassName(node);
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
        printClassName(node);
		node.getId().apply(this);
		node.getType().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
    }

    public void caseAVarlisttwo(AVarlisttwo node)
    {
        printClassName(node);
		if (node.getVarlisttwohelper() != null)
		{
			node.getVarlisttwohelper().apply(this);
		}
    }

    public void caseAVarlisttwohelper(AVarlisttwohelper node)
    {
        printClassName(node);
		node.getExpr().apply(this);
		
		List<PAdditionalexprlist> starList = new ArrayList<PAdditionalexprlist>(node.getAdditionalexprlist());
        for (PAdditionalexprlist starItem : starList)
        {
            starItem.apply(this);
        }
    }

    public void caseAAdditionalexprlist(AAdditionalexprlist node)
    {
        printClassName(node);
		node.getExpr().apply(this);
    }

    public void caseAOperationExpr(AOperationExpr node)
    {
        printClassName(node);
		node.getExpr().apply(this);
		node.getCond().apply(this);
		node.getOperand().apply(this);
    }

    public void caseAOperandExpr(AOperandExpr node)
    {
        printClassName(node);
		node.getOperand().apply(this);
    }

    public void caseAOperationOperand(AOperationOperand node)
    {
        printClassName(node);
		node.getOperand().apply(this);
		node.getAddop().apply(this);
		node.getTerm().apply(this);
    }

    public void caseATermOperand(ATermOperand node)
    {
        printClassName(node);
		node.getTerm().apply(this);
    }

    public void caseAOperationTerm(AOperationTerm node)
    {
        printClassName(node);
		node.getTerm().apply(this);
		node.getMultop().apply(this);
		node.getFactor().apply(this);
    }

    public void caseAFactorTerm(AFactorTerm node)
    {
        printClassName(node);
		node.getFactor().apply(this);
    }

    public void caseAParenFactor(AParenFactor node)
    {
        printClassName(node);
		node.getExpr().apply(this);
    }

    public void caseANegateFactor(ANegateFactor node)
    {
        printClassName(node);
		node.getFactor().apply(this);
    }

    public void caseAIntFactor(AIntFactor node)
    {
        printClassName(node);
		node.getInt().apply(this);
    }

    public void caseARealFactor(ARealFactor node)
    {
        printClassName(node);
		node.getReal().apply(this);
    }

    public void caseABooleanFactor(ABooleanFactor node)
    {
        printClassName(node);
		node.getBooleanliteral().apply(this);
    }

    public void caseAIdFactor(AIdFactor node)
    {
        printClassName(node);
		node.getId().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
    }

    public void caseAMethodcallFactor(AMethodcallFactor node)
    {
        printClassName(node);
		node.getId().apply(this);
		node.getVarlisttwo().apply(this);
    }

    public void caseAMethodcallonsomethingFactor(AMethodcallonsomethingFactor node)
    {
        printClassName(node);
		node.getFirstid().apply(this);
		if (node.getBracketedint() != null)
		{
			node.getBracketedint().apply(this);
		}
		node.getSecondid().apply(this);
		node.getVarlisttwo().apply(this);
    }

    public void caseALiteralBoolean(ALiteralBoolean node)
    {
        printClassName(node);
		node.getBooleanliteral().apply(this);
    }

    public void caseACondBoolean(ACondBoolean node)
    {
        printClassName(node);
		node.getExpr().apply(this);
		node.getCond().apply(this);
		node.getOperand().apply(this);
    }

    public void caseAIdBoolean(AIdBoolean node)
    {
        printClassName(node);
		node.getId().apply(this);
    }

    public void caseATrueBooleanliteral(ATrueBooleanliteral node)
    {
        printClassName(node);
    }

    public void caseAFalseBooleanliteral(AFalseBooleanliteral node)
    {
        printClassName(node);
    }

    public void caseAEqualsCond(AEqualsCond node)
    {
        printClassName(node);
    }

    public void caseANotequalsCond(ANotequalsCond node)
    {
        printClassName(node);
    }

    public void caseAGreaterorequalsCond(AGreaterorequalsCond node)
    {
        printClassName(node);
    }

    public void caseALessorequalsCond(ALessorequalsCond node)
    {
        printClassName(node);
    }

    public void caseAGreaterthanCond(AGreaterthanCond node)
    {
        printClassName(node);
    }

    public void caseALessthanCond(ALessthanCond node)
    {
        printClassName(node);
    }

    public void caseAPlusAddop(APlusAddop node)
    {
        printClassName(node);
    }

    public void caseAMinusAddop(AMinusAddop node)
    {
        printClassName(node);
    }

    public void caseATimesMultop(ATimesMultop node)
    {
        printClassName(node);
    }

    public void caseADivideMultop(ADivideMultop node)
    {
        printClassName(node);
    }

    public void caseAIntType(AIntType node)
    {
        printClassName(node);
    }

    public void caseARealType(ARealType node)
    {
        printClassName(node);
    }

    public void caseAStringType(AStringType node)
    {
        printClassName(node);
    }

    public void caseABooleanType(ABooleanType node)
    {
        printClassName(node);
    }

    public void caseAVoidType(AVoidType node)
    {
        printClassName(node);
    }

    public void caseAIdType(AIdType node)
    {
        printClassName(node);
		node.getId().apply(this);
    }

    public void caseAAdditionalidlist(AAdditionalidlist node)
    {
        printClassName(node);
		node.getId().apply(this);
    }

    public void caseABracketedint(ABracketedint node)
    {
        printClassName(node);
		node.getInt().apply(this);
    }
}
