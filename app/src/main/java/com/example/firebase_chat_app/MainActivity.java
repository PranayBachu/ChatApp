package com.example.firebase_chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Toolbar mToolbar;

    ViewPager viewPager;
    private SectionPageAdaptor sectionPageAdaptor;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.mainpage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat App");
        viewPager =findViewById(R.id.main_viewPager);
        sectionPageAdaptor = new SectionPageAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(sectionPageAdaptor);
        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            start_activity();
        }
    }

    private void start_activity() {
        Intent startIntent = new Intent(MainActivity.this, FirstActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            start_activity();
        }
        if (item.getItemId() == R.id.account_setting){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.all_users){
            Intent intent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(intent);
        }
        return true;
    }
}