package info.devexchanges.chatbubble.Presenters;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

public interface DiscoveryPresenter {
    void handleDiscovery();
    void  HandleDisconnect();
    void handleDiscoveryConnection(WifiP2pConfig config, WifiP2pDevice device);
    WifiP2pManager GetManager();
    WifiP2pManager.Channel GetChannel();
}
