package info.devexchanges.chatbubble.Sockets;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import info.devexchanges.chatbubble.Data.GlobalVariables;
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
   public ServerMessageSendingClass(String IPaddress,ServerChat activity,Uri uris,String fileNames) {
        this.activity = activity;
        this.IPaddress = IPaddress;
        new TransferData(activity,uris,fileNames).execute();
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
                            if(!Message.contains(GlobalVariables.MediaIndicator)) {
                                activity.setchatAdapter(Message,null);
                                //activity. edt_Message.append(Message);
                            }
                        }
                    });

                } catch (IOException e) {
                    Message = "\nError:" + e.getMessage();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.setchatAdapter(Message,null);
                            //activity. edt_Message.append(Message);
                        }
                    });
                }

            return "";
        }

    }

    @SuppressLint("StaticFieldLeak")
    class TransferData extends AsyncTask<Void, String, Void> {
        private Context context;
        private Uri uris;
        private String fileNames;
        public TransferData(Context context,
                            Uri uris,
                            String fileNames) {
            this.context = context;
            this.uris = uris;
            this.fileNames = fileNames;
        }
        private void sendData(Context context, Uri uris) {

            int len = 0;
            byte buf[] = new byte[1024];
            Log.d("Data Transfer", "Transfer Starter");

            Socket socket = new Socket();

            try {
                socket.bind(null);
                Log.d("Client Address", socket.getLocalSocketAddress().toString());

                socket.connect(new InetSocketAddress(IPaddress, 8082));
                Log.d("Client", "Client Connected 8888");

                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                ContentResolver cr = context.getContentResolver();
                objectOutputStream.writeObject(fileNames);
                objectOutputStream.flush();
                InputStream inputStream = cr.openInputStream(uris);
                    while ((len = inputStream.read(buf)) != -1) {
                        objectOutputStream.write(buf, 0, len);
                    }
                    inputStream.close();
                    Log.d("TRANSFER", "Writing Data Final   -" + len);
                objectOutputStream.close();
                socket.close();
            } catch (Exception e) {
                Log.d("Data Transfer", e.toString());
                e.printStackTrace();
            } finally {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            sendData(context, uris);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("Sender", "Finished!");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}