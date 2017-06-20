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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.domain.Customer;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity{


    private static final String TAG = LoginActivity.class.getName();

    // UI references.
    private AutoCompleteTextView usernameView;
    private EditText passwordView;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Login form
        usernameView = (AutoCompleteTextView) findViewById(R.id.username_EditText);
        passwordView = (EditText) findViewById(R.id.password_EditText);

        // Login with existing account
        signInButton = (Button) findViewById(R.id.sign_in_Button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Store values at the time of the login attempt.
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();
                signInButton.setEnabled(false);
                volleyLogin(username, password);
            }
        });

        // Register new account
        Button registerBtn = (Button)findViewById(R.id.register_Button);
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(i);
            }
        });
    }

    public void volleyLogin(String u, String p) {

        final String username = u;
        final String password = p;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        //USE 10.0.2.2 INSTEAD OF localhost IF USING AN EMULATOR
        String url = "https://tentprog4.herokuapp.com/api/v1/login";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        String token = " ";
                        String id = "";

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (jsonObject != null) {
                            if (!jsonObject.has("error")){
                                Log.i(TAG, "Response: " + response);

                                //Start activity and put response/token in extras
                                Intent i = new Intent(getApplicationContext(), FilmActivity.class);
                                i.putExtra("USERNAME", usernameView.getEditableText().toString());
                                Customer customer = new Customer();

                                try {
                                    token = jsonObject.getString("token");
                                    id = jsonObject.getString("customer_id");
                                    customer.setCustomer_id(id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.i(TAG, "" + customer.getCustomer_id());
                                Log.i(TAG, token);

                                i.putExtra("CUSTOMER", customer);
                                i.putExtra("TOKEN", token);
                                startActivity(i);
                                signInButton.setEnabled(true);

                            } else {
                                Log.e(TAG, "Response: " + response);
                            }
                        }

                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Something went wrong.");
                        signInButton.setEnabled(true);
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

                String mContent = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
                byte[] body = new byte[0];
                try {
                    body = mContent.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Unable to gets bytes from JSON", e.fillInStackTrace());
                }
                return body;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    @Override
    public void onBackPressed() {
    }

}

