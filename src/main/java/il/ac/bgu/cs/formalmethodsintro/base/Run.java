package il.ac.bgu.cs.formalmethodsintro.base;

import static il.ac.bgu.cs.formalmethodsintro.base.util.CollectionHelper.set;

import java.util.*;

import il.ac.bgu.cs.formalmethodsintro.base.automata.MultiColorAutomaton;
import il.ac.bgu.cs.formalmethodsintro.base.automata.Automaton;
import il.ac.bgu.cs.formalmethodsintro.base.goal.AutomatonIO;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.AP;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.Until;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TSTransition;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.formalmethodsintro.base.util.GraphvizPainter;
import il.ac.bgu.cs.formalmethodsintro.base.util.Pair;
import il.ac.bgu.cs.formalmethodsintro.base.util.Util;
import org.svvrl.goal.gui.action.AutomatonConsistencyAction;

public class Run {
	static enum colors {
		red, blue
	};

	public static enum States {
		s0, s1, s2, s3, s4, s5
	}

	public static Automaton<String, String> Baraa_get_aut() {
		Automaton<String,String> aut = new Automaton();

		String q0 = "q0";
		String q1 = "q1";
		String q2 = "q2";
		String q3 = "q3";

		List<Set<String>> all_sets = new ArrayList<>();
		all_sets.add(new HashSet<>(Arrays.asList()));
		all_sets.add(new HashSet<>(Arrays.asList("a")));
		all_sets.add(new HashSet<>(Arrays.asList("b")));
		all_sets.add(new HashSet<>(Arrays.asList("c")));
		all_sets.add(new HashSet<>(Arrays.asList("a","b")));
		all_sets.add(new HashSet<>(Arrays.asList("b", "c")));
		all_sets.add(new HashSet<>(Arrays.asList("a", "c")));
		all_sets.add(new HashSet<>(Arrays.asList("a", "b", "c")));

		int[] q0_q0 = {0,1,3,5,6,7};
		int[] q0_q1 = {2,4};
		int[] q1_q1 = {2};
		int[] q1_q0 = {0,3,5};
		int[] q1_q2 = {1,4,6,7};
		int[] q2_q0 = {3,5,6,7};
		int[] q2_q2 = {0};
		int[] q2_q3 = {1,2,4};

		aut.addState(q0);
		aut.addState(q1);
		aut.addState(q2);
		aut.addState(q3);

		aut.setInitial(q0);
		aut.setAccepting(q3);

		for(int i = 0; i< q0_q0.length; i++)
			aut.addTransition(q0,all_sets.get(q0_q0[i]),q0);

		for(int i = 0; i< q0_q1.length; i++)
			aut.addTransition(q0,all_sets.get(q0_q1[i]),q1);

		for(int i = 0; i< q1_q1.length; i++)
			aut.addTransition(q1,all_sets.get(q1_q1[i]),q1);

		for(int i = 0; i< q1_q0.length; i++)
			aut.addTransition(q1,all_sets.get(q1_q0[i]),q0);

		for(int i = 0; i< q1_q2.length; i++)
			aut.addTransition(q1,all_sets.get(q1_q2[i]),q2);

		for(int i = 0; i< q2_q0.length; i++)
			aut.addTransition(q2,all_sets.get(q2_q0[i]),q0);

		for(int i = 0; i< q2_q2.length; i++)
			aut.addTransition(q2,all_sets.get(q2_q2[i]),q2);

		for(int i = 0; i< q2_q3.length; i++)
			aut.addTransition(q2,all_sets.get(q2_q3[i]),q3);





		return aut;

	}
	public static TransitionSystem<String,String,String> Baraa_get_TS(){
		TransitionSystem<String,String,String> ts = new TransitionSystem<>();
		String[] AP = {"a","b","c"};
		String[] ACT = {"alpha","beta","gama"};


		String alpha = "alpha";
		String beta = "beta";
		String gama = "gama";


		String s0 = "s0";
		String s1 = "s1";
		String s2 = "s2";
		String s3 = "s3";
		String s4 = "s4";
		String s5 = "s5";

		ts.addInitialState(s0);
		ts.addState(s1);
		ts.addState(s2);
		ts.addState(s3);
		ts.addState(s4);
		ts.addState(s5);
		ts.addAllAtomicPropositions(AP);
		ts.addAllActions(ACT);
		ts.addToLabel(s0,AP[0]);  ts.addToLabel(s0,AP[1]);
		ts.addToLabel(s1,AP[0]);  ts.addToLabel(s1,AP[1]);   ts.addToLabel(s1,AP[2]);
		ts.addToLabel(s2,AP[1]);  ts.addToLabel(s2,AP[2]);
		ts.addToLabel(s3,AP[0]);  ts.addToLabel(s3,AP[2]);
		ts.addToLabel(s4,AP[0]);  ts.addToLabel(s4,AP[2]);
		ts.addToLabel(s5,AP[0]);  ts.addToLabel(s5,AP[1]);


		ts.addTransition(new TSTransition<>(s0,beta,s1));
		ts.addTransition(new TSTransition<>(s0,alpha,s3));
		ts.addTransition(new TSTransition<>(s1,alpha,s4));
		ts.addTransition(new TSTransition<>(s2,gama,s1));
		ts.addTransition(new TSTransition<>(s3,gama,s1));
		ts.addTransition(new TSTransition<>(s4,gama,s1));
		ts.addTransition(new TSTransition<>(s4,beta,s1));
		ts.addTransition(new TSTransition<>(s5,beta,s1));
		ts.addTransition(new TSTransition<>(s5,alpha,s2));
		return ts;
	}
	public static void main(String[] args) {
		//LTL_GNBA();
		/*FvmFacade app = new FvmFacade();
		var p = new AP<String>("p");
		var q = new AP<String>("q");
		var s = new AP<String>("s");
		System.out.println(app.isConsistent(set(
				LTL.not(p),
				q,
				LTL.not(s),
				LTL.until(q, s),
				LTL.until(p, LTL.until(q, s))
								), 5*2));
		System.out.print(set(
				p,
				q,
				LTL.not(s),
				LTL.not(LTL.until(q, s)),
				LTL.until(p, LTL.until(q, s))));*/
		FvmFacade app = new FvmFacade();
		TransitionSystem<String,String,String> ts = Baraa_get_TS();
		Automaton<String,String> aut = Baraa_get_aut();
		TransitionSystem<Pair<String, String>, String, String> res =  app.product(ts,aut);

		System.out.println("AP : "  + res.getAtomicPropositions());
		System.out.println("Act : " + res.getActions());
		System.out.println("I : "   + res.getInitialStates());
		System.out.println("S : "   + res.getStates());
		System.out.println("S : "   + app.reach(res));




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
		
		Automaton<?, String> nba = app.LTL2NBA(ltl_6);
		
	
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
		// gnba.setAccepting(States.s1,colors.blue.ordinal());

		gnba.setInitial(States.s0);

		nba = (Automaton<States, String>) app.GNBA2NBA(gnba);

		System.out.println(nba.getTransitions());
		System.out.println(nba.getInitialStates());
		System.out.println(nba.getAcceptingStates());
	}

}
