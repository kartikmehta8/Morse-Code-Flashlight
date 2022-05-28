package android.example.morsecodeflashlight;

import android.example.morsecodeflashlight.database.Alphabet;
import android.example.morsecodeflashlight.database.MorseCodeViewModel;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomInputActivity extends AppCompatActivity {
    private Button mSendButton;
    private EditText mEditText;
    private TextView mMessageRepeated;
    private TextView mMorseCodeTranslation;



    // ViewModel to get the alphabets from the database (repository to be exact)
    private MorseCodeViewModel mMorseCodeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_input);

        // Initialize views
        mSendButton = findViewById(R.id.sendButton);
        mEditText = findViewById(R.id.editText);
        mMessageRepeated = findViewById(R.id.messageRepeated);
        mMorseCodeTranslation = findViewById(R.id.morseCodeTranslation);

        // Morse Code mapping
        final Map<Character, String> morseCodeMapping = new HashMap<>();

        // Initialize the ViewModel
        mMorseCodeViewModel = new ViewModelProvider(this).get(MorseCodeViewModel.class);

        // Create the observer
        final Observer<List<Alphabet>> observer = new Observer<List<Alphabet>>(){
            @Override
            public void onChanged(List<Alphabet> alphabets) {
                // When the LiveData changes, recreate the mapping
                for (Alphabet item: alphabets){
                    morseCodeMapping.put(item.alphabet, item.morseCode);
                }
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mMorseCodeViewModel.getAllAlphabets().observe(this, observer);

        // Initialize OnClicks for Views
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from mEditText;
                String textMessage = mEditText.getText().toString();
                // Set the text from mEditText to
                mMessageRepeated.setText(textMessage);

                // Translate the text into Morse Code
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < textMessage.length(); i++){
                    char c = textMessage.charAt(i);
                    String correspondingMorseCode = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        correspondingMorseCode = morseCodeMapping.getOrDefault(c, "?");
                    }
                    sb.append(correspondingMorseCode);
                }

                String translated = sb.toString();
                mMorseCodeTranslation.setText(translated);
            }
        });








    }


}