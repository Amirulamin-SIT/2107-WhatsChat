package whatschat;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.util.concurrent.LinkedBlockingQueue;

import java.util.logging.*;

/**
 * Listener
 */
public class Listener implements Runnable {
    // allows listener to listen to localhost
    static final boolean DEBUG = true;
    static final int PORT = 1153;
    static final int BUFLENGTH = 1024;

    private LinkedBlockingQueue<String> processingQueue;
    private MulticastSocket socket;
    private InetAddress addr;
    // private int port;
    private String localhost;

    // Logger
    Logger logger = Logger.getLogger(Listener.class.getName());

    public Listener(MulticastSocket socket, LinkedBlockingQueue<String> processingQueue) {
        try {
            this.addr = InetAddress.getByName("230.1.1.1");
        } catch (Exception e) {
            logger.log(Level.INFO, e.toString());
            logger.log(Level.INFO, e.getMessage());
            e.printStackTrace();
        }

        this.socket = socket;
        this.processingQueue = processingQueue;
    }

    public Listener(String ip, LinkedBlockingQueue<String> processingQueue) {
        try {
            this.addr = InetAddress.getByName(ip);
            this.socket = new MulticastSocket(null);
            this.socket.setReuseAddress(true);
            SocketAddress sockAddr = new InetSocketAddress(PORT);
            this.socket.bind(sockAddr);

            this.socket.joinGroup(addr);
        } catch (Exception e) {
            logger.log(Level.INFO, e.toString());
            logger.log(Level.INFO, e.getMessage());
            e.printStackTrace();
        }

        this.processingQueue = processingQueue;
    }

    @Override
    public void run() {
        // logger.log(Level.INFO, "Starting Listener on: " +
        // socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());

        try {
            if (!DEBUG) {
                socket.setLoopbackMode(true);
            }

            localhost = InetAddress.getLocalHost().getHostAddress();
            byte[] buf = new byte[BUFLENGTH];

            Boolean running = true;
            logger.log(Level.INFO, "Listening...");
            while (running) {
                DatagramPacket packet = new DatagramPacket(buf, BUFLENGTH);
                socket.receive(packet);
                // Ignore if sent from same host
                if (DEBUG || !packet.getAddress().getHostAddress().equals(localhost)) {
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    if (msg.substring(0, 7).equals("MESSAGE")) {
                        msg = "MESSAGE:" + addr.toString().substring(1) + "!" + msg.substring(8);
                    }
                    processingQueue.put(msg);
                    System.out.println("Recieved: " + msg);
                }
            }
            socket.leaveGroup(addr);
            socket.close();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception on socket: " + addr.getHostAddress());
            logger.log(Level.INFO, e.toString());
            logger.log(Level.INFO, e.getMessage());
            e.printStackTrace();
        }

    }
}