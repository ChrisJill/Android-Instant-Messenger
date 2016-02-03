package com.example.pranavdadlani.instantmessenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import serializable.*;
/**
 * Created by pranavdadlani on 11/28/15.
 */
public class ChatRoomFragment extends Fragment{
    public static ArrayList<String> chatList = new ArrayList<String>();
    public ListView listView;
    public String destinationIp;
    public int destinationPort;
    public static ArrayAdapter<String> arrayAdapter;
    public String name;
    public ChatRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
         name = intent.getExtras().getString("name");
        destinationIp= intent.getExtras().getString("desAddress");
        destinationPort= Integer.valueOf(intent.getExtras().getString("desPort"));

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.chat, container, false);
        listView =(ListView)rootView.findViewById(R.id.chatRoomListView);
        chatList.add("Welcome " + name + "!");
        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, chatList);
        listView.setAdapter(arrayAdapter);

        new ReceiverThread(getActivity(),name).start();
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //sleep to make sure the android serverSocket is started
        Message joinMessage = new Message("join",new JoinPacket(name,6000));
        new TcpClient(getActivity().getApplicationContext(),joinMessage,destinationIp,destinationPort).execute();
        //will have to send "send" after socket is opened for listening

        Toast.makeText(getActivity().getApplicationContext(), "Connected to Server", Toast.LENGTH_LONG).show();
        //android receiver listening at 6000..connecting TestServer at port 5001
        //set up a receiver to receive the numbers and fisplay it dynamically in list view
        Button btnSend =(Button)rootView.findViewById(R.id.button_send);
        final EditText textToSend =(EditText)rootView.findViewById(R.id.chat_text);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatList.add(name + " : " + textToSend.getText().toString());
                arrayAdapter.notifyDataSetChanged();
                Message chatMessage = new Message("chat", new ChatMessage(name,textToSend.getText().toString()));
                 new TcpClient(getActivity().getApplicationContext(),chatMessage,destinationIp,destinationPort).execute();
                textToSend.setText("");
            }
        });
        // Inflate the layout for this fragment
        return rootView;

    }

}

class ReceiverThread extends Thread
{
    ServerSocket androidServSocket = null;
    Socket incomingSocket=null;
    Context context;
    int sourceSocketPort;
    public String myName;
    ReceiverThread(Context context,String name )
    {
        this.myName=name;
        this.sourceSocketPort=6000;
        this.context=context;
        try {
            androidServSocket = new ServerSocket(sourceSocketPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run()
    {

        while(true)
        {
            try {
                final Socket incomingSocket = androidServSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(incomingSocket.getInputStream());
                try {
                    final Message message = (Message) ois.readObject();
                    if(message.type.equals("chat"))
                    {
                        final ChatMessage receivedChatMsg =(ChatMessage) message.data;

                     // add   receivedChatMsg.name to receivedFragment
                        //check if getting the data
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!receivedChatMsg.name.equals("server")) {
                                    if (!PrivateChatFragment.nameList.contains(receivedChatMsg.name)) {
                                        PrivateChatFragment.nameList.add(receivedChatMsg.name);
                                        PrivateChatFragment.privateChatAdapter.notifyDataSetChanged();
                                    }

                                }
                                ChatRoomFragment.chatList.add(receivedChatMsg.name + " : " + receivedChatMsg.message);
                                ChatRoomFragment.arrayAdapter.notifyDataSetChanged();
                                //update listview adapter
                            }
                        });

                        //update listview dynamically
                        //   Toast.makeText(context,message.data.toString(),Toast.LENGTH_LONG).show();
                    }
                    else if (message.type.equals("metadata"))
                    {
                        final ArrayList<Member> listOfMembers =(ArrayList<Member>) message.data;
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                for (int index = 0; index < listOfMembers.size(); index++) {
                                    if(!listOfMembers.get(index).equals("server")) {
                                        PrivateChatFragment.nameList.add(listOfMembers.get(index).name);
                                        PrivateChatFragment.privateChatAdapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        });

                    }
                    else if (message.type.equals("private"))
                    {
                        Log.d("private",message.data.toString());
                        String [] nameIpPair = message.data.toString().split(" ");
                                Message msg = new Message("requestP2P",message.data.toString()); //name,ip pair  passed
                       //send my own ip
                        //name ip (Received packet message)
                        //got ip not send request to peer on same port
                        //    TcpClient(Context context,Message msg,String destinationAddress,int destinationPort )

                        new TcpClient(context,msg,nameIpPair[1],sourceSocketPort).execute();
                    }
                    else if (message.type.equals("requestP2P"))
                    {

                        final String [] nameIpPairReceived = message.data.toString().split(" ");

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                Message msg1 = new Message("confirm",nameIpPairReceived[0]);
                                                Log.d("ip",incomingSocket.getInetAddress().getHostAddress());

                                                new TcpClient(context, msg1, incomingSocket.getInetAddress().getHostAddress(),6000).execute();
                                                Intent intent = new Intent(context, PrivateChatActivity.class);
                                                intent.putExtra("privateChatAddress",incomingSocket.getInetAddress().getHostAddress());
                                                intent.putExtra("namePrivate",nameIpPairReceived[0]);
                                                intent.putExtra("myName",myName);
                                                context.startActivity(intent);

                                                //confirm ..send confirm message and start new activity

                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:

                                                dialog.dismiss();

                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Do you want to chat with " + nameIpPairReceived[0] + " ?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();

                            }
                        });



                        //show dialog box ..to join or no ?
                        //send no


                    }
                    else if (message.type.equals("confirm"))
                    {
                        Log.d("msg", "confirmed");
                        Intent intent = new Intent(context, PrivateChatActivity.class);
                        intent.putExtra("namePrivate",message.data.toString());
                        intent.putExtra("privateChatAddress", incomingSocket.getInetAddress().getHostAddress());
                        intent.putExtra("myName",myName);
                        context.startActivity(intent);

                        //    and start activity ..start receiving at port 7000..If ip is the same

                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}