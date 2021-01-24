package src.Servidor;//package g8;

import src.TaggedConnection;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

//import static g8.src.TaggedConnection.Frame;
//import static src.TaggedConnection.Frame;

public class SimpleServerWithWorkers {
    //final static int WORKERS_PER_CONNECTION = 3;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);

        while(true) {
            Socket s = ss.accept();
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

            Runnable worker = () -> {
                try{
                    for (;;) {
                        int tag = in.readInt();
                        switch(tag){
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

                }catch(Exception e){
                    System.out.println("Erro");
                }
                
            };
            new Thread(worker).start();
            /*    
            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i)
                new Thread(worker).start();
            */
        }

    }
}

