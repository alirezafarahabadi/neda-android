package ir.batna.neda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent(getApplicationContext(), WebSocketService.class);
                startService(serviceIntent);
            }
        });
        serviceThread.run();
    }
}
