package com.example.pranavdadlani.instantmessenger;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import serializable.*;


public class TcpClient extends AsyncTask<Void, Void, Void> {
    String desAddress = null;
    int destinationPort=0;
    Socket clientSocket=null;
    Context context;
    Message msg;
    //sending android and receiving android on 5000
    TcpClient(Context context,Message msg,String destinationAddress,int destinationPort )
    {

        this.msg=msg;
        this.context=context;
        this.desAddress=destinationAddress;
        this.destinationPort=destinationPort;
    }



    @Override
    protected Void doInBackground(Void... params) {
        try {
            clientSocket = new Socket(desAddress,destinationPort);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.writeObject(msg);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


    }

    @Override
    protected void onProgressUpdate(Void... values) {

        //set progress bar
        super.onProgressUpdate(values);
    }
}
