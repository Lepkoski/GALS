package gesser.gals.generator.parser.ll;

import java.util.HashSet;
import java.util.Set;

import gesser.gals.HTMLDialog;
import gesser.gals.ebnf.EbnfGrammar;
import gesser.gals.ebnf.Production;
import gesser.gals.ebnf.SymbolManager;
import gesser.gals.ebnf.Terminal;
import gesser.gals.generator.parser.AbstractGrammar;
import gesser.gals.generator.parser.ConflictSolver;
import gesser.gals.generator.parser.Grammar;
import gesser.gals.util.IntegerSet;

/**
 * LL1Grammar representa a classe das gram�ticas LL(1)
 * Esta classe possui um algor�timo de parsing preditivo.
 *
 * @author Carlos Eduardo Gesser
 */

public class LLParser
{
	private AbstractGrammar g;
    public LLParser(EbnfGrammar g) throws NotLLException
    {
    	if (! g.isFactored())
    		throw new NotLLException("Gram�tica n�o Fatorada");
    	//if (g.hasLeftRecursion())
			//throw new NotLLException("Gram�tica possui Recurs�o � Esquerda");
    	
    	this.g = g;    	
    }
    
    public AbstractGrammar getGrammar()
    {
    	return g;
    }

    /**
     * @param p produ��o para se calcular o conjunto predict
     *
     * @return BitSet contendo os tokens do lookahead de p
     */
    private Set<Terminal> lookahead(Production p)
    {
    	Set<Terminal> result = new HashSet<Terminal>(((EbnfGrammar)g).firstOf(p.getRhs()));
        if (result.contains(SymbolManager.EPSILON))
        {
            result.remove(SymbolManager.EPSILON);
            result.addAll(p.getLhs().getFollow());
        }
        return result;
    }

    public int[][] generateTable()
    {
    	IntegerSet[][] table = new IntegerSet[g.getSymbols().length-g.FIRST_NON_TERMINAL][g.FIRST_NON_TERMINAL-1];
/*
        for (int i = 0; i < table.length; i++)
            for (int j = 0; j < table[i].length; j++)
                table[i][j] = new IntegerSet();
*/
        for (int i=0; i<g.getProductions().size(); i++)
        {
            Production p = g.getProductions().get(i);
            Set<Terminal> pred = lookahead(p);
            for (Terminal t : ((EbnfGrammar)g).getSymbolManager().getTerminals())            
            {
            	if (pred.contains(t))
            	{
                	table[p.getLhs().getNumber()][t.getNumber()].add(i);
            	}
            }
        }

		return resolveConflicts(table,ConflictSolver.getInstance());
    }

	private int[][] resolveConflicts(IntegerSet[][] table, LLConflictSolver cs)
	{
		int[][] result = new int[table.length][table[0].length];
		
		for (int i=0; i<table.length; i++)
			for (int j=0; j<table[i].length; j++)
            {
                switch (table[i][j].size())
                {
                    case 0:
                        result[i][j] = -1;
                        break;        
                    case 1:
                        result[i][j] = table[i][j].first();
                        break;
                    default:
                        result[i][j] = cs.resolve(g, table[i][j], j, i);
                        break;
                }
            }
			
		return result;
	}    
	
	public String tableAsHTML()
	{
		int [][] tbl = generateTable();
		StringBuffer result = new StringBuffer();

		result.append(
			"<HTML>"+
			"<HEAD>"+
			"<TITLE>Tabela de An�lise LL(1)</TITLE>"+
			"</HEAD>"+
			"<BODY><FONT face=\"Verdana, Arial, Helvetica, sans-serif\">"+
			"<TABLE border=1 cellspacing=0>");
			
		result.append(
			"<TR align=center>"+
			"<TD bgcolor=black><FONT color=white><B>&nbsp;</B></FONT></TD>"+
			"<TD bgcolor=black><FONT color=white><B>$</B></FONT></TD>");
		
		for (int i=Grammar.FIRST_TERMINAL; i<g.FIRST_NON_TERMINAL; i++)
		{
			result.append("<TD nowrap bgcolor=black><FONT color=white><B>"+HTMLDialog.translateString(g.getSymbols()[i])+"</B></FONT></TD>");
		}
		
		result.append(
			"</TR>");
		
		for (int i=0; i<tbl.length; i++)
		{
			result.append(
				"<TR align=center>"+
				"<TD nowrap bgcolor=black><FONT color=white><B>"+HTMLDialog.translateString(g.getSymbols()[i+g.FIRST_NON_TERMINAL])+"</B></FONT></TD>");
			
			for (int j=0; j<tbl[i].length; j++)
			{
				int val = tbl[i][j];
				
				if (val >= 0)					
					result.append("<TD width=40 bgcolor=#F5F5F5>"+val+"</TD>");
				else
					result.append("<TD width=40 bgcolor=#F5F5F5>-</TD>");
			}
				
			result.append(
				"</TR>");
		}
		
		result.append("</TABLE>");
			
		result.append(			
			"<BR></FONT><CODE><TABLE border=0>");
			
		for (int i=0;i<g.getProductions().size(); i++)
		{
			result.append("<TR>");
			
			result.append("<TD align=right nowrap>"+i+"&nbsp;-&nbsp;</TD>");
			result.append("<TD>"+HTMLDialog.translateString(g.getProductions().get(i).toString())+"</TD>");
			
			result.append("</TR>");
		}
			
		result.append(
			"</TABLE></CODE>"+
			"</BODY>"+
			"</HTML>");
				
		return result.toString();
	}	
}
