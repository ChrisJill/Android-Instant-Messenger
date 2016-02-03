package mockserver;

import com.example.pranavdadlani.instantmessenger.PrivateChatActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import serializable.*;
public class SendMessage extends Thread {

	Socket socket;
	ObjectOutputStream oos;
    ChatMessage chat;

	SendMessage(InetAddress androidIp,ChatMessage chat) {
		try {

			this.socket = new Socket(androidIp,6000);
            this.chat=chat;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {


        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(new Message("chat", chat));

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(500);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


}
