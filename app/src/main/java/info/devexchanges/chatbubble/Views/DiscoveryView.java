package info.devexchanges.chatbubble.Views;

public interface DiscoveryView {
    void DisconnectUser();
    void FailedToDisconnect(String DeviceName);
    void IsUserDiscover();
    void IsUserNotDiscover();
    void ConnectedToUser(String DeviceName);
    void FailedConnectedTo(String DeviceName);
    void DiscoveryException(String localizedMessage);
}
