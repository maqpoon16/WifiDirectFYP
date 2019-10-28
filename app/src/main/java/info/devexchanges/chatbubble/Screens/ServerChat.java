package info.devexchanges.chatbubble.Screens;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import info.devexchanges.chatbubble.Data.GlobalVariables;
import info.devexchanges.chatbubble.Data.TempData;
import info.devexchanges.chatbubble.Implementers.DiscoveryPresenterImplementer;
import info.devexchanges.chatbubble.Presenters.DiscoveryPresenter;
import info.devexchanges.chatbubble.R;
import info.devexchanges.chatbubble.Sockets.ServerMessageSendingClass;
import info.devexchanges.chatbubble.Views.DiscoveryView;

public class ServerChat extends AppCompatActivity implements View.OnClickListener, DiscoveryView {
    private  EditText edt_ServMessage;
    public TextView edt_Message,edt_IPaddress;
    private ImageView send_img;
    public  String ClientIps;
    private boolean PreviouseChatSended , IsServerListening ;
    private TempData tempData;
    private Button btn_disconnected;

    private TextView UserName;

    //To disconnect user
    private DiscoveryPresenter discoveryPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_chat_lan);
        InitializedObjectNow();
        Thread serverThread = new Thread(new ServerClass());
        serverThread.start();
    }
    private void InitializedObjectNow() {
        discoveryPresenter = new DiscoveryPresenterImplementer(this, ServerChat.this);

        UserName = (TextView) findViewById(R.id.Username);
        UserName.setText("Server :"+ GlobalVariables.Username);
        edt_IPaddress = (TextView) findViewById(R.id.edt_IPaddress);
        edt_Message = (TextView) findViewById(R.id.edt_MessageBox);
        edt_ServMessage=(EditText)findViewById(R.id.edt_ServMessage);
        send_img=(findViewById(R.id.send_img));
        btn_disconnected = (Button) findViewById(R.id.btn_disconnected);
        btn_disconnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsServerListening = false;
                discoveryPresenter.HandleDisconnect();

            }
        });
        send_img.setOnClickListener(this);
        PreviouseChatSended   = false;
        IsServerListening = true;
        tempData = new TempData(ServerChat.this,"Chatting");
    }

    @Override
    public void onClick(View v) {
        new ServerMessageSendingClass(ClientIps, ServerChat.this,"\n"+GlobalVariables.Username+":"+edt_ServMessage.getText().toString());
        edt_ServMessage.setText("");
    }

    class ServerClass implements Runnable {
        ServerSocket serverSocket;
        Socket socket;
        DataInputStream dataInputStream;
        String messages;
        Handler handler = new Handler();

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8080);
                while (IsServerListening) {
                    socket = serverSocket.accept();
                    ClientIps  =(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
                    if(false == PreviouseChatSended && tempData.IsDataStored()){
                        new ServerMessageSendingClass(ClientIps, ServerChat.this,tempData.GetStoredData());
                        PreviouseChatSended = true;
                    }
                    Log.d("Cleint",ClientIps);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    messages = dataInputStream.readUTF();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                             edt_Message.append("\n"+messages);
                        }
                    });

                }
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                messages = "\nerror :" + e.getMessage();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        edt_Message.append(messages);
                    }
                });
            }

        }
    }

    @Override
    protected void onDestroy() {
        String chatting = "";
        if(tempData.IsDataStored()){
            chatting = tempData.GetStoredData();

        }
        chatting = chatting +"\n"+edt_Message.getText().toString();
        tempData.Storedata(chatting);
        super.onDestroy();
    }

    @Override
    public void DisconnectUser() {
        Intent disconnect = new Intent(ServerChat.this,MainMenu.class);
        startActivity(disconnect);
        finish();
    }

    @Override
    public void FailedToDisconnect(String DeviceName) {
        Toast.makeText(ServerChat.this, DeviceName, Toast.LENGTH_SHORT).show();

    }

    //Not used
    @Override
    public void IsUserDiscover() {

    }

    @Override
    public void IsUserNotDiscover() {

    }

    @Override
    public void ConnectedToUser(String DeviceName) {

    }

    @Override
    public void FailedConnectedTo(String DeviceName) {

    }

    @Override
    public void DiscoveryException(String localizedMessage) {

    }
}