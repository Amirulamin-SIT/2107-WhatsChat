package whatschat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddNewGroupGUI extends JFrame {
    public AddNewGroupGUI() {
        JFrame frame = new JFrame("WhatsChat - New Member");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(2, 0));

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

        JList onlineUsersList = new JList();
        constraints.weighty = 0.9;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        topPanel.add(onlineUsersList, constraints);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 0));
        JButton addButton = new JButton("->");
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
        });
        JButton removeButton = new JButton("<-");
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

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

        JList memberList = new JList();
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
        constraints.gridx = 0;
        constraints.gridy = 0;
        bottomPanel.add(groupNameLabel, constraints);

        JTextField groupNameTextField = new JTextField();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        bottomPanel.add(groupNameTextField, constraints);

        JButton createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
        });
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 0;
        bottomPanel.add(createButton, constraints);

        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);

        frame.setSize(600, 500);
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
