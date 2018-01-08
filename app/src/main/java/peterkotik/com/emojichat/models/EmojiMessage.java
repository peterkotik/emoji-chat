package peterkotik.com.emojichat.models;

import android.graphics.Bitmap;

public class EmojiMessage {
    public String message;
    public Bitmap bitmap;

    public EmojiMessage(String message, Bitmap bitmap) {
        this.message = message;
        this.bitmap = bitmap;
    }

    public boolean isEmpty() {
        return (message == null && bitmap == null);
    }
}
