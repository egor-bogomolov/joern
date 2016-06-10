package tests.languages.c.parseTreeToAST;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ast.c.statements.blockstarters.CElseStatement;
import ast.c.statements.blockstarters.CIfStatement;
import ast.expressions.Expression;
import ast.logical.statements.BlockStarter;
import ast.logical.statements.CompoundStatement;
import ast.logical.statements.Condition;

public class IfNestingTests
{

	@Test
	public void ifBlockCompound()
	{
		String input = "if(foo){}";
		CompoundStatement compound = (CompoundStatement) FunctionContentTestUtil
				.parseAndWalk(input);
		assertFirstChildIsIfStatement(compound);
	}

	@Test
	public void ifBlockNoCompound()
	{
		String input = "if(foo) bar();";
		CompoundStatement compound = (CompoundStatement) FunctionContentTestUtil
				.parseAndWalk(input);
		assertFirstChildIsIfStatement(compound);
	}

	@Test
	public void nestedIfBlocksNoCompound()
	{
		String input = "if(foo) if(fooAgain) bar();";
		CompoundStatement compound = (CompoundStatement) FunctionContentTestUtil
				.parseAndWalk(input);
		CIfStatement ifStatement = (CIfStatement) compound.getStatements().get(0);
		CIfStatement innerStatement = (CIfStatement) ifStatement.getStatement();

		assertFirstChildIsIfStatement(compound);
		assertTrue(innerStatement.getCondition() != null);
	}

	@Test
	public void conditionString()
	{
		String input = "if(foo){}";
		CompoundStatement item = (CompoundStatement) FunctionContentTestUtil
				.parseAndWalk(input);
		BlockStarter starter = (BlockStarter) item.getStatements().get(0);
		Expression condition = ((Condition)starter.getCondition()).getExpression();
		assertTrue(condition.getEscapedCodeStr().equals("foo"));
	}

	@Test
	public void ifElse()
	{
		String input = "if(foo) lr->f = stdin; else lr->f = fopen(pathname, \"r\");";
		CompoundStatement compound = (CompoundStatement) FunctionContentTestUtil
				.parseAndWalk(input);

		assertFirstChildIsIfStatement(compound);
		assertFirstIfHasElse(compound);
	}

	@Test
	public void ifElseChain()
	{
		String input = "if(foo1) bar1(); else if(foo2) bar2(); else if(foo3) bar3();";
		CompoundStatement compound = (CompoundStatement) FunctionContentTestUtil
				.parseAndWalk(input);

		CIfStatement ifItem = (CIfStatement) compound.getStatements().get(0);
		for (int i = 0; i < 2; i++)
		{
			assertHasElse(ifItem);
			ifItem = (CIfStatement) ifItem.getElseNode().getStatement();
		}
	}

	@Test
	public void ifInElse()
	{
		String input = "if (foo1){} else { if (foo2) { foo(); } }";
		CompoundStatement compound = (CompoundStatement) FunctionContentTestUtil
				.parseAndWalk(input);
		CIfStatement ifItem = (CIfStatement) compound.getStatements().get(0);

		assertFirstChildIsIfStatement(compound);
		assertFirstIfHasElse(compound);

		CElseStatement elseNode = ifItem.getElseNode();
		CompoundStatement innerCompound = (CompoundStatement) elseNode
				.getStatement();
		assertTrue(innerCompound.getChildCount() == 1);
		CIfStatement innerIf = (CIfStatement) innerCompound.getChild(0);
		assertTrue(innerIf.getCondition() != null);
	}

	private void assertFirstChildIsIfStatement(CompoundStatement compound)
	{
		CIfStatement ifStatement = (CIfStatement) compound.getStatements().get(0);
		assertTrue(compound.getStatements().size() == 1);
		assertTrue(ifStatement.getCondition() != null);
	}

	private void assertFirstIfHasElse(CompoundStatement compound)
	{
		CIfStatement ifItem = (CIfStatement) compound.getStatements().get(0);
		assertHasElse(ifItem);
	}

	private void assertHasElse(CIfStatement ifItem)
	{
		CElseStatement elseNode = ifItem.getElseNode();
		assertTrue(elseNode != null);
		assertTrue(elseNode.getChild(0) != null);
	}

}
