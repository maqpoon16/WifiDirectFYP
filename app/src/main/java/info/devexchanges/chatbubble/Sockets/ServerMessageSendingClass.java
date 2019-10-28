package info.devexchanges.chatbubble.Sockets;
import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import info.devexchanges.chatbubble.Screens.ServerChat;

public class ServerMessageSendingClass extends Thread {
    private ServerChat activity;
    private String Message;
    private String IPaddress;



    public ServerMessageSendingClass(String IPaddress,ServerChat activity ,String Messages) {
        this.activity = activity;
        this.Message = Messages;
        this.IPaddress = IPaddress;
        new BackgroundTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    class BackgroundTask extends AsyncTask<String, Void, String> {
        Socket socket;
        DataOutputStream dataOutputStream;

        @Override
        protected String doInBackground(String... strings) {

                try {
                    socket = new Socket(IPaddress, 8081);
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(Message);
                    dataOutputStream.close();
                    socket.close();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity. edt_Message.append(Message);
                        }
                    });

                } catch (IOException e) {
                    Message = "\nError:" + e.getMessage();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity. edt_Message.append(Message);
                        }
                    });
                }

            return "";
        }

    }

}