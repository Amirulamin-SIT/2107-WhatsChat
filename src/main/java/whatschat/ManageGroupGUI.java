package whatschat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageGroupGUI extends JFrame {
    public ManageGroupGUI(String gIp) {
        String grpName = WhatsChat.groups.get(gIp).name;
        Group grp = WhatsChat.groups.get(gIp);
        JFrame frame = new JFrame("WhatsChat - Manage Groups");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainConstrain = new GridBagConstraints();

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel onlineUsersLabel = new JLabel("Online Users");
        constraints.weighty = 0.1;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        topPanel.add(onlineUsersLabel, constraints);

        JList<String> onlineUsersList = new JList<String>(new DefaultListModel<String>());
        DefaultListModel<String> onlineModel = (DefaultListModel<String>) onlineUsersList.getModel();

        for (String user : WhatsChat.ONLINE_USERS) {
            // If online user not contained in grp.members then put to Online Users
            if (!grp.members.contains(user)) {
                onlineModel.addElement(user);
            }
        }
        constraints.weighty = 0.9;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        topPanel.add(onlineUsersList, constraints);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 0));
        JButton addButton = new JButton("->");

        constraints.weightx = 0.2;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 1;
        topPanel.add(buttonPanel, constraints);
        constraints.weightx = 1;

        JLabel memberLabel = new JLabel("Member");
        constraints.weighty = 0.1;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 2;
        constraints.gridy = 0;
        topPanel.add(memberLabel, constraints);

        JList<String> memberList = new JList<String>(new DefaultListModel<String>());
        DefaultListModel<String> memberModel = (DefaultListModel<String>) memberList.getModel();
        for (String user : WhatsChat.ONLINE_USERS) {
            // If online user not contained in grp.members then put to Online Users
            if (grp.members.contains(user) && !user.equals(WhatsChat.name)) {
                memberModel.addElement(user);
            }
        }
        constraints.weighty = 0.9;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = 1;
        topPanel.add(memberList, constraints);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onlineUsersList.getSelectedIndex() != -1) {
                    String name = onlineUsersList.getSelectedValue();
                    ((DefaultListModel<String>) onlineUsersList.getModel()).remove(onlineUsersList.getSelectedIndex());
                    ((DefaultListModel<String>) memberList.getModel()).addElement(name);
                    String msg = "GROUPMT:" + gIp + "!" + "add:" + name;

                    try {
                        WhatsChat.SENDER_QUEUE.add(msg);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        JButton removeButton = new JButton("<-");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (memberList.getSelectedIndex() != -1) {
                    String name = memberList.getSelectedValue();
                    ((DefaultListModel<String>) memberList.getModel()).remove(memberList.getSelectedIndex());
                    ((DefaultListModel<String>) onlineUsersList.getModel()).addElement(name);
                    String msg = "GROUPMT:" + gIp + "!" + "rmv:" + name;

                    try {
                        WhatsChat.SENDER_QUEUE.add(msg);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        JPanel bottomPanel = new JPanel(new GridBagLayout());

        JLabel groupNameLabel = new JLabel("Group Name");
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        bottomPanel.add(groupNameLabel, constraints);

        JTextField groupNameTextField = new JTextField(grpName);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.9;
        constraints.weighty = 0.1;
        constraints.gridx = 1;
        constraints.gridy = 0;
        bottomPanel.add(groupNameTextField, constraints);

        JButton renameButton = new JButton("Rename");
        renameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    WhatsChat.SENDER_QUEUE.put("GROUPMT:" + gIp + "!" + "rnm:" + groupNameTextField.getText());
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
        bottomPanel.add(renameButton, constraints);

        mainConstrain.anchor = GridBagConstraints.NORTHWEST;
        mainConstrain.fill = GridBagConstraints.BOTH;
        mainConstrain.weightx = 0.1;
        mainConstrain.weighty = 0.1;
        mainConstrain.gridx = 0;
        mainConstrain.gridy = 0;
        mainPanel.add(bottomPanel, mainConstrain);

        mainConstrain.anchor = GridBagConstraints.NORTHWEST;
        mainConstrain.fill = GridBagConstraints.BOTH;
        mainConstrain.weightx = 0.9;
        mainConstrain.weighty = 0.9;
        mainConstrain.gridx = 0;
        mainConstrain.gridy = 1;
        mainPanel.add(topPanel, mainConstrain);

        frame.setSize(600, 500);
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
