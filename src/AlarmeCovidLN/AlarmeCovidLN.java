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
                u.lock();
                l.unlock();
                try{
                    u.setlAtual(new Localizacao(x,y));
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
            for (Utilizador x : c) {
                x.lock();
                if (u.getContactos().contains(x.getUsername())) {
                    try {
                        x.setRisco();
                    } finally {
                        x.unlock();
                    }
                }
            }
            u.unlock();
        } finally {
            l.unlock();
        }
    }
}
