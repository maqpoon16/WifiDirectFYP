package info.devexchanges.chatbubble.Recievers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.net.InetAddress;

import info.devexchanges.chatbubble.Screens.ClientChat;
import info.devexchanges.chatbubble.Screens.MainMenu;
import info.devexchanges.chatbubble.Screens.ServerChat;

public class WifiDirectBroadcastReciever extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainMenu mainActivity;

    public WifiDirectBroadcastReciever(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, MainMenu mainActivity) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            if(mManager==null){
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()){
                mManager.requestConnectionInfo(mChannel,connectionInfoListener);
            }else {
                mainActivity.tv_status.setText("Device Disconnect!");
            }
        }else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context, "Wifi is On!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "Wifi is Off!", Toast.LENGTH_SHORT).show();
            }

        }else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(mManager!=null){
                mManager.requestPeers(mChannel,peerListListener);
            }
        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }
    public WifiP2pManager.ConnectionInfoListener connectionInfoListener =  new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress OwnerAddress =  info.groupOwnerAddress;
            Activity activity=mainActivity;
            if(info.groupFormed && info.isGroupOwner){
                Intent intent =new Intent (activity.getApplicationContext(), ServerChat.class);
                activity.startActivity(intent);
            }else  if(info.groupFormed){
                Intent intent =new Intent (activity.getApplicationContext(), ClientChat.class);
                intent.putExtra("Host", OwnerAddress.getHostAddress());
                activity.startActivity(intent);

            }
        }
    };
    public WifiP2pManager.PeerListListener peerListListener =  new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerslst) {
            mainActivity.UpdateList(peerslst);
        }
    };
}
