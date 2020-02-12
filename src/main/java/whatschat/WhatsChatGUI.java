package whatschat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class WhatsChatGUI extends JFrame {
    private JPanel contentPane;
    public static JFrame frame;

    /**
     * Launch the application.
     */
    public WhatsChatGUI() {
        frame = new JFrame("ICT2103 - WhatsChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(2, 0));

        JPanel topItemsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel onlineUsersLabel = new JLabel("Users Online");
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        topItemsPanel.add(onlineUsersLabel, constraints);

        JTextArea onlineUsersTextArea = new JTextArea();
        // jTextArea1.addMouseListener(new java.awt.event.MouseAdapter() {
        // public void mouseClicked(java.awt.event.MouseEvent evt) {
        // jTextArea1MouseClicked(evt);
        // }
        // });
        onlineUsersTextArea.setEditable(false);
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        topItemsPanel.add(onlineUsersTextArea, constraints);

        JLabel groupsLabel = new JLabel("Groups");
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 0;
        topItemsPanel.add(groupsLabel, constraints);

        JTextArea groupsTextArea = new JTextArea();
        groupsTextArea.setEditable(false);
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 1;
        topItemsPanel.add(groupsTextArea, constraints);

        JPanel joinPanel = new JPanel(new GridLayout(0, 2));
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 3;

        JButton joinButton = new JButton("Join");
        joinButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
        });
        JButton disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
        });
        joinPanel.add(joinButton);
        joinPanel.add(disconnectButton);
        topItemsPanel.add(joinPanel, constraints);

        JPanel manageGroupPanel = new JPanel(new GridLayout(3, 0));
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 1;
        JButton newGroupButton = new JButton("New Group");
        newGroupButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                AddNewGroupGUI newgrp = new AddNewGroupGUI();
                newgrp.setVisible(true);
            }
        });
        JButton manageGroupButton = new JButton("Manage Group");
        manageGroupButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                ManageGroupGUI mnggrp = new ManageGroupGUI();
                mnggrp.setVisible(true);
            }
        });
        JButton leaveButton = new JButton("Leave");
        leaveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
        });
        manageGroupPanel.add(newGroupButton);
        manageGroupPanel.add(manageGroupButton);
        manageGroupPanel.add(leaveButton);
        topItemsPanel.add(manageGroupPanel, constraints);

        JTextField textField = new JTextField();
        textField.setEditable(false);
        mainPanel.add(topItemsPanel);
        mainPanel.add(textField);
        frame.setSize(600, 500);
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        updateGroup();

    }

    public static void updateChat() {
        JTextField chat = (JTextField) frame.getContentPane().getComponent(1);
        ArrayList<String> messages = WhatsChat.groups.get(WhatsChat.activeGroupIp).messages;
        String msgs = "";
        for (String msg : messages) {
            msgs += (msg + "\n");
        }
        chat.setText(msgs);
    }

    public static void appendChat(String message) {
        JTextField chat = (JTextField) frame.getContentPane().getComponent(1);
        chat.setText(chat.getText() + message + "\n");
    }

    public static void updateOnlineUsers() {
        JTextArea onlineUsersField = (JTextArea) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(1);
        String names = "";
        for (String user : WhatsChat.ONLINE_USERS) {
            names += (user + "\n");
        }
        onlineUsersField.setText(names);
    }

    public static void updateGroup() {
        JTextArea groupField = (JTextArea) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(3);
        String groupString = "";
        for (Group group : WhatsChat.groups.values()) {
            groupString += (group.name + "\n");
        }
        groupField.setText(groupString);

    }

    // public static void main(String[] args) {
    //// new WhatsChat();
    //// new AddNewGroupGUI();
    // new UserProfile();
    // }
}
