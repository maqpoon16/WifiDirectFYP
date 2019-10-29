package info.devexchanges.chatbubble.Screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.List;

import info.devexchanges.chatbubble.Adapters.ChatAdapters;
import info.devexchanges.chatbubble.Data.ChatModel;
import info.devexchanges.chatbubble.Data.GlobalVariables;
import info.devexchanges.chatbubble.Data.TempData;
import info.devexchanges.chatbubble.Implementers.DiscoveryPresenterImplementer;
import info.devexchanges.chatbubble.Presenters.DiscoveryPresenter;
import info.devexchanges.chatbubble.R;
import info.devexchanges.chatbubble.Sockets.ServerMessageSendingClass;
import info.devexchanges.chatbubble.Views.DiscoveryView;

public class ServerChat extends AppCompatActivity implements View.OnClickListener, DiscoveryView  {
    private  EditText edt_ServMessage;
    public TextView edt_Message,edt_IPaddress;
    private ImageView send_img,media_img,media_pdf_img;
    public  String ClientIps;
    private boolean PreviouseChatSended , IsServerListening ;
    private TempData tempData;
    private Button btn_disconnected;

    private TextView UserName;

    //To disconnect user
    private DiscoveryPresenter discoveryPresenter;
    //Media share variables
    private int PICK_IMAGE_REQUEST;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ChatModel> mDataset;
    private String Mediatype;
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
        PICK_IMAGE_REQUEST = 1;
        mDataset = new ArrayList<>();
        UserName = (TextView) findViewById(R.id.Username);
        UserName.setText("Server :"+ GlobalVariables.Username);
        edt_IPaddress = (TextView) findViewById(R.id.edt_IPaddress);
        edt_Message = (TextView) findViewById(R.id.edt_MessageBox);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        edt_ServMessage=(EditText)findViewById(R.id.edt_ServMessage);
        send_img=(findViewById(R.id.send_img));
        media_img=(findViewById(R.id.media_img));
        media_pdf_img=(findViewById(R.id.media_pdf));
        btn_disconnected = (Button) findViewById(R.id.btn_disconnected);
        btn_disconnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsServerListening = false;
                discoveryPresenter.HandleDisconnect();

            }
        });
        //GetMedia From Storage
        media_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mediatype = "Image";
                GetMedia("Image");
            }
        });
        //GetMedia From Storage
        media_pdf_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mediatype = "Docs";
                GetMedia("Docs");
            }
        });
        send_img.setOnClickListener(this);
        PreviouseChatSended   = false;
        IsServerListening = true;
        tempData = new TempData(ServerChat.this,"Chatting");
    }

    private void GetMedia(String Mediatype) {
        try {

            String[] mimeTypes =
                    {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                            "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                            "text/plain",
                            "application/pdf",
                            "application/zip"};

            Intent intent = new Intent();
            // Show only images, no videos or anything else
            if(Mediatype.equals("Image")){
                intent.setType("image/*");
            }
            if(Mediatype.equals("Docs")){
                intent.setType(mimeTypes.length == 1 ? mimeTypes[7] : "*/*");
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }catch (Exception e){
           edt_Message.append("\nError :"+e.getLocalizedMessage());
        }
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
                            setchatAdapter(messages,null);

                             //edt_Message.append("\n"+messages);
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
                        setchatAdapter(messages,null);
                        //edt_Message.append(messages);
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


    public void setchatAdapter(String Message, Bitmap Media){
        mDataset.add(new ChatModel(Message,Media));
        mAdapter = new ChatAdapters(ServerChat.this,mDataset);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                if(Mediatype.equals("Image")) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    String img = GlobalVariables.encodeToBase64(bitmap, Bitmap.CompressFormat.WEBP, 50);
                    new ServerMessageSendingClass(ClientIps, ServerChat.this, "\n" + GlobalVariables.Username + GlobalVariables.MediaIndicator + img);
                    setchatAdapter("\n" + GlobalVariables.Username, bitmap);
                }
                else {
                    new ServerMessageSendingClass(ClientIps, ServerChat.this,uri,GlobalVariables.queryName(uri));
                }
            } catch (IOException e) {
                edt_Message.append("Error :"+e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
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