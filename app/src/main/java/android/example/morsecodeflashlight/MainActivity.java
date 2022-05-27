package android.example.morsecodeflashlight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

        // Initialize ImageButton
        mImageButton = (ImageButton) findViewById(R.id.imageButton);

        // hasFlash is true if and only if the device supports flashlight(s).
        boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        // If the device does not have a flashlight, then we create an alert dialog and the application exits
        // automatically upo dismissal of the alert.
        if (!hasFlash) {
           createAndShowAlert();
           return;
        }



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


    /**
     * Create and show a custom alert to tell the user that their device does not support flashlight.
     */
    private void createAndShowAlert() {
        // Show alert message and close the application
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Error: Device Not Supported")
                .setIcon(R.drawable.ic_warning_48px)
                .setMessage("Sorry, your device does not support flashlights.")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}