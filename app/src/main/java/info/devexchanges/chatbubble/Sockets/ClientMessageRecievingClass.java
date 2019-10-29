package info.devexchanges.chatbubble.Sockets;
import android.graphics.Bitmap;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import info.devexchanges.chatbubble.Data.GlobalVariables;
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
        Bitmap Mediashared;
        @Override
        public void run() {
            try {
                messages = null;
                Mediashared = null;
                serverSocket = new ServerSocket(8081);
                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    messages = dataInputStream.readUTF();
                    if(messages!=null && messages.contains(GlobalVariables.MediaIndicator)){
                        Mediashared = GlobalVariables.decodeBase64(messages.split(GlobalVariables.MediaIndicator)[1]);
                        messages = messages.split(GlobalVariables.MediaIndicator)[0]+":\n";
                    }
                   activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.setchatAdapter(messages,Mediashared);
                            //activity.edt_MessageBox.append("\n"+messages);
                        }
                    });

                }
            } catch (IOException e) {
                messages = "\nError:" + e.getMessage();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.setchatAdapter(messages,null);
                        //activity.edt_MessageBox.append(messages);
                    }
                });
            }

        }
    }


}