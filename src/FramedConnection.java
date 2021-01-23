package src;//package g8;

import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.locks.*;
import java.io.*;
import java.util.*;


public class FramedConnection implements AutoCloseable{
	private Lock write=new ReentrantLock();
	private Lock read=new ReentrantLock();
	
	private Socket s;
	private DataInputStream is;
	private DataOutputStream os;

	public FramedConnection(Socket socket) throws IOException{
		this.s=socket;
		this.is = new DataInputStream(new BufferedInputStream(this.s.getInputStream()));
		this.os = new DataOutputStream(new BufferedOutputStream(this.s.getOutputStream()));
	}

	public void send(byte[] data) throws IOException{
		try{
			write.lock();
			os.writeInt(data.length);
			os.write(data);
			os.flush();
		}finally{
			write.unlock();
		}
	}

	public byte[] receive() throws IOException{
		byte[] data;
		try{
			read.lock();
        	data = new byte[is.readInt()];
	    	is.readFully(data);
		}finally{
			read.unlock();
		}	
	    return data;
	}

	public void close() throws IOException{
		this.s.close();
	}
}