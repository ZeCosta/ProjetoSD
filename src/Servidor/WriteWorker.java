package src.Servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class WriteWorker implements Runnable{
    private DataOutputStream out;

    public void run() {
            for (; ; ) {
                int tag = in.readInt();
                switch (tag) {
                    case 1:
                        System.out.println("Login");
                        String user = in.readUTF();
                        String pass = in.readUTF();
                        System.out.println(user + "->" + pass);

                        out.writeUTF("2");
                        out.flush();

                        break;
                    default:
                        System.out.println("Opção " + tag + "não implementada");

                        out.writeBoolean(false);
                        out.flush();
                        break;
                }

                System.out.println();
            }
    }
}

