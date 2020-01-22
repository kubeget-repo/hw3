package il.ac.bgu.cs.formalmethodsintro.base;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import il.ac.bgu.cs.formalmethodsintro.base.automata.MultiColorAutomaton;
import il.ac.bgu.cs.formalmethodsintro.base.automata.Automaton;
import il.ac.bgu.cs.formalmethodsintro.base.goal.AutomatonIO;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.AP;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.Until;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TSTransition;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.formalmethodsintro.base.util.GraphvizPainter;

public class Run {
	static enum colors {
		red, blue
	};

	public static enum States {
		s0, s1, s2, s3, s4, s5
	}

	public static void main(String[] args) {
		LTL_GNBA();
	}

	public static void LTL_GNBA() {
		FvmFacade app = new FvmFacade();
		var a = new AP<String>("a");
		var b = new AP<String>("b");
		var ltl = LTL.until(a, b) ;
		
		var ltl_2 = LTL.until(a, LTL.and(b, LTL.until(LTL.not(a), LTL.not(b))));
		
		
		Automaton<?, String> nba = app.LTL2NBA(ltl);
		
	
		System.out.println("\n\n/***** Transitions *****/\n");
		System.out.println(nba.getTransitions());
		System.out.println("\n\n/***** InitialStates *****/\n");
		System.out.println(nba.getInitialStates());
		System.out.println("\n\n/***** AcceptingStates *****/\n");
		System.out.println(nba.getAcceptingStates());
		
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
		// gnba.setAccepting(States.s1,colors.blue.ordinal());

		gnba.setInitial(States.s0);

		nba = (Automaton<States, String>) app.GNBA2NBA(gnba);

		System.out.println(nba.getTransitions());
		System.out.println(nba.getInitialStates());
		System.out.println(nba.getAcceptingStates());
	}

}
