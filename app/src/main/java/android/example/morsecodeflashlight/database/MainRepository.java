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
        new insertAsyncTask(mAlphabetDao).execute(alphabet);
    }


    LiveData<List<Alphabet>> getAllAlphabets(){
        return mAllAlphabets;
    }

    /*
    TODO: AsyncTask is deprecated. Migrate to AndroidX-supported methods.
     */
    private class insertAsyncTask extends AsyncTask<Alphabet, Void, Void> {
        private AlphabetDao mAlphabetDao;

        public insertAsyncTask(AlphabetDao AlphabetDao) {
            mAlphabetDao = AlphabetDao;
        }

        @Override
        protected Void doInBackground(Alphabet... alphabets) {
            mAlphabetDao.insert(alphabets[0]);
            return null;
        }
    }
}
