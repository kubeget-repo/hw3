package il.ac.bgu.cs.formalmethodsintro.base.ltl;

import il.ac.bgu.cs.formalmethodsintro.base.fairness.ComposedAtomicProposition;
import il.ac.bgu.cs.formalmethodsintro.base.util.Pair;
import il.ac.bgu.cs.formalmethodsintro.base.util.Util;

import java.util.HashSet;
import java.util.Set;

public class LTL2GNBA_tools {
    private static <A> int get_APs_helper(LTL<A> ltl,Set<Pair<Integer,LTL<A>>> s,Class a){
        int res = 0;
        if(ltl instanceof AP){
            res = 0;
        }
        else if(ltl instanceof TRUE){
            res = 0;
        }
        else if(ltl instanceof And){
            int d1 = get_APs_helper (((And<A>)ltl).getLeft(),s,a);
            int d2 = get_APs_helper (((And<A>)ltl).getRight(),s,a);
            res = Math.max(d1,d2)+1;
        }
        else if(ltl instanceof Next){
            res = get_APs_helper (((Next<A>)ltl).getInner(),s,a) + 1;
        }
        else if(ltl instanceof Not){
            res = get_APs_helper (((Not<A>)ltl).getInner(),s,a) + 1;
        }
        else if(ltl instanceof Until){
            int d1 = get_APs_helper (((Until<A>)ltl).getLeft(),s,a);
            int d2 = get_APs_helper (((Until<A>)ltl).getRight(),s,a);
            res = Math.max(d1,d2)+1;
        }
        if(ltl.getClass() == a){
            s.add(new Pair<>(res,ltl));
        }
        return res;
    }

    public static <A> Set<Pair<Integer,LTL<A>>> get_APs(LTL<A> ltl){
        Set<Pair<Integer,LTL<A>>> s = new HashSet<>();
        get_APs_helper(ltl,s,AP.class);
        return s;
    }
    public static <A> Set<Pair<Integer,LTL<A>>> get_Untils(LTL<A> ltl){
        Set<Pair<Integer,LTL<A>>> s = new HashSet<>();
        get_APs_helper(ltl,s,Until.class);
        return s;
    }
    public static <A>  boolean contains_true(LTL<A> ltl){
        Set<Pair<Integer,LTL<A>>> s = new HashSet<>();
        get_APs_helper(ltl,s,TRUE.class);

        return s.size()>0;
    }
    public static <A> Set<Pair<Integer,LTL<A>>> get_Nexts(LTL<A> ltl){
        Set<Pair<Integer,LTL<A>>> s = new HashSet<>();
        get_APs_helper(ltl,s,Next.class);
        return s;
    }

    public static <A> Set<Pair<Integer,LTL<A>>> get_Ands(LTL<A> ltl){
        Set<Pair<Integer,LTL<A>>> s = new HashSet<>();
        get_APs_helper(ltl,s,And.class);
        return s;
    }

    private static <T> int max_depth(Set<Pair<Integer,T>> X){
        int x = -1000000;
        for(Pair<Integer,T> pair : X){
            x = Math.max(x,pair.first);
        }
        return x;
    }
    public static <A> boolean my_contains(Set<Pair<Integer, A>> set, A element){
        for(Pair<Integer,A> p : set){
            if(p.getSecond().equals(element)){
                return true;
            }
        }
        return false;
    }
    private static <A> Set<Set<A>> clear_indices(Set<Set<Pair<Integer,A>>> set){
        Set<Set<A>> res = new HashSet<>();
        for(Set<Pair<Integer,A>> s : set){
            Set<A> new_set = new HashSet<>();
            res.add(new_set);
            for(Pair<Integer,A> p : s){
                new_set.add(p.getSecond());
            }
        }
        return res;
    }

    public static <A> Set<LTL<A>> get_not_used_aps(Set<A> all_APs,LTL<A> final_ltl){
        Set<LTL<A>> res = new HashSet<>();
        Set<Pair<Integer,LTL<A>>> APs = get_APs(final_ltl);
        for(A ap : all_APs){
            LTL<A> ltl_ap = new AP(ap);
            if(!my_contains(APs,ltl_ap)){
                res.add(ltl_ap);
            }
        }
        return res;

    }
    public static <A> Set<Set<LTL<A>>> get_states(LTL<A> ltl){
        Set<Pair<Integer,LTL<A>>> APs = get_APs(ltl);
        Set<Pair<Integer,LTL<A>>> Ands = get_Ands(ltl);
        Set<Pair<Integer,LTL<A>>> Nexts = get_Nexts(ltl);
        Set<Pair<Integer,LTL<A>>> Untils = get_Untils(ltl);
        boolean there_is_true = contains_true(ltl);
        int max_ands = max_depth(Ands);
        int max_untils = max_depth(Untils);

        APs.addAll(Nexts);
        Set<Set<Pair<Integer,LTL<A>>>> states = Util.powerSet(APs);
        for(Pair<Integer,LTL<A>> ap : APs){
            for(Set<Pair<Integer,LTL<A>>> state : states){
                if(there_is_true){
                    state.add(new Pair<>(-7,new TRUE()));
                }
                if(!state.contains(ap)){
                    Pair<Integer,LTL<A>> new_not = new Pair(ap.getFirst(),LTL.not(ap.getSecond()));
                    state.add(new_not);
                }
            }
        }

        for (int i = 1; i < Math.max(max_ands,max_untils) + 1; i++){
            Set<Set<Pair<Integer,LTL<A>>>> to_add = new HashSet<>();


            for(Set<Pair<Integer,LTL<A>>> B : states){

                //add ands of depth i
                for(Pair<Integer,LTL<A>> p : Ands){
                    int level = p.getFirst();
                    LTL<A> and = p.getSecond();
                    if(i == level){
                        if(my_contains(B,((And)and).getLeft()) && my_contains(B,((And)and).getRight())){
                            B.add(new Pair<>(-1,and));
                        } else {
                            B.add(new Pair<>(-2,LTL.not(and)));
                        }
                    }
                }


                //add untils of depth i
                for(Pair<Integer,LTL<A>> p : Untils){
                    int level = p.getFirst();
                    LTL<A> until = p.getSecond();
                    if(i == level){
                        if(my_contains(B,((Until)until).getRight())){
                            B.add(new Pair<>(-1,until));
                        } else if(my_contains(B,((Until)until).getLeft()))  {
                            Set<Pair<Integer,LTL<A>>> to_add_set = new HashSet<>(B);
                            to_add_set.add(new Pair<>(-2,LTL.not(until)));
                            B.add(new Pair<>(-3,until));

                            to_add.add(to_add_set);
                        } else {
                            B.add(new Pair<>(-4,LTL.not(until)));
                        }
                    }
                }


            }
            states.addAll(to_add);
        }

        return clear_indices(states);
    }

}
