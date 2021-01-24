package src.Cliente;


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

    /*
        public void run(){
            try {
                BufferedReader systemin = new BufferedReader(new InputStreamReader(System.in));

                int tag;
                String input = null;

                while((input = systemin.readLine()) != null){
                    tag =  Integer.parseInt(input);
                    
                    out.writeInt(tag);
                    switch(tag){
                        case 0: // 0 -> registar utilizador 
                        case 1: //)) 1 -> autenticar 
                            out.writeUTF(systemin.readLine()); // Username 
                            out.writeUTF(systemin.readLine()); // Password 
                            break;
                    }
                    out.flush();
                }

                out.writeInt(-1); // Não vai escrever mais nada 
                out.flush();

                s.shutdownOutput();
                s.shutdownInput();
                s.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    */

    public boolean[] login(String user, String pass) throws Exception{
    	try{
	    	out.writeInt(1);

	        out.writeUTF(user); /* Username */
	        out.writeUTF(pass); /* Password */
	   
	        out.flush();

            boolean[] b = new boolean[2];
	        b[0] = in.readBoolean();
            if(b[0]) b[1] = in.readBoolean();

	       	return b;
    	}catch(Exception e){
    		throw e;
    	}


    }

    public boolean register(String user, String pass) throws Exception{
    	try{
	    	out.writeInt(2);

	        out.writeUTF(user); /* Username */
	        out.writeUTF(pass); /* Password */
	   
	        out.flush();

	       	return in.readBoolean();
    	}catch(Exception e){
    		throw e;
    	}


    }
}
