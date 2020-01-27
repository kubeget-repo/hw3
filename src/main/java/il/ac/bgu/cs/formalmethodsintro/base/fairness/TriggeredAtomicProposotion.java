package il.ac.bgu.cs.formalmethodsintro.base.fairness;

public class TriggeredAtomicProposotion<P,A> extends ComposedAtomicProposition<P,A> {
    private A action;
    @Override
    public boolean IsOriginal() {
        return false;
    }

    @Override
    public boolean IsTriggered() {
        return true;
    }

    @Override
    public boolean IsEnabled() {
        return false;
    }

    @Override
    public String toString(){
        return "triggered(" + action.toString()+")";
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TriggeredAtomicProposotion)) {
            return false;
        }
        return this.toString().equals( obj.toString());

    }
    @Override
    public int hashCode() {
        final int prime = 49;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        return result;
    }

    public TriggeredAtomicProposotion(A instance){
        this.action = instance;
    }
}
