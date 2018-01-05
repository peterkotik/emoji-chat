package peterkotik.com.emojichat.screens.camerapreview;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.text.emoji.widget.EmojiAppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.ArrayList;
import java.util.List;

import peterkotik.com.emojichat.R;
import peterkotik.com.emojichat.models.EmojiMessage;

public class CameraPreviewActivity extends Activity implements CameraPreviewContract.View, Detector.ImageListener, CameraDetector.CameraEventListener {

    CameraPreviewContract.Presenter presenter;
    SurfaceView cameraPreview;
    RecyclerView recyclerView;
    ConstraintLayout rootLayout;
    MessageListAdapter adapter;
    CameraDetector detector;
    EmojiAppCompatButton emojiButton;
    EditText editText;
    ImageButton sendButton;
    List<EmojiMessage> messageList;
    int previewWidth = 0;
    int previewHeight = 0;
    private String messageRecipient = "@username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        presenter = new CameraPreviewPresenter(this);

        rootLayout = findViewById(R.id.root_layout);
        cameraPreview = findViewById(R.id.camera_preview);
        cameraPreview.setDrawingCacheEnabled(true);
        emojiButton = findViewById(R.id.emoji_button);
        editText = findViewById(R.id.edit_text);
        editText.setHint(getString(R.string.message_field_hint, messageRecipient));

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send message with just the emoji and the picture
                messageList.add(new EmojiMessage(emojiButton.getText().toString(), null));
                adapter.setDataset(messageList);
            }
        });

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageList.add(new EmojiMessage(editText.getText().toString(), null));
                adapter.setDataset(messageList);
                editText.setText("");
            }
        });

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectAllEmotions(true);
        detector.setDetectAllEmojis(true);
        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        detector.setMaxProcessRate(1);

        setupRecyclerView();
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
        float scaleFactor = rootLayout.getMaxWidth() / (previewWidth * 4);
        ViewGroup.LayoutParams layoutParams = cameraPreview.getLayoutParams();
        layoutParams.width = Math.round(previewWidth * scaleFactor);
        layoutParams.height = Math.round(previewHeight * scaleFactor);
        cameraPreview.requestLayout();
    }
}