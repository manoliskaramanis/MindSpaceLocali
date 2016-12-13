package gr.ntua.tutorials.mindspacelocali;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import gr.ntua.tutorials.mindspacelocali.utils.VolleyRequests;

public class MainActivity extends AppCompatActivity {

    private static int GO_TO_SIGNUP = 10010;
    boolean hasUsernameText = false;
    boolean hasPasswordText = false;
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = (Button) findViewById(R.id.login);
//        loginButton.setEnabled(false);
        loginButton.setVisibility(View.GONE);
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() > 0){
                    hasUsernameText = true;
                    if(hasPasswordText){
//                        loginButton.setEnabled(true);
                        loginButton.setVisibility(View.VISIBLE);
                    }
                }else{
                    hasUsernameText = false;
//                    loginButton.setEnabled(false);
                    loginButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() > 0){
                    hasPasswordText = true;
                    if(hasUsernameText){
//                        loginButton.setEnabled(true);
                        loginButton.setVisibility(View.VISIBLE);
                    }
                }else{
                    hasPasswordText = false;
//                    loginButton.setEnabled(false);
                    loginButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }

    private void signup() {

    }

    private void login() throws JSONException {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        JSONObject jo = new JSONObject();
        jo.put("username", username);
        jo.put("password", password);

        Log.d("Logs", jo.toString());
//        VolleyRequests volley = new VolleyRequests();
//        volley.login(jo, new VolleyRequests.VolleyCallbackLogin() {
//            @Override
//            public void onSuccess(String id) throws JSONException {
//                if(id != null){
//                    goToSecondActivity();
//                }
//            }
//        }, this);

        goToSecondActivity();
    }

    private void goToSecondActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivityForResult(intent, GO_TO_SIGNUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GO_TO_SIGNUP && resultCode == RESULT_OK){
            usernameEditText.setText((String) data.getSerializableExtra("password"));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent servIntent = new Intent(this, LocationUpdates.class);
        startService(servIntent);
    }
}
