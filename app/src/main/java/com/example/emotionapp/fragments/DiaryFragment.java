package com.example.emotionapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.emotionapp.R;

public class DiaryFragment extends Fragment {

    private EditText editTextDiary;
    private Button buttonSaveDiary;

    public static final String SHARED_PREFS = "sharedPrefs"; // Consider moving to a constants file
    public static final String LAST_DIARY_ENTRY = "last_diary_entry"; // Consider moving

    public DiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextDiary = view.findViewById(R.id.editTextDiary);
        buttonSaveDiary = view.findViewById(R.id.buttonSaveDiary);

        // Set activity title (optional, if you want fragment to control activity title)
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.diary_activity_title));
        }

        buttonSaveDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiaryEntry();
            }
        });

        // Optionally, load the last saved entry
        loadLastDiaryEntry();
    }

    private void saveDiaryEntry() {
        String diaryText = editTextDiary.getText().toString().trim();

        if (!diaryText.isEmpty()) {
            if (getContext() != null) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LAST_DIARY_ENTRY, diaryText);
                editor.apply();
                Toast.makeText(getContext(), "Entry saved", Toast.LENGTH_SHORT).show();
                // editTextDiary.setText(""); // Optionally clear after save
            }
        } else {
            Toast.makeText(getContext(), "Entry cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLastDiaryEntry() {
        if (getContext() != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            String lastEntry = sharedPreferences.getString(LAST_DIARY_ENTRY, "");
            editTextDiary.setText(lastEntry);
        }
    }
}
