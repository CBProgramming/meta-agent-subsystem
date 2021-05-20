package GUI;

import GUI.GUIRouterMonitor;
import GUI.GUIPortalMonitor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import metaagentsubsystem.FileManager;
import metaagentsubsystem.Portal;
import metaagentsubsystem.Router;
import metaagentsubsystem.UserAgent;

/**
 * Class Router Controller enables the displaying of information from routers
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class RouterController extends JPanel
{ 
    Router router;
    
    FileManager sysMsgFM;
    FileManager userMsgFM;
    TableManager userMsgTM;
    
    Vector<Portal> portals; // temp would get it out of the router.    
    
    JPanel routerControlPanel;
    
    JPanel routerFunctionPanel;
    
    Border border;
    
    JPanel portAdvertisePanel;
    JLabel portAdvertiseLabel;
    JTextField portAdvertiseTextField;
    JButton portAdvertiseButton;
    
    JPanel manageAgentsPanel;
    JComboBox manageAgentsComboBox;
    JButton addAgentButton;
    JButton removeAgentButton;
    
    JPanel routerConnectPanel;
    JLabel routerConnectPortLabel;
    JTextField routerConnectPortTextField;
    JLabel routerConnectAddressLabel;
    JTextField routerConnectAddressTextField;
    JButton routerConnectButton;
    
    JScrollPane routerInformationScrollPane;
    JTabbedPane routerInformationTabbedPane;
    
    JPanel routerRoutingTablePanel;
    
    JPanel routerMessagePanel;
    JTextArea routerMessageTextArea;
    
    JPanel nodesPanel;
    JPanel portalNodesPanel;
    JPanel routerNodesPanel;
    
    JPanel portalActivityMonitorPanel;
    
    JPanel agentListPanel;
    DefaultMutableTreeNode top;
    JTree agentListTree;
    
    JScrollPane agentActivityScrollPane;
    
    JTable agentActivityTable;
    
    /**
     * Constructor for the Router controller 
     */
    public RouterController()
    {
        router = Router.createNew(JOptionPane.showInputDialog(null, "Router Name:", "New Router", PLAIN_MESSAGE));
        
        if(router.getHandle() == null || router.getHandle() == "")
        {
            System.exit(0);
        }
        
        sysMsgFM = new FileManager(router.getHandle() + "_SysMsgLog");
        userMsgFM = new FileManager(router.getHandle() + "_userMsgLog");
        userMsgTM = new TableManager(this);
        
        router.setMonitor(new GUIRouterMonitor(sysMsgFM, userMsgFM, userMsgTM));

        portals = new Vector();
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        routerControlPanel = new JPanel();
        routerControlPanel.setLayout(new BoxLayout(routerControlPanel, BoxLayout.Y_AXIS));
        border = BorderFactory.createTitledBorder("Router Control Panel: [" + router.getHandle() + "]");
        routerControlPanel.setBorder(border);
        this.add(routerControlPanel);
        
        routerFunctionPanel = new JPanel();
        routerFunctionPanel.setLayout(new BoxLayout(routerFunctionPanel, BoxLayout.X_AXIS));
        routerControlPanel.add(routerFunctionPanel);
      
        portAdvertisePanel = new JPanel();
        portAdvertisePanel.setLayout(new BoxLayout(portAdvertisePanel, BoxLayout.X_AXIS));
        border = BorderFactory.createTitledBorder("Advertise Router");
        portAdvertisePanel.setBorder(border);
        routerFunctionPanel.add(portAdvertisePanel);
        
        portAdvertiseLabel = new JLabel("Port ");
        portAdvertisePanel.add(portAdvertiseLabel);
        
        portAdvertiseTextField = new JTextField(3);
        portAdvertisePanel.add(portAdvertiseTextField);

        portAdvertiseButton = new JButton("Advertise");
        portAdvertiseButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String portAdvertiseText = portAdvertiseTextField.getText();
                int portAdvertiseInt = Integer.parseInt(portAdvertiseText.trim());
                
                //to do validate to make sure its an int
                
                try 
                {
                    router.advertiseConnection(portAdvertiseInt);
                } 
                catch (IOException ex) 
                {
                    
                }
                portAdvertiseButton.setEnabled(false);
                portAdvertiseTextField.setEditable(false);
                //don't know much about try-catch's not sure if someone can help me with this.
            }
        });
        portAdvertisePanel.add(portAdvertiseButton);
        
        manageAgentsPanel = new JPanel();
        manageAgentsPanel.setLayout(new BoxLayout(manageAgentsPanel, BoxLayout.X_AXIS));
        border = BorderFactory.createTitledBorder("Manage Agents");
        manageAgentsPanel.setBorder(border);
        routerFunctionPanel.add(manageAgentsPanel);
        
        String[] agentTypeArray = {"Portal", "User"};
        manageAgentsComboBox = new JComboBox(agentTypeArray);
        manageAgentsPanel.add(manageAgentsComboBox);
        
        addAgentButton = new JButton("Add");
        addAgentButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(manageAgentsComboBox.getSelectedIndex() == 0)
                {
                    Portal newPortal = new Portal(JOptionPane.showInputDialog(null, "Portal Name:", "Add Portal", PLAIN_MESSAGE), router);
                    addPortal(newPortal);
                }
                
                else if(manageAgentsComboBox.getSelectedIndex() == 1)
                {
                    if(portals.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null, "No portals to connect to.", "Error", ERROR_MESSAGE);
                    }
                    
                    else
                    {
                        PortalController portalController = new PortalController("New User", RouterController.this, portals);
                        //portalController.pack();
                        portalController.setResizable(false);
                        portalController.setSize(new Dimension(200,150));
                        portalController.setVisible(true);
                        

//                        MsgWindow msgWindow = new MsgWindow(testUser.getHandle(), testUser);
//                        msgWindow.pack();
//                        msgWindow.setVisible(true);
                        //TreePath path = agentListTree.getNextMatch(newUser.portal.name, 0, Position.Bias.Backward);
                        //DefaultMutableTreeNode node = path.getLastPathComponent();
                        //createNode(node , newUser.name);
                    }
                }
            }           
        });
        manageAgentsPanel.add(addAgentButton);
        
        removeAgentButton = new JButton("Remove");
        removeAgentButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String portalHandle = JOptionPane.showInputDialog(null, "Agent Name:", "Remove Agent", PLAIN_MESSAGE);
                portals.removeIf(p -> (p.getHandle().equals(portalHandle)));
                router.removeAgent(portalHandle);
            }           
        });
        manageAgentsPanel.add(removeAgentButton);
        
        routerConnectPanel = new JPanel();
        routerConnectPanel.setLayout(new BoxLayout(routerConnectPanel, BoxLayout.X_AXIS));
        border = BorderFactory.createTitledBorder("Connect Router");
        routerConnectPanel.setBorder(border);
        routerFunctionPanel.add(routerConnectPanel);
        
        routerConnectPortLabel = new JLabel("Port ");
        routerConnectPanel.add(routerConnectPortLabel);
        
        routerConnectPortTextField = new JTextField(3);
        routerConnectPanel.add(routerConnectPortTextField);
        
        routerConnectAddressLabel = new JLabel("Address ");
        routerConnectPanel.add(routerConnectAddressLabel);
        
        routerConnectAddressTextField = new JTextField(9);
        routerConnectPanel.add(routerConnectAddressTextField);
        
        routerConnectButton = new JButton("Connect");
        routerConnectButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String routerConnectPortText = routerConnectPortTextField.getText();
                int routerConnectPortInt = Integer.parseInt(routerConnectPortText.trim());

                router.newConnection(routerConnectAddressTextField.getText(), routerConnectPortInt);
            }
        });
        routerConnectPanel.add(routerConnectButton);
        
        routerInformationTabbedPane = new JTabbedPane();
        
        routerRoutingTablePanel = new JPanel();
        routerInformationTabbedPane.addTab("Routing Table", null, 
                                           routerRoutingTablePanel, 
                                           "In Development. List of pathways to all nodes connected to this router."); // thinking a JTextArea instead that reads from a file
        
        routerMessagePanel = new JPanel(new BorderLayout());
        routerInformationTabbedPane.addTab("System Messages", null, 
                                           routerMessagePanel,
                                           "System messages passed by monitors."); // thinking a JTextArea instead that reads from a file
        
        routerMessageTextArea = new JTextArea();
        routerMessagePanel.add(routerMessageTextArea, BorderLayout.WEST);
        
        nodesPanel = new JPanel();
        nodesPanel.setLayout(new BoxLayout(nodesPanel, BoxLayout.X_AXIS)); 
        
        portalNodesPanel = new JPanel();
        portalNodesPanel.setLayout(new BoxLayout(portalNodesPanel, BoxLayout.Y_AXIS));
        border = BorderFactory.createTitledBorder("Portals");
        portalNodesPanel.setBorder(border);
        nodesPanel.add(portalNodesPanel);
        
        routerNodesPanel = new JPanel();
        routerNodesPanel.setLayout(new BoxLayout(routerNodesPanel, BoxLayout.Y_AXIS));
        border = BorderFactory.createTitledBorder("Routers");
        routerNodesPanel.setBorder(border);
        nodesPanel.add(routerNodesPanel);
        
        routerInformationTabbedPane.addTab("Nodes", null, 
                                           nodesPanel,
                                           "In Development. List of nodes connected to this router."); //would add labels to represent nodes to corresponding panel
        
        routerInformationScrollPane = new JScrollPane(routerInformationTabbedPane);
        routerControlPanel.add(routerInformationScrollPane);
        
        this.add(routerControlPanel);
        
        portalActivityMonitorPanel = new JPanel();
        portalActivityMonitorPanel.setLayout(new BoxLayout(portalActivityMonitorPanel,
                                             BoxLayout.X_AXIS));
        border = BorderFactory.createTitledBorder("Portal Activity Monitor: [" + router.getHandle() + "]");
        portalActivityMonitorPanel.setBorder(border);
        
        agentListPanel = new JPanel();
        border = BorderFactory.createTitledBorder("Agent List");
        agentListPanel.setBorder(border);
        portalActivityMonitorPanel.add(agentListPanel);
        
        top = new DefaultMutableTreeNode(router.getHandle());
        agentListTree = new JTree(top);
        agentListPanel.add(agentListTree);

        String[] columnNames = {"Time", "Session", "Id", "From", "To", "Message"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        agentActivityTable = new JTable(tableModel);
        agentActivityTable.setEnabled(false);
        agentActivityTable.getTableHeader().setReorderingAllowed(false);
        
        agentActivityScrollPane = new JScrollPane(agentActivityTable); 
        portalActivityMonitorPanel.add(agentActivityScrollPane);
        
        this.add(portalActivityMonitorPanel);
    }
    
    /**
     * Method to display to node creation
     * @param node name
     * @param agentName agent name 
     */
    public void createNode(DefaultMutableTreeNode node, String agentName)
    {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(agentName);
        node.add(newNode);
        agentListTree.repaint();
    }
    
    /**
     * Method to update the system message log 
     * @param msg message data
     */
    public void updateSysMsgLog(String msg)
    {
        routerMessageTextArea.append(msg);
        //this would do one line at a time, 
        //I think this is incorrect as I would want to update from here and not from the file manager
        //but also think it should update as soon as something is fired.
    }
    
    /**
     * Method for displaying port information when portal added
     * @param newPortal portal object
     */
    public void addPortal(Portal newPortal)
    {
        router.addAgent(newPortal);
        newPortal.setMonitor(new GUIPortalMonitor(sysMsgFM, userMsgFM, userMsgTM));
                   
        portals.add(newPortal);
        createNode(top, newPortal.getHandle());
        
        portalNodesPanel.add(new JLabel(newPortal.getHandle()));
    }
    
    /**
     * Display method for add connection in the router control panel 
     * @param handle name of the connection
     */
    public void addConnection(String handle)
    {
        routerNodesPanel.add(new JLabel(handle));
    }
    
    /**
     * This method is for updating the table
     * @param time time of the update
     * @param session number
     * @param id identification
     * @param from name
     * @param to name 
     * @param msg message data
     */
    public void updateTable(String time, int session, int id, String from, String to, String msg)
    {        
        String sessionString = Integer.toString(session);
        String idString = Integer.toString(id);
        
        String[] rowData = {time, sessionString, idString, from, to, msg};
        
        DefaultTableModel tableModel = (DefaultTableModel) agentActivityTable.getModel();
        tableModel.addRow(rowData);
        
        agentActivityTable.repaint();
    }
    
    /**
     * No Idea
     * @param line 
     */
    public void updateTab(String line)
    {
        routerMessageTextArea.append(line + "\n");
    }
}