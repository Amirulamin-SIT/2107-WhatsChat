package whatschat;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * MessageManager
 */

public class MessageManager implements Runnable {
    private LinkedBlockingQueue<String> processingQueue;
    private LinkedBlockingQueue<String> senderQueue;
    private String name;

    public MessageManager(LinkedBlockingQueue<String> processingQueue, LinkedBlockingQueue<String> senderQueue,
            String name) {
        this.processingQueue = processingQueue;
        this.senderQueue = senderQueue;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            Scanner input = new Scanner(System.in);
            boolean running = true;
            while (running) {
                String message = processingQueue.take();
                String type = message.substring(0, 7);
                String leftovers = message.substring(8);

                switch (type) {
                case "REQUEST": // Request for info
                    String[] reqFor = leftovers.split(":");
                    if (reqFor[0].equals("Users")) { // Request for User names
                        retname(WhatsChat.users.keySet());
                    } else if (reqFor[0].equals("Groups")) { // Request for Group names

                    } else if (reqFor[0].equals("Online"))
                        if (name != null) {
                            senderQueue.put("ONLINEU:" + name);
                        }

                    break;

                case "ONLINEU": // User Has come online
                    WhatsChat.ONLINE_USERS.add(leftovers);
                    WhatsChat.users.putIfAbsent(leftovers, new User(leftovers));
                    WhatsChatGUI.updateOnlineUsers();
                    break;

                case "OFLINEU": // User has went offline
                    WhatsChat.ONLINE_USERS.remove(leftovers);
                    WhatsChatGUI.updateOnlineUsers();
                    break;

                case "MESSAGE": // Message recieved. Extract the IP and store into the respective group
                    String ip = leftovers.split("!")[0];
                    String msg = leftovers.replace(ip + "!", "");
                    WhatsChat.groups.get(ip).messages.add(msg); // Add message to group
                    if (ip.equals(WhatsChat.activeGroupIp)) {
                        WhatsChatGUI.appendChat(msg);
                    }
                    break;

                // ===== USER REGISTRATION =====
                case "REGISTR": // Registration of user
                    // TODO: Add options for description
                    WhatsChat.users.putIfAbsent(leftovers, new User(leftovers));
                    WhatsChat.groups.get("230.1.1.1").members.add(leftovers);
                    WhatsChat.ONLINE_USERS.add(leftovers);
                    WhatsChatGUI.updateOnlineUsers();
                    break;

                case "RETNAME": // List of names from network
                    String[] names = leftovers.split(",");
                    for (String name : names) {
                        WhatsChat.ONLINE_USERS.add(name);
                        WhatsChat.users.putIfAbsent(name, new User(name));
                        WhatsChat.groups.get("230.1.1.1").members.add(name);
                        WhatsChatGUI.updateOnlineUsers();
                    }
                    break;

                case "NAMECHK": // If it detects that someone trying to use same name then send name back
                    if (leftovers.equals(name)) {
                        senderQueue.put("RETNAME:" + name);
                    }
                    break;

                case "REGGROP": // Register Group
                    String groupName = leftovers.split(":")[0];
                    String groupIp = leftovers.split(":")[1];
                    Group group = new Group(groupName, groupIp);

                    String[] members = leftovers.replace(groupName + ":", "").replace(groupIp + ":", "").split(",");
                    ArrayList<String> memers = new ArrayList<String>(Arrays.asList(members));
                    group.setUsers(memers);
                    if (memers.contains(WhatsChat.name)) {
                        group.memberOf = true;

                    }
                    WhatsChat.groups.putIfAbsent(groupIp, group);
                    WhatsChatGUI.updateGroup();
                    break;

                case "GROUPMT": // Group Matters
                    String gIp = leftovers.split("!")[0];
                    leftovers = leftovers.replace(gIp + "!", "");
                    String gType = leftovers.substring(0, 3);
                    String gLeft = leftovers.substring(4);
                    switch (gType) {
                    case "add": // add member
                        WhatsChat.groups.get(gIp).members.add(gLeft);
                        WhatsChatGUI.updateOnlineUsers();
                        break;
                    case "rmv": // remove member
                        WhatsChat.groups.get(gIp).members.remove(gLeft);
                        WhatsChatGUI.updateOnlineUsers();
                        break;
                    case "rnm": // rename group
                        WhatsChat.groups.get(gIp).name = gLeft;
                        WhatsChatGUI.updateGroup();
                        break;
                    default:
                        break;
                    }
                default:
                    break;
                }
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ===== STATIC METHODS =====

    // Send list of names to multicast
    static void retname(Set<String> users) {
        String msg = "RETNAME:";
        for (String user : users) {
            // If msg longer than 1000 chars then split into more than one message
            if (msg.length() + user.length() >= 1000) {
                WhatsChat.SENDER_QUEUE.add(msg);
                msg = "RETNAME:";
                msg += (user + ",");
            } else { // Append username to end of message
                msg += (user + ",");
            }
            WhatsChat.SENDER_QUEUE.add(msg);
        }

    }
}