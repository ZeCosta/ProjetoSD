package src.Cliente;

import java.net.Socket;
import java.util.Random;

import java.util.Scanner;

public class ClienteSimples {
	
	private static Socket s;
    private static Stub stub;
    private static boolean permissao;

	private static void apresentarMenuRL(){
		System.out.println("--------Menu RL--------");
		System.out.println("0. Sair");
		System.out.println("1. Login");
		System.out.println("2. Register");
	}
    private static void apresentarMenuLog(){
        System.out.println("--------Menu Login--------");
        System.out.println("0. Sair");
        System.out.println("1. Comunicar Localização Atual");
        System.out.println("2. Verificar ocupação de uma Localizacao");
        System.out.println("3. Imprimir Mapa de ocupaçoes e doentes");
        if(permissao){
            System.out.println("(tem_permissao)");
        }else{
            System.out.println("(nao_tem_permissao)");
        }
        System.out.println("4. Comunicar que está infetado");
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

    private static int lerInt(String text) {
        int op;
        Scanner is = new Scanner(System.in);
        System.out.print(text);
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);
        }
        catch (NumberFormatException e) { // Não foi inscrito um int
            op = -1;
        }
        return op;
    }

    private static boolean login(){
    	//System.out.println("Nao implementado");
        String user=lerString("Insira o username: ");
        String pass=lerString("Insira a password: ");
        boolean b;

        try{
		    // send request
		    b = stub.login(user,pass);

            System.out.println("Login bem sucedido");
            permissao=b;
            
	       return true;
	    }catch(Exception e){
        	System.out.println("Erro: "+e);
        	return false;
        }

    }

    private static void register(){
    	//System.out.println("Nao implementado");
    	String user=lerString("Escolha o username: ");
        String pass=lerString("Escolha a password: ");
        boolean b;

        try{
		    // send request
		    stub.register(user,pass);
		    
		    System.out.println("Registo bem sucedido");

        }catch(Exception e){
         	System.out.println("Erro: "+e);
        }
    }

    private static void comunicarLocalizacao(){
    	//System.out.println("Nao implementado");
    	int x = lerInt("Insira a coordenada x: ");
    	int y = lerInt("Insira a coordenada y: ");

        boolean b;

        try{
		    // send request
		   	stub.comunicarLocalizacao(x,y);
 
            System.out.println("Comunicacao bem sucedida");
             

        }catch(Exception e){
         	System.out.println("Erro: "+e);
        }
    }

    private static void verificarOcupacao(){
    	//System.out.println("Nao implementado");
    	int x = lerInt("Insira a coordenada x: ");
    	int y = lerInt("Insira a coordenada y: ");

        boolean b;

        try{
		    // send request
		    stub.verificarOcupacao(x,y);

            System.out.println("Estao " +stub.verificarOcupacao(x,y) + " pessoa(s) no local (" + x + "," + y + ")");
            

        }catch(Exception e){
         	System.out.println("Erro: "+e);
        }

    }

    public static void comunicarInfecao(){
	    try{
	        String res = stub.comunicarInfecao();
	        System.out.println("Utilizador " + res + " registado como infetado");
        } catch(Exception e){
	        System.out.println("Erro " +e);
        }
    }
    

    public static void main(String[] args) throws Exception {
        s = new Socket("localhost", 12345);

        try {
        	stub = new Stub(s);
        }catch(Exception e){
        	System.out.println("Erro a criar Stub");
        }


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
        			break;
        		case 2:
                    System.out.println("Register...");
        			register();
        			break;
        		default:
        			System.out.println("Erro na escolha");
        			break;
        	}

        	System.out.println();
        }

        if(loggedin){
        	//fazer cenas
            while(op!=0){
                apresentarMenuLog();
                op=readOption();

                switch(op){
                    case 0:
                        System.out.println("A sair");
                        break;
                    case 1:
                        System.out.println("Comunicar Localização Atual");
                        comunicarLocalizacao();
                        break;
                    case 2:
                        System.out.println("Verificar ocupação de uma Localizacao");
                        verificarOcupacao();
                        break;
                    case 3:
                        System.out.println("Imprimir Mapa de ocupaçoes e doentes");
                        if(permissao){
                            System.out.println("Tem permissao");
                        }
                        else{
                           System.out.println("Não tem permissao");
                        }
                        break;
                    case 4:
                        System.out.println("Comunicar infeção");
                        comunicarInfecao();
                        break;
                    default:
                        System.out.println("Erro na escolha");
                        break;
                }
            }
        }
    }
}
