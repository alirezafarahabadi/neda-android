package ir.batna.neda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ir.batna.neda.service.NedaService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NedaService.initialize(this);
    }
}