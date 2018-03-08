package peterkotik.com.emojichat.screens.camerapreview

import com.affectiva.android.affdex.sdk.detector.Face

internal interface CameraPreviewContract {
    interface View {
        fun setEmojiButtonText(text: String)

        fun setEmojiButtonVisible(visible: Boolean)

    }

    interface Presenter {
        fun onImageResult(faces: List<Face>): Boolean

    }
}
