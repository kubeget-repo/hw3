package il.ac.bgu.cs.formalmethodsintro.base;


import static il.ac.bgu.cs.formalmethodsintro.base.Baraa_Tests.*;

import static il.ac.bgu.cs.formalmethodsintro.base.FvmFacade.build_LTLf;
import static il.ac.bgu.cs.formalmethodsintro.base.util.CollectionHelper.product;
import static il.ac.bgu.cs.formalmethodsintro.base.util.CollectionHelper.set;

import java.lang.reflect.Array;
import java.util.*;

import il.ac.bgu.cs.formalmethodsintro.base.automata.MultiColorAutomaton;
import il.ac.bgu.cs.formalmethodsintro.base.automata.Automaton;
import il.ac.bgu.cs.formalmethodsintro.base.fairness.ComposedAtomicProposition;
import il.ac.bgu.cs.formalmethodsintro.base.fairness.FairnessCondition;
import il.ac.bgu.cs.formalmethodsintro.base.fairness.TriggeredAtomicProposotion;
import il.ac.bgu.cs.formalmethodsintro.base.goal.AutomatonIO;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.AP;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL2GNBA_tools;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.Until;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TSTransition;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.formalmethodsintro.base.util.GraphvizPainter;
import il.ac.bgu.cs.formalmethodsintro.base.util.Pair;
import il.ac.bgu.cs.formalmethodsintro.base.util.Util;
import il.ac.bgu.cs.formalmethodsintro.base.verification.VerificationResult;
import org.svvrl.goal.gui.action.AutomatonConsistencyAction;

public class Run {
	static enum colors {
		red, blue
	};

	public static enum States {
		s0, s1, s2, s3, s4, s5
	}



	public static void main(String[] args) {
//		LTL_GNBA();
		FvmFacade app = new FvmFacade();
		TransitionSystem<String,String,String> ts = Baraa_get_TS5();
		LTL<String> ltl = Baraa_get_LTL();
		FairnessCondition<String> fc = Baraa_get_fc();
		System.out.println(app.verifyFairLTLFormula(ts,fc,ltl));




	}

	public static void LTL_GNBA() {
		FvmFacade app = new FvmFacade();
		var a = new AP<String>("a");
		var b = new AP<String>("b");
		var p = new AP<String>("p");
		var q = new AP<String>("q");
		var s = new AP<String>("s");
		
		var ltl = LTL.until(a, b) ;
		
		var ltl_2 = LTL.until(a, LTL.and(b, LTL.until(LTL.not(a), LTL.not(b))));
		var ltl_3 = LTL.until(a, LTL.until(b, LTL.not(a)));
		var ltl_4 = LTL.next(a);
		var ltl_5 =  LTL.and(LTL.not(p),LTL.next(p));
		/******quiz 4 ********* !(p U q) and (q U s) ***/
		var ltl_6 = LTL.and(LTL.not(LTL.until(p, q)),LTL.until(q, s));
		/******quiz 4 ********* p U (q U s)  ***/
		var ltl_7 = LTL.until(p, LTL.until(q, s));
		
		Automaton<?, String> nba = app.LTL2NBA(ltl_5);
		
	
//		System.out.println("\n\n/***** Transitions *****/\n");
//		System.out.println(nba.getTransitions());
//		System.out.println("\n\n/***** InitialStates *****/\n");
//		System.out.println(nba.getInitialStates());
//		System.out.println("\n\n/***** AcceptingStates *****/\n");
//		System.out.println(nba.getAcceptingStates());
		
	}
	static MultiColorAutomaton<String, String> getMCAut() {
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
	public static void GNBA_NBA() {
		// TODO Auto-generated method stub
		FvmFacade app = new FvmFacade();

		MultiColorAutomaton<States, String> gnba = new MultiColorAutomaton<States, String>();

		/****** init states *******/
		gnba.addState(States.s0);
		gnba.addState(States.s1);
		gnba.addState(States.s2);
		gnba.addState(States.s3);
		gnba.addState(States.s4);
		gnba.addState(States.s5);

		/******* Transition ******/

		gnba.addTransition(States.s0, new HashSet<>(Arrays.asList("b")), States.s4);
		gnba.addTransition(States.s0, new HashSet<>(Arrays.asList("a&b")), States.s5);

		gnba.addTransition(States.s1, new HashSet<>(Arrays.asList("a&b")), States.s2);
		gnba.addTransition(States.s1, new HashSet<>(Arrays.asList("a")), States.s1);

		gnba.addTransition(States.s2, new HashSet<>(Arrays.asList("a&b")), States.s5);

		gnba.addTransition(States.s3, new HashSet<>(Arrays.asList("true")), States.s3);
		gnba.addTransition(States.s3, new HashSet<>(Arrays.asList("b")), States.s0);
		gnba.addTransition(States.s3, new HashSet<>(Arrays.asList("a")), States.s1);
		gnba.addTransition(States.s3, new HashSet<>(Arrays.asList("a&b")), States.s2);

		gnba.addTransition(States.s4, new HashSet<>(Arrays.asList("true")), States.s4);
		gnba.addTransition(States.s4, new HashSet<>(Arrays.asList("a")), States.s5);

		gnba.addTransition(States.s5, new HashSet<>(Arrays.asList("a")), States.s5);

		/****** Colors *****/
		gnba.setAccepting(States.s0, colors.red.ordinal());
		gnba.setAccepting(States.s1, colors.blue.ordinal());
		gnba.setAccepting(States.s2, colors.blue.ordinal());
		gnba.setAccepting(States.s2, colors.red.ordinal());
		gnba.setAccepting(States.s4, colors.red.ordinal());
		gnba.setAccepting(States.s5, colors.blue.ordinal());
		gnba.setAccepting(States.s5, colors.red.ordinal());

		/*** init states ***/
		gnba.setInitial(States.s3);

		Automaton<States, String> nba = (Automaton<States, String>) app.GNBA2NBA(gnba);
		System.out.println(nba.getTransitions());
		System.out.println(nba.getInitialStates());
		System.out.println(nba.getAcceptingStates());

		System.out.println("\n\n/***** TEST 2*****/\n");
		gnba = new MultiColorAutomaton<States, String>();

		gnba.addTransition(States.s0, new HashSet<>(Arrays.asList("A")), States.s1);

		gnba.addTransition(States.s1, new HashSet<>(Arrays.asList("A")), States.s0);

		gnba.setAccepting(States.s0, colors.red.ordinal());
	    gnba.setAccepting(States.s1,colors.blue.ordinal());

		gnba.setInitial(States.s0);

		nba = (Automaton<States, String>) app.GNBA2NBA(gnba);

		/*System.out.println(nba.getTransitions());
		System.out.println(nba.getInitialStates());
		System.out.println(nba.getAcceptingStates());*/
		
		//System.out.println(GraphvizPainter.toStringPainter().makeDotCode(nba));
	}

}
