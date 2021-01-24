package src.AlarmeCovidLN;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class AlarmeCovidLN {
    private Map<String, Utilizador> users;
    private int N;
    private final Celula[][] mapa; /* Mapa que guarda quem já esteve em cada localização */
    private Lock l;

    public AlarmeCovidLN(int N){
        this.N = N;
        this.users = new HashMap<>();
        this.mapa = new Celula[N][N];
    }

    public Collection<String> getContactos(Localizacao loc){
        int x = loc.getX();
        int y = loc.getY();
        Collection<String> res;

        l.lock();
        try{
            Celula c = mapa[x][y];
            c.lock();
            res = c.getUsers();
            c.unlock();
            return res;
        }finally {
            l.unlock();
        }
    }

    /**
     * Verificar se existe um utilizador registado
     */
    public boolean estaRegistado(String username){
        l.lock();
        try {
            return this.users.containsKey(username);
        } finally {
            l.unlock();
        }
    }

    /**
     * Registar um utilizador
     */
    public boolean registar(String username, String password, boolean autorizacao){
        if(estaRegistado(username))
            return false;
        else{
            l.lock();
            try{
                this.users.put(username, new Utilizador(username, password,
                        autorizacao,-1,-1));
            }finally {
                l.unlock();
            }
            return true;
        }
    }

    /**
     * Fazer login
     */
    public boolean login(String username, String password) {
        l.lock();
        try {
            Utilizador u = users.get(username);
            if ( u != null && password.equals(u.getPassword()) && !u.isLogged()) {
                u.lock();
                l.unlock();
                try {
                    u.setLogged(true);
                } finally {
                    u.unlock();
                }
                return true;
            }
            else {
                return false;
            }
        } finally {
            l.unlock();
        }
    }

    /**
     * Atualiza a localização de um dado utilizador
     * @param user Utilizador
     * @param x coordenada x
     * @param y coordenada y
     */
    public void comunicarLocalizacao(String user, int x, int y){
        l.lock();
        try{
            Utilizador u = users.get(user);
            if(u != null){
                Localizacao loc = new Localizacao(x,y);
                u.lock();
                try{
                    u.setlAtual(loc);
                    // 1.Adicionar pessoas desta localizacao nos contactos do u
                    // 2.Adicionar o u como contacto das restantes pessoas
                    Collection<Utilizador> us = users.values();
                    for(Utilizador ut: us)
                        if(ut.getlAtual().equals(loc) && !ut.equals(u))
                            ut.lock();

                    for(Utilizador ut: us)
                        if(ut.getlAtual().equals(loc) && !ut.equals(u)) {
                            u.addContacto(ut.getUsername());
                            ut.addContacto(user);
                        }

                    for(Utilizador ut: us)
                        if(ut.getlAtual().equals(loc) && !ut.equals(u))
                            ut.unlock();

                    // 3.Adicionar este utilizador ao mapa
                    Celula cel = mapa[x][y];
                    if(cel == null)
                        cel = new Celula();
                    cel.lock();
                    cel.addUser(user);
                    mapa[x][y] = cel;
                    cel.unlock();

                } finally {
                    u.unlock();
                }
            }
        } finally {
            l.unlock();
        }
    }

    /**
     *  Indica quantas pessoas estão numa localização atualmente
     * @param x coordenada x
     * @param y coordenada y
     * @return num
     */
    public int quantasPessoas(int x, int y){
        this.l.lock();
        int count = 0;
        try{
            Localizacao loc = new Localizacao(x,y);
            Collection<Utilizador> c = users.values();
            for(Utilizador u: c)
                u.lock();

            this.l.unlock();

            for(Utilizador u : c)
                if(u.estaNumaLocalizacao(loc))
                    count++;

            for(Utilizador u: c)
                u.unlock();
        } finally {
            this.l.unlock();
        }
        return count;
    }

    public void estaInfetado (String username) {
        l.lock();
        try {
            Utilizador u = users.get(username);
            u.lock();
            u.setInfetado(true);
            Collection<Utilizador> c = users.values();
            for (Utilizador x : c)
                x.lock();

            for(Utilizador x: c)
                if (u.getContactos().contains(x.getUsername()))
                    x.setRisco();

            for(Utilizador x: c)
                x.unlock();

            u.unlock();
        } finally {
            l.unlock();
        }
    }
}
