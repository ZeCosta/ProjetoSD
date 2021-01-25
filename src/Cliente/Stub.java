package src.Cliente;


import src.Exceptions.FromServerException;

import java.io.*;
import java.net.Socket;


public class Stub{
    private Socket s;
    private DataOutputStream out;
    private DataInputStream in;

    public Stub(Socket s) throws Exception{
        this.s = s;
        this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
        this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
           
    }


    public boolean login(String user, String pass) throws Exception{
    	try{
	    	out.writeInt(1);

	        out.writeUTF(user); /* Username */
	        out.writeUTF(pass); /* Password */
	   
	        out.flush();

            boolean b;
	        b = in.readBoolean();

            if(b){
                b = in.readBoolean();
            }else{
                throw new FromServerException("Stub error - Login inválido!");
            } 

	       	return b;
    	}catch(Exception e){
    		throw e;
    	}
    }

    public void register(String user, String pass) throws Exception{
    	try{
	    	out.writeInt(2);

	        out.writeUTF(user); 
	        out.writeUTF(pass); 
	   
	        out.flush();

	        if(!in.readBoolean()) throw new FromServerException("Stub error - Registo inválido (já existe?)");

    	}catch(Exception e){
    		throw e;
    	}
    }

    public void comunicarLocalizacao(int x, int y) throws Exception{
        try{
            out.writeInt(3);

            out.writeInt(x); 
            out.writeInt(y); 
       
            out.flush();

            if(!in.readBoolean()) throw new FromServerException("Stub error - Não foi possível comunicar localização");

        }catch(Exception e){
            throw e;
        }
    }

    public int verificarOcupacao(int x, int y) throws Exception{
        try{
            out.writeInt(4);

            out.writeInt(x); 
            out.writeInt(y); 
       
            out.flush();

            if(!in.readBoolean())
                throw new FromServerException("Stub error - Falha na verificação da ocupação em (" + x + "," + y + ")");

            return in.readInt();

        }catch(Exception e){
            throw e;
        }
    }
    
    public int[][][] imprimirMapa() throws Exception{
    	try{
            out.writeInt(5);
            out.flush();
            
            int l=in.readInt();
            int[][][] mapa = new int[l][l][2];
            
            for(int i = 0; i < l; i++)
                for(int j = 0; j < l; j++){
                    mapa[i][j][0]=in.readInt();
                    mapa[i][j][1]=in.readInt();
                }

            return mapa;
        } catch(Exception e){
            throw e;
        }
    }

    public boolean verificarRiscoInfecao() throws Exception{
        try{
            out.writeInt(7);
            out.flush();
  			
  			if(!in.readBoolean()) throw new FromServerException("Stub error - Falhou a comunicar infeção");
            return in.readBoolean();

        } catch(Exception e){
            throw e;
        }
    }

    public String comunicarInfecao() throws Exception{
        try{
            out.writeInt(6);
            out.flush();

            if(!in.readBoolean())
                throw new FromServerException("Stub error - Falhou a comunicar infeção");

            else
                return in.readUTF();
        } catch(Exception e){
            throw e;
        }
    }

}