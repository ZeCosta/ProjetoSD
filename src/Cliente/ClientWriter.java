package src.Cliente;

import java.io.DataOutputStream;

public class ClientWriter implements Runnable{
    private DataOutputStream out;
    private Barrier b;

    public ClientWriter(DataOutputStream out, Barrier b){
        this.out = out;
        this.b = b;
    }

    public void run(){

    }
}
