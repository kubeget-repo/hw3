package ex3;

import static il.ac.bgu.cs.formalmethodsintro.base.util.CollectionHelper.set;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;



import il.ac.bgu.cs.formalmethodsintro.base.FvmFacade;
import il.ac.bgu.cs.formalmethodsintro.base.automata.Automaton;
import il.ac.bgu.cs.formalmethodsintro.base.automata.MultiColorAutomaton;
import il.ac.bgu.cs.formalmethodsintro.base.util.Pair;
import il.ac.bgu.cs.formalmethodsintro.base.util.Util;

public class GNBACritSectionTest {
	FvmFacade fvmFacadeImpl = FvmFacade.get();

	@Test
	public void simpleTest() throws Exception {

		MultiColorAutomaton<String, String> mulAut = getMCAut();
		Automaton<?, String> aut = fvmFacadeImpl.GNBA2NBA(mulAut);
			
		System.out.println("\n\n\n");
		
		Util.printAutomatonTransitions(getExpected());
		
		//assertEquals(getExpected(), aut);

	}

	MultiColorAutomaton<String, String> getMCAut() {
		MultiColorAutomaton<String, String> aut = new MultiColorAutomaton<>();

		aut.addTransition("s0", set("crit2"), "s2");
		aut.addTransition("s0", set("crit1"), "s1");

		// True transitions
		for (Set<String> s : Util.powerSet(set("crit1", "crit2"))) {
			aut.addTransition("s0", new HashSet<>(s), "s0");
			aut.addTransition("s1", new HashSet<>(s), "s0");
			aut.addTransition("s2", new HashSet<>(s), "s0");
		}

		aut.setInitial("s0");
		aut.setAccepting("s1", 0);
		aut.setAccepting("s2", 1);

		return aut;
	}

	Automaton<Pair<String,Integer>, String> getExpected() {
		Automaton<Pair<String,Integer>, String> aut = new Automaton<>();

		Set<String> none = set();
		Set<String> crit1 = set("crit1");
		Set<String> crit2 = set("crit2");
		Set<String> both = set("crit2", "crit1");

		
		aut.addTransition(new Pair("s0",1), crit2, new Pair("s2",0));
		aut.addTransition(new Pair("s0",1), crit1, new Pair("s1",1));
		aut.addTransition(new Pair("s0",2), crit2, new Pair("s2",2));
		aut.addTransition(new Pair("s0",2), crit1, new Pair("s1",2));

		// True transitions
		for (Set<String> s : asList(none, crit1, crit2, both)) {
			aut.addTransition(new Pair("s0",1), s,new Pair("s0",1) );
			aut.addTransition(new Pair("s1",1), s, new Pair("s0",2));
			aut.addTransition(new Pair("s0",2), s,new Pair("s0",2) );
			aut.addTransition(new Pair("s2",1), s, new Pair("s0",1));
			aut.addTransition(new Pair("s1",2), s, new Pair("s0",2));
			aut.addTransition(new Pair("s2",2), s, new Pair("s0",1));
			
			
		}

		aut.setInitial(new Pair("s0",1));

		aut.setAccepting(new Pair("s1",1));

		return aut;
	}

}
