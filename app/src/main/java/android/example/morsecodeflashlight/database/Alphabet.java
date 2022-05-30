package android.example.morsecodeflashlight.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;

@Entity(tableName = "alphabet_table")
public class Alphabet {
    Alphabet(char alphabet, @NotNull String morseCode) {
        this.alphabet = alphabet;
        this.morseCode = morseCode;
    }

    @PrimaryKey
    @ColumnInfo(name = "alphabet")
    public char alphabet;

    @NonNull
    @ColumnInfo(name = "morse_code")
    // A string of '.' and '-'
    public String morseCode;
}
