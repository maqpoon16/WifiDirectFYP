package info.devexchanges.chatbubble.Data;

import android.graphics.Bitmap;

public class ChatModel {
    private String Message;
    private Bitmap Medial;

    public ChatModel(String message, Bitmap medial) {
        Message = message;
        Medial = medial;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Bitmap getMedial() {
        return Medial;
    }

    public void setMedial(Bitmap medial) {
        Medial = medial;
    }
}
