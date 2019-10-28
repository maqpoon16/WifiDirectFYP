package info.devexchanges.chatbubble.Sockets;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import info.devexchanges.chatbubble.Screens.ClientChat;

public class ClientMessageRecievingClass extends Thread {
    private ClientChat activity;

    public ClientMessageRecievingClass(ClientChat activity){
        this.activity = activity;
        Thread serverThread = new Thread(new ServerClass());
        serverThread.start();
    }
    class ServerClass implements Runnable {
        ServerSocket serverSocket;
        Socket socket;
        DataInputStream dataInputStream;
        String messages;
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8081);
                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    messages = dataInputStream.readUTF();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.edt_MessageBox.append("\n"+messages);
                        }
                    });

                }
            } catch (IOException e) {
                messages = "\nError:" + e.getMessage();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.edt_MessageBox.append(messages);
                    }
                });
            }

        }
    }

}