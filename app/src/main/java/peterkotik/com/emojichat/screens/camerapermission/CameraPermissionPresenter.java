package peterkotik.com.emojichat.screens.camerapermission;

public class CameraPermissionPresenter implements CameraPermissionContract.Presenter {

    private final static int CAMERA_PERMISSION_REQUEST_CODE = 999;

    CameraPermissionContract.View view;

    CameraPermissionPresenter(CameraPermissionContract.View view) {
        this.view = view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length != 0) {
            view.goToCameraPreviewActivity();
        }
    }

    @Override
    public void requestPermissionOrContinue() {
        if (view.hasCameraPermission()) {
            view.goToCameraPreviewActivity();
        } else {
            view.requestCameraPermission(CAMERA_PERMISSION_REQUEST_CODE);
        }
    }
}
