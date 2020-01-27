package il.ac.bgu.cs.formalmethodsintro.base;

import il.ac.bgu.cs.formalmethodsintro.base.automata.Automaton;
import il.ac.bgu.cs.formalmethodsintro.base.fairness.FairnessCondition;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.AP;
import il.ac.bgu.cs.formalmethodsintro.base.ltl.LTL;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TSTransition;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TransitionSystem;

import java.util.*;

public class Baraa_Tests {
    public static Automaton<String, String> Baraa_get_aut2(){
        Automaton<String,String> aut = new Automaton();

        String q0 = "q0";
        String q1 = "q1";
        String q2 = "q2";
        String q3 = "q3";



        Set<String> q0_q0 = new HashSet<>(Arrays.asList("r"));
        Set<String> q0_q1 = new HashSet<>(Arrays.asList());
        Set<String> q1_q2 = new HashSet<>(Arrays.asList());
        Set<String> q1_q0 = new HashSet<>(Arrays.asList("r"));
        Set<String> q2_q3 = new HashSet<>(Arrays.asList());
        Set<String> q2_q0 = new HashSet<>(Arrays.asList("r"));
        Set<String> q3_q3_1 = new HashSet<>(Arrays.asList("r"));
        Set<String> q3_q3_2 = new HashSet<>(Arrays.asList());

        aut.addState(q0);
        aut.addState(q1);
        aut.addState(q2);
        aut.addState(q3);

        aut.setInitial(q0);
        aut.setAccepting(q3);

        aut.addTransition(q0,q0_q0,q0);
        aut.addTransition(q0,q0_q1,q1);
        aut.addTransition(q1,q1_q2,q2);
        aut.addTransition(q1,q1_q0,q0);
        aut.addTransition(q2,q2_q3,q3);
        aut.addTransition(q2,q2_q0,q0);
        aut.addTransition(q3,q3_q3_1,q3);
        aut.addTransition(q3,q3_q3_2,q3);


        return aut;
    }


    public static TransitionSystem<String,String,String> Baraa_get_TS2() {
        TransitionSystem<String,String,String> ts = new TransitionSystem<>();
        String[] AP = {"r"};
        String[] ACT = {"alpha"};


        String alpha = "alpha";


        String s0 = "s0";
        String s1 = "s1";
        String s2 = "s2";

        ts.addInitialState(s0);
        ts.addState(s1);
        ts.addState(s2);
        ts.addAllAtomicPropositions(AP);
        ts.addAllActions(ACT);


        ts.addToLabel(s1,AP[0]);



        ts.addTransition(new TSTransition<>(s0,alpha,s1));
        ts.addTransition(new TSTransition<>(s2,alpha,s1));
        ts.addTransition(new TSTransition<>(s2,alpha,s0));
        ts.addTransition(new TSTransition<>(s1,alpha,s0));
        ts.addTransition(new TSTransition<>(s1,alpha,s2));

        return ts;

    }
    public static TransitionSystem<String,String,String> Baraa_get_TS3() {
        TransitionSystem<String,String,String> ts = new TransitionSystem<>();
        String[] AP = {"r","y"};
        String[] ACT = {"alpha"};


        String alpha = "alpha";


        String sy = "sy";
        String sr = "sr";
        String sry = "sry";
        String sg = "sg";


        ts.addInitialState(sg);
        ts.addState(sr);
        ts.addState(sy);
        ts.addState(sry);

        ts.addAllAtomicPropositions(AP);
        ts.addAllActions(ACT);


        ts.addToLabel(sr,AP[0]);
        ts.addToLabel(sy,AP[1]);



        ts.addTransition(new TSTransition<>(sg,alpha,sy));
        ts.addTransition(new TSTransition<>(sy,alpha,sr));
        ts.addTransition(new TSTransition<>(sr,alpha,sry));
        ts.addTransition(new TSTransition<>(sry,alpha,sg));

        return ts;

    }
    public static Automaton<String, String> Baraa_get_aut3() {
        Automaton<String,String> aut = new Automaton();

        String q0 = "q0";
        String q1 = "q1";
        String q2 = "q2";


        List<Set<String>> a = Arrays.asList(
                new HashSet<>(),
                new HashSet<>(Arrays.asList("y")),
                new HashSet<>(Arrays.asList("r")),
                new HashSet<>(Arrays.asList("r","y"))
        );


        aut.addState(q0);
        aut.addState(q1);
        aut.addState(q2);

        aut.setInitial(q0);
        aut.setAccepting(q2);

        aut.addTransition(q1,a.get(1),q1); aut.addTransition(q1,a.get(3),q1);
        aut.addTransition(q1,a.get(0),q0); aut.addTransition(q1,a.get(2),q0);

        aut.addTransition(q0,a.get(0),q0);
        aut.addTransition(q0,a.get(1),q1);

        aut.addTransition(q0,a.get(2),q2);  aut.addTransition(q0,a.get(3),q2);



        return aut;

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
        ts.addTransition(new TSTransition<>(s4,beta,s5));
        ts.addTransition(new TSTransition<>(s5,beta,s1));
        ts.addTransition(new TSTransition<>(s5,alpha,s2));
        return ts;
    }

