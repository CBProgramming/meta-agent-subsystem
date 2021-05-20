package GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import metaagentsubsystem.Portal;
import metaagentsubsystem.Router;
import metaagentsubsystem.UserAgent;

/**
 * Class Portal Controller enables the displaying of information from routers
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class PortalController extends JFrame
{
    //Instance Variables
    private RouterController rc;
    private PortalController self;
    
    private Vector<Portal> portals;
    private Vector<String> portalHandles;
    
    private JPanel panel;
    
    private JLabel portalLabel;
    private JComboBox<String> portalComboBox;
    
    private JLabel portalTitleLabel;
    private JTextField portalTitleTextField;
    
    private JLabel userTitleLabel;
    private JTextField userTitleTextField;
    
    private JButton confirmButton;
    private JButton cancelButton;
    
    /**
     * Constructor for the GUI
     * @param title name
     * @param rc router controller
     * @param portals list of portals 
     */
    public PortalController(String title, RouterController rc, Vector<Portal> portals)
    {
        super(title);
        
        this.rc = rc;
        self = this;
        
        this.portals = portals;
        this.portalHandles = new Vector();
        this.portalHandles.add("New Portal");
        portals.forEach((n) -> portalHandles.add(n.getHandle()));
        
        panel = new JPanel();
        
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
                
        portalLabel = new JLabel("Portal:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(portalLabel, gbc);
        
        portalComboBox = new JComboBox<String>(portalHandles);
        portalComboBox.setSelectedIndex(1);
        
        portalComboBox.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e) 
            {
                if(portalComboBox.getSelectedIndex() == 0)
                {
                    portalTitleLabel.setVisible(true);
                    portalTitleTextField.setVisible(true);        
                }
                else
                {
                    portalTitleLabel.setVisible(false);
                    portalTitleTextField.setVisible(false);  
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        });
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(portalComboBox, gbc);
        
        portalTitleLabel = new JLabel("Portal Name:");
        portalTitleLabel.setVisible(false);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(portalTitleLabel, gbc);
        
        portalTitleTextField = new JTextField(6);
        portalTitleTextField.setVisible(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(portalTitleTextField, gbc);
        
        userTitleLabel = new JLabel("User Name:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(userTitleLabel, gbc);
        
        userTitleTextField = new JTextField(6);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(userTitleTextField, gbc);
        
        confirmButton = new JButton("Ok");
        confirmButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                UserAgent newUser = null;
                
                if(portalComboBox.getSelectedIndex() == 0)
                {
                    Portal newPortal = new Portal(portalTitleTextField.getText(), rc.router);
                    rc.addPortal(newPortal);
                    
                    newUser = new UserAgent(userTitleTextField.getText(), newPortal);
                    newPortal.addAgent(newUser);
                }
                else
                {
                    int i = 0;
                    String portalHandle = portalHandles.elementAt(portalComboBox.getSelectedIndex());
                    Portal portal = null;
                    
                    while(!portals.isEmpty() && i < portals.size())
                    {
                        if(portals.elementAt(i).getHandle() == portalHandle)
                        {
                            portal = portals.elementAt(i);
                        }
                        
                        i++;
                    }
                    
                    newUser = new UserAgent(userTitleTextField.getText(), portal);
                    portal.addAgent(newUser);
                }
                
                MsgWindow msgWindow = new MsgWindow(newUser.getHandle(), newUser);
                msgWindow.pack();
                msgWindow.setVisible(true);
                
                newUser.setMonitor(new GUIUserMonitor(msgWindow));
                
                self.dispose();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(confirmButton, gbc);
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(cancelButton, gbc);
        
        this.add(panel);
    }
}