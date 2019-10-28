package info.devexchanges.chatbubble.Implementers;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import info.devexchanges.chatbubble.Presenters.DiscoveryPresenter;
import info.devexchanges.chatbubble.Views.DiscoveryView;

import static android.os.Looper.getMainLooper;

public class DiscoveryPresenterImplementer implements DiscoveryPresenter
{
    private DiscoveryView discoveryView;
    private Context Mcontext;
    //for On and Off Wifi
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Boolean IsConnected = false;
    public DiscoveryPresenterImplementer(DiscoveryView discoveryView, Context Mcontext)
    {
        this.Mcontext = Mcontext;
        this.discoveryView = discoveryView;
        mManager = (WifiP2pManager) Mcontext.getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(Mcontext,getMainLooper(),null);

    }
    @Override
    public void handleDiscovery() {
        try {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    discoveryView.IsUserDiscover();
                }

                @Override
                public void onFailure(int reason) {
                    discoveryView.IsUserNotDiscover();
                }
            });
        }catch (Exception e){
            discoveryView.DiscoveryException(e.getLocalizedMessage());
        }
    }

    @Override
    public void HandleDisconnect() {
        mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    discoveryView.DisconnectUser();

                }

                @Override
                public void onFailure(int reason) {
                    discoveryView.FailedToDisconnect("Failed code :"+reason);
                }
            });
    }

    @Override
    public void handleDiscoveryConnection(WifiP2pConfig config , final WifiP2pDevice device) {
        try {

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                    discoveryView.ConnectedToUser(device.deviceName);
                }

                @Override
                public void onFailure(int reason) {
                    discoveryView.FailedConnectedTo(device.deviceName);
                }
            });
        }catch (Exception e){
            discoveryView.DiscoveryException(e.getLocalizedMessage());
        }
    }

    @Override
    public WifiP2pManager GetManager() {
        return mManager;
    }

    @Override
    public WifiP2pManager.Channel GetChannel() {
        return mChannel;
    }

}
