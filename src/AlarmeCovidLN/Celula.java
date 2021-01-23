package src.AlarmeCovidLN;

import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Celula {
    private Collection<String> users;
    private Lock l;

    public Celula(){
        this.users = new TreeSet<>();
        this.l = new ReentrantLock();
    }
}
