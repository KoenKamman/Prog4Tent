package nl.code7.prog4tent_android.presentation.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.adapter.FilmAdapter;
import nl.code7.prog4tent_android.domain.Film;

public class FilmDetailActivity extends AppCompatActivity {

    private static final String TAG = FilmActivity.class.getName();

    private ListView filmListView;
    private ArrayList<Film> filmList;
    private FilmAdapter filmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);


        filmAdapter = new FilmAdapter(getApplicationContext(), filmList);
        filmListView.setAdapter(filmAdapter);

        volleyFilms();
    }

    public void volleyFilms() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        //USE 10.0.2.2 INSTEAD OF localhost IF USING AN EMULATOR
        String url = "http://10.0.2.2:8080/api/v1/cities";

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                Film film = new Film();
                                film.setTitle(obj.getString("CountryCode"));
                                City city = new City();
                                city.setCountryCode(obj.getString("CountryCode"));
                                city.setDistrict(obj.getString("District"));
                                city.setID(obj.getString("ID"));
                                city.setName(obj.getString("Name"));
                                city.setPopulation(obj.getString("Population"));
                                cityList.add(city);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cityAdapter.notifyDataSetChanged();
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

        };

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == 1) {
//
//            if(resultCode == 2){
//                cityList.remove(data.getExtras().getInt("POSITION"));
//                cityAdapter.notifyDataSetChanged();
//            } else if (resultCode == 3){
//                City city = cityList.get(data.getExtras().getInt("POSITION"));
//                Bundle b = data.getExtras();
//                city.setDistrict(b.getString("DISTRICT"));
//                city.setName(b.getString("NAME"));
//                city.setPopulation(b.getString("POPULATION"));
//                cityAdapter.notifyDataSetChanged();
//            }
//
//        }
//    }
}
