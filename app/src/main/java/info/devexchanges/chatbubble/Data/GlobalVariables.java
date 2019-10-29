package info.devexchanges.chatbubble.Data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class GlobalVariables {
   public static String Username = "";
   public static String MediaIndicator = ": MEDIA::";

   public static  String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
   {
      ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
      image.compress(compressFormat, quality, byteArrayOS);
      return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
   }

   public static Bitmap decodeBase64(String input)
   {
      byte[] decodedBytes = Base64.decode(input, 0);
      return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
   }
   public static String queryName(Uri uri) {
      File file= new File(uri.getPath());
      return  file.getName();
   }
}
