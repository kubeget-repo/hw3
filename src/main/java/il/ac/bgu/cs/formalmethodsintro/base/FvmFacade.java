package il.ac.bgu.cs.formalmethodsintro.base;

import java.io.InputStream;
import java.util.*;

import il.ac.bgu.cs.formalmethodsintro.base.automata.Automaton;
import il.ac.bgu.cs.formalmethodsintro.base.automata.MultiColorAutomaton;
import il.ac.bgu.cs.formalmethodsintro.base.channelsystem.ChannelSystem;
import il.ac.bgu.cs.formalmethodsintro.base.channelsystem.InterleavingActDef;
import il.ac.bgu.cs.formalmethodsintro.base.channelsystem.ParserBasedInterleavingActDef;
import il.ac.bgu.cs.formalmethodsintro.base.circuits.Circuit;
import il.ac.bgu.cs.formalmethodsintro.base.exceptions.StateNotFoundException;
import il.ac.bgu.cs.formalmethodsintro.base.fairness.*;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.*;
import il.ac.bgu.cs.formalmethodsintro.base.nanopromela.NanoPromelaFileReader;
import il.ac.bgu.cs.formalmethodsintro.base.nanopromela.NanoPromelaParser;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.*;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.AlternatingSequence;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TSTransition;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.formalmethodsintro.base.util.Pair;
import il.ac.bgu.cs.formalmethodsintro.base.util.Util;
import il.ac.bgu.cs.formalmethodsintro.base.verification.VerificationFailed;
import il.ac.bgu.cs.formalmethodsintro.base.verification.VerificationResult;
import il.ac.bgu.cs.formalmethodsintro.base.verification.VerificationSucceeded;
import il.ac.bgu.cs.formalmethodsintro.base.wrapperclasses.ChannelSystemTransitionWrapper;

/**
 * Interface for the entry point class to the HW in this class. Our
 * client/testing code interfaces with the student solutions through this
 * interface only. <br>
 * More about facade: {@linkplain ://www.vincehuston.org/dp/facade.html}.
 */
public class FvmFacade {



		private static FvmFacade INSTANCE = null;

		/**
		 *
		 * @return an instance of this class.
		 */
		public static FvmFacade get() {
			if (INSTANCE == null) {
				INSTANCE = new FvmFacade();
			}
			return INSTANCE;
		}

		/**
		 * Checks whether a transition system is action deterministic. I.e., if for
		 * any given p and α there exists only a single tuple (p,α,q) in →. Note
		 * that this must be true even for non-reachable states.
		 *
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param <P> Type of atomic propositions.
		 * @param ts The transition system being tested.
		 * @return {@code true} iff the action is deterministic.
		 */
		public <S, A, P> boolean isActionDeterministic(TransitionSystem<S, A, P> ts) {
			for (S state:ts.getStates()) {                  //for each state
				for (A action:ts.getActions()) {            //and for each action
					if(post(ts,state,action).size()>1){
						return false;
					}
				}
			}

			if(ts.getInitialStates().size() > 1) {
				return false;
			}
			return true;
		}

		/**
		 * Checks whether an action is ap-deterministic (as defined in class), in
		 * the context of a given {@link TransitionSystem}.
		 *
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param <P> Type of atomic propositions.
		 * @param ts The transition system being tested.
		 * @return {@code true} iff the action is ap-deterministic.
		 */
		public <S, A, P> boolean isAPDeterministic(TransitionSystem<S, A, P> ts) {

			Set<Set<P>> labels_per_state;
			for (S state:ts.getStates()) {                  //for each state
				labels_per_state = new HashSet<>();
				for (S in_post:post(ts,state)) {
					labels_per_state.add(ts.getLabel(in_post));
				}
				if(post(ts,state).size() != labels_per_state.size()){
					return false;
				}
			}

			if(ts.getInitialStates().size() > 1) {
				return false;
			}
			return true;
		}

		/**
		 * Checks whether an alternating sequence is an execution of a
		 * {@link TransitionSystem}, as defined in class.
		 *
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param <P> Type of atomic propositions.
		 * @param ts The transition system being tested.
		 * @param e The sequence that may or may not be an execution of {@code ts}.
		 * @return {@code true} iff {@code e} is an execution of {@code ts}.
		 */
		public <S, A, P> boolean isExecution(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
			boolean initial = isInitialExecutionFragment(ts,e);
			boolean maximum = isMaximalExecutionFragment(ts,e);
			return maximum && initial;
		}

