package whatschat;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class WhatsChatGUI extends JFrame {
    public static JFrame frame;
    public static DefaultListModel<String> onlineUserListModel = new DefaultListModel<>();
    public static DefaultListModel<String> groupsListModel = new DefaultListModel<>();

    /**
     * Launch the application.
     */
    public WhatsChatGUI() {
        frame = new JFrame("ICT2103 - WhatsChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainConstraints = new GridBagConstraints();
        mainConstraints.weightx = 1;
        mainConstraints.weighty = 1;
        mainConstraints.insets = new Insets(10, 10, 10, 10);

        JPanel topItemsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel onlineUsersLabel = new JLabel("Users Online");
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        topItemsPanel.add(onlineUsersLabel, constraints);

        JList<String> onlineUsersList = new JList<String>(onlineUserListModel);
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.1;
        constraints.weighty = 0.9;
        constraints.gridx = 0;
        constraints.gridy = 1;
        topItemsPanel.add(onlineUsersList, constraints);

        JLabel groupsLabel = new JLabel("Groups");
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.9;
        constraints.weighty = 0.1;
        constraints.gridx = 1;
        constraints.gridy = 0;
        topItemsPanel.add(groupsLabel, constraints);

        JList<String> groupsList = new JList<String>(groupsListModel);
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.1;
        constraints.weighty = 0.9;
        constraints.gridx = 1;
        constraints.gridy = 1;
        topItemsPanel.add(groupsList, constraints);

        JPanel manageGroupPanel = new JPanel(new GridLayout(5, 0));
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.9;
        constraints.weighty = 0.1;
        constraints.gridx = 2;
        constraints.gridy = 1;

        JButton joinButton = new JButton("Join");
        joinButton.setEnabled(false);
        joinButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (groupsList.getSelectedIndex() != -1) {
                    String name = groupsList.getSelectedValue();
                    name = name.replace("    <<Active>>", "");
                    WhatsChat.activeGroupIp = getIpFromGroupName(name);
                    updateGroup();
                    updateChat();
                    updateOnlineUsers();
                }

            }
        });
        JButton newGroupButton = new JButton("New Group");
        newGroupButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AddNewGroupGUI newgrp = new AddNewGroupGUI();
                // newgrp.setVisible(true);
            }
        });
        JButton manageGroupButton = new JButton("Manage Group");
        manageGroupButton.setEnabled(false);
        manageGroupButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (groupsList.getSelectedIndex() != -1) {
                    String name = groupsList.getSelectedValue();
                    name = name.replace("    <<Active>>", "");
                    ManageGroupGUI mnggrp = new ManageGroupGUI(getIpFromGroupName(name));
                }
                // mnggrp.setVisible(true);
            }
        });
        JButton leaveButton = new JButton("Leave");
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (groupsList.getSelectedIndex() != -1) {
                    String name = groupsList.getSelectedValue();
                    String ip = getIpFromGroupName(name);
                    WhatsChat.activeGroupIp = "230.1.1.1";
                    try {
                        WhatsChat.SENDER_QUEUE.put("GROUP:" + ip + "!" + name);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    WhatsChat.groups.remove(ip);

                    updateGroup();
                    updateChat();
                    updateOnlineUsers();
                }

            }

        });
        manageGroupPanel.add(joinButton);
        manageGroupPanel.add(newGroupButton);
        manageGroupPanel.add(manageGroupButton);
        manageGroupPanel.add(leaveButton);
        topItemsPanel.add(manageGroupPanel, constraints);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        JScrollPane textAreaScrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel sendMessagePanel = new JPanel(new GridBagLayout());

        JLabel sendMessageLabel = new JLabel("Message");
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        sendMessagePanel.add(sendMessageLabel, constraints);

        JTextField messageTextView = new JTextField();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.9;
        constraints.weighty = 0.9;
        constraints.gridx = 1;
        constraints.gridy = 0;
        sendMessagePanel.add(messageTextView, constraints);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String msgString = "MESSAGE:" + WhatsChat.activeGroupIp + "!" + WhatsChat.name + ": "
                        + messageTextView.getText();
                try {
                    WhatsChat.SENDER_QUEUE.put(msgString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        constraints.gridx = 2;
        constraints.gridy = 0;
        sendMessagePanel.add(sendButton, constraints);

        mainConstraints.anchor = GridBagConstraints.NORTH;
        mainConstraints.fill = GridBagConstraints.BOTH;
        mainConstraints.weightx = 0.1;
        mainConstraints.weighty = 0.1;
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 0;
        mainPanel.add(topItemsPanel, mainConstraints);

        mainConstraints.anchor = GridBagConstraints.NORTH;
        mainConstraints.fill = GridBagConstraints.BOTH;
        mainConstraints.weightx = 0.9;
        mainConstraints.weighty = 0.9;
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 1;
        mainPanel.add(textAreaScrollPane, mainConstraints);

        mainConstraints.anchor = GridBagConstraints.NORTH;
        mainConstraints.fill = GridBagConstraints.BOTH;
        mainConstraints.weightx = 0.1;
        mainConstraints.weighty = 0.1;
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 2;
        mainPanel.add(sendMessagePanel, mainConstraints);

        frame.setLayout(null);
        frame.setSize(600, 600);
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null);
        updateGroup();

        groupsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (groupsList.getSelectedValue() != null) {
                    if (groupsList.getSelectedValue().replace("    <<Active>>", "").equals("All")) {
                        manageGroupButton.setEnabled(false);
                    } else {
                        manageGroupButton.setEnabled(true);
                    }

                    if (groupsList.getSelectedValue().contains("    <<Active>>")) {
                        joinButton.setEnabled(false);
                    } else {
                        joinButton.setEnabled(true);
                    }
                }
            }
        });
    }

    public static void updateChat() {
        JTextArea chat = ((JTextArea) ((JScrollPane) frame.getContentPane().getComponent(1)).getViewport()
                .getComponent(0));
        ArrayList<String> messages = WhatsChat.groups.get(WhatsChat.activeGroupIp).messages;
        String msgs = "";
        for (String msg : messages) {
            msgs += (msg + "\n");
        }
        chat.setText(msgs);
    }

    public static void appendChat(String message) {
        JTextArea chat = ((JTextArea) ((JScrollPane) frame.getContentPane().getComponent(1)).getViewport()
                .getComponent(0));
        chat.setText(chat.getText() + message + "\n");
    }

    public static void updateOnlineUsers() {
        Group activeGroup = WhatsChat.groups.get(WhatsChat.activeGroupIp);
        onlineUserListModel.clear();
        for (String user : WhatsChat.ONLINE_USERS) {
            if (activeGroup.members.contains(user)) {
                onlineUserListModel.addElement(user);
            }
        }
    }

    public static void updateGroup() {
        groupsListModel.clear();
        for (Group group : WhatsChat.groups.values()) {
            String grpName = group.name;
            if (grpName.equals(WhatsChat.groups.get(WhatsChat.activeGroupIp).name)) {
                grpName += "    <<Active>>";
            }
            if (group.memberOf == true) {
                groupsListModel.addElement(grpName);
            }

        }
    }

    public static String getIpFromGroupName(String grpName) {
        for (Group group : WhatsChat.groups.values()) {
            if (grpName.equals(group.name)) {
                return group.ip;
            }
        }
        return null;
    }
}
