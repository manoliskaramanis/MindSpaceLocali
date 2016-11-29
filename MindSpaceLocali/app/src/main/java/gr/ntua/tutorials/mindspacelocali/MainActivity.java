package gr.ntua.tutorials.mindspacelocali;

import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppController.getInstance()
                .getmLastLocation();
//        Log.d("Logs", latitude.toString());

        try {
            JSONObject jo = new JSONObject();
            jo.put("name", "Manolis");
            jo.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString("name", "Manolis").apply();
        PreferenceManager.getDefaultSharedPreferences(this).
                getString("name", "don't know name");
    }
}
