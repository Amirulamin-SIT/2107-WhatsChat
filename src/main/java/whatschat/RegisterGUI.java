package whatschat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/**
 * RegisterGUI
 */
public class RegisterGUI {

    public static JFrame register()
    {
        JFrame frame = new JFrame("WhatsChat - Enter Username");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,200);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelName = new JLabel("Enter Username: ");
        c.gridy = 0;
        c.gridx = 0;
        frame.add(labelName, c);
        JTextField textFieldName = new JTextField();
        c.gridy = 1;
        c.gridx = 0;
        frame.add(textFieldName, c);
        JButton buttonConfirm = new JButton("Confirm");
        c.gridy = 1;
        c.gridx = 1;
        frame.add(buttonConfirm, c);
        JLabel labelWarning = new JLabel();
        c.gridy = 2;
        c.gridx = 0;
        frame.add(labelWarning, c);

        return frame;
    }
}