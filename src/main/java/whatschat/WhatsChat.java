package whatschat;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Hashtable;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.*;

public class WhatsChat {
    static final int BUFLENGTH = 1024;
    static final int PORT = 1153;
    static final String IP = "230.1.1.1";
    static final LinkedBlockingQueue<String> PROCESSING_QUEUE = new LinkedBlockingQueue<String>();
    static final LinkedBlockingQueue<String> SENDER_QUEUE = new LinkedBlockingQueue<String>();
    public static final HashSet<String> ONLINE_USERS = new HashSet<String>();
    public static final Hashtable<String, User> users = new Hashtable<String, User>(); // <Name, User>
    public static final Hashtable<String, Group> groups = new Hashtable<String, Group>(); // <IP, Group>
    public static String name;
    public static String activeGroupIp;

    public static void main(String[] args) throws IOException {
        // TODO: Do the data insertion stuff here
        if (!groups.containsKey("230.1.1.1")) {
            groups.put("230.1.1.1", new Group("All", "230.1.1.1"));

        }
        activeGroupIp = IP;
        InetAddress addr = InetAddress.getByName(IP);

        MulticastSocket socket = new MulticastSocket(PORT);
        socket.joinGroup(addr);

        Thread mainSender = new Thread(new Sender(socket, SENDER_QUEUE));
        mainSender.start();

        Thread mainListener = new Thread(new Listener(socket, PROCESSING_QUEUE));
        mainListener.start();

        MessageManager msgManager = new MessageManager(PROCESSING_QUEUE, SENDER_QUEUE, name);
        Thread messageManager = new Thread(msgManager);
        messageManager.start();

        try {
            SENDER_QUEUE.put("REQUEST:Users");
            Thread.sleep(3000);

            // Get the relevant components
            JFrame register = RegisterGUI.register();
            JTextField nameInput = (JTextField) register.getContentPane().getComponent(1);
            JButton btn = (JButton) register.getContentPane().getComponent(2);
            JLabel error = (JLabel) register.getContentPane().getComponent(3);

            btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println(nameInput.getText());
                    String tempName = nameInput.getText();
                    if (!isNameValid(tempName)) {
                        error.setText("Invalid Username");
                        return;
                    }
                    try {
                        SENDER_QUEUE.put("NAMECHK:" + tempName);
                        Thread.sleep(3000);

                        if (!isNameValid(tempName)) {
                            error.setText("Username in use");
                            return;
                        } else {
                            SENDER_QUEUE.put("REGISTR:" + tempName);
                            name = tempName;
                            ONLINE_USERS.add(name);
                            User u = new User(name);
                            users.put(name, u);
                            register.dispose();
                            WhatsChatGUI wcg = new WhatsChatGUI();
                            //wcg.setVisible(true);
                            WhatsChatGUI.updateOnlineUsers();
                            WhatsChatGUI.updateGroup();
                            WhatsChatGUI.updateChat();
                            SENDER_QUEUE.put("MESSAGE:Hello my name is" + name);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
            register.setVisible(true);

            // Thread msgInput = new Thread(new Input(SENDER_QUEUE, name));
            // msgInput.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ========== STATIC METHODS ==========
    static boolean isNameValid(String uName) {
        if (uName.length() > 8) {
            return false;
        }
        // Check for spaces
        if (uName.contains(" ")) {
            return false;
        }
        // Check for numeric at begining
        if (Character.isDigit(uName.charAt(0))) {
            return false;
        }

        for (String user : ONLINE_USERS) {
            if (user.equals(uName)) {
                return false;
            }
        }

        return true;
    }
}