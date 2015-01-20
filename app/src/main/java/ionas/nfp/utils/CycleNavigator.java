package ionas.nfp.utils;

import java.io.Serializable;

import ionas.nfp.db.Cycle;

/**
 * Created by sczaja on 12/01/2015.
 */
public class CycleNavigator implements Serializable {

    Cycle previous = null;
    Cycle current = null;
    Cycle next = null;

    public void setPrevious(Cycle previous) {
        this.previous = previous;
    }

    public void setCurrent(Cycle current) {
        this.current = current;
    }

    public void setNext(Cycle next) {
        this.next = next;
    }

    public Cycle getPrevious() {
        return previous;
    }

    public Cycle getCurrent() {
        return current;
    }

    public Cycle getNext() {
        return next;
    }

    @Override
    public String toString() {
        return "CycleNavigator{" +
                "previous=" + previous +
                ", current=" + current +
                ", next=" + next +
                '}';
    }
}
