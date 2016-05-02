package gesser.gals.generator.parser.ll;

import gesser.gals.generator.parser.AbstractGrammar;
import gesser.gals.util.IntegerSet;


public interface LLConflictSolver
{
	int resolve(AbstractGrammar g, IntegerSet conflict, int input, int stackTop);
}
