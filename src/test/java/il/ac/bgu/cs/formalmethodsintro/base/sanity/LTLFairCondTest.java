package il.ac.bgu.cs.formalmethodsintro.base.sanity;


import static il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL.and;
import static il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL.next;
import static il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL.not;
import static il.ac.bgu.cs.formalmethodsintro.base.util.CollectionHelper.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import il.ac.bgu.cs.formalmethodsintro.base.FvmFacade;
import il.ac.bgu.cs.formalmethodsintro.base.automata.Automaton;
import il.ac.bgu.cs.formalmethodsintro.base.fairness.FairnessCondition;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.AP;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.formalmethodsintro.base.util.Pair;
import il.ac.bgu.cs.formalmethodsintro.base.verification.VerificationResult;
import il.ac.bgu.cs.formalmethodsintro.base.verification.VerificationSucceeded;
import il.ac.bgu.cs.formalmethodsintro.base.fairness.FairnessCondition;;

public class LTLFairCondTest {

	FvmFacade fvmFacadeImpl = FvmFacade.get();

	
	private <L> LTL<L> eventually(LTL<L> p) {
		return LTL.until(LTL.true_(), p);
	}

	private <L> LTL<L> always(LTL<L> p) {
		return LTL.not(eventually(LTL.not(p)));
	}
	
	@Test
	public void test() {
		AP<String> p = new AP<>("a");
		
		
		LTL<String> ltl = always(eventually(p));
		Set<String> internal_set =  set("alpha","beta");
		
		
		TransitionSystem<String,String,String> ts = buildTransitionSystem();
		FairnessCondition<String> fc = new FairnessCondition<String>(set(), set(), set(internal_set));
		VerificationResult<String> ver = fvmFacadeImpl.verifyFairLTLFormula(ts,fc,ltl);
		
		/*aut.getInitialStates().forEach(t -> {
			System.out.println("Initial:"+t);
			});
		
		aut.getTransitions().keySet().forEach(t -> {
			System.out.println("Src:"+t+"\nDst:"+ aut.getTransitions().get(t));
			});
		
		aut.getAcceptingStates().forEach(t -> {
			System.out.println("Accepting State:"+t);
			});
		*/
		

		
		assertTrue(ver instanceof VerificationSucceeded<?>);
	}
	
	
	private TransitionSystem<String, String, String> buildTransitionSystem() {
		TransitionSystem<String, String, String> ts = new TransitionSystem<String, String, String>();

		ts.addAllActions(set("alpha","beta","gama"));
		ts.addAllStates(set("s1","s2","s3"));
		ts.addInitialState("s1");
		ts.addAllAtomicPropositions(set("a",""));
		ts.addToLabel("s1","");
		ts.addToLabel("s2","");
		ts.addToLabel("s3","a");
		ts.addTransitionFrom("s1").action("gama").to("s2");
		ts.addTransitionFrom("s1").action("alpha").to("s3");
		ts.addTransitionFrom("s2").action("gama").to("s1");
		ts.addTransitionFrom("s2").action("beta").to("s3");
		ts.addTransitionFrom("s3").action("beta").to("s3");
		
		return ts;
	}



}

