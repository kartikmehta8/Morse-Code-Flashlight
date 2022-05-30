package android.example.morsecodeflashlight.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Alphabet.class}, version = 1)
public abstract class MorseCodeRoomDatabase extends RoomDatabase {

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
                mAlphabetDao = getAlphabetDao(db);
                addAlphabetsToHelperList();


            }

            private void addAlphabetsToHelperList() {
                mPredefinedAlphabets = new ArrayList<>();

                // Add alphabets and digits to the list
                mPredefinedAlphabets.add(new Alphabet('a', ".-"));
                mPredefinedAlphabets.add(new Alphabet('b', "-..."));
                mPredefinedAlphabets.add(new Alphabet('c', "-.-."));
                mPredefinedAlphabets.add(new Alphabet('d', "-.."));
                mPredefinedAlphabets.add(new Alphabet('e', "."));
                mPredefinedAlphabets.add(new Alphabet('f', "..-."));
                mPredefinedAlphabets.add(new Alphabet('g', "--."));
                mPredefinedAlphabets.add(new Alphabet('h', "...."));
                mPredefinedAlphabets.add(new Alphabet('i', ".."));
                mPredefinedAlphabets.add(new Alphabet('j', ".---"));
                mPredefinedAlphabets.add(new Alphabet('k', "-.-"));
                mPredefinedAlphabets.add(new Alphabet('l', ".-.."));
                mPredefinedAlphabets.add(new Alphabet('m', "--"));
                mPredefinedAlphabets.add(new Alphabet('n', "-."));
                mPredefinedAlphabets.add(new Alphabet('o', "---"));
                mPredefinedAlphabets.add(new Alphabet('p', ".--."));
                mPredefinedAlphabets.add(new Alphabet('q', "--.-"));
                mPredefinedAlphabets.add(new Alphabet('r', ".-."));
                mPredefinedAlphabets.add(new Alphabet('s', "..."));
                mPredefinedAlphabets.add(new Alphabet('t', "-"));
                mPredefinedAlphabets.add(new Alphabet('u', "..-"));
                mPredefinedAlphabets.add(new Alphabet('v', "...-"));
                mPredefinedAlphabets.add(new Alphabet('w', ".--"));
                mPredefinedAlphabets.add(new Alphabet('x', "-..-"));
                mPredefinedAlphabets.add(new Alphabet('y', "-.--"));
                mPredefinedAlphabets.add(new Alphabet('z', "--.."));
                mPredefinedAlphabets.add(new Alphabet('0', "-----"));
                mPredefinedAlphabets.add(new Alphabet('1', ".----"));
                mPredefinedAlphabets.add(new Alphabet('2', "..---"));
                mPredefinedAlphabets.add(new Alphabet('3', "...--"));
                mPredefinedAlphabets.add(new Alphabet('4', "....-"));
                mPredefinedAlphabets.add(new Alphabet('5', "....."));
                mPredefinedAlphabets.add(new Alphabet('6', "-...."));
                mPredefinedAlphabets.add(new Alphabet('7', "--..."));
                mPredefinedAlphabets.add(new Alphabet('8', "---.."));
                mPredefinedAlphabets.add(new Alphabet('9', "----."));
                mPredefinedAlphabets.add(new Alphabet(' ', "_")); // two spaces
            }

            private AlphabetDao getAlphabetDao(MorseCodeRoomDatabase db) {
                final AlphabetDao mAlphabetDao;
                mAlphabetDao = db.alphabetDao();
                return mAlphabetDao;
            }

            @Override
            public void run() {
                for (Alphabet mPredefinedAlphabet : mPredefinedAlphabets) {
                    mAlphabetDao.insert(mPredefinedAlphabet);
                }
            }
        }
    };
}
