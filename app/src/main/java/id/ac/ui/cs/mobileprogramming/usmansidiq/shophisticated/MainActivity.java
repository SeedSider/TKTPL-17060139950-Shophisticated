package id.ac.ui.cs.mobileprogramming.usmansidiq.shophisticated;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView internetStatus;
    private ActionBar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internetStatus = (TextView) findViewById(R.id.internet_status);

        // At activity startup we manually check the internet status and change
        // the text status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeTextStatus(true);
        } else {
            changeTextStatus(false);
        }

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle(R.string.home);
    }

    public void changeTextStatus(boolean isConnected) {

        // Change status according to boolean value
        if (isConnected) {
            internetStatus.setText(R.string.connected);
            internetStatus.setTextColor(Color.parseColor("#00ff00"));
        } else {
            internetStatus.setText(R.string.disconnected);
            internetStatus.setTextColor(Color.parseColor("#ff0000"));
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        Shophisticated.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
        Shophisticated.activityResumed();// On Resume notify the Application
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_history:
                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                    return true;
                case R.id.navigation_sell:
                    startActivity(new Intent(MainActivity.this, SellActivity.class));
                    return true;
            }
            return false;
        }
    };
}