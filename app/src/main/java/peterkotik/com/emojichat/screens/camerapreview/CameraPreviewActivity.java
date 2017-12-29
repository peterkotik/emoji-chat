package peterkotik.com.emojichat.screens.camerapreview;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.text.emoji.widget.EmojiAppCompatButton;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.List;

import peterkotik.com.emojichat.R;

public class CameraPreviewActivity extends Activity implements CameraPreviewContract.View, Detector.ImageListener, CameraDetector.CameraEventListener {

    private String messageRecipient = "@username";

    CameraPreviewContract.Presenter presenter;

    SurfaceView cameraPreview;

    CameraDetector detector;

    EmojiAppCompatButton emojiButton;

    EditText editText;

    ImageButton sendButton;

    int previewWidth = 0;
    int previewHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        presenter = new CameraPreviewPresenter(this);

        cameraPreview = findViewById(R.id.camera_preview);
        emojiButton = findViewById(R.id.emoji_button);
        editText = findViewById(R.id.edit_text);
        editText.setHint(getString(R.string.message_field_hint, messageRecipient));

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int start = Math.max(editText.getSelectionStart(), 0);
                int end = Math.max(editText.getSelectionEnd(), 0);
                editText.getText().replace(Math.min(start, end), Math.max(start, end),
                                           emojiButton.getText(), 0, emojiButton.getText().length());
            }
        });

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectAllEmotions(true);
        detector.setDetectAllEmojis(true);
        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        detector.setMaxProcessRate(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDetector();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopDetector();
    }

    void startDetector() {
        if (!detector.isRunning()) {
            detector.start();
            detector.disableAnalytics();
        }
    }

    void stopDetector() {
        if (detector.isRunning()) {
            detector.stop();
        }
    }

    @Override
    public void onImageResults(List<Face> faces, Frame frame, float v) {
        presenter.onImageResult(faces);
    }

    @Override
    public void setEmojiButtonText(String text) {
        emojiButton.setText(text);
    }

    @Override
    public void setEmojiButtonVisible(boolean visible) {
        emojiButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void onCameraSizeSelected(int width, int height, Frame.ROTATE rotate) {
        if (rotate == Frame.ROTATE.BY_90_CCW || rotate == Frame.ROTATE.BY_90_CW) {
            previewWidth = height;
            previewHeight = width;
        } else {
            previewHeight = height;
            previewWidth = width;
        }
        cameraPreview.requestLayout();
    }
}