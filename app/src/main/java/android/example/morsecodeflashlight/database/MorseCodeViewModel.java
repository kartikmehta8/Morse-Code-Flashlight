package android.example.morsecodeflashlight.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MorseCodeViewModel extends AndroidViewModel {
    private MainRepository mMainRepository;
    private LiveData<List<Alphabet>> mAllAlphabets;

    // Constructor
    public MorseCodeViewModel(@NonNull @NotNull Application application) {
        super(application);
        mMainRepository = new MainRepository(application);
        mAllAlphabets = mMainRepository.getAllAlphabets();
    }

    // Getter method to get all Alphabets.
    public LiveData<List<Alphabet>> getAllAlphabets() {
        return mAllAlphabets;
    }
}
