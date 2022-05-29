package android.example.morsecodeflashlight;

import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class ParserRunnable extends CameraFlashManager implements Runnable {
    String message;

    ParserRunnable(CameraManager cameraManager, String message) {
        super(cameraManager);
        this.message = message;
    }

    @Override
    public void run() {
        super.TurnOffAllFlashlights();

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == '.') {
                try {
                    flashDit();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (c == '-') {
                try {
                    flashDah();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    flashPauseLetter();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void flashPauseLetter() throws InterruptedException {
        Thread.sleep(300);
    }

    private void flashDah() throws InterruptedException {
        TurnOnAllFlashlights();
        flashPauseLetter();
        TurnOffAllFlashlights();
        Thread.sleep(100);
    }

    private void flashDit() throws InterruptedException {
        TurnOnAllFlashlights();
        Thread.sleep(100);
        TurnOffAllFlashlights();
        Thread.sleep(100);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

