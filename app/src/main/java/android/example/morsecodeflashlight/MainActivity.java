package android.example.morsecodeflashlight;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.example.morsecodeflashlight.database.ThreadKillerRunnable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import org.xml.sax.Parser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CameraManager mCameraManager;
    private CameraFlashManager mCameraFlashManager;

    // Views
    private ImageButton mImageButton;
    private Button mSosButton;
    private Button mTextActivityButton;
    private Button mStopButton;

    // TODO: add timescale to runnable
    private float mTimeScale = 1;

    // Only allow one extra thread in mainactivity
    Thread t, killer = new Thread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Buttons
        mImageButton = findViewById(R.id.imageButton);
        mSosButton = findViewById(R.id.sosButton);
        mTextActivityButton = findViewById(R.id.textActivityButton);
        mStopButton = findViewById(R.id.stopButton);


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
        // Register Torch Callback to Camera Manager
        mCameraFlashManager.registerTorchCallback();


        /*
        Register OnClicks
         */
        mTextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CustomInputActivity.class);
                startActivity(intent);
            }
        });

        mSosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Turn off the flashlight
                // TODO: remove this line and delegate task to sosRunnable since it extends CameraFlashManager
                mCameraFlashManager.TurnOffAllFlashlights();

                ParserRunnable parserRunnable = new ParserRunnable(mCameraManager, "... --- ...");

                if (t == null | !killer.isAlive())  {
                    t = new Thread(parserRunnable);
                    ThreadKillerRunnable killerRunnable = new ThreadKillerRunnable(t);
                    killer = new Thread(killerRunnable);
                    killer.start();
                    t.start();
                }

                else {
                    Snackbar.make(findViewById(R.id.main), "Press the STOP button and try again.", Snackbar.LENGTH_SHORT).show();
                }


            }
        });


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

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d(TAG + "Thread", "" + t.isAlive());
                }
                catch (NullPointerException e){}
                try {
                    t.interrupt();
                    t = null;
                }
                catch (NullPointerException e){

                }
            }
        });
    }


}