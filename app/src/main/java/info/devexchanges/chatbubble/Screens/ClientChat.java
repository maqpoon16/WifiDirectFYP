package info.devexchanges.chatbubble.Screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import info.devexchanges.chatbubble.Data.GlobalVariables;
import info.devexchanges.chatbubble.Data.TempData;
import info.devexchanges.chatbubble.Implementers.DiscoveryPresenterImplementer;
import info.devexchanges.chatbubble.Presenters.DiscoveryPresenter;
import info.devexchanges.chatbubble.R;
import info.devexchanges.chatbubble.Sockets.ClientMessageRecievingClass;
import info.devexchanges.chatbubble.Sockets.ServerMessageSendingClass;
import info.devexchanges.chatbubble.Views.DiscoveryView;

public class ClientChat extends AppCompatActivity  implements View.OnClickListener , DiscoveryView {
    private  EditText edt_Message;
    private ImageView send_img;
   public TextView edt_MessageBox;
    private TextView edt_IPaddress;
    private  String ServerIp;
private Button btn_disconnected;
    private boolean PreviouseChatSended ;
    private TempData tempData;

    private TextView UserName;

//To disconnect user
    private DiscoveryPresenter discoveryPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_chat_lan);
        ServerIp = getIntent().getStringExtra("Host");
        InitializedObjectNow();
         new ClientMessageRecievingClass(ClientChat.this);
    }

    private void InitializedObjectNow() {
        discoveryPresenter = new DiscoveryPresenterImplementer(this, ClientChat.this);
        UserName = (TextView) findViewById(R.id.Username);
        UserName.setText("Client :"+GlobalVariables.Username);

        send_img = (ImageView) findViewById(R.id.send_img);
        edt_IPaddress = (TextView) findViewById(R.id.edt_IPaddress);
        edt_IPaddress.setText(ServerIp);
        edt_Message = (EditText) findViewById(R.id.edt_Message);
        edt_MessageBox=(TextView)findViewById(R.id.edt_MessageBox);
        btn_disconnected = (Button) findViewById(R.id.btn_disconnected);
        btn_disconnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoveryPresenter.HandleDisconnect();
            }
        });
        send_img.setOnClickListener(this);
        PreviouseChatSended   = false;
        tempData = new TempData(ClientChat
                .this,"Chatting");


    }
    @Override
    public void onClick(View v) {
     // if(v.getId() == R.id.btn_chat_send) {
          BackgroundTask backgroundTask = new BackgroundTask();
          backgroundTask.execute(ServerIp, "\n"+GlobalVariables.Username+":"+edt_Message.getText().toString());
          edt_Message.setText("");
      //}
    }



    @SuppressLint("StaticFieldLeak")
    class BackgroundTask extends AsyncTask<String, Void, String> {
        Socket socket;
        DataOutputStream dataOutputStream;
        String IpAddress, Messages;
        Handler handler = new Handler();

        @Override
        protected String doInBackground(String... strings) {
            IpAddress = strings[0];
            Messages = strings[1];
            try {
                socket = new Socket(IpAddress, 8080);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                if(false == PreviouseChatSended && tempData.IsDataStored()){
                    Messages = tempData.GetStoredData() + Messages;
                    PreviouseChatSended = true;
                }
                dataOutputStream.writeUTF(Messages);
                dataOutputStream.close();
                socket.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        edt_MessageBox.append(Messages);
                    }
                });

            } catch (IOException e) {
                Messages = "\nerror :" + e.getMessage();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        edt_MessageBox.append(Messages);
                    }
                });
            }


            return "";
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
        Intent disconnect = new Intent(ClientChat.this,MainMenu.class);
        startActivity(disconnect);
        finish();
    }

    @Override
    public void FailedToDisconnect(String DeviceName) {
        Toast.makeText(ClientChat.this, DeviceName, Toast.LENGTH_SHORT).show();

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
