package com.example.pranavdadlani.instantmessenger;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


import serializable.*;

public class PrivateChatActivity extends AppCompatActivity {

    public int portNumber= 7001;
    public static ArrayAdapter<String> privateArrayAdapter;

    public static ArrayList<String> privateChatList = new ArrayList<String>();
    public ListView privatListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);
        final String destIp = getIntent().getExtras().getString("privateChatAddress");
        final String nameOfDestination = getIntent().getExtras().getString("namePrivate");
        final String myName = getIntent().getExtras().getString("myName");
        privatListView=(ListView)findViewById(R.id.listViewPrivate);
        privateArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,privateChatList);
        privatListView.setAdapter(privateArrayAdapter);
        new ReceiverThreadPrivate(destIp,this).start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final EditText chatText = (EditText)findViewById(R.id.editTextPrivate);
        Button btn_send = (Button)findViewById(R.id.buttonPrivate_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateChatList.add(myName + " : " + chatText.getText().toString());
                privateArrayAdapter.notifyDataSetChanged();
                new TcpClient( getApplicationContext(),new Message("chat",new ChatMessage(myName,chatText.getText().toString())) ,destIp,portNumber ).execute();
                chatText.setText("");

            }
        });

    }



}

class ReceiverThreadPrivate extends Thread
{
    public String destinationIp;
    public ServerSocket privateServSocket = null;
    public Socket incomingSocket=null;
    public int sourceSocketPort;
    public Context context;
    ReceiverThreadPrivate(String destIp,Context context)
    {
        this.destinationIp=destIp;
        this.sourceSocketPort=7001;
        this.context=context;
        try {
            privateServSocket = new ServerSocket(sourceSocketPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void run()
    {
        while(true)
        {
            try {
                incomingSocket = privateServSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(incomingSocket.getInetAddress().getHostAddress().equals(destinationIp))
            {
                try {
                    ObjectInputStream ois = new ObjectInputStream(incomingSocket.getInputStream());
                    final Message message = (Message) ois.readObject();
                    if(message.type.equals("chat")) {
                        final ChatMessage receivedChatMsg = (ChatMessage) message.data;
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                PrivateChatActivity.privateChatList.add(receivedChatMsg.name + " : " + receivedChatMsg.message);
                                PrivateChatActivity.privateArrayAdapter.notifyDataSetChanged();
                            }
                        });



                    }

                    } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }

    }

}



