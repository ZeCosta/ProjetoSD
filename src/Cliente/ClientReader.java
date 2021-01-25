package src.Cliente;

import javax.xml.crypto.Data;
import java.io.DataInputStream;

public class ClientReader implements Runnable{
    private DataInputStream in;
    private Barreira b;

    public ClientReader(DataInputStream in, Barreira b){
        this.in = in;
        this.b = b;
    }

    public void run(){
        int tag;
        boolean b;
        try {
            while (true) {
                tag = in.readInt();
                b = in.readBoolean();
                if(b) {
                    switch (tag) {
                        case 1:
                            b.await

                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
