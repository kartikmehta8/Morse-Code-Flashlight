package android.example.morsecodeflashlight;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CameraFlashManager extends Service {
    private CameraManager mCameraManager;
    protected boolean isFlashOn;
    private CameraManager.TorchCallback mTorchCallback;

    // TODO: fix this
    private Context mParentActivityContext;

    CameraFlashManager(CameraManager cameraManager) {
        this.mCameraManager = cameraManager;
    }

    protected void ToggleAllFlashlights(){
        try {
            // Get a list of cameras of the device
            String[] cameras = getCameraStringList();
            // Iterate through every camera, every lens IS a camera.
            for (String camera : cameras) {
                boolean isFlashAvailable = checkFlashAvailable(camera);
                // Only toggle the flashlight if it is available, i.e. not used by other applications (busy)
                if (isFlashAvailable) {
                    ToggleSingleFlashlight(camera);
                }
                // We removed the Snackbar here for the else branch, because it is highly likely that a phone
                // has more than camera and the Snackbar here will cover the other Snackbar(s) above.
            }
        } catch (CameraAccessException e) { // Permission denied.
            e.printStackTrace();
        }
    }


    protected void TurnOnAllFlashlights() {
        try {
            // Get a list of cameras of the device
            String[] cameras = getCameraStringList();
            // Iterate through every camera, every lens IS a camera.
            for (String camera : cameras) {
                boolean isFlashAvailable = checkFlashAvailable(camera);
                // Only toggle the flashlight if it is available, i.e. not used by other applications (busy)
                if (isFlashAvailable && !isFlashOn) {
                    ToggleSingleFlashlight(camera);
                }
            }
        } catch (CameraAccessException e) { // Permission denied.
            e.printStackTrace();
        }
    }

    protected void TurnOffAllFlashlights() {
        try {
            // Get a list of cameras of the device
            String[] cameras = getCameraStringList();
            // Iterate through every camera, every lens IS a camera.
            for (String camera : cameras) {
                boolean isFlashAvailable = checkFlashAvailable(camera);
                // Only toggle the flashlight if it is available, i.e. not used by other applications (busy)
                if (isFlashAvailable && isFlashOn) {
                    ToggleSingleFlashlight(camera);
                }
            }
        } catch (CameraAccessException e) { // Permission denied.
            e.printStackTrace();
        }
    }

    protected void ToggleSingleFlashlight(String camera) throws CameraAccessException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Update the MainActivity class variable
            isFlashOn = !isFlashOn;
            // Toggle the flashlight
            mCameraManager.setTorchMode(camera, isFlashOn);
        }
    }

    protected boolean checkFlashAvailable(String camera) throws CameraAccessException {
        boolean isFlashAvailable = mCameraManager.getCameraCharacteristics(camera).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        return isFlashAvailable;
    }

    protected String[] getCameraStringList() throws CameraAccessException {
        String[] cameras = mCameraManager.getCameraIdList();
        return cameras;
    }

    protected void registerTorchCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager.registerTorchCallback(mTorchCallback, null);
        }
    }

    protected void createTorchCallback() {
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




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
