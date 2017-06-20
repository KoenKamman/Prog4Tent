package nl.code7.prog4tent_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import nl.code7.prog4tent_android.presentation.activities.FilmActivity;
import nl.code7.prog4tent_android.presentation.activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }
}


