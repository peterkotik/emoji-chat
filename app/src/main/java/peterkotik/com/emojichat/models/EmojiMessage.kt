package peterkotik.com.emojichat.models

import android.graphics.Bitmap

data class EmojiMessage(val message: String, val bitmap: Bitmap?) {
    fun isEmpty() {
        return
    }
}
