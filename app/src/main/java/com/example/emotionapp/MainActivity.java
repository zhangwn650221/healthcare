package com.example.emotionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import com.example.emotionapp.fragments.DiaryFragment;
import com.example.emotionapp.fragments.HomeFragment;
import com.example.emotionapp.fragments.VideoRecordingFragment;
import com.example.emotionapp.fragments.VoiceRecordingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_diary) {
                selectedFragment = new DiaryFragment();
            } else if (itemId == R.id.nav_voice) {
                selectedFragment = new VoiceRecordingFragment();
            } else if (itemId == R.id.nav_video) {
                selectedFragment = new VideoRecordingFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            // This will also trigger onItemSelected, loading the HomeFragment
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        // fragmentTransaction.addToBackStack(null); // Optional: if you want back stack behavior
        fragmentTransaction.commit();
    }
}
