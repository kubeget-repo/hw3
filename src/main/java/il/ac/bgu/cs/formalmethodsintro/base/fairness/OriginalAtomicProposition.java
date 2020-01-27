package il.ac.bgu.cs.formalmethodsintro.base.fairness;

import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TransitionSystem;

import java.util.Objects;

public class OriginalAtomicProposition<P,A> extends  ComposedAtomicProposition<P,A> {
    private P atomic_proposition;
    @Override
    public boolean IsOriginal() {
        return true;
    }

    @Override
    public boolean IsTriggered() {
        return false;
    }

    @Override
    public boolean IsEnabled() {
        return false;
    }

    @Override
    public String toString(){
        return "Original(" + atomic_proposition.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OriginalAtomicProposition)) {
            return false;
        }
        return this.toString().equals( obj.toString());

    }

    @Override
    public int hashCode() {
        final int prime = 101;
        int result = 1;
        result = prime * result + ((atomic_proposition == null) ? 0 : atomic_proposition.hashCode());
        return result;
    }
    public OriginalAtomicProposition(P instance){
        this.atomic_proposition = instance;
    }
}
