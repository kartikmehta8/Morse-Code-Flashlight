package android.example.morsecodeflashlight;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.example.morsecodeflashlight.database.ThreadKillerRunnable;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    // Only allow one extra thread in MainActivity
    Thread t, killer = new Thread();
    private CameraManager mCameraManager;
    private CameraFlashManager mCameraFlashManager;
    // Views
    private ImageButton mImageButton;
    private Button mSosButton;
    private Button mTextActivityButton;
    private Button mStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Default
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        initializeViews();

        // Initialize CameraFlashManager class
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mCameraFlashManager = initializeCameraFlashManager();

        // If the device does not have a flashlight, then prompt the user to exit
        if (doesNotHaveAFlashlight()) return;

        // Additional settings (e.g. register callbacks) to CameraFlashManager
        setUpCameraFlashManager();

        /*
        Register OnClicks
         */
        setUpTextActivityButton();
        setUpSosButton();
        setUpImageButton();
        setUpStopButton();
    }

    private boolean doesNotHaveAFlashlight() {
        // hasFlash is true if and only if the device supports flashlight(s).
        boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        // If the device does not have a flashlight, then we create an alert dialog and the application exits
        // automatically upo dismissal of the alert.
        if (!hasFlash) {
            createAndShowAlert();
            return true;
        }
        return false;
    }

    private void setUpCameraFlashManager() {
        // Create a callback to be registered to camera manager
        mCameraFlashManager.createTorchCallback();
        // Register Torch Callback to Camera Manager
        mCameraFlashManager.registerTorchCallback();
    }

    private void setUpTextActivityButton() {
        mTextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CustomInputActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpSosButton() {
        mSosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Turn off the flashlight
                // TODO: remove this line and delegate task to sosRunnable since it extends CameraFlashManager
                // TODO" Calling TurnOffAllFlashlights in run() of runnable doesn't work.
                mCameraFlashManager.TurnOffAllFlashlights();

                ParserRunnable parserRunnable = new ParserRunnable(mCameraManager, "... --- ...");

                // TODO: not sure if this is the best approach.
                if (t == null | !killer.isAlive()) {
                    t = new Thread(parserRunnable);
                    ThreadKillerRunnable killerRunnable = new ThreadKillerRunnable(t);
                    killer = new Thread(killerRunnable);
                    killer.start();
                    t.start();
                } else {
                    Snackbar.make(findViewById(R.id.main), "Press the STOP button and try again.", Snackbar.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void setUpStopButton() {
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    t.interrupt();
                    t = null;
                } catch (NullPointerException ignored) {
                }
            }
        });
    }

    private void setUpImageButton() {
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraFlashManager.ToggleAllFlashlights();
                makeToggleSnackbarMessage();
            }

            private void makeToggleSnackbarMessage() {
                if (mCameraFlashManager.isFlashOn) {
                    Snackbar.make(findViewById(R.id.main), "Flashlight ON", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(R.id.main), "Flashlight OFF", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        mImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (t == null | !killer.isAlive()) {
                    ParserRunnable parserRunnable = new ParserRunnable(mCameraManager, "...........................");
                    t = new Thread(parserRunnable);
                    t.start();

                    return true;
                } else {
                    Snackbar.make(findViewById(R.id.main), "Press the STOP button and try again.", Snackbar.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

    @NotNull
    private CameraFlashManager initializeCameraFlashManager() {
        return new CameraFlashManager(mCameraManager);
    }

    private void initializeViews() {
        mImageButton = findViewById(R.id.imageButton);
        mSosButton = findViewById(R.id.sosButton);
        mTextActivityButton = findViewById(R.id.textActivityButton);
        mStopButton = findViewById(R.id.stopButton);
    }

    /**
     * Create and show a custom alert to tell the user that their device does not support flashlight.
     */
    protected void createAndShowAlert() {
        // Show alert message and close the application
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Error: Device Not Supported")
                .setIcon(R.drawable.ic_warning_48px)
                .setMessage("Sorry, your device does not support flashlights.")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((Activity) getApplicationContext()).finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}