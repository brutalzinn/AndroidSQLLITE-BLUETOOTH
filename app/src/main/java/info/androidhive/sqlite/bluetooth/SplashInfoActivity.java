package info.androidhive.sqlite.bluetooth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import info.androidhive.sqlite.R;

public class SplashInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_info);



        Button next = (Button) findViewById(R.id.back);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
finish();

            }

        });

    }

}
