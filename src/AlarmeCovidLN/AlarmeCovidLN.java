package src.AlarmeCovidLN;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AlarmeCovidLN {
    private Map<String, Utilizador> users;
    private int N;
    private final Celula[][] mapa; /* Mapa que guarda quem esteve ou está em cada localização */
    private Lock l;

    public AlarmeCovidLN(int N){
        this.N = N;
        this.users = new HashMap<>();
        this.mapa = new Celula[N][N];
        this.l = new ReentrantLock();
    }

    /**
     * Devolve o número de pessoas a ocupar uma localização no momento atual
     * @param x coordenada x
     * @param y coordenada y
     * @return inteiro
     */
    public int getOcupacao(int x, int y){
        l.lock();
        int res = 0;
        Localizacao loc = new Localizacao(x,y);
        try {
            Collection<Utilizador> us = users.values();
            for(Utilizador u: us)
                u.lock();

            l.unlock();

            for(Utilizador u: us)
                if(u.getlAtual().equals(loc)) {
                    res++;
                    u.unlock();
                }
            return res;
        } finally {
            l.unlock();
        }
    }

    /**
     * Devolve o mapa com as ocupações
     * @return matriz de inteiros (cada localização tem uma quantidade de pessoas associada)
     */
    public int[][] getOcupacoes(){
        int x, y;
        int[][] res = new int[N][N];
        l.lock();
        try{
            //Dar lock
            for(x = 0; x <N; x++)
                for(y = 0; y < N; y++)
                    mapa[x][y].lock();

            l.unlock();

                //Obter ocupacao
            for(x = 0; x <N; x++)
                for(y = 0; y < N; y++)
                    res[x][y] = getOcupacao(x,y);

                //Dar unlock
            for(x = 0; x <N; x++)
                for(y = 0; y < N; y++)
                    mapa[x][y].unlock();

            return res;
        } finally {
            l.unlock();
        }
    }

    /**
     * Verifica se um utilizador está registado
     * @param username
     * @return true se sim
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
     * Regista um utilizador
     * @param username
     * @param password
     * @return true se for registado com sucesso
     */
    public boolean registar(String username, String password){
        if(estaRegistado(username))
            return false;
        else{
            l.lock();
            try{
                this.users.put(username, new Utilizador(username, password,
                        false,-1,-1));
                return true;
            }finally {
                l.unlock();
            }
        }
    }

    /**
     * Fazer login
     * @param username
     * @param password
     * @return true se for feito com sucesso, false caso contrário
     */
    public boolean[] login(String username, String password) {
        boolean[] res = new boolean[2];
        l.lock();
        try {
            Utilizador u = users.get(username);
            if ( u != null && password.equals(u.getPassword()) && !u.isLogged()) {
                u.lock();
                l.unlock();
                try {
                    u.setLogged(true);
                    res[0] = true;
                    res[1] = u.isAutorizado();
                } finally {
                    u.unlock();
                }
            }
            return res;
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
                try{
                    // 1.Adicionar pessoas desta localizacao nos contactos do u
                    // 2.Adicionar o u como contacto das restantes pessoas
                    Collection<Utilizador> us = users.values();
                    for(Utilizador ut: us)
                        ut.lock();

                    Celula cel = mapa[x][y];
                    if(cel == null)
                        mapa[x][y] = new Celula();
                    mapa[x][y].lock();

                    l.unlock();

                    u.setlAtual(loc);

                    for(Utilizador ut: us)
                        if(ut.getlAtual().equals(loc) && !ut.equals(u)) {
                            u.addContacto(ut.getUsername());
                            ut.addContacto(user);
                        }

                    for(Utilizador ut: us)
                        ut.unlock();

                    // 3.Adicionar este utilizador ao mapa
                    mapa[x][y].addUser(user);
                    mapa[x][y].unlock();

                } finally {
                    u.unlock();
                }
            }
        } finally {
            l.unlock();
        }
    }

    /**
     * Marca um utilizador como infetado, e adiciona os seus contactos ao grupo de risco
     * @param username username do infetado
     */
    public void estaInfetado (String username) {
        l.lock();
        try {
            Utilizador u = users.get(username);
            u.lock();
            u.setInfetado(true);
            Collection<String> c = u.getContactos();
            for(String x : c)
                users.get(x).lock();

            l.unlock();

            for(String x: c)
                users.get(x).setRisco();

            for(String x: c)
                users.get(x).unlock();

            u.unlock();
        } finally {
            l.unlock();
        }
    }
}
