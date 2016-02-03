package com.example.pranavdadlani.instantmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public String name;
    public Socket socket =null;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
       final String destinationAddress = "localhost";
        final int destinationPort = 5000;
        final int sourcePort = 6000;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText nameTxt = (EditText)findViewById(R.id.editTxtName);

        Button joinButton  = (Button)findViewById(R.id.buttonJoin);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=nameTxt.getText().toString();

        //        Toast.makeText(getApplicationContext(),"YOOOO " +name,Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), ChatRoomActivity.class);
                i.putExtra("name", name);
                i.putExtra("desAddress","129.21.138.168");
                i.putExtra("desPort","5000");
                //129.21.138.168

                //    Log.d("name_sent",name);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                //   new TcpClient(getApplicationContext(),destinationAddress,sourcePort,destinationPort,name).execute();
            }
        });
    }




}
