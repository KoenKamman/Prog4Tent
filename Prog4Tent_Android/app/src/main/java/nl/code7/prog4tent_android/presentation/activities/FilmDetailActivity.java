package nl.code7.prog4tent_android.presentation.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.domain.Film;

import static nl.code7.prog4tent_android.R.string.film;

public class FilmDetailActivity extends AppCompatActivity {

    private static final String TAG = FilmDetailActivity.class.getName();

    TextView filmID, filmTitle, filmDescription, filmReleaseYear, filmLangID, filmOrgLangID,
            filmRentDur, filmRentRate, filmLength, filmRepCost, filmRating, filmSpecialFeat,
            filmLastUpdate;
    Film film;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        film = (Film) getIntent().getSerializableExtra("FILM");

        filmID          = (TextView) findViewById(R.id.film_id_TextView);
        filmTitle       = (TextView) findViewById(R.id.film_title_TextView);
        filmDescription = (TextView) findViewById(R.id.film_description_TextView);
        filmReleaseYear = (TextView) findViewById(R.id.film_releaseyear_TextView);
        filmLangID      = (TextView) findViewById(R.id.film_languageID_TextView);
        filmOrgLangID   = (TextView) findViewById(R.id.film_original_languageID_TextView);
        filmRentDur     = (TextView) findViewById(R.id.film_rental_duration_TextView);
        filmRentRate    = (TextView) findViewById(R.id.film_rental_rate_TextView);
        filmLength      = (TextView) findViewById(R.id.film_length_TextView);
        filmRepCost     = (TextView) findViewById(R.id.film_replacement_cost_TextView);
        filmRating      = (TextView) findViewById(R.id.film_rating_TextView);
        filmSpecialFeat = (TextView) findViewById(R.id.film_special_features_TextView);
        filmLastUpdate  = (TextView) findViewById(R.id.film_last_update_TextView);

        filmID.setText("ID: " + film.getFilm_id());
        filmTitle.setText("Title: " + film.getTitle());
        filmDescription.setText("Description:" + film.getDescription());
        filmReleaseYear.setText("Release Year: " + film.getRelease_year());
        filmLangID.setText("Language ID: " + film.getLanguage_id());
        filmOrgLangID.setText("Orignal Language ID:  " + film.getOriginal_language_id());
        filmRentDur.setText("Rental Duration: " + film.getRental_duration());
        filmRentRate.setText("Rental Rate: " + film.getRental_rate());
        filmLength.setText("Length: " + film.getLength());
        filmRepCost.setText("Replacement Cost: " + film.getReplacement_cost());
        filmRating.setText("Rating: " + film.getRating());
        filmSpecialFeat.setText("Special Features: " + film.getSpecial_feature());
        filmLastUpdate.setText("Last Update: " + film.getLast_update());

//        Button removeButton = (Button) findViewById(R.id.rent_film_Btn);
//        removeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                volleyRent();
//            }
//        });

    }


}
