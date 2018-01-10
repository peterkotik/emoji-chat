package peterkotik.com.emojichat.screens.camerapreview

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.text.emoji.widget.EmojiAppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageButton
import butterknife.BindView
import butterknife.ButterKnife
import com.affectiva.android.affdex.sdk.Frame
import com.affectiva.android.affdex.sdk.detector.CameraDetector
import com.affectiva.android.affdex.sdk.detector.Detector
import com.affectiva.android.affdex.sdk.detector.Face
import peterkotik.com.emojichat.R
import peterkotik.com.emojichat.models.EmojiMessage
import peterkotik.com.emojichat.utils.AffdexUtils
import timber.log.Timber

class CameraPreviewActivity : Activity(), CameraPreviewContract.View, Detector.ImageListener, CameraDetector.CameraEventListener {

    lateinit private var presenter: CameraPreviewContract.Presenter

    lateinit private var rootLayout: ConstraintLayout
    lateinit private var cameraPreview: SurfaceView
    lateinit private var recyclerView: RecyclerView
    lateinit private var emojiButton: EmojiAppCompatButton
    lateinit private var editText: EditText
    lateinit private var sendButton: ImageButton

    lateinit private var detector: CameraDetector
    private var messageList: MutableList<EmojiMessage> = mutableListOf<EmojiMessage>()
    private var adapter: MessageListAdapter = MessageListAdapter(messageList)
    private var lastFrame: Bitmap? = null
    private var cameraWidth: Int = 0
    private var cameraHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_preview)

        rootLayout = findViewById(R.id.root_layout)
        cameraPreview = findViewById(R.id.camera_preview)
        recyclerView = findViewById(R.id.message_list)
        emojiButton = findViewById(R.id.emoji_button)
        editText = findViewById(R.id.edit_text)
        sendButton = findViewById(R.id.send_button)

        presenter = CameraPreviewPresenter(this)
        rootLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val containerWidth: Float = rootLayout.width * .25f
                val containerHeight: Float = rootLayout.height * .25f
                val layoutAspectRatio: Float = containerWidth/containerHeight
                val cameraAspectRatio: Float = cameraWidth.toFloat() / cameraHeight
                val previewWidth: Int = if (cameraAspectRatio > layoutAspectRatio) Math.round(containerWidth) else Math.round(containerHeight * cameraAspectRatio)
                val previewHeight: Int = if (cameraAspectRatio > layoutAspectRatio) Math.round(containerWidth / cameraAspectRatio) else Math.round(containerHeight)
                val params: ViewGroup.LayoutParams = cameraPreview.layoutParams
                params.width = previewWidth
                params.height = previewHeight
                cameraPreview.layoutParams = params
            }

        })
        editText.hint = "Send a message"
        emojiButton.setOnClickListener {
            if (lastFrame != null) {
                messageList.add(EmojiMessage(emojiButton.text.toString(), lastFrame))
                adapter.setDataset(messageList)
            } else {
                Timber.e("Attempted to send a picture, but last frame is null")
            }
        }
        sendButton.setOnClickListener {
            var messageText: String = editText.text.toString()
            if (!messageText.isBlank()) {
                messageList.add(EmojiMessage(messageText, null))
                adapter.setDataset(messageList)
                editText.setText("")
            }
        }

        setupCameraDetector()
        setupRecyclerView()
    }

    private fun setupCameraDetector() {
        detector = CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview)
        detector.setDetectAllEmojis(true)
        detector.setImageListener(this)
        detector.setOnCameraEventListener(this)
        detector.setMaxProcessRate(1f)
        detector.disableAnalytics()
    }

    private fun setupRecyclerView() {
        val layoutManager: LinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (!detector.isRunning) {
            detector.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (detector.isRunning) {
            detector.stop()
        }
    }

    override fun onImageResults(faces: MutableList<Face>?, frame: Frame?, timestamp: Float) {
        if (presenter.onImageResult(faces)) {
            frame?.let {
                lastFrame = AffdexUtils.getBitmapFromFrame(frame)
            }
        }
    }

    override fun setEmojiButtonText(text: String?) {
        emojiButton.text = text ?: ""
    }

    override fun setEmojiButtonVisible(visible: Boolean) {
        emojiButton.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onCameraSizeSelected(cameraWidth: Int, cameraHeight: Int, rotation: Frame.ROTATE?) {
        if (rotation == Frame.ROTATE.BY_90_CCW || rotation == Frame.ROTATE.BY_90_CW) {
            this.cameraWidth = cameraHeight
            this.cameraHeight = cameraWidth
        } else {
            this.cameraWidth = cameraWidth
            this.cameraHeight = cameraHeight
        }
    }

}