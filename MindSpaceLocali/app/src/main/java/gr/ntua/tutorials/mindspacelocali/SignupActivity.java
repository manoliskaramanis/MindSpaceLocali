package gr.ntua.tutorials.mindspacelocali;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("password", "1111");
        setResult(RESULT_OK, intent);
        finish();
    }
}
