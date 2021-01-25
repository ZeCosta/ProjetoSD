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

	        if(!in.readBoolean()) throw new Exception();

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

            if(!in.readBoolean()) throw new Exception();

            return in.readInt();

        }catch(Exception e){
            throw e;
        }
    }
}