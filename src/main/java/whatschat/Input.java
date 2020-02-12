package whatschat;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.Scanner;

/**
 * MessageManager
 */

public class Input implements Runnable {
    private LinkedBlockingQueue<String> senderQueue;
    private String name;
    
    public Input(LinkedBlockingQueue<String> senderQueue, String name) {
        this.senderQueue = senderQueue;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            Scanner input = new Scanner(System.in);
            boolean running = true;
            while (running)
            {
                System.out.print("Enter Message: ");
                String msg = input.nextLine();
                senderQueue.add("MESSAGE:" + name + ":" + msg);
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}