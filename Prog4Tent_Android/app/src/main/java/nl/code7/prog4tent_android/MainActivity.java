package nl.code7.prog4tent_android;

import android.content.Intent;
import android.os.Bundle;

import nl.code7.prog4tent_android.activities.FilmActivity;
import nl.code7.prog4tent_android.activities.MenuActivity;

public class MainActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        Intent i = new Intent(getApplicationContext(), FilmActivity.class);
        startActivity(i);
    }
}
