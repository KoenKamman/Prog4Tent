package nl.code7.prog4tent_android.presentation.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.presentation.fragments.FilmFragment;

public class RegistrationActivity extends AppCompatActivity {

    // UI references.
    private EditText firstnameView, lastnameView, emailView, usernameView, passwordView, addressView, storeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Login form
        firstnameView = (EditText) findViewById(R.id.new_firstname_EditText);
        lastnameView = (EditText) findViewById(R.id.new_lastname_EditText);
        emailView = (EditText) findViewById(R.id.new_email_EditText);
        usernameView = (EditText) findViewById(R.id.new_username_EditText);
        passwordView = (EditText) findViewById(R.id.new_password_EditText);
        addressView = (EditText) findViewById(R.id.new_adress_EditText);
        storeView = (EditText) findViewById(R.id.new_storeid_EditText);


        Button make_account_Btn = (Button) findViewById(R.id.complete_registration_Button);
        make_account_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String firstname = firstnameView.getText().toString();
                String lastname = lastnameView.getText().toString();
                String email = emailView.getText().toString();
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();
                String address = addressView.getText().toString();
                String store = storeView.getText().toString();

                volleyRegistration(firstname, lastname, email, username, password, address, store);



            }
        });


    }

    public void volleyRegistration(String fn, String ln,String em, String u, String p, String add, String st) {

        final String firstname = fn;
        final String lastname = ln;
        final String email = em;
        final String username = u;
        final String password = p;
        final String address = add;
        final String store = st;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        //USE 10.0.2.2 INSTEAD OF localhost IF USING AN EMULATOR
        String url = "https://tentprog4.herokuapp.com/api/v1/register";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        //TODO: Rework if statement
                        if (!response.contains("error") && !response.isEmpty()){
                            //Log.i(TAG, "Response: " + response);

                            //Start activity and put response/token in extras
                            Intent i = new Intent(getApplicationContext(), FilmActivity.class);
                            i.putExtra("EMAIL", emailView.getEditableText().toString());
                            String token = response.replaceAll("^\"|\"$", "");
                            i.putExtra("TOKEN", token);
                            startActivity(i);
                        } else {
                            //Log.e(TAG, "Response: " + response);
                        }

                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e(TAG, "Something went wrong.");
                    }
                }) {

            //Set headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            //Set content-type of the body
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            //Set the body of the request
            @Override
            public byte[] getBody() throws AuthFailureError {

                String mContent = "{\"first_name\":\"" + firstname + "\",\"last_name\":\"" + lastname + "\",\"email\":\"" + email + "\",\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"address_id\":\"" + address + "\",\"store_id\":\"" + store + "\"}";
                Log.i("TAG", mContent);
                byte[] body = new byte[0];
                try {
                    body = mContent.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    //Log.e(TAG, "Unable to gets bytes from JSON", e.fillInStackTrace());
                }
                return body;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
