package mockserver;

import serializable.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class having functionality to add new user and multicast messages
 * @author satyajeet
 */
public class ChatServer {
    
    ServerSocket serverSocket;
    ArrayList<Member> members;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    
    ChatServer() {
        try {
            serverSocket = new ServerSocket(5000);
            members = new ArrayList<Member>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Accepts the new user and broadcasts his addition to all members
     */
    void start() {
        Socket s;
        Message message;
        ChatMessage cm;
        JoinPacket jp;
        
        try {
            while (true) {
                s = new Socket();                
                s = serverSocket.accept();
                ois = new ObjectInputStream(s.getInputStream());
                message = (Message) ois.readObject();

                //new user joining
                if ( message.type.equals("join") ) {
                    jp = (JoinPacket) message.data;
                    
                    //send metadata of all users in system to the new user
                    sendMessage(s.getInetAddress().getHostAddress(),
                            jp.listenPort, members, "metadata");
                    Thread.sleep(300);
                    //construct and broadcast a chat message
                    cm = new ChatMessage("server", jp.name + " has joined the chat");
                    broadcast(cm, jp.name);
                    
                    //add the new member
                    members.add(new Member(jp.name, 
                            s.getInetAddress().getHostAddress(), jp.listenPort));
                    System.out.println("New user: " + jp.name);
                } else if ( message.type.equals("chat") ) {
                    cm = (ChatMessage)message.data;
                    System.out.println("broadcasting msg from: " + cm.name);
                    broadcast(cm, cm.name);
                } else if ( message.type.equals("private") ) {
                    //find requested ip from client name and port of requesting 
                    //client

                    String clientName = (String)message.data;
                    System.out.println(clientName );

                    String clientIp = null;
                    String reqFromIp = s.getInetAddress().getHostAddress();
                    int reqFromPort = -1;
                    for ( int i = 0 ; i < members.size() ; i++ ) {
                        if ( members.get(i).name.equals(clientName) )
                            clientIp = members.get(i).ip;
                        if ( members.get(i).ip.equals( reqFromIp ) )
                            reqFromPort = members.get(i).port;
                    }
                    sendMessage(reqFromIp, reqFromPort, clientName + " " + clientIp, "private");
                }
                else {
                    //unknow message
                }
            }
        } catch( Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Broadcasts the ChatMessage object to the list of members
     */
    void broadcast(ChatMessage cm, String sender) {
        ObjectOutputStream oos;
        Socket s;
        Member m;
        try {
            for ( int i = 0 ; i < members.size() ; i++ ) {
                m = members.get(i);
                if ( !m.name.equals(sender) ) {
                    s = new Socket(m.ip, m.port);
                    oos = new ObjectOutputStream(s.getOutputStream());
                    oos.writeObject(new Message("chat", cm));
                    Thread.sleep(150);
                }
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encapsulates payload in message and sends to user
     */
    void sendMessage(String ip, int port, Object payload, String type) {
        Socket s;
        Message m;
        ObjectOutputStream oos;
        try {
            m = new Message(type, payload);
            s = new Socket(ip, port);
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}