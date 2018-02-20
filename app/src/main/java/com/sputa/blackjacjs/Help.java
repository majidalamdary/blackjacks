package com.sputa.blackjacjs;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Help extends AppCompatActivity {
    functions fun;
    String
            font_name = "";
    Typeface tf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        fun = new functions();
        font_name = fun.font_name;
        tf = Typeface.createFromAsset(getAssets(),font_name );

        TextView txt1 = findViewById(R.id.textView);
        TextView txt2 = findViewById(R.id.textView2);
        txt1.setTypeface(tf);
        txt2.setTypeface(tf);


    }
}
