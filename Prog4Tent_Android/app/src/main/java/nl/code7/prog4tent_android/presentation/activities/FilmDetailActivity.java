package nl.code7.prog4tent_android.presentation.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.domain.Customer;
import nl.code7.prog4tent_android.domain.Film;
import nl.code7.prog4tent_android.domain.Inventory;

public class FilmDetailActivity extends AppCompatActivity {

    private static final String TAG = FilmDetailActivity.class.getName();

    TextView filmID, filmTitle, filmDescription, filmReleaseYear, filmLangID, filmOrgLangID,
            filmRentDur, filmRentRate, filmLength, filmRepCost, filmRating, filmSpecialFeat,
            filmLastUpdate;
    Film film;
    Button rentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        film = (Film) getIntent().getSerializableExtra("FILM");

        rentBtn = (Button) findViewById(R.id.rent_film_Btn);
        rentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volleyRent();
            }
        });

        filmID = (TextView) findViewById(R.id.film_id_TextView);
        filmTitle = (TextView) findViewById(R.id.film_title_TextView);
        filmDescription = (TextView) findViewById(R.id.film_description_TextView);
        filmReleaseYear = (TextView) findViewById(R.id.film_releaseyear_TextView);
        filmLangID = (TextView) findViewById(R.id.film_languageID_TextView);
        filmOrgLangID = (TextView) findViewById(R.id.film_original_languageID_TextView);
        filmRentDur = (TextView) findViewById(R.id.film_rental_duration_TextView);
        filmRentRate = (TextView) findViewById(R.id.film_rental_rate_TextView);
        filmLength = (TextView) findViewById(R.id.film_length_TextView);
        filmRepCost = (TextView) findViewById(R.id.film_replacement_cost_TextView);
        filmRating = (TextView) findViewById(R.id.film_rating_TextView);
        filmSpecialFeat = (TextView) findViewById(R.id.film_special_features_TextView);
        filmLastUpdate = (TextView) findViewById(R.id.film_last_update_TextView);

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

    }

    public void volleyRent() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        Film film = (Film) getIntent().getExtras().getSerializable("FILM");
        Inventory inv = (Inventory) getIntent().getExtras().getSerializable("INVENTORY");
        Customer customer = (Customer) getIntent().getExtras().getSerializable("CUSTOMER");

        //USE 10.0.2.2 INSTEAD OF localhost IF USING AN EMULATOR
        String url = "https://tentprog4.herokuapp.com/api/v1/rentals/" + customer.getCustomer_id() + "/" + inv.getInventory_id();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Something went wrong.");
                    }
                }) {

            //Set headers and provide X-Access-Token generated during login.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                String token = getIntent().getExtras().getString("TOKEN");
                headers.put("X-Access-Token", token);
                return headers;
            }

//            //Set content-type of the body
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//            //Set the body of the request
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//
//                String mContent = "{\"staff_id\":\"85\", \"return_date\":\"\"}";
//                byte[] body = new byte[0];
//                try {
//                    body = mContent.getBytes("UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    Log.e("TAG", "Unable to gets bytes from JSON", e.fillInStackTrace());
//                }
//                return body;
//            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


}
