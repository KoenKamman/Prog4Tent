package nl.code7.prog4tent_android.presentation.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.adapter.InventoryAdapter;
import nl.code7.prog4tent_android.domain.Customer;
import nl.code7.prog4tent_android.domain.Film;
import nl.code7.prog4tent_android.domain.Inventory;

public class InventoryActivity extends AppCompatActivity {

    private static final String TAG = InventoryActivity.class.getName();

    private ListView inventoryListView;
    private ArrayList<Inventory> inventoryList;
    private InventoryAdapter inventoryAdapter;
    private Film film;

    private Bundle bundle;
    private String token;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        bundle = getIntent().getExtras();
        token = bundle.getString("TOKEN");
        customer = (Customer) bundle.getSerializable("CUSTOMER");

        film = (Film) getIntent().getExtras().getSerializable("FILM");

        inventoryList = new ArrayList<>();

        inventoryListView = (ListView) findViewById(R.id.inventoryLV);
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder alert = new AlertDialog.Builder(InventoryActivity.this);
                Boolean available = !inventoryList.get(position).getReturn_date().contains("null");
                if (available) {
                    alert.setMessage("Do you want to rent this film?");
                    alert.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    volleyRent(inventoryList.get(position));
                                }
                            });
                    alert.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                } else {
                    alert.setMessage("This copy is currently not available.");
                    alert.setNeutralButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
                alert.show();
            }
        });

        inventoryAdapter = new InventoryAdapter(getApplicationContext(), inventoryList);
        inventoryListView.setAdapter(inventoryAdapter);

        volleyInventory();
    }

    public void volleyInventory() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //USE 10.0.2.2 INSTEAD OF localhost IF USING AN EMULATOR
        String url = "https://tentprog4.herokuapp.com/api/v1/films/" + film.getFilm_id() + "/inventory";

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        inventoryList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                Inventory inv = new Inventory();
                                inv.setFilm_id(obj.getInt("film_id"));
                                inv.setInventory_id(obj.getInt("inventory_id"));
                                inv.setStore_id(obj.getInt("store_id"));
                                inv.setReturn_date(obj.getString("return_date"));
                                Log.d(TAG, inv.getReturn_date() + "");
                                inventoryList.add(inv);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        inventoryAdapter.notifyDataSetChanged();
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
                headers.put("X-Access-Token", token);
                return headers;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }

    public void volleyRent(Inventory inventory) {

        int inventory_id = inventory.getInventory_id();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        //USE 10.0.2.2 INSTEAD OF localhost IF USING AN EMULATOR
        String url = "http://tentprog4.herokuapp.com/api/v1/rentals/" + customer.getCustomer_id() + "/" + inventory_id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                        volleyInventory();
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Something went wrong.");
                    }
                }) {

            //Set headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Access-Token", token);
                return headers;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

}
