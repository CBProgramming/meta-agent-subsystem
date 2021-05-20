package metaagentsubsystem;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Represents the message wrapper.
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class MsgData implements Serializable
{
    /**
     * Class Instance Variables
     */
    public static int msgCount;  
    private int msgId;
    private MessageType type;
    private String handle;
    private String timestamp;
    private String sender;
    private String recipient;
    private String body;
    private List<String> agentsList;
    
    /**
     * Constructor for messages including only two parameters
     * @param handle handle or name of agent
     * @param type message type
     */
    public MsgData(String handle, MessageType type)
    {
        this(handle, null, null, null, type);
    }


    /**
     * Constructor for messages including three parameters
     * @param sender takes a sender in
     * @param recipient and a recipient
     * @param type and type of message
     */
    public MsgData(String sender, String recipient, MessageType type)
    {
        this(null, sender, recipient, null, type);
    }
    
    /**
     * Constructor for messages including four parameters
     * @param sender sender handle
     * @param recipient recipient handle
     * @param body message body
     */
    public MsgData(String sender, String recipient, String body)
    {
        this(null, sender, recipient, body, MessageType.USER);
    }
    
    /**
     * Constructor for messages only containing list of agents
     * @param agentsList list of agent names
     */
    public MsgData(List<String> agentsList)
    {
        this(null, null, null, null, MessageType.LIST);
        setAgentsList(agentsList);
    }
    
    /**
     * Complete constructor that is used by the others
     * @param handle name of message
     * @param sender sender handle
     * @param recipient recipient handle
     * @param body message body
     * @param type type of message
     */
    public MsgData(String handle, String sender, String recipient, String body, MessageType type)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        msgCount++;
        this.msgId = msgCount;
        this.handle = handle; 
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
        this.type = type;
        this.timestamp = sdf.format(timestamp);
    }
    
    /**
     * Returns the list of agents
     * @return a list of Agents
     */
    public List<String> getAgentsList() {
        return agentsList;
    }
    
    /**
     * Method accepts a list of strings and sets the local list accordingly
     * @param agentsList is the name of a passed in list 
     */
    public void setAgentsList(List<String> agentsList) {
        this.agentsList = agentsList;
    }
    
    /**
     * Returns the message body
     * @return string 
     */
    public String getBody() {
        return body;
    }
    
    /**
     * Method sets the object body to the parameter passed in
     * @param body string is the message body
     */
    public void setBody(String body) {
        this.body = body;
    }
    
    /**
     * Method returns Id of the message object
     * @return message Id
     */
    public int getId() 
    {
        return msgId;
    }

    /**
     * Returns the handle
     * @return handle is name
     */
    public String getHandle() 
    {
        return handle;
    }
    
    /**
     * Returns the timestamp
     * @return is a string
     */
    public String getTimestamp() 
    {
        return timestamp;
    }
    
    /**
     * Returns the type
     * @return enum identifier 
     */
    public MessageType getType()
    {
        return type;
    }

    /**
     * Method which returns the sender's name 
     * @return is a string
     */
    public String getSender() {
        return sender;
    }
    
    /**
     * Method takes a parameter and sets the local variable
     * @param sender is the string name
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Method returns the message recipient
     * @return string name
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the message ID
     * @param msgId is the integer passed in
     */
    public void setId(int msgId) {
        this.msgId = msgId;
    }
    
    /**
     * Sets the recipient
     * @param recipient is the recipient's handle
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    /**
     * Returns a string containing all the important information
     * @return string
     */
    @Override
    public String toString()
    {
        return handle + "/" + sender + "/" + recipient + "/" + body + "/" + type.toString();
    }
    
    /**
     * Method to set the message Id
     * @param msgId ID
     */
    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    /**
     * Method to set the message type
     * @param type type of message
     */
    public void setType(MessageType type) {
        this.type = type;
    }

    /**
     * Method to set the message handle
     * @param handle name given
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }
    
}
