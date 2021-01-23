package src.AlarmeCovidLN;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizador {
    private String username;
    private String password;
    private boolean infetado;
    public boolean risco;
    private Localizacao lAtual;
    private boolean logged;
    private boolean temAutorizacao;
    private Collection<String> contactos; /* Contactos desde sempre */
    public Lock ul;

    public Utilizador(String username, String password, boolean temAutorizacao,
                      int x, int y){
        this.username = username;
        this.password = password;
        this.infetado = false;
        this.risco = false;
        this.lAtual = new Localizacao(x, y);
        this.logged = false;
        this.temAutorizacao = temAutorizacao;
        this.contactos = new HashSet<>();
        this.ul = new ReentrantLock();
    }

    public void addContacto(String c){
        contactos.add(c);
    }

    public void addContactos(Collection<String> cs){
        contactos.addAll(cs);
    }

    public boolean estaNumaLocalizacao(Localizacao l){
        return (logged && l.equals(this.lAtual));
    }

    public void lock(){
        ul.lock();
    }

    public void unlock(){
        ul.unlock();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isInfetado() {
        return infetado;
    }

    public void setInfetado(boolean infetado) {
        this.infetado = infetado;
    }

    public Localizacao getlAtual() {
        return lAtual;
    }

    public void setlAtual(Localizacao lAtual) {
        this.lAtual = lAtual;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean isTemAutorizacao() {
        return temAutorizacao;
    }

    public void setTemAutorizacao(boolean temAutorizacao) {
        this.temAutorizacao = temAutorizacao;
    }

    public Collection<String> getContactos () {
        return new HashSet<>(this.contactos);
    }

    public void setRisco () {
        this.risco = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilizador that = (Utilizador) o;
        return infetado == that.infetado &&
                risco == that.risco &&
                logged == that.logged &&
                temAutorizacao == that.temAutorizacao &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(lAtual, that.lAtual) &&
                Objects.equals(contactos, that.contactos) &&
                Objects.equals(ul, that.ul);
    }
}
