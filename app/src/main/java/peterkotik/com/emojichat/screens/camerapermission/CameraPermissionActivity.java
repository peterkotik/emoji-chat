package peterkotik.com.emojichat.screens.camerapermission;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import peterkotik.com.emojichat.R;
import peterkotik.com.emojichat.screens.camerapreview.CameraPreviewActivity;

public class CameraPermissionActivity extends Activity implements CameraPermissionContract.View {

    CameraPermissionContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_permission);

        presenter = new CameraPermissionPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.requestPermissionOrContinue();
    }

    @Override
    public void requestCameraPermission(int requestCode) {
        ActivityCompat.requestPermissions(this,
                                          new String[] { Manifest.permission.CAMERA },
                                          requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        presenter.onRequestPermissionsResult(requestCode, grantResults);
    }

    @Override
    public boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void goToCameraPreviewActivity() {
        startActivity(new Intent(this, CameraPreviewActivity.class));
    }
}
