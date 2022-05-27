package android.example.morsecodeflashlight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private CameraManager mCameraManager;
    private CameraFlashManager mCameraFlashManager;

    // Views
    private ImageButton mImageButton;
    private Button mSosButton;

    private float mTimeScale = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize mImageButton
        mImageButton = findViewById(R.id.imageButton);
        // Initialize mSosButton;
        mSosButton = findViewById(R.id.sosButton);

        // Initialize CameraFlashManager class
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mCameraFlashManager = new CameraFlashManager(mCameraManager);

        // hasFlash is true if and only if the device supports flashlight(s).
        boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        // If the device does not have a flashlight, then we create an alert dialog and the application exits
        // automatically upo dismissal of the alert.
        if (!hasFlash) {
            mCameraFlashManager.createAndShowAlert();
            return;
        }

        // Create a callback to be registered to camera manager
        mCameraFlashManager.createTorchCallback();

        // Create a camera manager
        // Removed because we create a manager on mainactivity instead of inside the class
        // mCameraFlashManager.createCameraManager();

        // Register Torch Callback to Camera Manager
        mCameraFlashManager.registerTorchCallback();

        // Register OnClick for mSosButton
        mSosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Turn off the flashlight
                mCameraFlashManager.TurnOffAllFlashlights();

                // 2. Call a new runnable on a new thread
                SosRunnable sosRunnable = new SosRunnable(mCameraManager);
                new Thread(sosRunnable).start();
            }
        });


        // Register OnClick for mImageButton
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Get a list of cameras of the device
                    String[] cameras = mCameraFlashManager.getCameraStringList();
                    // Iterate through every camera, every lens IS a camera.
                    for (String camera : cameras) {
                        boolean isFlashAvailable = mCameraFlashManager.checkFlashAvailable(camera);
                        // Only toggle the flashlight if it is available, i.e. not used by other applications (busy)
                        if (isFlashAvailable) {
                            mCameraFlashManager.ToggleFlashlight(camera);
                            if (mCameraFlashManager.isFlashOn) {
                                Snackbar.make(findViewById(R.id.main), "Flashlight ON", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(findViewById(R.id.main), "Flashlight OFF", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        // We removed the Snackbar here for the else branch, because it is highly likely that a phone
                        // has more than camera and the Snackbar here will cover the other Snackbar(s) above.
                    }
                } catch (CameraAccessException e) { // Permission denied.
                    e.printStackTrace();
                }
            }


        });
    }


}