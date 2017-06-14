package nl.code7.prog4tent_android.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import nl.code7.prog4tent_android.presentation.activities.FilmActivity;
import nl.code7.prog4tent_android.presentation.activities.FilmDetailActivity;
import nl.code7.prog4tent_android.presentation.activities.RentalDetailActivity;

public class FilmFragment extends Fragment {

    private static final String TAG = FilmActivity.class.getName();
    
    private ListView filmListView;
    private ArrayList<Film> filmList;
    private FilmAdapter filmAdapter;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_film);
//    }

    public FilmFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_film, container, false);

        filmList = new ArrayList<Film>();

        filmListView = (ListView) rootView.findViewById(R.id.film_ListView);
        filmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(getContext(), FilmDetailActivity.class);

                Intent i = new Intent(view.getContext(), FilmDetailActivity.class);
                Film film = filmList.get(position);
                i.putExtras(getActivity().getIntent().getExtras());
                i.putExtra("POSITION", position);
                i.putExtra("FILM", film);
                startActivityForResult(i, 1);
                // getrentalslist
                //i.putExtra("RENTALS", emailView.getEditableText().toString());
                startActivity(i);
            }
        });

        filmAdapter = new FilmAdapter(getContext(), filmList);
        filmListView.setAdapter(filmAdapter);

        volleyFilms();
        return rootView;


    }


    public void volleyFilms() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        //USE 10.0.2.2 INSTEAD OF localhost IF USING AN EMULATOR
        String url = "http://10.0.2.2:8080/api/v1/films";

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                Film film = new Film();
                                film.setTitle(obj.getString("title"));
//                                City city = new City();
//                                city.setCountryCode(obj.getString("CountryCode"));
//                                city.setDistrict(obj.getString("District"));
//                                city.setID(obj.getString("ID"));
//                                city.setName(obj.getString("Name"));
//                                city.setPopulation(obj.getString("Population"));
                                filmList.add(film);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        filmAdapter.notifyDataSetChanged();
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e(TAG, "Something went wrong.");
                    }
                }) {

            //Set headers and provide X-Access-Token generated during login.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                String token = getActivity().getIntent().getExtras().getString("TOKEN");
                headers.put("X-Access-Token", token);
                return headers;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == 2){
                filmList.remove(data.getExtras().getInt("POSITION"));
                filmAdapter.notifyDataSetChanged();
            } else if (resultCode == 3){
                Film film = filmList.get(data.getExtras().getInt("POSITION"));
                Bundle b = data.getExtras();
                film.setTitle(b.getString("TITLE"));
//                film.setName(b.getString("NAME"));
//                film.setPopulation(b.getString("POPULATION"));
                filmAdapter.notifyDataSetChanged();
            }

        }
    }

}

