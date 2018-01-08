package peterkotik.com.emojichat.screens.camerapreview;

import android.support.annotation.NonNull;

import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.List;

import timber.log.Timber;

class CameraPreviewPresenter implements CameraPreviewContract.Presenter {

    private CameraPreviewContract.View view;

    private Face.EMOJI mostRecentEmoji;

    CameraPreviewPresenter(@NonNull CameraPreviewContract.View view) {
        this.view = view;
        mostRecentEmoji = Face.EMOJI.UNKNOWN;
    }

    @Override
    public boolean onImageResult(List<Face> faces) {
        if (faces == null) {
            Timber.e("Image not processed");
            return false;
        }

        if (faces.size() == 0) {
            Timber.w("No faces found");
            return false;
        }

        Face face = faces.get(0);

        mostRecentEmoji = face.emojis.getDominantEmoji();

        view.setEmojiButtonText(mostRecentEmoji.getUnicode());
        view.setEmojiButtonVisible(true);
        return true;
    }

}
