package android.example.morsecodeflashlight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private CameraManager mCameraManager;
    private ImageButton mImageButton;
    private boolean isFlashOn;

    private CameraManager.TorchCallback mTorchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isFlashOn = false;

        // Initialize ImageButton
        mImageButton = (ImageButton) findViewById(R.id.imageButton);


        /*
         * First check if device is supporting flashlight or not
         */
        // hasFlash is true if and only if the device supports flashlight(s).
        Boolean hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");

            alert.show();
            return;
        }

        //


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTorchCallback = new CameraManager.TorchCallback(){
                @Override
                public void onTorchModeUnavailable(@NonNull String cameraId) {
                    super.onTorchModeUnavailable(cameraId);
                }

                @Override
                public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                    super.onTorchModeChanged(cameraId, enabled);
                    isFlashOn = enabled;
                }
            };
        }


        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager.registerTorchCallback(mTorchCallback, null);
        }
        


        mImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(isFlashOn){
                    try{
                        String[] cameras = mCameraManager.getCameraIdList();
                        for (int i = 0; i < cameras.length; i++){
                            boolean isFlashAvailable = mCameraManager.getCameraCharacteristics(cameras[i]).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                            if (isFlashAvailable){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    mCameraManager.setTorchMode(cameras[i], !isFlashOn);
                                }
                            }
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main),"Flashlight OFF", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else{
                    try{
                        String[] cameras = mCameraManager.getCameraIdList();
                        for (int i = 0; i < cameras.length; i++){
                            boolean isFlashAvailable = mCameraManager.getCameraCharacteristics(cameras[i]).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                            if (isFlashAvailable){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    mCameraManager.setTorchMode(cameras[i], !isFlashOn);
                                }
                            }
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "Flashlight ON", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
    }


    private void turnOnFlash(){

    }

    private void turnOffFlash(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            CameraManager.TorchCallback torchCallback = new CameraManager.TorchCallback(){
                @Override
                public void onTorchModeUnavailable(@NonNull String cameraId) {
                    super.onTorchModeUnavailable(cameraId);
                }

                @Override
                public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                    super.onTorchModeChanged(cameraId, false);
                    isFlashOn = false;
                }
            };
        }
    }




}