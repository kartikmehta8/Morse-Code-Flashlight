package android.example.morsecodeflashlight;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.example.morsecodeflashlight.database.ThreadKillerRunnable;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    // Only allow one extra thread in MainActivity
    Thread t;
    private CameraManager mCameraManager;
    private CameraFlashManager mCameraFlashManager;
    // Views
    private ImageButton mImageButton;
    private Button mSosButton;
    private Button mTextActivityButton;
    private Button mStopButton;

    // SharedPreferences keys and their variables
    public static final String SHARED_PREF_FILE = "android.example.morsecodeflashlight";
    private SharedPreferences mPreferences;
    public static final String KEY_PREF_NIGHT_MODE = "night_mode";
    private int mNightMode = AppCompatDelegate.MODE_NIGHT_YES;

    private boolean mSound;
    private int mFlashlightIntensity;
    private int mSpeed;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Change the label of the menu based on the state of the app.
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            menu.findItem(R.id.night_mode).setTitle(R.string.day_mode);
        } else {
            menu.findItem(R.id.night_mode).setTitle(R.string.night_mode);
        }
        return true;
    }

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

        // Set SharedPreferences
        // Default values of preferences cannot be set more than once
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        mPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);

        mNightMode = mPreferences.getInt(KEY_PREF_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_YES);
        AppCompatDelegate.setDefaultNightMode(mNightMode);


    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt(KEY_PREF_NIGHT_MODE, mNightMode);
        preferencesEditor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSound = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_SOUND_SWITCH, false);
        mFlashlightIntensity = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_PREF_FLASHLIGHT_INTENSITY, "1"));
        mSpeed = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_PREF_SPEED, "1"));
        // Snackbar.make(findViewById(R.id.main), "sound_switch " + mSound + "; flashlight_intensity: " + mFlashlightIntensity + "; speed: " + mSpeed, Snackbar.LENGTH_SHORT).show();
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
                intent.putExtra("mSpeed", mSpeed);
                intent.putExtra("mSound", mSound);
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

                ParserRunnable parserRunnable = new ParserRunnable(mCameraManager, getString(R.string.morse_sos), mSpeed, mSound);

                // TODO: not sure if this is the best approach.
                if (t == null) {
                    t = new Thread(parserRunnable);
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
                if (t == null) {
                    ParserRunnable parserRunnable = new ParserRunnable(mCameraManager, getString(R.string.morse_siren), mSpeed, mSound);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Check if the correct item was clicked
        switch (item.getItemId()) {
            case R.id.night_mode: {
                // Get the night mode state of the app.
                //Set the theme mode for the restarted activity
                if (mNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    mNightMode = AppCompatDelegate.MODE_NIGHT_NO;
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    mNightMode = AppCompatDelegate.MODE_NIGHT_YES;
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_YES);
                }
                // Recreate the activity for the theme change to take effect.
                recreate();
                return true;
            }

            case R.id.about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            }

            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }

        return false;


    }
}