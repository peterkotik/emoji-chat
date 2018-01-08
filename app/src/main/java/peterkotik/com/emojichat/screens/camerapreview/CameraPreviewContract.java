package peterkotik.com.emojichat.screens.camerapreview;

import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.List;

interface CameraPreviewContract {
    interface View {
        void setEmojiButtonText(String text);

        void setEmojiButtonVisible(boolean visible);

    }

    interface Presenter {
        boolean onImageResult(List<Face> faces);

    }
}
