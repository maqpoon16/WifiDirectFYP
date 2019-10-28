package info.devexchanges.chatbubble.Screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import info.devexchanges.chatbubble.Data.GlobalVariables;
import info.devexchanges.chatbubble.R;

public class EnterUserNameActivity extends AppCompatActivity {
        private EditText username;
        private Button btn_saveusername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_username_layout);
        InitializeVariables();

        btn_saveusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IsdataValid()){
                    Intent startmenu = new Intent(EnterUserNameActivity.this,MainMenu.class);
//                    startmenu.putExtra("User",username.getText().toString());
                    GlobalVariables.Username = username.getText().toString();
                    startActivity(startmenu);
                    finish();
                }else {
                    Toast.makeText(EnterUserNameActivity.this, "Username is Invalid / Empty!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void InitializeVariables(){
        username = (EditText) findViewById(R.id.ed_username);
        btn_saveusername  = (Button) findViewById(R.id.btn_saveuser);
    }

    private boolean IsdataValid(){
        if(username.getText().toString().isEmpty() && username.getText().toString().matches("^[a-zA-Z0-9_-]{6,14}$") ){
            return false;
        }
        return true;
    }
}
