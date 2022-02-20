package com.example.wifi_direct_example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    Button btnOnOff, btnDiscover, btnSend;
    ListView listView;
    TextView read_msg_box, connectionStatus;
    EditText writeMsg;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> eers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOnOff=(Button) findViewById(R.id.btnOnOff);
        btnDiscover=(Button) findViewById(R.id.btnDiscover);
        btnSend=(Button) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.listView);
        read_msg_box= (TextView) findViewById(R.id.read_msg_box);
        connectionStatus= (TextView) findViewById(R.id.connectionStatus);
        writeMsg=(EditText) findViewById(R.id.writeMsg);

        //initialWork();
        exqListener();

//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//
//        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
//        channel = (Channel) manager.initialize(this,getMainLooper(),null);


    }

    private void exqListener()
    {
        btnOnOff.setOnClickListener((view ->
        {
            if(wifiManager.isWifiEnabled())
            {
                wifiManager.setWifiEnabled(false);
                btnOnOff.setText("ON");
            }
            else
            {
                wifiManager.setWifiEnabled(true);
                btnOnOff.setText("OFF");
            }
        }));
    }



}