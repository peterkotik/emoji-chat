package peterkotik.com.emojichat.screens.camerapreview;

import android.support.annotation.NonNull;

import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.List;

import timber.log.Timber;

class CameraPreviewPresenter implements CameraPreviewContract.Presenter {

    private CameraPreviewContract.View view;

    private Face.EMOJI emoji;

    CameraPreviewPresenter(@NonNull CameraPreviewContract.View view) {
        this.view = view;
        emoji = Face.EMOJI.UNKNOWN;
    }

    @Override
    public void onImageResult(List<Face> faces) {
        if (faces == null) {
            Timber.e("Image not processed");
            return;
        }

        if (faces.size() == 0) {
            Timber.w("No faces found");
            return;
        }

        Face face = faces.get(0);

        emoji = face.emojis.getDominantEmoji();

        view.setEmojiButtonText(emoji.getUnicode());
        view.setEmojiButtonVisible(true);
    }

}
