package android.example.morsecodeflashlight.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlphabetDao {
    /*
    Inserts an alphabet to the database table
     */
    @Insert
    void insert(Alphabet alphabet);

    /*
    Deletes all alphabets from the database table
     */
    @Query("DELETE FROM alphabet_table")
    void deleteAll();

    /*
    Returns all alphabets from the database table
     */
    @Query("SELECT * from alphabet_table ORDER BY alphabet ASC")
    LiveData<List<Alphabet>> getAllAlphabets();
}
