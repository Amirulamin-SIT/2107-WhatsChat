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
                        for (Group group : WhatsChat.groups.values()) {
                            String groupName = group.name;
                            String groupIp = group.ip;

                            String members = "";
                            for (String memer : group.members) {
                                members += memer + ",";
                            }
                            String msg = "GRPINFO:" + groupIp + ":" + groupName + ":" + members;
                            WhatsChat.SENDER_QUEUE.put(msg);
                        }
                    } else if (reqFor[0].equals("Online")) {
                        if (name != null) {
                            senderQueue.put("ONLINEU:" + name);
                        }
                    } else if (reqFor[0].equals("GroupMsg")) {
                        if (WhatsChat.groups.containsKey(reqFor[1])) {
                            ArrayList<String> grpMsgs = WhatsChat.groups.get(reqFor[1]).messages;
                            String msgs = "GROUPMG:" + reqFor[1] + ":";
                            for (String string : grpMsgs) {
                                msgs += string + ",";
                            }
                            senderQueue.put(msgs);
                        }
                    }
                    break;

                case "GROUPMG": // Get group messages
                    String mgGroup = leftovers.split(":")[0];
                    String[] mgs = leftovers.replace(mgGroup + ":", "").split(",");
                    for (String string : mgs) {
                        if (!WhatsChat.groups.get(mgGroup).messages.contains(string)) {
                            WhatsChat.groups.get(mgGroup).messages.add(string);
                        }
                    }

                    break;

                // ===== USER =====
                case "ONLINEU": // User Has come online
                    if (!WhatsChat.ONLINE_USERS.contains(leftovers)) {
                        WhatsChat.ONLINE_USERS.add(leftovers);
                    }
                    WhatsChat.users.putIfAbsent(leftovers, new User(leftovers));
                    WhatsChatGUI.updateOnlineUsers();
                    processingQueue.put("MESSAGE:230.1.1.1!" + name + " has come online");
                    break;

                case "OFLINEU": // User has went offline
                    WhatsChat.ONLINE_USERS.remove(leftovers);
                    WhatsChatGUI.updateOnlineUsers();
                    break;

                case "LEAVEAL": // User is leaving All chat remove name from list
                    WhatsChat.users.remove(leftovers);
                    for (Group group : WhatsChat.groups.values()) {
                        group.members.remove(leftovers);
                    }
                    break;

                case "USRDESC":

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

                // ===== GROUP REGISTRATION =====
                case "REGGROP": // Register Group
                    String groupName = leftovers.split(":")[0];
                    String groupIp = leftovers.split(":")[1];
                    Group group = new Group(groupName, groupIp);

                    String[] members = leftovers.replace(groupName + ":", "").replace(groupIp + ":", "").split(",");
                    ArrayList<String> memers = new ArrayList<String>(Arrays.asList(members));
                    group.setUsers(memers);
                    if (memers.contains(WhatsChat.name)) {
                        group.memberOf = true;
                        Thread groupListener = new Thread(new Listener(groupIp, WhatsChat.PROCESSING_QUEUE));
                        groupListener.start();
                        WhatsChat.threads.put(groupIp, groupListener);
                    }
                    senderQueue.put("REQUEST:GroupMsg:" + groupIp);

                    WhatsChat.groups.putIfAbsent(groupIp, group);
                    WhatsChatGUI.updateGroup();
                    break;

                case "RMVGROP":
                    WhatsChat.groups.remove(leftovers);
                    break;

                case "GRPINFO":
                    String grpIp = leftovers.split(":")[0];
                    String grpName = leftovers.split(":")[1];
                    String[] grpmembers = leftovers.replace(grpIp + ":", "").replace(grpName + ":", "").split(",");
                    Group infgroup;
                    if (!WhatsChat.groups.containsKey(grpIp)) {
                        infgroup = new Group(grpName, grpIp);
                    } else {
                        infgroup = WhatsChat.groups.get(grpIp);
                    }
                    for (String member : grpmembers) {
                        infgroup.members.add(member);
                        if (member.equals(WhatsChat.name)) {
                            infgroup.memberOf = true;
                        }
                    }
                    WhatsChat.groups.put(grpIp, infgroup);
                    WhatsChatGUI.updateGroup();
                    break;

                case "GROUPMT": // Group Matters
                    String gIp = leftovers.split("!")[0];
                    leftovers = leftovers.replace(gIp + "!", "");
                    String gType = leftovers.substring(0, 3);
                    String gLeft = leftovers.substring(4);
                    switch (gType) {
                    case "add": // add member
                        if (!WhatsChat.groups.containsKey(gIp)) {
                            WhatsChat.groups.put(gIp, new Group("", gIp));
                            senderQueue.put("REQUEST:Groups");
                        }
                        WhatsChat.groups.get(gIp).members.add(gLeft);
                        if (gLeft.equals(WhatsChat.name)) {
                            WhatsChat.groups.get(gIp).memberOf = true;
                            Thread groupListener = new Thread(new Listener(gIp, WhatsChat.PROCESSING_QUEUE));
                            groupListener.start();
                            WhatsChat.threads.put(gIp, groupListener);
                        }
                        senderQueue.put("REQUEST:GroupMsg:" + gIp);
                        WhatsChatGUI.updateOnlineUsers();
                        WhatsChatGUI.updateGroup();
                        break;
                    case "rmv": // remove member
                        WhatsChat.groups.get(gIp).members.remove(gLeft);
                        if (gLeft.equals(WhatsChat.name)) {
                            try {
                                WhatsChat.groups.get(gIp).memberOf = false;
                                WhatsChat.threads.get(gIp).interrupt();
                                WhatsChat.threads.remove(gIp);
                                System.out.println(gIp);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        if (gIp.equals(WhatsChat.activeGroupIp)) {
                            WhatsChat.activeGroupIp = "230.1.1.1";
                            WhatsChatGUI.updateChat();
                        }
                        WhatsChatGUI.updateOnlineUsers();
                        WhatsChatGUI.updateGroup();
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