package info.devexchanges.chatbubble.Views;

public interface WifiConnectionView {
    void IsWifiConected();
    void IsWifiDisconected();
    void WifiException(String localizedMessage);
}
