package android.example.morsecodeflashlight;

import android.example.morsecodeflashlight.database.MorseCodeViewModel;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;

public class CustomInputActivity extends AppCompatActivity {
    private Button mSendButton;
    private EditText mEditText;
    private TextView mMessageRepeated;

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

        // Initialize the ViewModel for the repository
        mMorseCodeViewModel = new ViewModelProvider(this).get(MorseCodeViewModel.class);

        // Initialize OnClicks for Views
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from mEditText;
                String textMessage = mEditText.getText().toString();
                // Set the text from mEditText to
                mMessageRepeated.setText(textMessage);
            }
        });
    }
}