//package g8;

import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.locks.*;
import java.io.*;
import java.util.*;

public class TaggedConnection implements AutoCloseable {
	public static class Frame {
		public final int tag;
		public final byte[] data;
		
		public Frame(int tag, byte[] data) {
			this.tag = tag; 
			this.data = data; 
		}
	}


	private Lock write=new ReentrantLock();
	private Lock read=new ReentrantLock();

	private Socket s;
	private DataInputStream is;
	private DataOutputStream os;

	public TaggedConnection(Socket socket) throws IOException {
		this.s=socket;

		this.is = new DataInputStream(new BufferedInputStream(this.s.getInputStream()));
		this.os = new DataOutputStream(new BufferedOutputStream(this.s.getOutputStream()));
	}
	
	public void send(Frame frame) throws IOException {
		send(frame.tag,frame.data);
	}
	
	public void send(int tag, byte[] data) throws IOException {
		try{
			write.lock();
			os.writeInt(4+data.length);
			os.writeInt(tag);
			os.write(data);
			os.flush();
		}finally{
			write.unlock();
		}
	}
	
	public Frame receive() throws IOException {
		byte[] data;
		int tag;
		int size;
		try{
			read.lock();
			size=is.readInt();
			tag=is.readInt();
        	data = new byte[size-4];
	    	is.readFully(data);
		}finally{
			read.unlock();
		}	
	    return new Frame(tag,data);

	}
	
	public void close() throws IOException {
		this.s.close();
	}
}