		/**
		 * Checks whether an alternating sequence is an execution fragment of a
		 * {@link TransitionSystem}, as defined in class.
		 *
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param <P> Type of atomic propositions.
		 * @param ts The transition system being tested.
		 * @param e The sequence that may or may not be an execution fragment of
		 * {@code ts}.
		 * @return {@code true} iff {@code e} is an execution fragment of
		 * {@code ts}.
		 */
		public <S, A, P> boolean isExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
			if(e.size() == 0)
				return true;
			Boolean output;
			AlternatingSequence<A, S> e_tag;
			S from = e.head();
			S to = null;
			A action;
			while(!e.isEmpty()){
				e_tag = e.tail();
				if(e_tag.isEmpty()){
					break;
				}
				action = e_tag.head();
				e = e_tag.tail();
				to = e.head();

				try {
					if (!post(ts, from, action).contains(to)) {
						return false;
					}
				} catch(StateNotFoundException exception){
					return false;
				}
				from = to;
			}
			return true;
		}

		/**
		 * Checks whether an alternating sequence is an initial execution fragment
		 * of a {@link TransitionSystem}, as defined in class.
		 *
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param <P> Type of atomic propositions.
		 * @param ts The transition system being tested.
		 * @param e The sequence that may or may not be an initial execution
		 * fragment of {@code ts}.
		 * @return {@code true} iff {@code e} is an execution fragment of
		 * {@code ts}.
		 */
		public <S, A, P> boolean isInitialExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
			Boolean exec = isExecutionFragment(ts,e);
			Boolean init = ts.getInitialStates().contains(e.head());
			return init && exec;
		}

		/**
		 * Checks whether an alternating sequence is a maximal execution fragment of
		 * a {@link TransitionSystem}, as defined in class.
		 *
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param <P> Type of atomic propositions.
		 * @param ts The transition system being tested.
		 * @param e The sequence that may or may not be a maximal execution fragment
		 * of {@code ts}.
		 * @return {@code true} iff {@code e} is a maximal fragment of {@code ts}.
		 */
		public <S, A, P> boolean isMaximalExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
			return isStateTerminal(ts,e.last());
		}

		/**
		 * Checks whether a state in {@code ts} is terminal.
		 *
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param ts Transition system of {@code s}.
		 * @param s The state being tested for terminality.
		 * @return {@code true} iff state {@code s} is terminal in {@code ts}.
		 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
		 */
		public <S, A> boolean isStateTerminal(TransitionSystem<S, A, ?> ts, S s) {
			if(!ts.getStates().contains(s)){
				throw new StateNotFoundException("");
			}
			return post(ts,s).isEmpty();
		}

		/**
		 * @param <S> Type of states.
		 * @param ts Transition system of {@code s}.
		 * @param s A state in {@code ts}.
		 * @return All the states in {@code Post(s)}, in the context of {@code ts}.
		 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
		 */
		public <S> Set<S> post(TransitionSystem<S, ?, ?> ts, S s) {
			if(!ts.getStates().contains(s))
				throw new StateNotFoundException("");
			Set<S> res = new HashSet<S>();
			for (TSTransition<S,?> transition :ts.getTransitions()) {
				if(transition.getFrom().equals(s)){
					if(!res.contains(transition.getTo())){
						res.add(transition.getTo());
					}
				}
			}
			return res;
		}

		/**
		 * @param <S> Type of states.
		 * @param ts Transition system of {@code s}.
		 * @param c States in {@code ts}.
		 * @return All the states in {@code Post(s)} where {@code s} is a member of
		 * {@code c}, in the context of {@code ts}.
		 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
		 */
		public <S> Set<S> post(TransitionSystem<S, ?, ?> ts, Set<S> c) {
			Set<S> res = new HashSet<S>();
			for (S state:c) {
				res.addAll(post(ts,state));
			}
			return res;
		}

		/**
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param ts Transition system of {@code s}.
		 * @param s A state in {@code ts}.
		 * @param a An action.
		 * @return All the states that {@code ts} might transition to from
		 * {@code s}, when action {@code a} is selected.
		 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
		 */
		public <S, A> Set<S> post(TransitionSystem<S, A, ?> ts, S s, A a) {
			if(!ts.getStates().contains(s))
				throw new StateNotFoundException("");
			Set<S> res = new HashSet<S>();
			for (TSTransition<S,A> transition:ts.getTransitions()) {
				if(transition.getFrom().equals(s) && transition.getAction().equals(a)){
					if(!res.contains(transition.getTo())){
						res.add(transition.getTo());
					}
				}
			}
			return res;
		}

		/**
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param ts Transition system of {@code s}.
		 * @param c Set of states in {@code ts}.
		 * @param a An action.
		 * @return All the states that {@code ts} might transition to from any state
		 * in {@code c}, when action {@code a} is selected.
		 */
		public <S, A> Set<S> post(TransitionSystem<S, A, ?> ts, Set<S> c, A a) {
			Set<S> res = new HashSet<S>();
			for (S state:c) {
				res.addAll(post(ts,state,a));
			}
			return res;
		}

		/**
		 * @param <S> Type of states.
		 * @param ts Transition system of {@code s}.
		 * @param s A state in {@code ts}.
		 * @return All the states in {@code Pre(s)}, in the context of {@code ts}.
		 */
		public <S> Set<S> pre(TransitionSystem<S, ?, ?> ts, S s) {

			try {
				Set<S> res = new HashSet<S>();
				for (TSTransition<S, ?> transition : ts.getTransitions()) {
					if (transition.getTo().equals(s)) {
						if (!res.contains(transition.getFrom())) {
							res.add(transition.getFrom());
						}
					}
				}
				return res;
			} catch(Exception e ) {
				return new HashSet<>();
			}

		}

		/**
		 * @param <S> Type of states.
		 * @param ts Transition system of {@code s}.
		 * @param c States in {@code ts}.
		 * @return All the states in {@code Pre(s)} where {@code s} is a member of
		 * {@code c}, in the context of {@code ts}.
		 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
		 */
		public <S> Set<S> pre(TransitionSystem<S, ?, ?> ts, Set<S> c) {
			Set<S> res = new HashSet<S>();
			for (S state:c) {
				res.addAll(pre(ts,state));
			}
			return res;
		}

		/**
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param ts Transition system of {@code s}.
		 * @param s A state in {@code ts}.
		 * @param a An action.
		 * @return All the states that {@code ts} might transitioned from, when in
		 * {@code s}, and the last action was {@code a}.
		 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
		 */
		public <S, A> Set<S> pre(TransitionSystem<S, A, ?> ts, S s, A a) {
			if(!ts.getStates().contains(s))
				throw new StateNotFoundException("");
			Set<S> res = new HashSet<S>();
			for (TSTransition<S,A> transition:ts.getTransitions()) {
				if(transition.getTo().equals(s) && transition.getAction().equals(a)){
					if(!res.contains(transition.getFrom())){
						res.add(transition.getFrom());
					}
				}
			}
			return res;
		}

		/**
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param ts Transition system of {@code s}.
		 * @param c Set of states in {@code ts}.
		 * @param a An action.
		 * @return All the states that {@code ts} might transitioned from, when in
		 * any state in {@code c}, and the last action was {@code a}.
		 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
		 */
		public <S, A> Set<S> pre(TransitionSystem<S, A, ?> ts, Set<S> c, A a) {
			Set<S> res = new HashSet<S>();
			for (S state:c) {
				res.addAll(pre(ts,state,a));
			}
			return res;
		}

		/**
		 * Implements the {@code reach(TS)} function.
		 *
		 * @param <S> Type of states.
		 * @param <A> Type of actions.
		 * @param ts Transition system of {@code s}.
		 * @return All states reachable in {@code ts}.
		 */
		public <S, A> Set<S> reach(TransitionSystem<S, A, ?> ts) {
			Set<S> states_of_distance_i = new HashSet<>(ts.getInitialStates());
			Set<S> res = new HashSet<>(ts.getInitialStates());
			int i = 0;
			while(!states_of_distance_i.isEmpty()){
//				System.out.println("before " + i +  states_of_distance_i);
				i++;
				states_of_distance_i = post(ts,states_of_distance_i);
//				System.out.println("middle " + i +  states_of_distance_i);
				states_of_distance_i.removeAll(res);
				res.addAll(states_of_distance_i);
//				System.out.println("after " + i +  states_of_distance_i);
			}
			return res;
		}


		/**
		 * Compute the synchronous product of two transition systems.
		 *
		 * @param <S1> Type of states in the first system.
		 * @param <S2> Type of states in the first system.
		 * @param <A> Type of actions (in both systems).
		 * @param <P> Type of atomic propositions (in both systems).
		 * @param ts1 The first transition system.
		 * @param ts2 The second transition system.
		 *
		 * @return A transition system that represents the product of the two.
		 */
		public <S1, S2, A, P> TransitionSystem<Pair<S1, S2>, A, P> interleave(TransitionSystem<S1, A, P> ts1,
																			  TransitionSystem<S2, A, P> ts2) {
			TransitionSystem<Pair<S1,S2>,A,P> res = new TransitionSystem<Pair<S1,S2>, A, P>();
			res.setName("(" + ts1.getName() + "|||" + ts2.getName() + ")");
			Set<Pair<S1,S2>> states = new HashSet<Pair<S1,S2>>();
			Set<A> actions = new HashSet<A>();
			Set<TSTransition<Pair<S1,S2>, A>> transitions = new HashSet<TSTransition<Pair<S1,S2>, A>>();
			Set<P> atomicPropositions = new HashSet<P>();
			HashMap<Pair<S1,S2>, Set<P>> labelingFunction = new HashMap<>();

			//Act
			actions.addAll(ts1.getActions());
			actions.addAll(ts2.getActions());

			//S+L
			for (S1 state1: ts1.getStates()) {
				for (S2 state2: ts2.getStates()) {
					Pair<S1,S2> pair = new Pair<S1,S2>(state1,state2);
					Set<P> labeling = new HashSet<P>();
					states.add(pair);
					labeling.addAll(ts1.getLabel(state1));
					labeling.addAll(ts2.getLabel(state2));
					for (P label:labeling) {
						res.addToLabel(pair,label);
					}
				}
			}

			//I
			for (S1 state1: ts1.getInitialStates()) {
				for (S2 state2: ts2.getInitialStates()) {
					res.addInitialState(new Pair<S1,S2>(state1,state2));

				}
			}

			//AP
			atomicPropositions.addAll(ts1.getAtomicPropositions());
			atomicPropositions.addAll(ts2.getAtomicPropositions());

			//->
			for(TSTransition<S1,A> transition1:ts1.getTransitions()){
				for (S2 state2:ts2.getStates()) {
					res.addTransition(new TSTransition<Pair<S1,S2>,A>(
							new Pair<S1,S2>(transition1.getFrom(),state2), //from
							transition1.getAction(),                       //action
							new Pair<S1,S2>(transition1.getTo(),state2)    //to
					));
				}
			}
			for(TSTransition<S2,A> transition2:ts2.getTransitions()){

				for (S1 state1:ts1.getStates()) {
					res.addTransition(new TSTransition<Pair<S1,S2>,A>(
							new Pair<S1,S2>(state1,transition2.getFrom()), //from
							transition2.getAction(),                       //action
							new Pair<S1,S2>(state1,transition2.getTo())    //to
					));
				}
			}

			res.addAllStates(states);
			res.addAllActions(actions);
			res.addAllAtomicPropositions(atomicPropositions);

			return res;
		}

		/**
		 * Compute the synchronous product of two transition systems.
		 *
		 * @param <S1> Type of states in the first system.
		 * @param <S2> Type of states in the first system.
		 * @param <A> Type of actions (in both systems).
		 * @param <P> Type of atomic propositions (in both systems).
		 * @param ts1 The first transition system.
		 * @param ts2 The second transition system.
		 * @param handShakingActions Set of actions both systems perform together.
		 *
		 * @return A transition system that represents the product of the two.
		 */
		public <S1, S2, A, P> TransitionSystem<Pair<S1, S2>, A, P> interleave(TransitionSystem<S1, A, P> ts1,
																			  TransitionSystem<S2, A, P> ts2, Set<A> handShakingActions) {
			TransitionSystem<Pair<S1,S2>,A,P> res = new TransitionSystem<Pair<S1,S2>, A, P>();
			res.setName("(" + ts1.getName()+ " |||H "+ts2.getName() +")");
			Set<Pair<S1,S2>> states = new HashSet<Pair<S1,S2>>();
			Set<A> actions = new HashSet<A>();
			Set<TSTransition<Pair<S1,S2>, A>> transitions = new HashSet<TSTransition<Pair<S1,S2>, A>>();
			Set<P> atomicPropositions = new HashSet<P>();
			HashMap<Pair<S1,S2>, Set<P>> labelingFunction = new HashMap<>();

			//Act
			actions.addAll(ts1.getActions());
			actions.addAll(ts2.getActions());

			//S+L
			for (S1 state1: ts1.getStates()) {
				for (S2 state2: ts2.getStates()) {
					Pair<S1,S2> pair = new Pair<S1,S2>(state1,state2);
					Set<P> labeling = new HashSet<P>();
					states.add(pair);
					labeling.addAll(ts1.getLabel(state1));
					labeling.addAll(ts2.getLabel(state2));
					for (P label:labeling) {
						res.addToLabel(pair,label);
					}
				}
			}

			//I
			for (S1 state1: ts1.getInitialStates()) {
				for (S2 state2: ts2.getInitialStates()) {
					res.addInitialState(new Pair<S1,S2>(state1,state2));

				}
			}

			//AP
			atomicPropositions.addAll(ts1.getAtomicPropositions());
			atomicPropositions.addAll(ts2.getAtomicPropositions());

			//->
			for(TSTransition<S1,A> transition1:ts1.getTransitions()){
				for (S2 state2:ts2.getStates()) {
					if(!handShakingActions.contains(transition1.getAction())) {
						res.addTransition(new TSTransition<Pair<S1, S2>, A>(
								new Pair<S1, S2>(transition1.getFrom(), state2), //from
								transition1.getAction(),                       //action
								new Pair<S1, S2>(transition1.getTo(), state2)    //to
						));
					}
				}
			}
			for(TSTransition<S2,A> transition2:ts2.getTransitions()){

				for (S1 state1:ts1.getStates()) {
					if(!handShakingActions.contains(transition2.getAction())) {
						res.addTransition(new TSTransition<Pair<S1, S2>, A>(
								new Pair<S1, S2>(state1, transition2.getFrom()), //from
								transition2.getAction(),                       //action
								new Pair<S1, S2>(state1, transition2.getTo())    //to
						));
					}
				}
			}

			for(TSTransition<S1,A> transition1: ts1.getTransitions()){
				for(TSTransition<S2,A> transition2: ts2.getTransitions()) {
					if(transition1.getAction().equals(transition2.getAction()) && handShakingActions.contains(transition1.getAction())){
						res.addTransition(new TSTransition<Pair<S1, S2>, A>(
								new Pair<S1,S2>(transition1.getFrom(),transition2.getFrom()),
								transition1.getAction(),
								new Pair<S1,S2>(transition1.getTo(),transition2.getTo())

						));
					}
				}
			}

			res.addAllStates(states);
			res.addAllActions(actions);
			res.addAllAtomicPropositions(atomicPropositions);

			return res;
		}

		/**
		 * Creates a new {@link ProgramGraph} object.
		 *
		 * @param <L> Type of locations in the graph.
		 * @param <A> Type of actions of the graph.
		 * @return A new program graph instance.
		 */
		public <L, A> ProgramGraph<L, A> createProgramGraph() {
			return new ProgramGraph<>();
		}

		/**
		 * Interleaves two program graphs.
		 *
		 * @param <L1> Type of locations in the first graph.
		 * @param <L2> Type of locations in the second graph.
		 * @param <A> Type of actions in BOTH GRAPHS.
		 * @param pg1 The first program graph.
		 * @param pg2 The second program graph.
		 * @return Interleaved program graph.
		 */
		public <L1, L2, A> ProgramGraph<Pair<L1, L2>, A> interleave(ProgramGraph<L1, A> pg1, ProgramGraph<L2, A> pg2) {
			ProgramGraph<Pair<L1,L2>, A> res = new ProgramGraph<Pair<L1,L2>, A>();
			res.setName("(" + pg1.getName() + "||" + pg2.getName() + ")");
			for (L1 location1:pg1.getLocations()) {
				for (L2 location2 : pg2.getLocations()) {
					Pair<L1,L2> location = new Pair(location1,location2);
					res.setInitial(location,
							pg1.getInitialLocations().contains(location1) &&
									pg2.getInitialLocations().contains(location2));

				}
			}

			for (PGTransition<L1,A> transition1:pg1.getTransitions()) {
				for (L2 location2 : pg2.getLocations()) {
					res.addTransition(new PGTransition<Pair<L1,L2>,A>(
							new Pair<L1,L2>(transition1.getFrom(),location2),
							transition1.getCondition(),
							transition1.getAction(),
							new Pair<L1,L2>(transition1.getTo(),location2)));
				}
			}


			for (PGTransition<L2,A> transition2:pg2.getTransitions()) {
				for (L1 location1 : pg1.getLocations()) {
					res.addTransition(new PGTransition<Pair<L1,L2>,A>(
							new Pair<L1,L2>(location1,transition2.getFrom()),
							transition2.getCondition(),
							transition2.getAction(),
							new Pair<L1,L2>(location1,transition2.getTo())));
				}
			}

			for (List<String> initialization1:pg1.getInitalizations()) {
				for (List<String> initialization2:pg2.getInitalizations()) {
					List<String> merged = new ArrayList<>();
					merged.addAll(initialization1);
					merged.addAll(initialization2);
					res.addInitalization(merged);
				}
			}
			return res;
		}

		private static  boolean all_falseList(List<Boolean> l)
		{
			for (Boolean b:l) {
				if(b)
					return false;
			}
			return true;
		}
		/**
		 * Creates a {@link TransitionSystem} representing the passed circuit.
		 *
		 * @param c The circuit to translate into a {@link TransitionSystem}.
		 * @return A {@link TransitionSystem} representing {@code c}.
		 */
		public TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>, Map<String, Boolean>, Object> transitionSystemFromCircuit(
				Circuit c) {
			TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>,Map<String, Boolean>,Object> res= new TransitionSystem();

			//S+I+Act
			Set<String> registers = c.getRegisterNames();
			Set<String> inputs = c.getInputPortNames();
			List<String> _registers = new ArrayList<>(registers);
			List<String> _inputs = new ArrayList<>(inputs);

			List<List<Boolean>> list_regs = new ArrayList<>();
			List<List<Boolean>> list_inputs = new ArrayList<>();

			for(int i = 0; i < registers.size(); i++){
				List<Boolean> tmp = new ArrayList<>();
				tmp.add(true);
				tmp.add(false);
				list_regs.add(tmp);
			}

			for(int i = 0; i < inputs.size(); i++){
				List<Boolean> tmp = new ArrayList<>();
				tmp.add(true);
				tmp.add(false);
				list_inputs.add(tmp);
			}
			list_inputs = cartesianProduct(list_inputs);
			list_regs = cartesianProduct(list_regs);


			for (List<Boolean> ins:list_inputs) {

				for (List<Boolean> regs:list_regs) {
//                int i = 0;
					Map<String,Boolean> new_regs = new HashMap<>();
					Map<String,Boolean> new_ins = new HashMap<>();
					for (int i = 0; i < _inputs.size();i++){
						new_ins.put(_inputs.get(i),ins.get(i));
					}
					for (int i = 0; i < _registers.size();i++){
						new_regs.put(_registers.get(i),regs.get(i));
					}
					res.addAction(new_ins);
					if(all_falseList(regs)){
						res.addInitialState(new Pair<>(new_ins,new_regs));
					}else{
						res.addState(new Pair<>(new_ins,new_regs));
					}

				}
			}



			//AP
			for (String s:registers) {
				res.addAtomicProposition(s);
			}
			for (String s:inputs) {
				res.addAtomicProposition(s);
			}
			for (String s:c.getOutputPortNames()) {
				res.addAtomicProposition(s);
			}



			//->
			for (  Pair<Map<String,Boolean>,Map<String,Boolean>> state:res.getStates()) {
				Map<String,Boolean> previous_input = state.getFirst();
				Map<String,Boolean> previous_regs = state.getSecond();

				Map<String,Boolean> new_regs;
				for(Map<String,Boolean> action : res.getActions()){
					new_regs = c.updateRegisters(previous_input,previous_regs);
					TSTransition<Pair<Map<String,Boolean>,Map<String,Boolean>>,Map<String,Boolean>> transition = new TSTransition(
							state,
							action,
							new Pair<Map<String,Boolean>,Map<String,Boolean>>(action,new_regs)
					);
					res.addTransition(transition);
				}
			}

			Set<Pair<Map<String,Boolean>,Map<String,Boolean>>> new_states = new HashSet<>();
			Set<TSTransition<Pair<Map<String,Boolean>,Map<String,Boolean>>,Map<String,Boolean>>> new_transitions = new HashSet<>();
			new_states = reach(res);
			for (TSTransition<Pair<Map<String,Boolean>,Map<String,Boolean>>,Map<String,Boolean>> trans: res.getTransitions()) {
				if(new_states.contains(trans.getFrom()) && new_states.contains(trans.getTo())){
					new_transitions.add(trans);
				}

			}

			TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>,Map<String, Boolean>,Object> res2= new TransitionSystem();
			res2.addAllStates(new_states);
			for (TSTransition<Pair<Map<String,Boolean>,Map<String,Boolean>>,Map<String,Boolean>> trans: new_transitions) {
				res2.addTransition(trans);
				res2.addAction(trans.getAction());
			}
			for (var init:res.getInitialStates()) {
				res2.addInitialState(init);
			}

			res2.addAllAtomicPropositions(res.getAtomicPropositions());
//L
			for (Pair<Map<String, Boolean>, Map<String, Boolean>> state:res2.getStates()) {
				Map<String,Boolean> in = state.getFirst();
				Map<String,Boolean> regs = state.getSecond();
				Map<String,Boolean> outs = c.computeOutputs(in,regs);

				for (Map.Entry<String,Boolean> input:in.entrySet()) {
					if(input.getValue()){
						res2.addToLabel(state,input.getKey());
					}
				}
				for (Map.Entry<String,Boolean> reg:regs.entrySet()) {
					if(reg.getValue()){
						res2.addToLabel(state,reg.getKey());
					}
				}
				for (Map.Entry<String,Boolean> out:outs.entrySet()) {
					if(out.getValue()){
						res2.addToLabel(state,out.getKey());
					}
				}
			}



			return res2;
		}

		/**
		 * Creates a {@link TransitionSystem} from a program graph.
		 *
		 * @param <L> Type of program graph locations.
		 * @param <A> Type of program graph actions.
		 * @param pg The program graph to be translated into a transition system.
		 * @param actionDefs Defines the effect of each action.
		 * @param conditionDefs Defines the conditions (guards) of the program
		 * graph.
		 * @return A transition system representing {@code pg}.
		 */
		public <L, A> TransitionSystem<Pair<L, Map<String, Object>>, A, String> transitionSystemFromProgramGraph(
				ProgramGraph<L, A> pg, Set<ActionDef> actionDefs, Set<ConditionDef> conditionDefs) {
			TransitionSystem<Pair<L, Map<String, Object>>, A, String> res = new TransitionSystem<>();

			//Act
			for (PGTransition<L,A> transition:pg.getTransitions()) {
				res.addAction(transition.getAction());
				res.addAtomicProposition(transition.getCondition());//AP
			}

			//AP
			for (L location:pg.getLocations()) {
				res.addAtomicProposition(location.toString());
			}

			Set<Map<String, Object>> initial_assignments = new HashSet<>();
			for (List<String> initializer:pg.getInitalizations()) {
				Map<String, Object> initial_assignment = new HashMap<>();
				for (String init_statement: initializer) {
					initial_assignment = ActionDef.effect(actionDefs,initial_assignment, init_statement);
				}
				initial_assignments.add(initial_assignment);
			}

			//I
			Set<Pair<L, Map<String, Object>>> states_with_distance_i = new HashSet<>();
			for (L initial_location:pg.getInitialLocations()) {
				for (Map<String, Object> initial_assignment:initial_assignments) {
					Pair<L,Map<String,Object>> pair = new Pair<L,Map<String,Object>>(initial_location,initial_assignment);
					states_with_distance_i.add(pair);
					res.addInitialState(pair);//I
				}
			}
			res.addAllStates(states_with_distance_i);//I

			//S + ->
			Set<Pair<L, Map<String, Object>>> tmp_states;
			Set<TSTransition<Pair<L,Map<String,Object>>, A>> tmp_transitions;
			while(states_with_distance_i.size() != 0){
				tmp_states = new HashSet<>();
				tmp_transitions = new HashSet<>();

				for (Pair<L,Map<String,Object>> state:states_with_distance_i) {
					for (PGTransition<L,A> transition:pg.getTransitions()) {
						if(state.getFirst().equals(transition.getFrom())){
							if(ConditionDef.evaluate(conditionDefs,state.getSecond(),transition.getCondition())){
								Pair<L,Map<String,Object>> new_state = new Pair(transition.getTo(),ActionDef.effect(actionDefs,state.getSecond(),transition.getAction()));
								TSTransition<Pair<L,Map<String,Object>>,A> ts_transition = new TSTransition(state,transition.getAction(),new_state);
								tmp_states.add(new_state);
								tmp_transitions.add(ts_transition);
							}
						}
					}
				}
				states_with_distance_i = tmp_states;
				states_with_distance_i.removeAll(res.getStates());
				res.addAllStates(states_with_distance_i);
				for(TSTransition<Pair<L,Map<String,Object>>,A> ts:tmp_transitions){
					res.addTransition(ts);
				}

			}


			//L
			for (Pair<L,Map<String,Object>> state:res.getStates()) {
				res.addToLabel(state,state.getFirst().toString());
				for (PGTransition<L,A> transition:pg.getTransitions()) {
					if(ConditionDef.evaluate(conditionDefs,state.getSecond(),transition.getCondition())){
						res.addToLabel(state,transition.getCondition());
					}
				}
			}
			return res;
		}






		/**
		 * Creates a transition system representing channel system {@code cs}.
		 *
		 * @param <L> Type of locations in the channel system.
		 * @param <A> Type of actions in the channel system.
		 * @param cs The channel system to be translated into a transition system.
		 * @return A transition system representing {@code cs}.
		 */
		public <L, A> TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> transitionSystemFromChannelSystem(
				ChannelSystem<L, A> cs ) {
			InterleavingActDef interleaving_act_def = new ParserBasedInterleavingActDef();
			Set<ActionDef> actions = Collections.singleton(new ParserBasedActDef());
			Set<ConditionDef> conditions = Collections.singleton(new ParserBasedCondDef());
			return transitionSystemFromChannelSystem(cs, actions, conditions);
		}

		public <L, A> TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> transitionSystemFromChannelSystem(
				ChannelSystem<L, A> cs, Set<ActionDef> actions, Set<ConditionDef> conditions) {

			TransitionSystem<Pair<List<L>,Map<String,Object>>,A,String> res = new TransitionSystem<>();
			ParserBasedInterleavingActDef interleaving_act_def = new ParserBasedInterleavingActDef();
			Set<ActionDef> interleaving_actiondefs = new HashSet<>();
			interleaving_actiondefs.add(interleaving_act_def);
			//<loc0,0 ; loc0,1 ...  loc0,n>
			List<List<L>> all_locations = new ArrayList<>();
			List< Map<String, Object>> all_all_initializations = new ArrayList<>();

			List<List<List<String>>> all_initializations = new ArrayList<>();
			for(ProgramGraph<L,A> pg : cs.getProgramGraphs()){
				List<L> locations = new ArrayList<L>(pg.getInitialLocations());
				if(pg.getInitalizations().size() != 0){
					List<List<String>> initializations = new ArrayList<>(pg.getInitalizations());
					all_initializations.add(initializations);
				} else {
					List<List<String>> initializations = new ArrayList<>();
					List<String> initialization = new ArrayList<>();

					initialization.add("skip");
					initializations.add(initialization);

					all_initializations.add(initializations);

				}
				all_locations.add(locations);
			}
			all_locations = cartesianProduct(all_locations);
			List<List<List<String>>> cartesian_initializatins = cartesianProduct(all_initializations);

			for (List<List<String>> group_of_initialaizors:cartesian_initializatins) {
				List<String> merged = new ArrayList<String>();
				for (List<String> list:group_of_initialaizors) {
					merged.addAll(list);
				}
				Map<String, Object> eval = new HashMap<>();
				for (String init:merged) {
					eval = ActionDef.effect(actions,eval,init);
				}
				all_all_initializations.add(eval);
			}
			//I
			if(all_all_initializations.size() == 0){

				for (List<L> locations : all_locations) {
					res.addInitialState(new Pair(locations, new HashMap<>()));
				}
			}
			else {
				for (List<L> locations : all_locations) {

					for (Map<String, Object> eval : all_all_initializations) {
						res.addInitialState(new Pair(locations, eval));
					}
				}
			}

			Set<String> _conditions = new HashSet<>();
			//AP
			for(ProgramGraph<L,A> pg : cs.getProgramGraphs()){
				for (L loc:pg.getLocations()) {
					res.addAtomicPropositions(loc.toString());
				}
				for (PGTransition<L,A> transition:pg.getTransitions()) {
					res.addAtomicPropositions(transition.getCondition());
					_conditions.add(transition.getCondition());
				}
			}

			//Act
			for(ProgramGraph<L,A> pg : cs.getProgramGraphs()){
				for (PGTransition<L,A> transition:pg.getTransitions()) {
					res.addAction(transition.getAction());
				}
			}

			Set<Pair<List<L>, Map<String, Object>>> states_at_distance_i = res.getInitialStates();
			Set<Pair<List<L>, Map<String, Object>>> new_states;//this set is for states that was first found in a current specification
			Set<ChannelSystemTransitionWrapper<L,A>> new_normal_transitions;//this set is for new transitions that was creaed a current specification
			Set<ChannelSystemTransitionWrapper<L,A>> new_queue_read_transitions;//this set is for states that was first found in a current specification
			Set<ChannelSystemTransitionWrapper<L,A>> new_queue_write_transitions;//this set is for states that was first found in a current specification

			while (states_at_distance_i.size() != 0){
				new_states = new HashSet<>();
				new_normal_transitions = new HashSet();
				new_queue_write_transitions = new HashSet();
				new_queue_read_transitions = new HashSet();

				for (Pair<List<L>, Map<String, Object>> state:states_at_distance_i) {
					for(int i = 0; i < cs.getProgramGraphs().size(); i++){
						ProgramGraph<L,A> pg = cs.getProgramGraphs().get(i);
						List<L> locations = state.getFirst();
						Map<String, Object> eval = state.getSecond();
						for (PGTransition<L,A> transition:pg.getTransitions()) {
							if(transition.getFrom().equals(locations.get(i)) && ConditionDef.evaluate(conditions,eval,transition.getCondition())){
								ChannelSystemTransitionWrapper<L,A> wrapper = new ChannelSystemTransitionWrapper<L,A>(i,transition,state);
								if (interleaving_act_def.isOneSidedAction_write((String)transition.getAction())) {//this action is  write to queue
									new_queue_write_transitions.add(wrapper);
								}
								else if (interleaving_act_def.isOneSidedAction_read((String)transition.getAction())){//this action is read from queue
									new_queue_read_transitions.add(wrapper);
								}
								else {//normal transition => not queue write or read with capacity==0
									new_normal_transitions.add(wrapper);
								}
							}
						}
					}
				}
				for (ChannelSystemTransitionWrapper<L,A> wrapper:new_normal_transitions) {
					TSTransition<Pair<List<L>, Map<String, Object>>,A> new_trans =  wrapper.get_transition(actions);
					if(new_trans == null){
						continue;
					}
					if(!res.getStates().contains(new_trans.getTo())) {
						new_states.add(new_trans.getTo());
					}
					res.addTransition(new_trans);
				}

				for (ChannelSystemTransitionWrapper wrapper_write : new_queue_write_transitions) {
					for (ChannelSystemTransitionWrapper wrapper_read : new_queue_read_transitions){
						if(wrapper_read.isMatching(wrapper_write,interleaving_act_def)){
							TSTransition<Pair<List<L>, Map<String, Object>>,A> new_trans =  wrapper_read.get_transition_two_sided(wrapper_write,interleaving_act_def);
							if(new_trans == null){
								continue;
							}
							if(!res.getStates().contains(new_trans.getTo())) {
								new_states.add(new_trans.getTo());
							}
							res.addTransition(new_trans);
						}
					}
				}
				states_at_distance_i = new_states;
			}

			for (Pair<List<L>, Map<String, Object>> state:res.getStates()) {
				List<L> left = state.getFirst();
				Map<String, Object> right = state.getSecond();
				for (L loc : left) {
					res.addToLabel(state,loc.toString());
				}
				for(String cond:_conditions){
					if(ConditionDef.evaluate(conditions,right,cond)){
						res.addToLabel(state,cond);
					}
				}
			}
			return res;
		}



		protected <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
			List<List<T>> resultLists = new ArrayList<List<T>>();
			if (lists.size() == 0) {
				resultLists.add(new ArrayList<T>());
				return resultLists;
			} else {
				List<T> firstList = lists.get(0);
				List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
				for (T condition : firstList) {
					for (List<T> remainingList : remainingLists) {
						ArrayList<T> resultList = new ArrayList<T>();
						resultList.add(condition);
						resultList.addAll(remainingList);
						resultLists.add(resultList);
					}
				}
			}
			return resultLists;
		}
		/**
		 * Construct a program graph from nanopromela code.
		 *
		 * @param filename The nanopromela code.
		 * @return A program graph for the given code.
		 * @throws Exception If the code is invalid.
		 */
		public ProgramGraph<String, String> programGraphFromNanoPromela(String filename) throws Exception {
			NanoPromelaParser.StmtContext pnpl = NanoPromelaFileReader.pareseNanoPromelaFile(filename);
			return programGraphFromNanoPromela_StmtContext(pnpl);
		}

		/**
		 * Construct a program graph from nanopromela code.
		 *
		 * @param nanopromela The nanopromela code.
		 * @return A program graph for the given code.
		 * @throws Exception If the code is invalid.
		 */
		public ProgramGraph<String, String> programGraphFromNanoPromelaString(String nanopromela) throws Exception {
			NanoPromelaParser.StmtContext pnpl = NanoPromelaFileReader.pareseNanoPromelaString(nanopromela);
			return programGraphFromNanoPromela_StmtContext(pnpl);
		}

		/**
		 * Construct a program graph from nanopromela code.
		 *
		 * @param inputStream The nanopromela code.
		 * @return A program graph for the given code.
		 * @throws Exception If the code is invalid.
		 */
		public ProgramGraph<String, String> programGraphFromNanoPromela(InputStream inputStream) throws Exception {

			NanoPromelaParser.StmtContext pnpl = NanoPromelaFileReader.parseNanoPromelaStream(inputStream);
			return programGraphFromNanoPromela_StmtContext(pnpl);

		}



		public ProgramGraph<String, String> programGraphFromNanoPromela_StmtContext(NanoPromelaParser.StmtContext np){
			ProgramGraph<String, String> res = new ProgramGraph<>();
			Set<String> locations = new HashSet<>();
			sub(np,locations);
			Set<PGTransition<String,String>> transitions = fix_transitions(locations);
			for (PGTransition<String,String> trans:transitions) {
				res.addTransition(trans);
			}
			res.setInitial(np.getText(),true);
			return res;
		}


		public static Set<String> sub(NanoPromelaParser.StmtContext context, Set<String> locations) {
			if (is_basic(context)) {
				sub_basic(locations, context);
			} else if (context.ifstmt() != null){
				sub_if(locations, context);
			} else if (context.dostmt() != null){
				sub_do(locations, context);
			} else{
				sub_complex(locations, context);
			}
			return locations;
		}
		private static void sub_if(Set<String> locations, NanoPromelaParser.StmtContext context) {

			locations.add(context.getText());
			locations.add("");

			for (NanoPromelaParser.OptionContext option : context.ifstmt().option()){
				sub(option.stmt(), locations);
			}

		}
		private static boolean is_basic(NanoPromelaParser.StmtContext context){
			return (context.assstmt() != null ||
					context.chanwritestmt() != null ||
					context.chanreadstmt() != null ||
					context.skipstmt() != null||
					context.atomicstmt() != null);
		}
		private static void sub_basic(Set<String> locations, NanoPromelaParser.StmtContext context ) {
			locations.add(context.getText());
			locations.add("");
		}

		private static void sub_complex(Set<String> locations, NanoPromelaParser.StmtContext context) {
			Set<String> sub_stmt1 = sub(context.stmt(0), new HashSet<>());
			sub_stmt1.remove("");
			locations.addAll(sub_stmt1);
			NanoPromelaParser.StmtContext stmt2 = context.stmt(1);
			sub(stmt2, locations);
			for (String str : sub_stmt1) {
				locations.add(str + ";" + stmt2.getText());
			}
		}

		private static void sub_do(Set<String> locations, NanoPromelaParser.StmtContext context) {
			locations.add("");
			locations.add(context.getText());

			for (NanoPromelaParser.OptionContext option : context.dostmt().option()) {
				Set<String> temp = new HashSet<>();
				sub(option.stmt(),temp );
				temp.remove("");
				locations.addAll(temp);
				for (String s : temp) {
					locations.add(s + ";" + context.getText());
				}
			}
		}

		private static void add_transitions_basic(Set<PGTransition<String,String>> transitions, NanoPromelaParser.StmtContext context ){

			transitions.add(new PGTransition<String,String>(
					context.getText(),
					"",
					context.skipstmt()!= null?"skip":context.getText(),
					"")
			);
		}


		private static void add_transitions_complex(Set<PGTransition<String,String>> transitions, NanoPromelaParser.StmtContext context ){
			Set<PGTransition<String, String>> tmp = new HashSet<>(transitions);
			for (PGTransition<String, String> transition : tmp) {
				if (transition.getFrom().equals(context.stmt(0).getText())) {
					String to = transition.getTo().equals("") ? context.stmt(1).getText() : (transition.getTo() + ";" + context.stmt(1).getText()) ;
					transitions.add(new PGTransition<>(context.getText(), transition.getCondition(), transition.getAction(), to));
				}
			}

		}
		private static void add_transitions_if(Set<PGTransition<String,String>> transitions, NanoPromelaParser.StmtContext context ){
			Set<PGTransition<String,String>> tmp = new HashSet<>();
			for (NanoPromelaParser.OptionContext option : context.ifstmt().option()) {
				String stmt_tag = option.stmt().getText();
				for (PGTransition<String, String> transition : transitions) {// transition is stmt1 ->(h:alpha) stmt1'
					if (transition.getFrom().equals(stmt_tag)) {
						String condition = transition.getCondition().equals("") ? "(" + option.boolexpr().getText() + ")" : //g
								"(" + option.boolexpr().getText() + ") && (" + transition.getCondition() + ")" ;            //g && h

						tmp.add(new PGTransition<>(
								context.getText(),
								condition,
								transition.getAction(),
								transition.getTo())
						);
					}
				}
			}
			transitions.addAll(tmp);

		}
		private static void add_transitions_do(Set<PGTransition<String,String>> transitions, NanoPromelaParser.StmtContext context ){

			//np condition is satisfied
			Set<String> conditions = new HashSet<>();
			for (NanoPromelaParser.OptionContext option : context.dostmt().option())
				conditions.add("(" + option.boolexpr().getText() + ")");

			transitions.add(new PGTransition<>(
					context.getText(),
					"!(" + String.join("||",conditions) + ")",
					"",
					""));

			Set<PGTransition<String, String>> tmp = new HashSet<>();
			for (NanoPromelaParser.OptionContext option : context.dostmt().option()) {
				for (PGTransition<String, String> transition : transitions) {
					if (option.stmt().getText().equals(transition.getFrom())) {
						String condition = transition.getCondition().equals("") ? "(" + option.boolexpr().getText() + ")" :
								"(" + option.boolexpr().getText() + ") && (" + transition.getCondition() + ")";
						String action = transition.getAction();
						String to = transition.getTo().equals("")? context.getText():
								transition.getTo() + ";" + context.getText();
						tmp.add(new PGTransition<String, String>(context.getText(),condition,action,to));

					}
				}
			}
			transitions.addAll(tmp);
		}

		private static void add_transitions(Set<PGTransition<String,String>> transitions, Set<NanoPromelaParser.StmtContext> contexts){
			for (NanoPromelaParser.StmtContext context: contexts) {
				if (is_basic(context)) {
					add_transitions_basic(transitions,context);
				} else if (context.ifstmt() != null) {
					add_transitions_if( transitions,context);
				} else if (context.dostmt() != null) {
					add_transitions_do(transitions,context);
				} else  {
					add_transitions_complex(  transitions,context);
				}
			}
		}
		private static Set<PGTransition<String,String>> fix_transitions(Set<String> locations){
			Set<NanoPromelaParser.StmtContext> contexts = new HashSet<>();
			for (String loc: locations) {
				if(loc != ""){
					contexts.add(NanoPromelaFileReader.pareseNanoPromelaString(loc));
				}
			}
			Set<PGTransition<String,String>> transitions = new HashSet<>();
			int before=0,after=0;

			do{
				before = transitions.size();
				add_transitions(transitions,contexts);
				after = transitions.size();

			}
			while(before != after);


			return transitions;
		}





		/**
	 * Creates a transition system from a transition system and an automaton.
	 *
	 * @param <Sts> Type of states in the transition system.
	 * @param <Saut> Type of states in the automaton.
	 * @param <A> Type of actions in the transition system.
	 * @param <P> Type of atomic propositions in the transition system, which is
	 * also the type of the automaton alphabet.
	 * @param ts The transition system.
	 * @param aut The automaton.
	 * @return The product of {@code ts} with {@code aut}.
	 */
	public <Sts, Saut, A, P> TransitionSystem<Pair<Sts, Saut>, A, Saut> product(TransitionSystem<Sts, A, P> ts,
																				Automaton<Saut, P> aut) {
		TransitionSystem<Pair<Sts, Saut>, A, Saut> res = new TransitionSystem<>();


		for(Saut state_aut : aut.getInitialStates()){
			for (Sts state_ts : ts.getInitialStates()) {

				Map<Saut, Map<Set<P>, Set<Saut>>> aut_transitions = aut.getTransitions();
				Map<Set<P>, Set<Saut>> state_aut_delta = aut_transitions.get(state_aut);
				if (state_aut_delta == null) continue;
				Set<Saut> all_state_aut_next = state_aut_delta.get(ts.getLabel(state_ts));
				if (all_state_aut_next  == null) continue;
				for (Saut state_aut_next : all_state_aut_next){
					Pair<Sts, Saut> p = new Pair(state_ts,state_aut_next);
					res.addInitialState(p);
					res.addToLabel(p,state_aut_next);
				}
			}
		}

		Map<Saut, Map<Set<P>, Set<Saut>>> aut_transitions = aut.getTransitions();
		for (TSTransition<Sts,A> transitions_ts : ts.getTransitions()){
			Set<P> action_aut = ts.getLabel(transitions_ts.getTo());
			for (Map.Entry<Saut,Map<Set<P>, Set<Saut>>> entry : aut_transitions.entrySet()) {
				Saut q = entry.getKey();
				Map<Set<P>, Set<Saut>> q_delta = entry.getValue();
				Set<Saut> next_of_q_and_alpha = q_delta.get(action_aut);
				if(next_of_q_and_alpha == null) continue;
				for (Saut state_aut:next_of_q_and_alpha){
					Pair<Sts,Saut> from_product = new Pair<Sts,Saut>(transitions_ts.getFrom(),q);
					Pair<Sts,Saut> to_product = new Pair<Sts,Saut>(transitions_ts.getTo(),state_aut);

					TSTransition<Pair<Sts,Saut>,A> transition_product = new TSTransition<Pair<Sts,Saut>,A>(from_product, transitions_ts.getAction(),to_product);

					res.addTransition(transition_product);
					res.addToLabel(from_product,q);
					res.addToLabel(to_product,state_aut);

				}

			}
		}

		Set<Pair<Sts, Saut>> reachable = reach(res);
		Set<TSTransition<Pair<Sts,Saut>,A>> to_delete = new HashSet<>();
		for(TSTransition<Pair<Sts,Saut>,A> ts_transition : res.getTransitions()){
			if(!(reachable.contains( ts_transition.getFrom()) && reachable.contains( ts_transition.getTo()) )){
				to_delete.add(ts_transition);
			}
		}

		for (TSTransition<Pair<Sts,Saut>,A> t : to_delete) {
			res.removeTransition(t);
		}
		return res;
	}

	/**
	 * Verify that a system satisfies an omega regular property.
	 *
	 * @param <S> Type of states in the transition system.
	 * @param <Saut> Type of states in the automaton.
	 * @param <A> Type of actions in the transition system.
	 * @param <P> Type of atomic propositions in the transition system, which is
	 * also the type of the automaton alphabet.
	 * @param ts The transition system.
	 * @param aut A Büchi automaton for the words that do not satisfy the
	 * property.
	 * @return A VerificationSucceeded object or a VerificationFailed object
	 * with a counterexample.
	 */
	public <S, A, P, Saut> VerificationResult<S> verifyAnOmegaRegularProperty(TransitionSystem<S, A, P> ts,
																			  Automaton<Saut, P> aut) {
		TransitionSystem<Pair<S, Saut>, A, Saut> product_ts = product(ts,aut);
		Set<Pair<S,Saut>> get_not_phi= get_not_phi_states(product_ts,aut.getAcceptingStates());
//		System.out.println("get_not_phi :" + get_not_phi);
		for(Pair<S,Saut> s : get_not_phi){
			List<Pair<S,Saut>> w = ReachFromInitialState(product_ts,s);
//			System.out.println("W :" + w);
			List<Pair<S,Saut>> v = ReachFrom(product_ts,s,s);

//			System.out.println("V :" + v);

			if(v != null && w != null && !v.isEmpty() && !w.isEmpty()){
				List<S> prefix = new ArrayList<>();
				List<S> cycle = new ArrayList<>();

				for (int i =0 ; i < w.size() ; i++){
					prefix.add(w.get(i).first);
				}
				for (int i =0 ; i < v.size() ; i++){
					cycle.add(v.get(i).first);
				}

				VerificationFailed<S> res =  new VerificationFailed();
				res.setPrefix(prefix);
				res.setCycle(cycle);
				return res;
			}

		}
		return new VerificationSucceeded();
	}

	public <S, A, P, Saut> List<Pair<S, Saut>> ReachFromInitialState(TransitionSystem<Pair<S, Saut>, A, Saut> product_ts,Pair<S, Saut> to){
		Set<Pair<S,Saut>> init = product_ts.getInitialStates();
		List<Pair<S,Saut>> res;
		for (Pair<S,Saut> i: init) {
			res = ReachFrom(product_ts,i,to);
			if(res != null) return res;
		}
		return null;
	}

	public <S, A, P, Saut> boolean ReachFrom_helper(TransitionSystem<Pair<S, Saut>, A, Saut> product_ts,
													Pair<S, Saut> from,
													Pair<S, Saut> to,
													Stack<Pair<S, Saut>> path){
		Set<Pair<S, Saut>> _post = post(product_ts,from);
		List<Pair<S, Saut>> to_remove = new ArrayList<>(path);
		to_remove.remove(to);
		_post.removeAll(to_remove);
		System.out.println("_post : " + _post);

		for (Pair<S, Saut> p:_post) {
			if(p.equals(to)){
				path.push(to);
				return true;
			}
			path.push(p);
			if(ReachFrom_helper(product_ts,p,to,path)){
				return true;
			}
			path.pop();
		}
		return false;
	}

	public <S, A, P, Saut> List<Pair<S, Saut>> ReachFrom(TransitionSystem<Pair<S, Saut>, A, Saut> product_ts,Pair<S, Saut> from,Pair<S, Saut> to){
		Stack<Pair<S, Saut>> res = new Stack<>();
		res.add(from);

		if(ReachFrom_helper(product_ts,from,to,res)){
			return res;
		} else {
			return null;
		}
	}

	public <S, A, P, Saut> Set<Pair<S,Saut>> get_not_phi_states(TransitionSystem<Pair<S, Saut>, A, Saut> product_ts,Set<Saut> accepting){
		Set<Pair<S,Saut>> res = new HashSet<>();
		Stack<Pair<S, Saut>> open_list = new Stack();
		Set<Pair<S,Saut>> visited = new HashSet<>();
		visited.addAll(product_ts.getInitialStates());
		open_list.addAll(product_ts.getInitialStates());
		while(!open_list.isEmpty()){
			Pair<S, Saut> state_product = open_list.pop();
			if(accepting.contains(state_product.getSecond())){ // found a state that satisfies not phi
				res.add(state_product);
			}
			Set<Pair<S,Saut>> sons = post(product_ts,state_product);
			visited.add(state_product);
			sons.removeAll(visited);
			for (var son:sons) {
				open_list.push(son);
			}
		}
		return res;

	}


	/**
	 * A translation of a Generalized Büchi Automaton (GNBA) to a Nondeterministic
	 * Büchi Automaton (NBA).
	 *
	 * @param <L>    Type of resultant automaton transition alphabet
	 * @param mulAut An automaton with a set of accepting states (colors).
	 * @return An equivalent automaton with a single set of accepting states.
	 */
	public <L> MultiColorAutomaton<?, L> duplicate(MultiColorAutomaton<?, L> mulAut, int color) {
		MultiColorAutomaton<? super Object, L> result = new MultiColorAutomaton<>();

		/**** Clone Transitions ****/
		for (var entry : mulAut.getTransitions().entrySet()) {
			// <state,<Set<L>,Set<state>>>
			var source = new Pair<>(entry.getKey(), color);// entry.getKey();
			for (var sym : entry.getValue().entrySet()) {
				var symbol = sym.getKey();
				for (var destination : sym.getValue()) {
					result.addTransition(source, symbol, new Pair(destination, color));
				}
			}
		}

		/*** init states ***/
		for (var s : mulAut.getInitialStates()) {
			result.setInitial(new Pair<>(s, color));
		}

		/*** colors acceptance **/
		for (var s : mulAut.getAcceptingStates(color)) {
			result.setAccepting(new Pair<>(s, color), color);

		}

		return result;
	}

	public <L> Map<Integer, MultiColorAutomaton<?, L>> duplicate(MultiColorAutomaton<?, L> mulAut) {
		Map<Integer, MultiColorAutomaton<?, L>> lst = new HashMap<Integer, MultiColorAutomaton<?, L>>();
		for (var color : mulAut.getColors()) {
			lst.put(color, duplicate(mulAut, color));
		}
		return lst;

	}

	public <L> Automaton<?, L> GNBA2NBA(MultiColorAutomaton<?, L> mulAut) {

		if (mulAut.getColors().size() == 0) {
			Automaton<? super Object, L> npa = new Automaton<>();

			for (var entry : mulAut.getTransitions().entrySet()) {
				var source = entry.getKey();
				npa.setAccepting(new Pair<>(source, 0));
				for (var sym : entry.getValue().entrySet()) {
					for (var destination : sym.getValue()) {
						npa.addTransition(new Pair<>(source, 0), sym.getKey(), new Pair(destination, 0));
					}
				}
			}

			return npa;

		}

		Map<Integer, MultiColorAutomaton<?, L>> a = duplicate(mulAut);

		Automaton<? super Object, L> result = new Automaton<>();

		for (var color : mulAut.getColors()) {
			MultiColorAutomaton<?, L> nb = a.get(color);

			for (var entry : nb.getTransitions().entrySet()) {
				// <state,<Set<L>,Set<state>>>
				Pair source = (Pair) entry.getKey();

				// its colored
				if (nb.getAcceptingStates(color).contains(source)) {
					for (var sym : entry.getValue().entrySet()) {

						var next_des = a.get((color + 1) % mulAut.getColors().size()).getTransitions()
								.get(new Pair(source.first, (color + 1) % mulAut.getColors().size())).get(sym.getKey());

						for (var destination : next_des) {
							result.addTransition(source, sym.getKey(), destination);

						}
					}
				} else // not colored
				{
					for (var sym : entry.getValue().entrySet()) {
						for (var destination : sym.getValue()) {
							result.addTransition(source, sym.getKey(), destination);
						}
					}
				}

			}

		}

		/**** init state ***/
		for (var s : mulAut.getInitialStates()) {
			result.setInitial(new Pair(s, 0));
		}

		/**** acceptance states ****/

		for (var s : mulAut.getAcceptingStates(0)) {
			result.setAccepting(new Pair(s, 0));
		}

		return result;
	}

	/**
	 * Translation of Linear Temporal Logic (LTL) formula to a Nondeterministic
	 * Büchi Automaton (NBA).
	 *
	 * @param <L> Type of resultant automaton transition alphabet
	 * @param ltl The LTL formula represented as a parse-tree.
	 * @return An automaton A such that L_\omega(A)=Words(ltl)
	 */

	public <L> void Subs(LTL<L> ltl, Set<LTL<L>> st) {

		if (st.contains(ltl) == false)
			st.add(ltl);
		if ((ltl instanceof Not) == false)
			st.add(new Not<L>(ltl));

		if (ltl instanceof AP) {
			return;
		}

		if (ltl instanceof Until) {
			Subs(((Until<L>) ltl).getLeft(), st);
			Subs(((Until<L>) ltl).getRight(), st);
			return;
		}

		if (ltl instanceof Not) {
			Subs(((Not<L>) ltl).getInner(), st);
			return;
		}

		if (ltl instanceof And) {
			Subs(((And<L>) ltl).getLeft(), st);
			Subs(((And<L>) ltl).getRight(), st);
			return;
		}

		if (ltl instanceof Next) {
			Subs(((Next<L>) ltl).getInner(), st);
			return;
		}
		if (ltl instanceof TRUE) {
			return;
		}

	}

	public <L> boolean isConsistent(Set<LTL<L>> sub, int length) {

		if (sub.size() * 2 != length)
			return false;

		// maximum
		for (var e : sub) {
			if (sub.contains(LTL.not(e)))
				return false;
		}

		// local Consistent
		for (var e : sub) {
			if (e instanceof Until) {
				var y2 = ((Until<L>) e).getRight();
				var y1 = ((Until<L>) e).getLeft();
				if (sub.contains(y2) == false && sub.contains(y1) == false)
					return false;
			} else if (e instanceof Not)
				if (((Not<L>) e).getInner() instanceof Until) {
					var e_tag = ((Not<L>) e).getInner();

					var y2 = ((Until<L>) e_tag).getRight();

					if (sub.contains(y2))
						return false;
				}
		}

		// Logical Consistent
		for (var e : sub) {
			if (e instanceof And) {
				var y1 = ((And<L>) e).getLeft();
				var y2 = ((And<L>) e).getRight();
				if ((sub.contains(y1) && sub.contains(y2)) == false)
					return false;
			}

			if (e instanceof Not)
				if (((Not<L>) e).getInner() instanceof And) {
					var n = ((And<L>) ((Not<L>) e).getInner());
					var y1 = n.getLeft();
					var y2 = n.getRight();
					if ((sub.contains(y1) && sub.contains(y2)))
						return false;
				}

			if (sub.contains(LTL.not(e)))
				return false;

		}

		return true;
	}

	public <L> Pair<Set<Set<LTL<L>>>, Set<AP<L>>> States(LTL<L> ltl) {

		var subs = new HashSet<LTL<L>>();
		Subs(ltl, subs);

		Set<AP<L>> AP = new HashSet<AP<L>>();
		for (var p : subs) {
			if (p instanceof AP)
				if (AP.contains(p) == false)
					AP.add((AP<L>) p);
		}

		Set<Set<LTL<L>>> pow_subs = Util.powerSet(subs);

		Set<Set<LTL<L>>> states = new HashSet<Set<LTL<L>>>();
		for (var sub : pow_subs) {
			if (isConsistent(sub, subs.size()))
				states.add(sub);
		}

		return new Pair<>(states, AP);

	}

	public <L> Set<L> getAP(Set<LTL<L>> state, Set<AP<L>> AP) {
		Set<L> c = new HashSet<>();
		for (var e : AP) {
			if (state.contains(e))
				c.add(e.getName());

		}
		return c;
	}

	public <L> void LTL_GNBA_BUILD(Pair<Set<Set<LTL<L>>>, Set<AP<L>>> states_AP,
			MultiColorAutomaton<Set<LTL<L>>, L> gnba, LTL<L> ltl) {

		var states = states_AP.first;

		System.out.println("\n ********* states ***********/");
		for (var s : states) {
			System.out.println(s);
		}
		System.out.println(" \n ********* states number : "+states.size()  +"\n\n\n");
		var AP = states_AP.second;

		MultiColorAutomaton<Set<LTL<L>>, L> temp_gnba = new MultiColorAutomaton<>();

		// Add ALL Transations && Set All States As Acceptance
		for (var b : states) {
			for (var b_tag : states)
				temp_gnba.addTransition(b, getAP(b, AP), b_tag);
		}
		int t = 0;
		// Remove - illegal transations

		for (var trans : temp_gnba.getTransitions().entrySet()) {
			var src = trans.getKey();
			// initialization
			if (src.contains(ltl))
				gnba.setInitial(src);

			// Transitions
			for (var ap : trans.getValue().entrySet())
				for (var des : ap.getValue()) {
					boolean isOK = true;
					for (var e : src) {
						// Until
						if (e instanceof Until) {
							var y2 = ((Until<L>) e).getRight();
							if (src.contains(y2) == false && des.contains(e) == false)
								isOK = false;
						}
						// not Until
						if (e instanceof Not)
							if (((Not<L>) e).getInner() instanceof Until) {
								var until = ((Until<L>) (((Not<L>) e).getInner()));
								var y1 = until.getLeft();
								if (src.contains(y1) && des.contains(until))
									isOK = false;
							}

						// Oalpha Next LTL - 1
						if (e instanceof Next)
							if (des.contains(((Next<L>) e).getInner()) == false)
								isOK = false;

						// Oalpha Next LTL - 2
						if (e instanceof Not)
							if (((Not<L>) e).getInner() instanceof Next)
								if (des.contains(((Next<L>) ((Not<L>) e).getInner()).getInner()))
									isOK = false;

					}

					if (isOK) {
						gnba.addTransition(src, ap.getKey(), des);
						t++;
					}
				}
		}
		System.out.println("*************Transitions number :" + t);

		// Acceptance states
		int colors = 0;
		var subs = new HashSet<LTL<L>>();
		Subs(ltl, subs);
		var untiles = new HashSet<Pair<Until<L>, Integer>>();
		for (var s : subs)
			if (s instanceof Until)
				untiles.add(new Pair<>((Until<L>) s, colors++));

		for (var s : states) {

			for (var un : untiles)
				if (s.contains(LTL.not(un.first)))
					gnba.setAccepting(s, un.second);
				else if (s.contains(un.first.getRight()))
					gnba.setAccepting(s, un.second);
		}

	}

	public <L> Automaton<?, L> LTL2NBA(LTL<L> ltl) {
		MultiColorAutomaton<Set<LTL<L>>, L> gnba = new MultiColorAutomaton<>();

		Pair<Set<Set<LTL<L>>>, Set<AP<L>>> States_AP = States(ltl);

		LTL_GNBA_BUILD(States_AP, gnba, ltl);

		Util.printColoredAutomatonTransitions(gnba);

		System.out.println("\n*****AcceptingStates color 1*******\n");
		for (var s : gnba.getAcceptingStates(1)) {
			System.out.println(s);
		}
		System.out.println("\n************\n");

		System.out.println("\n*****AcceptingStates color 0*******\n");
		for (var s : gnba.getAcceptingStates(1)) {
			System.out.println(s);
		}
		System.out.println("\n************\n");

		return GNBA2NBA(gnba);
	}



	/**
	 * Verify that a system satisfies an LTL formula under fairness conditions.
	 * @param ts Transition system
	 * @param fc Fairness condition
	 * @param ltl An LTL formula
	 * @param <S>  Type of states in the transition system
	 * @param <A> Type of actions in the transition system
	 * @param <P> Type of atomic propositions in the transition system
	 * @return a VerificationSucceeded object or a VerificationFailed object with a counterexample.
	 */
	public <S, A, P> VerificationResult<S> verifyFairLTLFormula(TransitionSystem<S, A, P> ts, FairnessCondition<A> fc, LTL<P> ltl){
		TransitionSystem<Pair<S,A>, A, ComposedAtomicProposition<P,A>> TSf = build_TSf(ts);


		System.out.println("TSf :" + TSf);
		LTL<ComposedAtomicProposition<P,A>> ltlf = build_LTLf(fc);
		LTL<ComposedAtomicProposition<P,A>> final_ltl = LTL.and(ltlf,LTL.not(Convert(ltl)));//A->B = -AUB = -(A^-B) ,we want -(a->b) = (A^-B)
		final_ltl = Optimize(final_ltl);
		System.out.println("final_ltl :" + final_ltl);
		Automaton<?, ComposedAtomicProposition<P,A>> automata = LTL2NBA(final_ltl);
		System.out.println("automata :" + automata);

		VerificationResult<Pair<S,A>> res = verifyAnOmegaRegularProperty(TSf,automata);
		if(res instanceof VerificationSucceeded){
			return new VerificationSucceeded<S>();
		}else if(res instanceof VerificationFailed){
			VerificationFailed<S> res2 = new VerificationFailed<>();
			List<S> prefix = new ArrayList<>();
			List<S> cycle = new ArrayList<>();
			for(Pair<S,A> state : ((VerificationFailed<Pair<S,A>>) res).getPrefix()){
				prefix.add(state.getFirst());
			}
			for(Pair<S,A> state : ((VerificationFailed<Pair<S,A>>) res).getCycle()){
				cycle.add(state.getFirst());
			}
			res2.setCycle(cycle);
			res2.setCycle(prefix);
			return res2;

		}else {
			return null;
		}
	}

	private static <P, A> LTL<ComposedAtomicProposition<P,A>> build_LTLf(FairnessCondition<A> fc) {
		LTL<ComposedAtomicProposition<P,A>> uncond_ltl = new TRUE();
		LTL<ComposedAtomicProposition<P,A>> strong_ltl = new TRUE();
		LTL<ComposedAtomicProposition<P,A>> weak_ltl = new TRUE();




		for (Set<A> action_set:fc.getUnconditional()) {
			List<LTL<ComposedAtomicProposition<P,A>>> triggered = new ArrayList<>();
			for(A action : action_set){
				triggered.add(new AP<ComposedAtomicProposition<P,A>>(new TriggeredAtomicProposotion<>(action)));
			}
			uncond_ltl = LTL.and(uncond_ltl,always(eventualy(Or(triggered))));
		}


		for (Set<A> action_set:fc.getStrong()) {
			List<LTL<ComposedAtomicProposition<P,A>>> enabled = new ArrayList<>();
			for(A action : action_set){
				enabled.add(new AP<ComposedAtomicProposition<P,A>>(new EnabledAtomicProposition<>(action)));
			}

			List<LTL<ComposedAtomicProposition<P,A>>> triggered = new ArrayList<>();
			for(A action : action_set){
				triggered.add(new AP<ComposedAtomicProposition<P,A>>(new TriggeredAtomicProposotion<>(action)));
			}
			LTL<ComposedAtomicProposition<P,A>> always_eventually_first = always(eventualy(Or(enabled)));
//			LTL<ComposedAtomicProposition<P,A>> not_always_eventually = LTL.not(always_eventually_first);

			LTL<ComposedAtomicProposition<P,A>> always_eventually_second = always(eventualy(Or(triggered)));
			LTL<ComposedAtomicProposition<P,A>> not_always_eventually = LTL.not(always_eventually_second);

			strong_ltl = LTL.and(strong_ltl,LTL.and(always_eventually_first,not_always_eventually));
		}


		for (Set<A> action_set:fc.getWeak()) {
			List<LTL<ComposedAtomicProposition<P,A>>> enabled = new ArrayList<>();
			for(A action : action_set){
				enabled.add(new AP<ComposedAtomicProposition<P,A>>(new EnabledAtomicProposition<>(action)));
			}

			List<LTL<ComposedAtomicProposition<P,A>>> triggered = new ArrayList<>();
			for(A action : action_set){
				triggered.add(new AP<ComposedAtomicProposition<P,A>>(new TriggeredAtomicProposotion<>(action)));
			}
			LTL<ComposedAtomicProposition<P,A>> always_eventually_first = eventualy(always(Or(enabled)));
//			LTL<ComposedAtomicProposition<P,A>> not_always_eventually = LTL.not(always_eventually_first);

			LTL<ComposedAtomicProposition<P,A>> always_eventually_second = always(eventualy(Or(triggered)));
			LTL<ComposedAtomicProposition<P,A>> not_always_eventually = LTL.not(always_eventually_second);

			weak_ltl = LTL.and(weak_ltl,LTL.and(always_eventually_first,not_always_eventually));
		}



		return Optimize(LTL.and(uncond_ltl,LTL.and(strong_ltl,weak_ltl)));
	}


	private  static <X> LTL<X> Optimize(LTL<X> ltl){
		if(ltl instanceof And){
			And ltl_and = (And)ltl;
			if (ltl_and.getLeft() instanceof TRUE){
				return Optimize(ltl_and.getRight());
			} else if (ltl_and.getRight() instanceof TRUE){
				return Optimize(ltl_and.getLeft());
			} else {
				return LTL.and(Optimize(ltl_and.getLeft()),Optimize(ltl_and.getRight()));
			}

		} else if (ltl instanceof Until){
			Until ltl_until = (Until)ltl;
			if(ltl_until.getRight() instanceof TRUE){
				return new TRUE();
			} else {
				return LTL.until(Optimize(ltl_until.getLeft()),Optimize(ltl_until.getRight()));
			}

		} else if (ltl instanceof Not){
			Not ltl_until = (Not)ltl;
			if(ltl_until.getInner() instanceof Not){
				return  ((Not) ltl_until.getInner()).getInner();
			} else {
				return LTL.not(Optimize(ltl_until.getInner()));
			}

		} else if (ltl instanceof Next){
			return LTL.next(Optimize(((Next)ltl).getInner()));
		} else if (ltl instanceof AP){
			return ltl;
		} else {
			return ltl;
		}

	}
	private  static <X,A> LTL<ComposedAtomicProposition<X,A>> Convert(LTL<X> ltl){
		if(ltl instanceof And){
			And ltl_and = (And)ltl;
			return LTL.and(Convert(ltl_and.getLeft()),Convert(ltl_and.getRight()));
		} else if (ltl instanceof Until){
			Until ltl_until = (Until)ltl;
			return LTL.until(Convert(ltl_until.getLeft()),Convert(ltl_until.getRight()));
		} else if (ltl instanceof Not){
			Not ltl_not = (Not)ltl;
			return LTL.not(Convert(ltl_not.getInner()));
		} else if (ltl instanceof Next){
			return LTL.next(Convert(((Next)ltl).getInner()));
		} else if (ltl instanceof AP){
			return new AP(new OriginalAtomicProposition<>(((AP)ltl).getName()));
		} else if (ltl instanceof TRUE){
			return new TRUE();
		} else {
			throw new IllegalArgumentException("Unknown type : ("+ltl+")");
		}
	}

	public  static <X> LTL<X> eventualy(LTL<X> ltl){
		return LTL.until(new TRUE(),ltl);
	}

	public  static <X> LTL<X> always(LTL<X> ltl){
		return LTL.not(eventualy(LTL.not(ltl)));
	}

	private  static <X> LTL<X> Or(List<LTL<X>> ltls){
		List<LTL<X>> list = new ArrayList<>();
		for(LTL<X> ltl:ltls){
			list.add(LTL.not(ltl));
		}
		return LTL.not(And(list));
	}

	private  static <X> LTL<X> And(List<LTL<X>> ltls){
		if(ltls.isEmpty()){
			return new TRUE<>();
		}
		LTL<X> acc = ltls.get(0);
		for(int i = 1; i < ltls.size(); i++){
			acc = LTL.and(acc,ltls.get(i));
		}
		return acc;
	}

	public <S, A, P>  TransitionSystem<Pair<S,A>, A, ComposedAtomicProposition<P,A>> build_TSf(TransitionSystem<S, A, P> ts){
		TransitionSystem<Pair<S,A>, A, ComposedAtomicProposition<P,A>> res = new TransitionSystem<>();

		//S
		for(S state:ts.getStates()){
			for(A action : ts.getActions()){
				res.addState(new Pair(state,action));
			}
		}

		//Act
		res.addAllActions(ts.getActions());

		//I
		for(S state:ts.getInitialStates()){
			for(A action : ts.getActions()){
				res.addInitialState(new Pair(state,action));
			}
		}

		//AP
		for(P atomic : ts.getAtomicPropositions()) {
			res.addAtomicProposition(new OriginalAtomicProposition<>(atomic));
		}
		for(A action : ts.getActions()){
			res.addAtomicProposition(new TriggeredAtomicProposotion<>(action));
			res.addAtomicProposition(new EnabledAtomicProposition<>(action));

		}

		//->
		for(TSTransition<S,A> transition : ts.getTransitions()){

			Pair<S,A> new_to = new Pair(transition.getTo(),transition.getAction());
			for(A action : ts.getActions()){
				Pair<S,A> new_from = new Pair(transition.getFrom(),action);
				TSTransition<Pair<S,A>,A> new_transition = new TSTransition<>(new_from,transition.getAction(),new_to);
				res.addTransition(new_transition);
			}

		}

		//L
		for(Pair<S,A> state : res.getStates()){

			//original
			for(P atomic : ts.getLabel(state.getFirst())){
				res.addToLabel(state,new OriginalAtomicProposition<P,A>(atomic));
			}

			//triggered
			res.addToLabel(state,new TriggeredAtomicProposotion<P,A>(state.getSecond()));

			//enabled
			for(A action : ts.getActions()){
				if(!post(ts,state.getFirst(),action).isEmpty()){
					res.addToLabel(state,new EnabledAtomicProposition<P,A>(action));
				}
			}
		}
		return res;
	}
}


