package com.example.pranavdadlani.instantmessenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.AlertDialog;
import java.util.ArrayList;
import android.util.*;
import android.widget.Toast;

import serializable.Message;

/**
 * Created by pranavdadlani on 11/28/15.
 */
public class PrivateChatFragment extends Fragment {

    public static ArrayList<String> nameList = new ArrayList<String>();
    public static ArrayAdapter<String> privateChatAdapter;
    public ListView lview;

    public String destinationIp;
    public int destinationPort;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        destinationIp= intent.getExtras().getString("desAddress");
        destinationPort= Integer.valueOf(intent.getExtras().getString("desPort"));
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.p2p, container, false);

        //names of list of people online ..
        lview = (ListView)rootView.findViewById(R.id.privateChatlistView);
        privateChatAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, nameList);
         lview.setAdapter(privateChatAdapter);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String recipient = (String) parent.getItemAtPosition(position);
                //send recipient data to server at 7001 server
                //start receiver thread in mainactivity at 7001 for client...
                //
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                           //     Toast.makeText(getContext(),"Destination ip is  "+destinationIp,Toast.LENGTH_LONG).show();
                         //       Toast.makeText(getContext(),"Destination port is  "+destinationPort,Toast.LENGTH_LONG).show();


                                Message msg = new Message("private",recipient);

                        //        Toast.makeText(getContext(),"Recipient is  "+recipient,Toast.LENGTH_LONG).show();

                                new TcpClient(getActivity().getApplicationContext(),msg,destinationIp,destinationPort).execute();
                                //Yes button clicked
                           //     new TcpClient()//get ip
                            //  new TcpClient() to ip

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                        //        Toast.makeText(getContext(),"In negative ",Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Start P2P with " + recipient + " ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        return rootView;


    }


    //  public static PrivateChatFragment newInstance()
}
