package metaagentsubsystem;

import com.protos.MsgDataProtos;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Connection Class used to encapsulate sockets and facilitate sending and receiving messages over the network
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class Connection {
    
    /**
     * Class Instance Variables 
     */
    private String handle; // might not need this
    private Socket socket;
    private InputStream networkStream;
    private ObjectInputStream networkSerializedReader;
    private ObjectOutputStream networkSerializedWriter;
    private InputStreamReader networkReader;
    private DataOutputStream byteOut;
    private OutputStream out;
    private DataInputStream byteIn;
    private BufferedReader networkBufferedReader;
    private PrintWriter networkWriter;
    
    
    /**
     * Constructor for Connection class 
     * @param socket a socket is needed as a parameter
     * @throws IOException if anything is incorrect error is thrown
     */
    Connection(Socket socket) throws IOException
    {
        this(null, socket);
    }
    
    /**
     * Constructor for Connection method initialises all the necessary objects
     * used in sending and receiving messages.
     * @param handle name given to the connection
     * @param socket a socket is needed as a parameter
     * @throws IOException if anything is incorrect error is thrown
     */
    Connection(String handle, Socket socket) throws IOException
    {
        this.handle = handle;
        this.socket = socket;
        networkStream = this.socket.getInputStream();
        //networkSerializedWriter = new ObjectOutputStream(socket.getOutputStream());
        //networkSerializedWriter.flush();
        byteOut = new DataOutputStream(socket.getOutputStream());
        byteOut.flush();
        //networkSerializedReader = new ObjectInputStream(socket.getInputStream());
        byteIn = new DataInputStream(socket.getInputStream());
//        networkReader = new InputStreamReader(networkStream);
//        networkBufferedReader = new BufferedReader(networkReader);
        out = this.socket.getOutputStream();
        networkWriter = new PrintWriter(this.socket.getOutputStream(), true);
    }
    
    /**
     * Sends a MsgData over the network.
     * It encapsulates the message in a MsgDataProtos, turns it into a byte array and sends it over the network
     * @param msg is of type MsgData and includes the message data and information
     * @throws IOException 
     */
    public void sendMessage(MsgData msg) throws IOException
    {
        MsgDataProtos.MsgData.Builder message = com.protos.MsgDataProtos.MsgData.newBuilder();
        message.setSender(msg.getSender() == null ? "" : msg.getSender());
        message.setRecipient(msg.getRecipient() == null ? "" : msg.getRecipient());
        message.setMsgId(msg.getId() == 0 ? -1 : msg.getId());
        message.setBody(msg.getBody() == null ? "" : msg.getBody());
        switch(msg.getType())
        {
            case ADD:
                message.setType(MsgDataProtos.MsgData.Type.ADD);
                break;
            case REMOVE:
                message.setType(MsgDataProtos.MsgData.Type.REMOVE);
                break;
            case LIST:
                message.setType(MsgDataProtos.MsgData.Type.LIST);
                break;
            case HANDLE:
                message.setType(MsgDataProtos.MsgData.Type.HANDLE);
                break;  
            case USER:
                message.setType(MsgDataProtos.MsgData.Type.USER);
                break;    
            case RETURN:
                message.setType(MsgDataProtos.MsgData.Type.RETURN);
                break;
            default:
                message.setType(MsgDataProtos.MsgData.Type.USER);
        }        
        message.addAllAgents(msg.getAgentsList() == null ? new ArrayList<>() : msg.getAgentsList());
        System.out.println(message.toString());
        byte[] bytes = message.build().toByteArray();
        byteOut.write(bytes);
        //networkSerializedWriter.writeObject(msg);
    }
    
    /**
     * Sets the name of the handle on the connection
     * @param handle takes a string name and updates object handle variable
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }
    
    /**
     * Gets the handle of the object
     * @return String name of the objects handle
     */
    public String getHandle()
    {
        return handle;
    }
    
    /**
     * Receives a message from the network.
     * Receives a byte array, parses it into a MsgDataProtos and then creates a MsgData, puts all the information in it and returns it
     * @return the message data received in a readable object
     * @throws IOException if any errors
     * @throws ClassNotFoundException if any issues with the method type
     */
    public MsgData receiveMessage() throws IOException, ClassNotFoundException
    {
        MsgData msg = null;
        //int length = byteIn.readInt(); 
        //System.out.println(length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[1024];
        baos.write(buffer, 0 , byteIn.read(buffer));
        byte result[] = baos.toByteArray();
        /*
        if (length > 0) 
        {
            bytes = new byte[length];
            byteIn.readFully(bytes, 0, bytes.length); 
        }
        */
        MsgDataProtos.MsgData message = MsgDataProtos.MsgData.parseFrom(result);
        System.out.println(message.getType());
        MsgDataProtos.MsgData.Type newType = message.getType();
        switch(newType)
        {
            case ADD:
                msg = new MsgData("", MessageType.ADD);
                break;
            case REMOVE:
                msg = new MsgData("", MessageType.REMOVE);
                break;
            case LIST:
                msg = new MsgData("", MessageType.LIST);
                break;
            case HANDLE:
                msg = new MsgData("", MessageType.HANDLE);
                break;  
            case USER:
                msg = new MsgData("", MessageType.USER);
                break;    
            case RETURN:
                msg = new MsgData("", MessageType.RETURN);
                break;
            default:
                msg = new MsgData("", MessageType.USER);
        }  
        msg.setHandle(message.getHandle());
        msg.setAgentsList(message.getAgentsList());
        msg.setBody(message.getBody());
        msg.setId(message.getMsgId());
        msg.setMsgId(message.getMsgId());
        msg.setRecipient(message.getRecipient());
        msg.setSender(message.getSender());
        return msg;
    }
    
    /**
     * Shows if there is a message in the queue
     * @return true or false if available
     * @throws IOException for any errors
     */
    public boolean hasMessageInQueue() throws IOException
    {
        return networkStream.available() > 0;
    }
}
