package android.example.morsecodeflashlight.database;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;

import java.util.List;

/*
Technically should not be in this package. However, the only source of data of the application is the Room database.
 */
public class MainRepository {
    private AlphabetDao mAlphabetDao;
    private LiveData<List<Alphabet>> mAllAlphabets;

    MainRepository(Application application){
        MorseCodeRoomDatabase db = MorseCodeRoomDatabase.getDatabase(application);
        mAlphabetDao = db.alphabetDao();
        mAllAlphabets = mAlphabetDao.getAllAlphabets();
    }

    // Inserts a single alphabet and its morse code representation into the database
    public void insert (Alphabet alphabet){
        InsertRunnable runnable = new InsertRunnable(alphabet);
        new Thread(runnable).start();
    }

    LiveData<List<Alphabet>> getAllAlphabets(){
        return mAllAlphabets;
    }

    private class InsertRunnable implements Runnable {
        Alphabet alphabetRunn;

        InsertRunnable(Alphabet alphabet){
            this.alphabetRunn = alphabet;
        }

        @Override
        public void run() {
            mAlphabetDao.insert(this.alphabetRunn);
        }
    }

    /*
    TODO: AsyncTask is deprecated. Migrate to AndroidX-supported methods.
     */
    private static class InsertAsyncTask extends AsyncTask<Alphabet, Void, Void> {
        private AlphabetDao mAlphabetDao;

        public void InsertAsyncTask(AlphabetDao alphabetDao) {
            mAlphabetDao = alphabetDao;
        }

        @Override
        protected Void doInBackground(Alphabet... alphabets) {
            mAlphabetDao.insert(alphabets[0]);
            return null;
        }
    }
}
