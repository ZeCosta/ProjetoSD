package src.Servidor;

import src.AlarmeCovidLN.AlarmeCovidLN;
import src.TaggedConnection;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class SimpleServerWithWorkers {

    public static void main(String[] args) throws Exception {
        AlarmeCovidLN ac = new AlarmeCovidLN(10); /* Damos como argumento o tamanho do mapa (NxN) */
        ServerSocket ss = new ServerSocket(12345);

        while(true) {
            Socket s = ss.accept();
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

            Runnable worker = () -> {
                try{
                    for (;;) {
                        int tag = in.readInt();
                        String user, pass;
                        int x,y;
                        switch(tag){
                            case 1:
                                boolean[] bs;
                                System.out.println("Login");
                                user = in.readUTF();
                                pass = in.readUTF();
                                bs = ac.login(user, pass);

                                out.writeBoolean(bs[0]);
                                if(bs[0])
                                    out.writeBoolean(bs[1]);

                                out.flush();
                                break;
                            case 2:
                                System.out.println("Registar");
                                user = in.readUTF();
                                pass = in.readUTF();
                                out.writeBoolean(ac.registar(user, pass));
                                out.flush();
                                break;
                            case 3:
                                System.out.println("Comunicar localização");
                                user = in.readUTF();
                                x = in.readInt();
                                y = in.readInt();
                                ac.comunicarLocalizacao(user, x, y);
                                out.writeBoolean(true);
                                out.flush();
                                break;
                            case 4:
                                System.out.println("Quantidade de pessoas numa localização");
                                x = in.readInt();
                                y = in.readInt();
                                out.writeInt(ac.getOcupacao(x,y));
                                out.flush();
                                break;
                            case 5:
                                System.out.println("Mapa com o nº de pessoas em cada localização");
                                int[][][] res = ac.getOcupacoes();
                                int l = res.length;
                                out.writeInt(l);
                                for(int i = 0; i < l; i++)
                                    for(int j = 0; j < l; j++){
                                        out.writeInt(res[i][j][0]);
                                        out.writeInt(res[i][j][1]);
                                    }

                                out.flush();
                                break;
                            case 6:
                                System.out.println("Comunicar que está infetado");
                                user = in.readUTF();
                                ac.estaInfetado(user);
                                out.writeBoolean(true);
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
                    e.printStackTrace();
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

