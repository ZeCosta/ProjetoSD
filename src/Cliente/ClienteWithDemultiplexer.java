package src.Cliente;

import src.Demultiplexer;
import src.TaggedConnection;

import java.net.Socket;
import java.util.Random;

import java.util.Scanner;

public class ClienteWithDemultiplexer {
	
	private static Socket s;
    private static Demultiplexer c;

	private static void apresentarMenuRL(){
		System.out.println("-------Menu RL-------");
		System.out.println("0. Sair");
		System.out.println("1. Login");
		System.out.println("2. Register");
	}

	private static int readOption() {
        int op;
        Scanner is = new Scanner(System.in);
        System.out.print("Opção: ");
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);
        }
        catch (NumberFormatException e) { // Não foi inscrito um int
            op = -1;
        }
        return op;
    }
    
    private static String lerString(String texto) {
        Scanner is = new Scanner(System.in);
        System.out.print(texto);
        return is.nextLine();
    }

    private static boolean login(){
    	try  {
            String user=lerString("Insira o username: ");
            String pass=lerString("Insira a password: ");

	        // send request
	        c.send(1, "Ola".getBytes());
	        // get reply
	        byte[] f = c.receive(1);
	        System.out.println("(1) Reply: " + new String(f));
	    }  catch (Exception ignored) {}
    	
    	System.out.println("Nao implementado");
    	return false;
    }

    private static void register(){
    	System.out.println("Nao implementado");
    }

    public static void main(String[] args) throws Exception {
        s = new Socket("localhost", 12345);
        c = new Demultiplexer(new TaggedConnection(s));
        c.start();

    	Scanner scin = new Scanner(System.in);

        // menu login ou register
        int op=-1;
        boolean loggedin=false;

        while(op!=0 && !loggedin){
        	apresentarMenuRL();
        	op=readOption();

        	switch(op){
        		case 0:
        			System.out.println("A sair");
        			break;
        		case 1:
        			System.out.println("Login...");
        			loggedin=login();
        			//login
        			break;
        		case 2:
        			System.out.println("Register...");
        			register();
        			//regitar
        			break;
        		default:
        			System.out.println("Erro na escolha");
        			break;
        	}

        	System.out.println();
        }

        if(loggedin){
        	//fazer cenas
        }

        /*
	        Thread[] threads = {

	            new Thread(() -> {
	                try  {
	                    // send request
	                    c.send(1, "Ola".getBytes());
	                    Thread.sleep(new Random().nextInt(100));
	                    // get reply
	                    byte[] f = c.receive(1);
	                    System.out.println("(1) Reply: " + new String(f));
	                }  catch (Exception ignored) {}
	            }),

	            new Thread(() -> {
	                try  {
	                    // send request
	                    c.send(3, "Hello".getBytes());
	                    Thread.sleep(new Random().nextInt(100));
	                    // get reply
	                    byte[] f = c.receive(3);
	                    System.out.println("(2) Reply: " + new String(f));
	                }  catch (Exception ignored) {}
	            }),

	            new Thread(() -> {
	                try  {
	                    // One-way
	                    c.send(0, ":-p".getBytes());
	                }  catch (Exception ignored) {}
	            }),

	            new Thread(() -> {
	                try  {
	                    // Get stream of messages until empty msg
	                    c.send(2, "ABCDE".getBytes());
	                    for (;;) {
	                        byte[] f = c.receive(2);
	                        if (f.length == 0)
	                            break;
	                        System.out.println("(4) From stream: " + new String(f));
	                    }
	                } catch (Exception ignored) {}
	            }),

	            new Thread(() -> {
	                try  {
	                    // Get stream of messages until empty msg
	                    c.send(4, "123".getBytes());
	                    for (;;) {
	                        byte[] f = c.receive(4);
	                        if (f.length == 0)
	                            break;
	                        System.out.println("(5) From stream: " + new String(f));
	                    }
	                } catch (Exception ignored) {}
	            })

	        };

	        for (Thread t: threads) t.start();
	        for (Thread t: threads) t.join();
	    */

        c.close();
    }
}
