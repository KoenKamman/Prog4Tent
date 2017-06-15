package nl.code7.prog4tent_android.presentation.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import nl.code7.prog4tent_android.adapter.RentalAdapter;
import nl.code7.prog4tent_android.domain.Customer;
import nl.code7.prog4tent_android.domain.Rental;
import nl.code7.prog4tent_android.presentation.activities.RentalActivity;
import nl.code7.prog4tent_android.presentation.activities.RentalDetailActivity;

public class RentalFragment extends Fragment {
    private static final String TAG = RentalActivity.class.getName();

    private ListView rentalListView;
    private ArrayList<Rental> rentalList;
    private RentalAdapter rentalAdapter;

    Customer customer;


    public RentalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        customer = (Customer) getActivity().getIntent().getSerializableExtra("CITY");
        View rootView = inflater.inflate(R.layout.fragment_rental, container, false);

        rentalList = new ArrayList<Rental>();
        rentalListView= (ListView) rootView.findViewById(R.id.rental_ListView);
        rentalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getContext(), RentalDetailActivity.class);
                Rental rental = rentalList.get(position);
                i.putExtras(getActivity().getIntent().getExtras());
                i.putExtra("POSITION", position);
                i.putExtra("RENTAL", rental);


                // getrentalslist
                //i.putExtra("RENTALS", emailView.getEditableText().toString());
                startActivity(i);
            }
        });
        rentalAdapter = new RentalAdapter(getContext(), rentalList);
        rentalListView.setAdapter(rentalAdapter);
        volleyRentals();
        return rootView;
    }
    public void volleyRentals() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        //USE 10.0.2.2 INSTEAD OF localhost IF USING AN EMULATOR
        String url = "http://10.0.2.2:8080/api/v1/rentals/" + customer.getCustomer_id();

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                Rental rental = new Rental();
                                rental.setRental_id(obj.getInt("rental_id"));
                                rental.setRental_date(obj.getString("rental_date"));
                                rental.setInventory_id(obj.getInt("inventory_id"));
                                rental.setCustomer_id(obj.getInt("customer_id"));
                                rental.setReturn_date(obj.getString("return_date"));
                                rental.setStaff_id(obj.getInt("staff_id"));
                                rental.setLast_update(obj.getString("last_update"));

//                                Film film = new Film();
//
//                                film.setFilm_id(obj.getInt("film_id"));
//                                film.setTitle(obj.getString("title"));
//                                film.setDescription(obj.getString("description"));
//                                film.setRelease_year(obj.getInt("release_year"));
//                                film.setLanguage_id(obj.getInt("language_id"));
//                                film.setRental_rate(obj.getInt("rental_rate"));
//                                film.setLength(obj.getInt("length"));
//                                film.setReplacement_cost(obj.getInt("replacement_cost"));
//                                film.setRating(obj.getString("rating"));
//                                film.setSpecial_feature(obj.getString("special_features"));
//                                film.setLast_update(obj.getString("last_update"));
                                rentalList.add(rental);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        rentalAdapter.notifyDataSetChanged();
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
}
