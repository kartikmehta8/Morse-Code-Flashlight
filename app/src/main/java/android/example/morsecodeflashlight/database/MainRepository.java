package android.example.morsecodeflashlight.database;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

/*
Technically should not be in this package. However, the only source of data of the application is the Room database.
 */
public class MainRepository {
    private AlphabetDao mAlphabetDao;
    private LiveData<List<Alphabet>> mAllAlphabets;

    MainRepository(Application application) {
        MorseCodeRoomDatabase db = MorseCodeRoomDatabase.getDatabase(application);
        mAlphabetDao = db.alphabetDao();
        mAllAlphabets = mAlphabetDao.getAllAlphabets();
    }

    // Inserts a single alphabet and its morse code representation into the database
    public void insert(Alphabet alphabet) {
        InsertRunnable runnable = new InsertRunnable(alphabet);
        new Thread(runnable).start();
    }

    LiveData<List<Alphabet>> getAllAlphabets() {
        return mAllAlphabets;
    }

    private class InsertRunnable implements Runnable {
        Alphabet alphabet;

        InsertRunnable(Alphabet alphabet) {
            this.alphabet = alphabet;
        }

        @Override
        public void run() {
            mAlphabetDao.insert(this.alphabet);
        }
    }
}
