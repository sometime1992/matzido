package com.tech.motjip;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tech.motjip.Fragment.HomeFragment;

// HomeFragment 단독 테스트용 액티비티
public class MockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mock_fragment_container, new HomeFragment())
                    .commit();
        }
    }
}
