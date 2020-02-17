package whatschat;

import java.io.IOException;
import java.io.*;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.swing.JLabel;
import java.awt.event.*;
import java.util.Scanner;
import java.util.LinkedHashMap;

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
    public static Hashtable<String, Thread> threads = new Hashtable<String, Thread>();
    public static boolean leftall = false;

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Add Users
        File userFile = new File("users.json");
        if (userFile.exists()) {
            try {
                String content = new Scanner(userFile).useDelimiter("\\Z").next();
                Hashtable<String, LinkedHashMap> map = mapper.readValue(content, Hashtable.class);
                for (String key : map.keySet()) {
                    LinkedHashMap<String, Object> hM = map.get(key);
                    User usr = new User(key);
                    usr.description = (String) hM.get("description");
                    usr.iconLocation = (String) hM.get("iconLocation");
                    users.put(key, usr);
                }

            } catch (Exception e) {
                e.printStackTrace();
                // userFile.delete();
            }

        }

        // Add groups
        File groupFile = new File("groups.json");
        if (groupFile.exists()) {
            try {
                String content = new Scanner(groupFile).useDelimiter("\\Z").next();
                Hashtable<String, LinkedHashMap> map = mapper.readValue(content, Hashtable.class);
                for (String key : map.keySet()) {
                    LinkedHashMap<String, Object> hM = map.get(key);
                    Group grp = new Group((String) hM.get("name"), key);
                    grp.members.addAll((ArrayList<String>) hM.get("members"));
                    grp.memberOf = (Boolean) hM.get("memberOf");
                    groups.put(key, grp);
                }

            } catch (Exception e) {
                e.printStackTrace();
                // groupFile.delete();
            }

        }
        File nameFile = new File("name.json");
        if (nameFile.exists()) {
            name = new Scanner(nameFile).useDelimiter("\\Z").next();
        }

        if (!groups.containsKey("230.1.1.1")) {
            groups.putIfAbsent("230.1.1.1", new Group("All", "230.1.1.1"));

        }
        groups.get("230.1.1.1").memberOf = true;
        activeGroupIp = IP;
        InetAddress addr = InetAddress.getByName(IP);

        WhatsChatGUI wcg = new WhatsChatGUI();

        MulticastSocket socket = new MulticastSocket(null);
        socket.setReuseAddress(true);
        SocketAddress sockAddr = new InetSocketAddress(PORT);
        socket.bind(sockAddr);
        socket.joinGroup(addr);

        Thread mainSender = new Thread(new Sender(SENDER_QUEUE));
        mainSender.start();

        Thread mainListener = new Thread(new Listener(socket, PROCESSING_QUEUE));
        mainListener.start();
        threads.put(IP, mainListener);

        MessageManager msgManager = new MessageManager(PROCESSING_QUEUE, SENDER_QUEUE, name);
        Thread messageManager = new Thread(msgManager);
        messageManager.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                shutdown();
            }
        }));

        try {
            SENDER_QUEUE.put("REQUEST:Users");
            Thread.sleep(3000);
            if (name == null || users.contains(name)) {
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
                                SENDER_QUEUE.put("REQUEST:Online");
                                SENDER_QUEUE.put("REQUEST:Groups");

                                ONLINE_USERS.add(name);
                                groups.get(IP).members.add(name);

                                User u = new User(name);
                                users.putIfAbsent(name, u);

                                register.dispose();
                                wcg.frame.setVisible(true);

                                WhatsChatGUI.updateOnlineUsers();
                                WhatsChatGUI.updateGroup();
                                WhatsChatGUI.updateChat();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
                register.setVisible(true);
            } else {
                SENDER_QUEUE.put("REQUEST:Online");
                SENDER_QUEUE.put("REQUEST:Groups");
                SENDER_QUEUE.put("ONLINEU:" + name);
                WhatsChatGUI.updateOnlineUsers();
                WhatsChatGUI.updateGroup();
                WhatsChatGUI.updateChat();
                wcg.frame.setVisible(true);

            }
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

    static void shutdown() {
        try {
            SENDER_QUEUE.put("OFLINEU:" + name);
            if (name != null && !leftall) {
                ObjectMapper mapper = new ObjectMapper();

                String groupString = mapper.writeValueAsString(groups);
                FileOutputStream groupFile = new FileOutputStream("groups.json");
                groupFile.write(groupString.getBytes());
                groupFile.close();

                String userString = mapper.writeValueAsString(users);
                FileOutputStream userFile = new FileOutputStream("users.json");
                userFile.write(userString.getBytes());
                userFile.close();

                FileOutputStream nameFile = new FileOutputStream("name.json");
                nameFile.write(name.getBytes());
                nameFile.close();
            } else {
                File usersFile = new File("users.json");
                usersFile.delete();
                File groupsFile = new File("groups.json");
                groupsFile.delete();
                File nameFile = new File("name.json");
                nameFile.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}