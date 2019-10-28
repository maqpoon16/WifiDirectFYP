package info.devexchanges.chatbubble.Implementers;

import android.content.Context;
import android.net.wifi.WifiManager;

import info.devexchanges.chatbubble.Presenters.WifiConnectionPresenter;
import info.devexchanges.chatbubble.Views.WifiConnectionView;

public class WifiConnectionPresenterImplementer implements WifiConnectionPresenter
{
    private WifiConnectionView wifiConnectionView;
    private Context Mcontext;
    //for On and Off Wifi
    WifiManager wifiManager;
    public WifiConnectionPresenterImplementer(WifiConnectionView wifiConnectionView, Context Mcontext)
    {
        this.wifiConnectionView = wifiConnectionView;
        wifiManager = (WifiManager) Mcontext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void handleWifiConnection() {
        try {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
                wifiConnectionView.IsWifiDisconected();
            } else {
                wifiManager.setWifiEnabled(true);
                wifiConnectionView.IsWifiConected();
            }
        }catch (Exception e){
            wifiConnectionView.WifiException(e.getLocalizedMessage());
        }
    }
}
