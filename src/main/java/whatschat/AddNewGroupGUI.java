package whatschat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

public class AddNewGroupGUI extends JFrame {
    public AddNewGroupGUI() {
        JFrame frame = new JFrame("WhatsChat - New Member");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainConstrain = new GridBagConstraints();
        mainConstrain.weightx = 1;
        mainConstrain.weighty = 1;

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.weightx = 1;
        constraints.weighty = 1;

        JLabel onlineUsersLabel = new JLabel("Online Users");
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weighty = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        topPanel.add(onlineUsersLabel, constraints);

        JList<String> onlineUsersList = new JList<String>(new DefaultListModel<String>());
        for (String name : WhatsChat.ONLINE_USERS) {
            ((DefaultListModel<String>) onlineUsersList.getModel()).addElement(name);
        }
        constraints.weighty = 0.9;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        topPanel.add(onlineUsersList, constraints);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 0));
        JButton addButton = new JButton("->");
        JButton removeButton = new JButton("<-");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.2;
        constraints.gridx = 1;
        constraints.gridy = 1;
        topPanel.add(buttonPanel, constraints);
        constraints.weightx = 1;

        JLabel memberLabel = new JLabel("Member");
        constraints.weighty = 0.1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 2;
        constraints.gridy = 0;
        topPanel.add(memberLabel, constraints);

        JList<String> memberList = new JList<String>(new DefaultListModel<String>());
        constraints.weighty = 0.9;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = 1;
        topPanel.add(memberList, constraints);

        JPanel bottomPanel = new JPanel(new GridBagLayout());

        JLabel groupNameLabel = new JLabel("Group Name");
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        bottomPanel.add(groupNameLabel, constraints);

        JTextField groupNameTextField = new JTextField();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.9;
        constraints.weighty = 0.1;
        constraints.gridx = 1;
        constraints.gridy = 0;
        bottomPanel.add(groupNameTextField, constraints);

        JButton createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<String> nameList = (DefaultListModel<String>) memberList.getModel();
                String ip;
                String grpName = groupNameTextField.getText();
                if (!isNameInUse(grpName)) {

                    do {
                        ip = generateIP();
                    } while (WhatsChat.groups.containsKey(ip));

                    String msg = "REGGROP:" + grpName + ":" + ip + ":";
                    Group grp = new Group(grpName, ip);

                    for (Object name : Arrays.asList(nameList.toArray())) {
                        String strName = (String) name;
                        msg += strName + ",";
                        grp.members.add(strName);
                        if(strName.equals(WhatsChat.name))
                        {
                            grp.memberOf = true;
                        }
                    }
                    WhatsChat.groups.put(ip, grp);
                    WhatsChatGUI.updateGroup();
                    try {
                        // Thread groupListener = new Thread(new Listener(ip,
                        // WhatsChat.PROCESSING_QUEUE));
                        // groupListener.start();
                        // WhatsChat.threads.put(ip, groupListener);

                        WhatsChat.SENDER_QUEUE.put(msg);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    frame.dispose();
                } else {
                    groupNameTextField.setText("Name in Use");
                }
            }

        });
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (onlineUsersList.getSelectedIndex() != -1) {
                    String name = onlineUsersList.getSelectedValue();
                    ((DefaultListModel<String>) onlineUsersList.getModel()).remove(onlineUsersList.getSelectedIndex());
                    ((DefaultListModel<String>) memberList.getModel()).addElement(name);
                }

            }
        });

        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (memberList.getSelectedIndex() != -1) {
                    String name = memberList.getSelectedValue();
                    ((DefaultListModel<String>) memberList.getModel()).remove(memberList.getSelectedIndex());
                    ((DefaultListModel<String>) onlineUsersList.getModel()).addElement(name);
                }
            }
        });

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        constraints.gridx = 2;
        constraints.gridy = 0;
        bottomPanel.add(createButton, constraints);

        mainConstrain.anchor = GridBagConstraints.NORTHWEST;
        mainConstrain.fill = GridBagConstraints.BOTH;
        mainConstrain.weightx = 0.1;
        mainConstrain.weighty = 0.1;
        mainConstrain.gridx = 0;
        mainConstrain.gridy = 0;
        mainPanel.add(topPanel, mainConstrain);

        mainConstrain.anchor = GridBagConstraints.NORTHWEST;
        mainConstrain.fill = GridBagConstraints.BOTH;
        mainConstrain.weightx = 0.9;
        mainConstrain.weighty = 0.9;
        mainConstrain.gridx = 0;
        mainConstrain.gridy = 1;
        mainPanel.add(bottomPanel, mainConstrain);

        frame.setSize(600, 500);
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String generateIP() {
        Random rand = new Random();
        int x = rand.nextInt(254) + 2;
        int y = rand.nextInt(254) + 2;
        String ip = "230.1." + Integer.toString(x) + "." + Integer.toString(y);
        return ip;
    }

    private static boolean isNameInUse(String name) {
        boolean inUse = false;
        for (Group group : WhatsChat.groups.values()) {
            if (name.equals(group.name)) {
                inUse = true;
            }
        }
        return inUse;
    }
}
