package samuli.androidbeacons.loginandregister;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import samuli.androidbeacons.MainActivity;
import samuli.androidbeacons.R;
import samuli.androidbeacons.utils.PreferenceUtils;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        //Setting proggress bar color
        progressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);

        //Check if username and password are saved
        if (PreferenceUtils.getUsername(this) != null && !PreferenceUtils.getUsername(this).equals("")){
            response(PreferenceUtils.getUsername(this), PreferenceUtils.getPassword(this));
        }else{
            Intent intent = new Intent(StartActivity.this, LogingActivity.class);
            StartActivity.this.startActivity(intent);
            finish();
        }
    }

    public void response(String username, String password){
        final String rUsername = username;
        final String rPassword = password;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.d("tag", ""+jsonResponse.toString());
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        PreferenceUtils.saveUsername(rUsername, StartActivity.this);
                        PreferenceUtils.savePassword(rPassword, StartActivity.this);
                        Log.d("tag", "success");
                        String name = jsonResponse.getString("name");
                        int user_id = jsonResponse.getInt("user_id");
                        int age = jsonResponse.getInt("age");

                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("user_id", user_id);
                        //intent.putExtra("age", age);
                        //intent.putExtra("username", username);
                        StartActivity.this.startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                        builder.setMessage("Login Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(StartActivity.this);
        queue.add(loginRequest);
    }
}
