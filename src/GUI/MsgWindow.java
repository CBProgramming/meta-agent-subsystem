package GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import metaagentsubsystem.UserAgent;

/**
 * GUI Creation class for a message window object
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)  
 */
public class MsgWindow extends JFrame
{
    //instance variables 
    UserAgent user;
    
    JPanel windowPanel;
    
    JTable msgTable;
    
    JScrollPane msgScrollPane;
    
    JPanel msgFunctionPanel;
    
    JLabel recipientLabel;
    JLabel msgLabel;
    
    JTextField recipientTextField;
    JTextField msgTextField;
    
    JButton sendButton;
    JButton clearButton;
    
    /**
     * Constructor for the message window
     * @param title name
     * @param user user agent object
     */
    public MsgWindow(String title, UserAgent user)
    {
        super(title);
        this.setResizable(false);
        
        this.user = user;
        
        windowPanel = new JPanel();
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        this.add(windowPanel);
        
        String[] columnNames = {"Time", "From", "To", "Message"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        msgTable = new JTable(tableModel);
        
        msgTable.setEnabled(false);
        msgTable.getTableHeader().setReorderingAllowed(false);
        
        msgScrollPane = new JScrollPane(msgTable); 
        windowPanel.add(msgScrollPane);
        
        msgFunctionPanel = new JPanel();
        msgFunctionPanel.setLayout(new GridBagLayout());
        windowPanel.add(msgFunctionPanel);
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        recipientLabel = new JLabel("  Recipient");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        msgFunctionPanel.add(recipientLabel, gbc);
        
        msgLabel = new JLabel("  Message");
        gbc.gridx = 1;
        gbc.gridy = 0;
        msgFunctionPanel.add(msgLabel, gbc);
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                DefaultTableModel tableModel = (DefaultTableModel) msgTable.getModel();
                tableModel.setRowCount(0);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 0;
        msgFunctionPanel.add(clearButton, gbc);
        
        recipientTextField = new JTextField(7);
        gbc.gridx = 0;
        gbc.gridy = 1;
        msgFunctionPanel.add(recipientTextField, gbc);
        
        msgTextField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        msgFunctionPanel.add(msgTextField, gbc);
        
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                user.sendMessage(recipientTextField.getText(), msgTextField.getText());
                System.out.println("Carter Test Point a");
                msgTextField.setText("");
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 1;
        msgFunctionPanel.add(sendButton, gbc);
    }
    
    /**
     * Creation method for a new message
     * @param time time of message
     * @param from sender
     * @param to recipient
     * @param msg message data
     */
    public void newMsg(String time, String from, String to, String msg)
    {
        String[] rowData = {time, from, to, msg};
        
        DefaultTableModel tableModel = (DefaultTableModel) msgTable.getModel();
        tableModel.addRow(rowData);
        msgTable.repaint();
    }
    
    /**
     * method to terminate the message window
     */
    public void terminate()
    {
        dispose();
    }
}

