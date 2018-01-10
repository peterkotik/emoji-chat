package peterkotik.com.emojichat.screens.camerapreview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.text.emoji.widget.EmojiAppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.ArrayList;
import java.util.List;

import peterkotik.com.emojichat.utils.AffdexUtils;
import peterkotik.com.emojichat.R;
import peterkotik.com.emojichat.models.EmojiMessage;
import timber.log.Timber;

public class CameraPreviewActivity extends Activity implements CameraPreviewContract.View, Detector.ImageListener, CameraDetector.CameraEventListener {

    CameraPreviewContract.Presenter presenter;
    ConstraintLayout rootLayout;
    SurfaceView cameraPreview;
    RecyclerView recyclerView;
    MessageListAdapter adapter;
    CameraDetector detector;
    EmojiAppCompatButton emojiButton;
    EditText editText;
    ImageButton sendButton;
    List<EmojiMessage> messageList;
    int cameraWidth;
    int cameraHeight;
    int previewWidth;
    int previewHeight;
    Bitmap lastFrame;
    private String messageRecipient = "@username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        presenter = new CameraPreviewPresenter(this);

        cameraPreview = findViewById(R.id.camera_preview);
        rootLayout = findViewById(R.id.root_layout);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int layoutWidth = Math.round(rootLayout.getWidth() * .25f);
                int layoutHeight = Math.round(rootLayout.getHeight() * .25f);

                float layoutAspectRatio = (float) layoutWidth / layoutHeight;
                float cameraPreviewAspectRatio = (float) cameraWidth / cameraHeight;

                int newWidth;
                int newHeight;

                if (cameraPreviewAspectRatio > layoutAspectRatio) {
                    newWidth = layoutWidth;
                    newHeight = (int) (layoutWidth / cameraPreviewAspectRatio);
                } else {
                    newWidth = (int) (layoutHeight * cameraPreviewAspectRatio);
                    newHeight = layoutHeight;
                }

                ViewGroup.LayoutParams params = cameraPreview.getLayoutParams();
                params.height = newHeight;
                params.width = newWidth;
                previewHeight = newHeight;
                previewWidth = newWidth;
                cameraPreview.setLayoutParams(params);
            }
        });
        emojiButton = findViewById(R.id.emoji_button);
        editText = findViewById(R.id.edit_text);
        editText.setHint(getString(R.string.message_field_hint, messageRecipient));

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastFrame != null) {
                    messageList.add(new EmojiMessage(emojiButton.getText().toString(), lastFrame));
                } else {
                    Timber.e("lastFrame is null somehow");
                }
                adapter.setDataset(messageList);
            }
        });

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = editText.getText().toString();
                if (!messageText.isEmpty()) {
                    messageList.add(new EmojiMessage(editText.getText().toString(), null));
                }
                adapter.setDataset(messageList);
                editText.setText("");
            }
        });

        setupCameraDetector();
        setupRecyclerView();
    }

    private void setupCameraDetector() {
        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectAllEmotions(true);
        detector.setDetectAllEmojis(true);
        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        detector.setMaxProcessRate(1);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.message_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        messageList = new ArrayList<>();
        adapter = new MessageListAdapter(messageList);
        recyclerView.setAdapter(adapter);
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
        if (presenter.onImageResult(faces)) {
            lastFrame = AffdexUtils.getBitmapFromFrame(frame);
        }
    }

    @Override
    public void setEmojiButtonText(String text) {
        emojiButton.setText(text);
    }

    @Override
    public void setEmojiButtonVisible(boolean visible) {
        emojiButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCameraSizeSelected(int cameraWidth, int cameraHeight, Frame.ROTATE rotation) {
        if (rotation == Frame.ROTATE.BY_90_CCW || rotation == Frame.ROTATE.BY_90_CW) {
            this.cameraWidth = cameraHeight;
            this.cameraHeight = cameraWidth;
        } else {
            this.cameraWidth = cameraWidth;
            this.cameraHeight = cameraHeight;
        }
    }
}