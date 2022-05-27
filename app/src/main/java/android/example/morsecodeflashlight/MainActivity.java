package android.example.morsecodeflashlight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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

        // Create a callback to be registered to camera manager
        createTorchCallback();

        // Create a camera manager
        createCameraManager();

        // Register Torch Callback to Camera Manager
        registerTorchCallback();


        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Get a list of cameras of the device
                    String[] cameras = getCameraStringList();
                    // Iterate through every camera, every lens IS a camera.
                    for (String camera : cameras) {
                        boolean isFlashAvailable = checkFlashAvailable(camera);
                        // Only toggle the flashlight if it is available, i.e. not used by other applications (busy)
                        if (isFlashAvailable) {
                            ToggleFlashlight(camera);
                            if (isFlashOn) {
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

            private void ToggleFlashlight(String camera) throws CameraAccessException {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Update the MainActivity class variable
                    isFlashOn = !isFlashOn;
                    // Toggle the flashlight
                    mCameraManager.setTorchMode(camera, isFlashOn);
                }
            }

            private boolean checkFlashAvailable(String camera) throws CameraAccessException {
                boolean isFlashAvailable = mCameraManager.getCameraCharacteristics(camera).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                return isFlashAvailable;
            }

            private String[] getCameraStringList() throws CameraAccessException {
                String[] cameras = mCameraManager.getCameraIdList();
                return cameras;
            }
        });
    }

    private void registerTorchCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager.registerTorchCallback(mTorchCallback, null);
        }
    }

    private void createCameraManager() {
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    }

    private void createTorchCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTorchCallback = new CameraManager.TorchCallback() {
                @Override
                public void onTorchModeUnavailable(@NonNull String cameraId) {
                    super.onTorchModeUnavailable(cameraId);
                }

                @Override
                public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                    super.onTorchModeChanged(cameraId, enabled);
                    // Update MainActivity class variable.
                    isFlashOn = enabled;
                }
            };
        }
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