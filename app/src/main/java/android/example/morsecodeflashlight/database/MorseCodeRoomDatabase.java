package android.example.morsecodeflashlight.database;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Alphabet.class}, version = 1, exportSchema = true)
public abstract class MorseCodeRoomDatabase extends RoomDatabase {
    private static final String TAG = "MorseCodeRoomDatabase";

    // Database should be singleton, since accessing a database uses a lot of resources.
    private static MorseCodeRoomDatabase INSTANCE;

    // Constructor ensuring singleton property
    static MorseCodeRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (MorseCodeRoomDatabase.class){
                if (INSTANCE == null){
                    // Build a new instance if and only if INSTANCE is null
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MorseCodeRoomDatabase.class, "morseCodeRoomDatabase")
                            .addCallback(sMorseCodeRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // An abstract getter method for DAO
    public abstract AlphabetDao alphabetDao();

    // Callback is called when the database is first created
    private static RoomDatabase.Callback sMorseCodeRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull @NotNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            PopulateRunnable runnable = new PopulateRunnable(INSTANCE);
            new Thread(runnable).start();
        }

        @Override
        public void onOpen(@NonNull @NotNull SupportSQLiteDatabase db) {
            super.onOpen(db);

        }

        class PopulateRunnable implements Runnable {
            private final AlphabetDao mAlphabetDao;
            private List<Alphabet> mPredefinedAlphabets;

            // Constructors initializes the list of Alphabets and add to database
            // TODO: create a helper class to construct the 26 alphabets
            PopulateRunnable(MorseCodeRoomDatabase db){
                // TODO: below is just an example, remove and finish later in a new class
                // Make a DAO
                mAlphabetDao = db.alphabetDao();
                mPredefinedAlphabets = new ArrayList<>();

                Alphabet s = new Alphabet('s', "...");
                Alphabet o = new Alphabet('o', "---");

                mPredefinedAlphabets.add(s);
                mPredefinedAlphabets.add(o);

                for (int i = 0; i < mPredefinedAlphabets.size(); i++){
                    Log.d(TAG + i, mPredefinedAlphabets.toString());
                }


            }
            @Override
            public void run() {
                for (int i = 0; i < mPredefinedAlphabets.size(); i++){
                    mAlphabetDao.insert(mPredefinedAlphabets.get(i));
                }
            }
        }
    };
}
