package src.Servidor;

import src.AlarmeCovidLN.AlarmeCovidLN;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class SimpleServerWithWorkers {

    public static void main(String[] args) throws Exception {
        AlarmeCovidLN ac = new AlarmeCovidLN(10); /* Damos como argumento o tamanho do mapa (NxN) */
        ServerSocket ss = new ServerSocket(12345);

        /* Administrador da aplicação, que possui a autorização especial para descarregar o mapa */
        System.out.println("Admin criado: " + ac.registar("1", "1",true));

        while(true) {
            Socket s = ss.accept();
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

            Runnable worker = () -> {
                String uniqueUser = null; /* Esta string contém o username do utilizador que 'invocou' este worker */
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
                                //System.out.println("bs(0) : " + bs[0]);
                                //System.out.println("bs(1) : " + bs[1]);

                                out.writeBoolean(bs[0]);
                                if(bs[0]){
                                    uniqueUser = user;
                                    out.writeBoolean(bs[1]);
                                }

                                out.flush();
                                break;
                            case 2:
                                System.out.println("Registar");
                                user = in.readUTF();
                                pass = in.readUTF();
                                out.writeBoolean(ac.registar(user, pass, false));
                                out.flush();
                                break;
                            case 3:
                                System.out.println("Comunicar localização");
                                //user = in.readUTF();
                                x = in.readInt();
                                y = in.readInt();
                                if(x < 0 || y < 0 || x >= ac.getN() || y >= ac.getN())
                                    out.writeBoolean(false);
                                else {
                                    System.out.println("user a comunicar localização -> " + uniqueUser);
                                    out.writeBoolean(ac.comunicarLocalizacao(uniqueUser, x, y));
                                }
                                out.flush();
                                System.out.println("fim de comunicacao");
                                break;
                            case 4:
                                System.out.println("Quantidade de pessoas numa localização");
                                x = in.readInt();
                                y = in.readInt();
                                if(x < 0 || y < 0 || x >= ac.getN() || y >= ac.getN())
                                    out.writeBoolean(false);
                                else {
                                    out.writeBoolean(true);
                                    out.writeInt(ac.getOcupacao(x, y));
                                }
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
                                if(uniqueUser != null) {
                                    out.writeBoolean(ac.estaInfetado(uniqueUser));
                                    out.writeUTF(uniqueUser);

                                    //Close socket?
                                
                                }
                                else out.writeBoolean(false);

                                out.flush();
                                break;
                            case 7:
                                System.out.println("Verificar se está em risco de contaminação");
                                boolean[] r;
                                r = ac.risco(uniqueUser);
                                if(r[0]) {
                                    out.writeBoolean(r[0]);
                                    out.writeBoolean(r[1]);
                                }
                                else
                                    out.writeBoolean(false);
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
                    // Try Log off unique user!!
                    ac.logoff(uniqueUser);
                    System.out.println(uniqueUser + " is now offline");
                    e.printStackTrace();
                }
                
            };
            new Thread(worker).start();
        }

    }
}

