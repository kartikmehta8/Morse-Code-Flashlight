package android.example.morsecodeflashlight.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Alphabet.class}, version = 1, exportSchema = true)
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
                            MorseCodeRoomDatabase.class, "morseCodeRoomDatabase").build();
                }
            }
        }
        return INSTANCE;
    }

    // An abstract getter method for DAO
    public abstract AlphabetDao alphabetDao();


}
