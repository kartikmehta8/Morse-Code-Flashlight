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
        // Handshake
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < message.length(); i++) {
            // Extract the current character
            char c = message.charAt(i);

            // Sends out dits or dahs or turn of flashlight depending on the morse code.
            if (c == '.') {
                try {
                    flashDit();
                } catch (InterruptedException e) {
                    handleInterruptedException(e);
                    return;
                }
            } else if (c == '-') {
                try {
                    flashDah();
                } catch (InterruptedException e) {
                    handleInterruptedException(e);
                    return;
                }
            } else if (c == '_') {
                try {
                    flashPauseWord();
                } catch (InterruptedException e) {
                    handleInterruptedException(e);
                    return;
                }
            } else {
                try {
                    flashPauseLetter();
                } catch (InterruptedException e) {
                    handleInterruptedException(e);
                    return;
                }
            }

        }
    }

    /*
    Clean up procedures if the thread is interrupted (probably by UI stop button)
     */
    private void handleInterruptedException(InterruptedException e) {
        TurnOffAllFlashlights();
        Thread.currentThread().interrupt();
        e.printStackTrace();
    }

    /*
    Flash sequence between words
     */
    private void flashPauseWord() throws InterruptedException {
        Thread.sleep(700);
    }

    /*
    Flash sequence between letters of the same word
     */
    private void flashPauseLetter() throws InterruptedException {
        Thread.sleep(300);
    }

    /*
    Flash sequence for a '-'
     */
    private void flashDah() throws InterruptedException {
        TurnOnAllFlashlights();
        flashPauseLetter();
        TurnOffAllFlashlights();
        Thread.sleep(100);
    }

    /*
    Flash sequence for a '.'
     */
    private void flashDit() throws InterruptedException {
        TurnOnAllFlashlights();
        Thread.sleep(100);
        TurnOffAllFlashlights();
        Thread.sleep(100);
    }


    /*
    Currently not in used. Set as default method.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

