import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;

import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.locks.*;
import java.io.*;
import java.util.*;


public class Demultiplexer implements AutoCloseable{
	private TaggedConnection conn;
	private Map<Integer, Entry> map = new HashMap<Integer,Entry>();

  	Lock lock=new ReentrantLock();

  	private IOException exception = null;

	public class Entry{
		Queue<byte[]> queue = new ArrayDeque<>();
  		Condition cond= lock.newCondition();
  		int waiters = 0;
	}

	public Demultiplexer(TaggedConnection conn) {
		this.conn=conn;
	}

	private Entry get(int tag){
		Entry e = map.get(tag);
		if(e==null){
			e=new Entry();
			map.put(tag,e);
		}

		return e;
	}
	

	public void start() {
		new Thread(() -> {
			try{
				while(true){
					TaggedConnection.Frame f = conn.receive();

					lock.lock();
					try{
						Entry e = get(f.tag);
						e.queue.add(f.data);
						e.cond.signal();
					}finally{
						lock.unlock();
					}
				
				}
			}catch(IOException e){
				lock.lock();
				try{
					exception=e;
					map.forEach((k,v)->v.cond.signalAll());
				}finally{
					lock.unlock();
				}
			}

		}).start();
	}

	public void send(TaggedConnection.Frame frame) throws IOException {
		conn.send(frame);
	}
	
	public void send(int tag, byte[] data) throws IOException {
		conn.send(tag,data);
	}
	
	public byte[] receive(int tag) throws IOException, InterruptedException {
		lock.lock();
		try{
			Entry e = get(tag);
			e.waiters++;

			while(true){
				if(!e.queue.isEmpty()){
					e.waiters--;
					byte[] res=e.queue.poll();

					//Gestao de memoria
					if(e.queue.isEmpty() && e.waiters==0)
						map.remove(tag);

					return res;
				}

				if(exception != null)
					throw exception;
				
				e.cond.await();
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	public void close() throws IOException {
		this.conn.close();
	}
}