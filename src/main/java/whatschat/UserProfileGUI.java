package whatschat;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UserProfileGUI extends JFrame {
    public UserProfileGUI(String username) throws IOException {
        JFrame frame = new JFrame("WhatsChat - User Profile");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(2, 0));
        
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(10, 10, 10, 10);
        
        JLabel userImageLabel = new JLabel("Profile Photo");
        constraints.weighty =0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        topPanel.add(userImageLabel, constraints);
        
        String fileName = "images/default.jpg";
        ImageIcon icon = new ImageIcon(fileName);
        constraints.weighty =1;
        JLabel label = new JLabel(icon); 
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        topPanel.add(label,constraints);
        
        JLabel userNameTextLabel = new JLabel("Name");
        constraints.weighty =0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 0;
        topPanel.add(userNameTextLabel, constraints);
        
        JLabel userNameLabel = new JLabel(username);
        constraints.weighty =1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 1;
        topPanel.add(userNameLabel, constraints);
        
        
        
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        
        JTextArea userDescription = new JTextArea();
        userDescription.setEditable(false);
        constraints.weighty =1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        userDescription.setWrapStyleWord(true);
        userDescription.setLineWrap(true);
        constraints.gridx = 0;
        constraints.gridy = 1;
        userDescription.setText("Hi, my name is " +  username);
        bottomPanel.add(userDescription, constraints);
        
        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);

        frame.setSize(600, 500);
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
