package mockserver;

import com.example.pranavdadlani.instantmessenger.PrivateChatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import serializable.*;
public class TestServer {

	// android app..connect to 5001 on button click .. send join message
	// ..server receives join and sends strings in name:
	public static void main(String[] args) throws ClassNotFoundException {
		InetAddress IP = null;
		try {
			IP = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("IP of my system is := "+IP.getHostAddress());

		try {
			ServerSocket sock = new ServerSocket(5001);
			while (true) {
				Socket connectionSocket = sock.accept();
				System.out.println("Client 1 ip "+connectionSocket.getInetAddress()+" port " +connectionSocket.getPort() );
				ObjectInputStream ois = new ObjectInputStream(connectionSocket.getInputStream());
				Message message = (Message) ois.readObject();
				if (message.type.equals("join")) {
                 	new SendMessage(connectionSocket.getInetAddress(),new ChatMessage("Pranav","Hi")).start();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new SendMessage(connectionSocket.getInetAddress(),new ChatMessage("Satya","Yo,wtsup")).start();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					new SendMessage(connectionSocket.getInetAddress(),new ChatMessage("Pranav","Where is Murar?")).start();



                }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
