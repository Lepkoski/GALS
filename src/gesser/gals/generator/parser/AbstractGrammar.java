package gesser.gals.generator.parser;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGrammar {
	public static final int EPSILON = 0;
	public static final int DOLLAR = 1;
	public static final int FIRST_TERMINAL = EPSILON + 2;

	public static final String EPSILON_STR = "î";

	protected String[] symbols;
	public int FIRST_NON_TERMINAL = 0;

	protected int startSymbol;

	protected List<Production> productions = new ArrayList<Production>();

	public int FIRST_SEMANTIC_ACTION() {
		return symbols.length;
	}

	public int LAST_SEMANTIC_ACTION() {
		return FIRST_SEMANTIC_ACTION() + SEMANTIC_ACTION_COUNT;
	}

	public int SEMANTIC_ACTION_COUNT = 0;

	public String[] getSymbols() {
		return symbols;
	}

	public int getStartSymbol() {
		return startSymbol;
	}

	public List<Production> getProductions() {
		return productions;
	}

	public String[] getTerminals() {
		String[] terminals = new String[FIRST_NON_TERMINAL - 2];
		System.arraycopy(symbols, 2, terminals, 0, terminals.length);
		return terminals;
	}

	public String[] getNonTerminals() {
		String[] nonTerminals = new String[FIRST_SEMANTIC_ACTION() - FIRST_NON_TERMINAL];
		System.arraycopy(symbols, FIRST_NON_TERMINAL, nonTerminals, 0, nonTerminals.length);
		return nonTerminals;
	}
}
