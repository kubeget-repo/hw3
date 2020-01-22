package il.ac.bgu.cs.formalmethodsintro.base;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import il.ac.bgu.cs.formalmethodsintro.base.automata.Automaton;
import il.ac.bgu.cs.formalmethodsintro.base.automata.MultiColorAutomaton;
import il.ac.bgu.cs.formalmethodsintro.base.channelsystem.ChannelSystem;
import il.ac.bgu.cs.formalmethodsintro.base.circuits.Circuit;
import il.ac.bgu.cs.formalmethodsintro.base.exceptions.StateNotFoundException;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.AP;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.And;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.Next;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.Not;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.TRUE;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.Until;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.ActionDef;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.ConditionDef;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.ParserBasedActDef;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.ParserBasedCondDef;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.ProgramGraph;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.AlternatingSequence;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.formalmethodsintro.base.util.Pair;
import il.ac.bgu.cs.formalmethodsintro.base.util.Util;
import il.ac.bgu.cs.formalmethodsintro.base.verification.VerificationResult;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Interface for the entry point class to the HW in this class. Our
 * client/testing code interfaces with the student solutions through this
 * interface only. <br>
 * More about facade: {@linkplain http://www.vincehuston.org/dp/facade.html}.
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
	 * Checks whether a transition system is action deterministic. I.e., if for any
	 * given p and α there exists only a single tuple (p,α,q) in →. Note that this
	 * must be true even for non-reachable states.
	 *
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param <P> Type of atomic propositions.
	 * @param ts  The transition system being tested.
	 * @return {@code true} iff the action is deterministic.
	 */
	public <S, A, P> boolean isActionDeterministic(TransitionSystem<S, A, P> ts) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Checks whether an action is ap-deterministic (as defined in class), in the
	 * context of a given {@link TransitionSystem}.
	 *
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param <P> Type of atomic propositions.
	 * @param ts  The transition system being tested.
	 * @return {@code true} iff the action is ap-deterministic.
	 */
	public <S, A, P> boolean isAPDeterministic(TransitionSystem<S, A, P> ts) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Checks whether an alternating sequence is an execution of a
	 * {@link TransitionSystem}, as defined in class.
	 *
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param <P> Type of atomic propositions.
	 * @param ts  The transition system being tested.
	 * @param e   The sequence that may or may not be an execution of {@code ts}.
	 * @return {@code true} iff {@code e} is an execution of {@code ts}.
	 */
	public <S, A, P> boolean isExecution(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Checks whether an alternating sequence is an execution fragment of a
	 * {@link TransitionSystem}, as defined in class.
	 *
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param <P> Type of atomic propositions.
	 * @param ts  The transition system being tested.
	 * @param e   The sequence that may or may not be an execution fragment of
	 *            {@code ts}.
	 * @return {@code true} iff {@code e} is an execution fragment of {@code ts}.
	 */
	public <S, A, P> boolean isExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Checks whether an alternating sequence is an initial execution fragment of a
	 * {@link TransitionSystem}, as defined in class.
	 *
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param <P> Type of atomic propositions.
	 * @param ts  The transition system being tested.
	 * @param e   The sequence that may or may not be an initial execution fragment
	 *            of {@code ts}.
	 * @return {@code true} iff {@code e} is an execution fragment of {@code ts}.
	 */
	public <S, A, P> boolean isInitialExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Checks whether an alternating sequence is a maximal execution fragment of a
	 * {@link TransitionSystem}, as defined in class.
	 *
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param <P> Type of atomic propositions.
	 * @param ts  The transition system being tested.
	 * @param e   The sequence that may or may not be a maximal execution fragment
	 *            of {@code ts}.
	 * @return {@code true} iff {@code e} is a maximal fragment of {@code ts}.
	 */
	public <S, A, P> boolean isMaximalExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Checks whether a state in {@code ts} is terminal.
	 *
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param ts  Transition system of {@code s}.
	 * @param s   The state being tested for terminality.
	 * @return {@code true} iff state {@code s} is terminal in {@code ts}.
	 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
	 */
	public <S, A> boolean isStateTerminal(TransitionSystem<S, A, ?> ts, S s) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * @param <S> Type of states.
	 * @param ts  Transition system of {@code s}.
	 * @param s   A state in {@code ts}.
	 * @return All the states in {@code Post(s)}, in the context of {@code ts}.
	 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
	 */
	public <S> Set<S> post(TransitionSystem<S, ?, ?> ts, S s) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * @param <S> Type of states.
	 * @param ts  Transition system of {@code s}.
	 * @param c   States in {@code ts}.
	 * @return All the states in {@code Post(s)} where {@code s} is a member of
	 *         {@code c}, in the context of {@code ts}.
	 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
	 */
	public <S> Set<S> post(TransitionSystem<S, ?, ?> ts, Set<S> c) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param ts  Transition system of {@code s}.
	 * @param s   A state in {@code ts}.
	 * @param a   An action.
	 * @return All the states that {@code ts} might transition to from {@code s},
	 *         when action {@code a} is selected.
	 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
	 */
	public <S, A> Set<S> post(TransitionSystem<S, A, ?> ts, S s, A a) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param ts  Transition system of {@code s}.
	 * @param c   Set of states in {@code ts}.
	 * @param a   An action.
	 * @return All the states that {@code ts} might transition to from any state in
	 *         {@code c}, when action {@code a} is selected.
	 */
	public <S, A> Set<S> post(TransitionSystem<S, A, ?> ts, Set<S> c, A a) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * @param <S> Type of states.
	 * @param ts  Transition system of {@code s}.
	 * @param s   A state in {@code ts}.
	 * @return All the states in {@code Pre(s)}, in the context of {@code ts}.
	 */
	public <S> Set<S> pre(TransitionSystem<S, ?, ?> ts, S s) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * @param <S> Type of states.
	 * @param ts  Transition system of {@code s}.
	 * @param c   States in {@code ts}.
	 * @return All the states in {@code Pre(s)} where {@code s} is a member of
	 *         {@code c}, in the context of {@code ts}.
	 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
	 */
	public <S> Set<S> pre(TransitionSystem<S, ?, ?> ts, Set<S> c) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param ts  Transition system of {@code s}.
	 * @param s   A state in {@code ts}.
	 * @param a   An action.
	 * @return All the states that {@code ts} might transitioned from, when in
	 *         {@code s}, and the last action was {@code a}.
	 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
	 */
	public <S, A> Set<S> pre(TransitionSystem<S, A, ?> ts, S s, A a) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param ts  Transition system of {@code s}.
	 * @param c   Set of states in {@code ts}.
	 * @param a   An action.
	 * @return All the states that {@code ts} might transitioned from, when in any
	 *         state in {@code c}, and the last action was {@code a}.
	 * @throws StateNotFoundException if {@code s} is not a state of {@code ts}.
	 */
	public <S, A> Set<S> pre(TransitionSystem<S, A, ?> ts, Set<S> c, A a) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Implements the {@code reach(TS)} function.
	 *
	 * @param <S> Type of states.
	 * @param <A> Type of actions.
	 * @param ts  Transition system of {@code s}.
	 * @return All states reachable in {@code ts}.
	 */
	public <S, A> Set<S> reach(TransitionSystem<S, A, ?> ts) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Compute the synchronous product of two transition systems.
	 *
	 * @param <S1> Type of states in the first system.
	 * @param <S2> Type of states in the first system.
	 * @param <A>  Type of actions (in both systems).
	 * @param <P>  Type of atomic propositions (in both systems).
	 * @param ts1  The first transition system.
	 * @param ts2  The second transition system.
	 *
	 * @return A transition system that represents the product of the two.
	 */
	public <S1, S2, A, P> TransitionSystem<Pair<S1, S2>, A, P> interleave(TransitionSystem<S1, A, P> ts1,
			TransitionSystem<S2, A, P> ts2) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Compute the synchronous product of two transition systems.
	 *
	 * @param <S1>               Type of states in the first system.
	 * @param <S2>               Type of states in the first system.
	 * @param <A>                Type of actions (in both systems).
	 * @param <P>                Type of atomic propositions (in both systems).
	 * @param ts1                The first transition system.
	 * @param ts2                The second transition system.
	 * @param handShakingActions Set of actions both systems perform together.
	 *
	 * @return A transition system that represents the product of the two.
	 */
	public <S1, S2, A, P> TransitionSystem<Pair<S1, S2>, A, P> interleave(TransitionSystem<S1, A, P> ts1,
			TransitionSystem<S2, A, P> ts2, Set<A> handShakingActions) {
		throw new java.lang.UnsupportedOperationException();
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
	 * @param <A>  Type of actions in BOTH GRAPHS.
	 * @param pg1  The first program graph.
	 * @param pg2  The second program graph.
	 * @return Interleaved program graph.
	 */
	public <L1, L2, A> ProgramGraph<Pair<L1, L2>, A> interleave(ProgramGraph<L1, A> pg1, ProgramGraph<L2, A> pg2) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Creates a {@link TransitionSystem} representing the passed circuit.
	 *
	 * @param c The circuit to translate into a {@link TransitionSystem}.
	 * @return A {@link TransitionSystem} representing {@code c}.
	 */
	public TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>, Map<String, Boolean>, Object> transitionSystemFromCircuit(
			Circuit c) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Creates a {@link TransitionSystem} from a program graph.
	 *
	 * @param <L>           Type of program graph locations.
	 * @param <A>           Type of program graph actions.
	 * @param pg            The program graph to be translated into a transition
	 *                      system.
	 * @param actionDefs    Defines the effect of each action.
	 * @param conditionDefs Defines the conditions (guards) of the program graph.
	 * @return A transition system representing {@code pg}.
	 */
	public <L, A> TransitionSystem<Pair<L, Map<String, Object>>, A, String> transitionSystemFromProgramGraph(
			ProgramGraph<L, A> pg, Set<ActionDef> actionDefs, Set<ConditionDef> conditionDefs) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Creates a transition system representing channel system {@code cs}.
	 *
	 * @param <L> Type of locations in the channel system.
	 * @param <A> Type of actions in the channel system.
	 * @param cs  The channel system to be translated into a transition system.
	 * @return A transition system representing {@code cs}.
	 */
	public <L, A> TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> transitionSystemFromChannelSystem(
			ChannelSystem<L, A> cs) {

		Set<ActionDef> actions = Collections.singleton(new ParserBasedActDef());
		Set<ConditionDef> conditions = Collections.singleton(new ParserBasedCondDef());
		return transitionSystemFromChannelSystem(cs, actions, conditions);
	}

	public <L, A> TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> transitionSystemFromChannelSystem(
			ChannelSystem<L, A> cs, Set<ActionDef> actions, Set<ConditionDef> conditions) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Construct a program graph from nanopromela code.
	 *
	 * @param filename The nanopromela code.
	 * @return A program graph for the given code.
	 * @throws Exception If the code is invalid.
	 */
	public ProgramGraph<String, String> programGraphFromNanoPromela(String filename) throws Exception {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Construct a program graph from nanopromela code.
	 *
	 * @param nanopromela The nanopromela code.
	 * @return A program graph for the given code.
	 * @throws Exception If the code is invalid.
	 */
	public ProgramGraph<String, String> programGraphFromNanoPromelaString(String nanopromela) throws Exception {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Construct a program graph from nanopromela code.
	 *
	 * @param inputStream The nanopromela code.
	 * @return A program graph for the given code.
	 * @throws Exception If the code is invalid.
	 */
	public ProgramGraph<String, String> programGraphFromNanoPromela(InputStream inputStream) throws Exception {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Creates a transition system from a transition system and an automaton.
	 *
	 * @param <Sts>  Type of states in the transition system.
	 * @param <Saut> Type of states in the automaton.
	 * @param <A>    Type of actions in the transition system.
	 * @param <P>    Type of atomic propositions in the transition system, which is
	 *               also the type of the automaton alphabet.
	 * @param ts     The transition system.
	 * @param aut    The automaton.
	 * @return The product of {@code ts} with {@code aut}.
	 */
	public <Sts, Saut, A, P> TransitionSystem<Pair<Sts, Saut>, A, Saut> product(TransitionSystem<Sts, A, P> ts,
			Automaton<Saut, P> aut) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Verify that a system satisfies an omega regular property.
	 *
	 * @param <S>    Type of states in the transition system.
	 * @param <Saut> Type of states in the automaton.
	 * @param <A>    Type of actions in the transition system.
	 * @param <P>    Type of atomic propositions in the transition system, which is
	 *               also the type of the automaton alphabet.
	 * @param ts     The transition system.
	 * @param aut    A Büchi automaton for the words that do not satisfy the
	 *               property.
	 * @return A VerificationSucceeded object or a VerificationFailed object with a
	 *         counterexample.
	 */
	public <S, A, P, Saut> VerificationResult<S> verifyAnOmegaRegularProperty(TransitionSystem<S, A, P> ts,
			Automaton<Saut, P> aut) {
		throw new java.lang.UnsupportedOperationException();
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

	public <L> void LTL_addTransitions(Pair<Set<Set<LTL<L>>>, Set<AP<L>>> states_AP,
			MultiColorAutomaton<Set<LTL<L>>, L> gnba, LTL<L> ltl) {

		var states = states_AP.first;
		var AP = states_AP.second;

		MultiColorAutomaton<Set<LTL<L>>, L> temp_gnba = new MultiColorAutomaton<>();

		// Add ALL Transations
		for (var b : states)
			for (var b_tag : states)
				temp_gnba.addTransition(b, getAP(b, AP), b_tag);

		// Until - illegal transations

		for (var trans : temp_gnba.getTransitions().entrySet()) {
			var src = trans.getKey();
			//init states
			if(src.contains(ltl))
				gnba.setInitial(src);
			
			
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
					}

					if (isOK)
						gnba.addTransition(src, ap.getKey(), des);
				}

		}

	}

	public <L> Automaton<?, L> LTL2NBA(LTL<L> ltl) {
		MultiColorAutomaton<Set<LTL<L>>, L> gnba = new MultiColorAutomaton<>();

		Pair<Set<Set<LTL<L>>>, Set<AP<L>>> states_AP = States(ltl);

		LTL_addTransitions(states_AP, gnba, ltl);

		Util.printColoredAutomatonTransitions(gnba);
		
		
		
		return null;
	}
}
