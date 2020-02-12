package whatschat;

import java.util.Scanner;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;

import java.util.logging.*;

/**
 * Sender
 */
public class Sender implements Runnable {
    // constants
    static final int BUFLENGTH = 1024;
    static final int PORT = 1153;
    static final String IP = "230.1.1.1";

    private LinkedBlockingQueue<String> senderQueue;
    private MulticastSocket socket;
    private InetAddress addr;
    private int port = PORT;

    // Logger
    Logger logger = Logger.getLogger(Listener.class.getName());

    public Sender(MulticastSocket socket, LinkedBlockingQueue<String> senderQueue) {
        this.socket = socket;
        this.senderQueue = senderQueue;
    }

    @Override
    public void run() {
        try {
            Scanner input = new Scanner(System.in);
            addr = InetAddress.getByName("230.1.1.1");
            Boolean running = true;
            while (running) {
                // System.out.print("Enter Message: ");
                // String message = input.nextLine();
                String message = senderQueue.take();

                System.out.println("Sent:" + message);
                byte[] b = message.getBytes();

                DatagramPacket packet = new DatagramPacket(b, b.length, addr, port);
                socket.send(packet);
            }
            socket.close();
            input.close();
        } catch (Exception e) {
            // logger.log(Level.SEVERE, "Exception on Sender: " + addr.getHostAddress());
            logger.log(Level.INFO, e.toString());
            logger.log(Level.INFO, e.getMessage());
            e.printStackTrace();
        }

    }

}