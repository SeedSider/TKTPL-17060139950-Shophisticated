package id.ac.ui.cs.mobileprogramming.usmansidiq.shophisticated;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Shophisticated extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
