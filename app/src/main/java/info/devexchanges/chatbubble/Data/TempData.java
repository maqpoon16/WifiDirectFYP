package info.devexchanges.chatbubble.Data;

import android.content.Context;
import android.content.SharedPreferences;

public class TempData {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context Mcontext;

    public TempData(Context mcontext,String DataName) {
        Mcontext = mcontext;
        pref = mcontext.getApplicationContext().getSharedPreferences(DataName, 0); // 0 - for private mode
        editor = pref.edit();
    }

    public void Storedata(String data){
        editor.putBoolean("IsChaDataAvailable", true); // Storing boolean - true/false
        editor.putString("Chat_data", data); // Storing string
        editor.commit(); // commit changes
    }
    public String GetStoredData(){

        return  pref.getString("Chat_data", null); // getting String
    }

    public boolean IsDataStored(){
        return  pref.getBoolean("IsChaDataAvailable", false); // getting String
    }

    public void ClearData() {
        editor.remove("Chat_data");
        editor.remove("IsChaDataAvailable");
        editor.commit();

    }
}