    public static TransitionSystem<String,String,String> Baraa_get_TS4() {
        TransitionSystem<String,String,String> ts = new TransitionSystem<>();
        String[] AP = {"red","green"};
        String[] ACT = {"alpha"};


        String alpha = "alpha";

        String s0 = "s0";
        String s1 = "s1";

        ts.addInitialState(s0);
        ts.addState(s1);

        ts.addAllAtomicPropositions(AP);
        ts.addAllActions(ACT);
        ts.addToLabel(s0,AP[0]);
        ts.addToLabel(s1,AP[1]);

        ts.addTransition(new TSTransition<>(s0,alpha,s1));
        ts.addTransition(new TSTransition<>(s1,alpha,s0));
        return ts;

    }

    public static TransitionSystem<String,String,String> Baraa_get_TS4_test() {
        TransitionSystem<String,String,String> ts = new TransitionSystem<>();
        String[] AP = {"red","green"};
        String[] ACT = {"alpha"};


        String alpha = "alpha";

        String s0 = "s0";
        String s1 = "s1";
        String s2 = "s2";

        ts.addInitialState(s0);
        ts.addState(s1);

        ts.addAllAtomicPropositions(AP);
        ts.addAllActions(ACT);
        ts.addToLabel(s0,AP[0]);
        ts.addToLabel(s1,AP[1]);

        ts.addTransition(new TSTransition<>(s0,alpha,s1));
        ts.addTransition(new TSTransition<>(s1,alpha,s0));
        ts.addTransition(new TSTransition<>(s0,alpha,s2));
        ts.addTransition(new TSTransition<>(s2,alpha,s0));

        return ts;

    }
    public static Automaton<String, String> Baraa_get_aut4(){
        Automaton<String,String> aut = new Automaton();

        String q0 = "q0";
        String q1 = "q1";
        String q2 = "q2";


        List<Set<String>> a = Arrays.asList(
                new HashSet<>(),
                new HashSet<>(Arrays.asList("red")),
                new HashSet<>(Arrays.asList("green")),
                new HashSet<>(Arrays.asList("red","green"))
        );


        aut.addState(q0);
        aut.addState(q1);
        aut.addState(q2);

        aut.setInitial(q0);
        aut.setAccepting(q1);

        aut.addTransition(q0,a.get(0),q0);
        aut.addTransition(q0,a.get(1),q0);
        aut.addTransition(q0,a.get(2),q0);
        aut.addTransition(q0,a.get(3),q0);

        aut.addTransition(q0,a.get(0),q1);
        aut.addTransition(q0,a.get(1),q1);

        aut.addTransition(q1,a.get(0),q1);
        aut.addTransition(q1,a.get(1),q1);

        aut.addTransition(q1,a.get(2),q2);
        aut.addTransition(q1,a.get(3),q2);

        aut.addTransition(q2,a.get(0),q2);
        aut.addTransition(q2,a.get(1),q2);
        aut.addTransition(q2,a.get(2),q2);
        aut.addTransition(q2,a.get(3),q2);



        return aut;

    }
    public static LTL<String> Baraa_get_LTL(){
       FvmFacade app = new FvmFacade();
        AP<String> a = new AP("a");
        return app.always( app.eventualy(a));
    }
    public static TransitionSystem<String,String,String> Baraa_get_TS5(){
        TransitionSystem<String,String,String> ts = new TransitionSystem<>();
        String[] AP = {"a"};
        String[] ACT = {"alpha","beta","omega"};

        String alpha = "alpha";
        String beta = "beta";
        String omega = "omega";

        String s1 = "s1";
        String s2 = "s2";
        String s3 = "s3";

        ts.addInitialState(s1);
        ts.addState(s2);
        ts.addState(s3);
        ts.addAllAtomicPropositions(AP);
        ts.addAllActions(ACT);

        ts.addToLabel(s3,AP[0]);

        ts.addTransition(new TSTransition<>(s1,alpha,s3));
        ts.addTransition(new TSTransition<>(s2,beta,s3));
        ts.addTransition(new TSTransition<>(s2,omega,s1));
        ts.addTransition(new TSTransition<>(s1,omega,s2));
        ts.addTransition(new TSTransition<>(s3,beta,s3));

        return ts;
    }
    public static FairnessCondition<String> Baraa_get_fc(){
        FairnessCondition<String> fc = new FairnessCondition<String>(
                new HashSet<>(Arrays.asList(
//						new HashSet<>(Arrays.asList())
//						new HashSet<>(Arrays.asList())
                )),
                new HashSet<>(Arrays.asList(
//                      new HashSet<>(Arrays.asList()),
//						new HashSet<>(Arrays.asList())
                )),
                new HashSet<>(Arrays.asList(
//						new HashSet<>(Arrays.asList()),
						new HashSet<>(Arrays.asList("alpha","beta"))
                )));
        return fc;
    }
    }
