package me.swirly.ash.X;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import me.swirly.ash.R;

public class Blank extends AppCompatActivity {

    //Controller Value init
    public Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x_blank);

        //Controller Value Setting
        controller = new Controller(this);


    }
}
