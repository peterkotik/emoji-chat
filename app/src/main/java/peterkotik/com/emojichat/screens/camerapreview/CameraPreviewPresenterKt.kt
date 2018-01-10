package peterkotik.com.emojichat.screens.camerapreview

import com.affectiva.android.affdex.sdk.detector.Face

internal class CameraPreviewPresenterKt(private val view: CameraPreviewContract.View) : CameraPreviewContract.Presenter {

    private var mostRecentEmoji: Face.EMOJI

    init {
        mostRecentEmoji = Face.EMOJI.UNKNOWN
    }

    override fun onImageResult(faces: List<Face>?): Boolean {
        val face: Face = faces?.get(0) ?: return false
        mostRecentEmoji = face.emojis.dominantEmoji

        view.setEmojiButtonText(mostRecentEmoji.unicode)
        view.setEmojiButtonVisible(true)

        return true
    }
}