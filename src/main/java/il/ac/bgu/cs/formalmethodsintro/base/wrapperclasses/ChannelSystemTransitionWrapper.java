package il.ac.bgu.cs.formalmethodsintro.base.wrapperclasses;

import il.ac.bgu.cs.formalmethodsintro.base.channelsystem.InterleavingActDef;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.ActionDef;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.PGTransition;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TSTransition;
import il.ac.bgu.cs.formalmethodsintro.base.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public  class ChannelSystemTransitionWrapper<L,A> {
    private int i;
    public Pair<List<L>, Map<String,Object>> from;
    public PGTransition<L,A> pg_transition;

    public ChannelSystemTransitionWrapper(int i, PGTransition<L, A> transition, Pair<List<L>, Map<String, Object>> state) {
        this.i=i;
        this.pg_transition = transition;
        this.from = state;
    }

    public int getI() {
        return i;
    }

    public Pair<List<L>, Map<String, Object>> getFrom() {
        return from;
    }

    public PGTransition<L, A> getPg_transition() {
        return pg_transition;
    }

    public boolean isMatching(ChannelSystemTransitionWrapper<L,A> other, InterleavingActDef iad) {
        String new_action = ((String)this.pg_transition.getAction()) + "|" + ((String)other.pg_transition.getAction());
        boolean matching_action = iad.isMatchingAction(new_action);
        boolean matching_from_states = this.from.equals(other.from);
        boolean different_pg = this.i != other.i;

        return matching_action && matching_from_states && different_pg;
    }

    public TSTransition<Pair<List<L>, Map<String, Object>>,A> get_transition(Set<ActionDef> actions){
        Map<String,Object> new_eval = ActionDef.effect(actions,from.getSecond(),pg_transition.getAction());
        if(new_eval == null){
            return null;
        }
        List<L> new_locations = new ArrayList<L>(from.getFirst());
        new_locations.set(this.getI(),pg_transition.getTo());
        Pair<List<L>, Map<String,Object>> new_state = new Pair<>(new_locations,new_eval);

        TSTransition<Pair<List<L>, Map<String, Object>>,A> res = new TSTransition<>(from,pg_transition.getAction(),new_state);
        return res;
    }




    public  TSTransition<Pair<List<L>, Map<String, Object>>,A> get_transition_two_sided(ChannelSystemTransitionWrapper<L,A> other, InterleavingActDef iad) {

        String new_action = this.pg_transition.getAction().toString() + "|" + other.pg_transition.getAction().toString();
        Map<String,Object> new_eval = iad.effect(from.getSecond(),new_action);
        if(new_eval == null){
            return null;
        }
        List<L> new_locations = new ArrayList<L>(from.getFirst());
        new_locations.set(this.getI(),pg_transition.getTo());
        new_locations.set(other.getI(),other.pg_transition.getTo());

        Pair<List<L>, Map<String,Object>> new_state = new Pair<>(new_locations,new_eval);
        TSTransition<Pair<List<L>, Map<String, Object>>, A> res;
        if(pg_transition.getAction() instanceof   String) {
            res = new TSTransition(from, "Tau", new_state);
        } else {
            res = new TSTransition(from, null, new_state);
        }
        return res;
    }
}
