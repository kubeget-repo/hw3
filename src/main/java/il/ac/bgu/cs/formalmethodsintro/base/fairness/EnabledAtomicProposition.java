package il.ac.bgu.cs.formalmethodsintro.base.fairness;


public class EnabledAtomicProposition<P,A> extends ComposedAtomicProposition<P,A> {

    private A action;
    @Override
    public boolean IsOriginal() {
        return false;
    }

    @Override
    public boolean IsTriggered() {
        return false;
    }

    @Override
    public boolean IsEnabled() {
        return true;
    }

    @Override
    public String toString(){
        return "Enabled(" + action.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EnabledAtomicProposition)) {
            return false;
        }
        return this.toString().equals( obj.toString());

    }


    @Override
    public int hashCode() {
        final int prime = 199;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        return result;
    }
    public EnabledAtomicProposition(A instance){
        this.action = instance;
    }
}
