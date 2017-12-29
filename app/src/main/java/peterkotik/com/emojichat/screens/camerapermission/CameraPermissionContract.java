package peterkotik.com.emojichat.screens.camerapermission;

interface CameraPermissionContract {
    interface View {
        boolean hasCameraPermission();

        void goToCameraPreviewActivity();

        void requestCameraPermission(int requestCode);
    }

    interface Presenter {
        void onRequestPermissionsResult(int requestCode, int[] grantResults);

        void requestPermissionOrContinue();
    }
}
