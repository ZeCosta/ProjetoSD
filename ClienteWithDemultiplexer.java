import java.net.Socket;
import java.util.Random;

public class ClienteWithDemultiplexer {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12345);
        Demultiplexer c = new Demultiplexer(new TaggedConnection(s));
        c.start();
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
        c.close();
    }
}
