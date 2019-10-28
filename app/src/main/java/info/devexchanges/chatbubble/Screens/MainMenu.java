package info.devexchanges.chatbubble.Screens;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.devexchanges.chatbubble.Data.GlobalVariables;
import info.devexchanges.chatbubble.Data.TempData;
import info.devexchanges.chatbubble.Implementers.DiscoveryPresenterImplementer;
import info.devexchanges.chatbubble.Implementers.WifiConnectionPresenterImplementer;
import info.devexchanges.chatbubble.Presenters.DiscoveryPresenter;
import info.devexchanges.chatbubble.Presenters.WifiConnectionPresenter;
import info.devexchanges.chatbubble.R;
import info.devexchanges.chatbubble.Recievers.WifiDirectBroadcastReciever;
import info.devexchanges.chatbubble.Views.DiscoveryView;
import info.devexchanges.chatbubble.Views.WifiConnectionView;

public class MainMenu extends AppCompatActivity implements WifiConnectionView, DiscoveryView {
    private Button btn_wifi,btn_discover;
    private WifiConnectionPresenter presenter;
    private DiscoveryPresenter discoveryPresenter;
    private ProgressBar progressBar;
    public TextView tv_status;
    private ListView connectionPeers;
    private   BroadcastReceiver mReciever;
    private   IntentFilter mIntentfilter;
    private List<WifiP2pDevice> peers ;
    private   WifiP2pDevice [] devicesArray;

    private boolean PreviousDataAvailable ;
    private TempData tempData;
//    private String Username;
    private TextView UserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Initializations();
       btn_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  presenter.handleWifiConnection();
            }
        });
        btn_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerReceiver(mReciever,mIntentfilter);
                progressBar.setVisibility(View.VISIBLE);
                discoveryPresenter.handleDiscovery();
            }
        });

        connectionPeers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiP2pConfig config =  new WifiP2pConfig();
                config.deviceAddress =devicesArray[position].deviceAddress;
                discoveryPresenter.handleDiscoveryConnection(config, devicesArray[position]);
            }
        });

    }
private void Initializations(){
    // Username = getIntent().getStringExtra("User");
    UserName = (TextView) findViewById(R.id.Username);
    UserName.setText(GlobalVariables.Username);
    tempData = new TempData(MainMenu.this,"Chatting");
    btn_wifi = (Button) findViewById(R.id.btn_wifi);
    CheckWifi();
    btn_discover = (Button) findViewById(R.id.btn_discover);
    progressBar = (ProgressBar) findViewById(R.id.progress_circular);
    tv_status = (TextView) findViewById(R.id.tv_status);
    connectionPeers = (ListView) findViewById(R.id.lst_peers);
    progressBar.setVisibility(View.INVISIBLE);
    presenter = new WifiConnectionPresenterImplementer(this,MainMenu.this);
    discoveryPresenter = new DiscoveryPresenterImplementer(this, MainMenu.this);
    mReciever =  new WifiDirectBroadcastReciever(discoveryPresenter.GetManager(),discoveryPresenter.GetChannel() , this);
    mIntentfilter =  new IntentFilter();
    mIntentfilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    mIntentfilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    mIntentfilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
    mIntentfilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    peers = new ArrayList<WifiP2pDevice>();
}

   @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
   @Override
    public void IsWifiConected() {
        btn_wifi.setBackground(getResources().getDrawable(R.drawable.wifi_icon));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void IsWifiDisconected() {
        btn_wifi.setBackground(getResources().getDrawable(R.drawable.wifi_icon_off));
    }

    @Override
    public void WifiException(String localizedMessage) {
        Toast.makeText(this, localizedMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void DisconnectUser() {
        tv_status.setVisibility(View.VISIBLE);
        tv_status.setText("Connection Disconnected!");
    }

    @Override
    public void FailedToDisconnect(String DeviceName) {
        tv_status.setVisibility(View.VISIBLE);
        tv_status.setText("Failed To Disconnection with "+DeviceName);
    }

    @Override
    public void IsUserDiscover() {
        tv_status.setVisibility(View.VISIBLE);
        tv_status.setText("Discovery is Started!");
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void IsUserNotDiscover() {
        tv_status.setVisibility(View.VISIBLE);
        tv_status.setText("Discovery is off!");
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void ConnectedToUser(String DeviceName) {
        Toast.makeText(MainMenu.this, "Connected To "+DeviceName, Toast.LENGTH_SHORT).show();
    }
    public  void UpdateList(WifiP2pDeviceList peerslst){
        if(!peerslst.getDeviceList().equals(peers)){
            peers.clear();
            peers.addAll(peerslst.getDeviceList());
            String[] deviceNameArrays = new String[peerslst.getDeviceList().size()];
            devicesArray =  new  WifiP2pDevice[peerslst.getDeviceList().size()];
            int index = 0 ;
            for( WifiP2pDevice device :  peerslst.getDeviceList()){
                deviceNameArrays[index] = device.deviceName;
                devicesArray[index] = device;
                index++;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>( getApplicationContext(),android.R.layout.simple_list_item_1, deviceNameArrays);
            connectionPeers.setAdapter(adapter);
        }
        if(peers.size() == 0){
            Toast.makeText(MainMenu.this, "No Devices Available!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    @Override
    public void FailedConnectedTo(String DeviceName) {
        Toast.makeText(MainMenu.this, "Failed to Connected with "+DeviceName, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void DiscoveryException(String localizedMessage) {
        Toast.makeText(MainMenu.this, "Error :"+localizedMessage, Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(mReciever,mIntentfilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // unregisterReceiver(mReciever);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void CheckWifi(){
        WifiManager  wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            if (wifiManager.isWifiEnabled()) {
                btn_wifi.setBackground(getResources().getDrawable(R.drawable.wifi_icon));
            }
        }catch (Exception e){
            Toast.makeText(this, "Error : "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();        }

    }

    @Override
    protected void onDestroy() {
        PreviousDataAvailable = tempData.IsDataStored();
        if(PreviousDataAvailable){
            tempData.ClearData();
        }
        super.onDestroy();
    }
}
