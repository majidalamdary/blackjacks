package com.sputa.blackjacjs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class after_game_select extends AppCompatActivity {

    functions fun;
    String
            font_name = "";
    Typeface tf;
    String
            bet ="";
    public String get_coint_count()
    {
        SQLiteDatabase mydatabase = openOrCreateDatabase(getResources().getString(R.string.database_name), MODE_PRIVATE, null);

        Cursor resultSet = mydatabase.rawQuery("Select * from "+getResources().getString(R.string.table_name) +" where meta_key='"+getResources().getString(R.string.coin_count)+"'", null);
        //Log.d("majid",String.valueOf(resultSet.getCount())+"1");
        if (resultSet.getCount() > 0) {
            resultSet.moveToFirst();
            //   et_guild_name.setText(resultSet.getString(1));
        }
        return resultSet.getString(2);
    }
    public void set_coint_count(int coint_cnt,String typ)
    {
        SQLiteDatabase mydatabase = openOrCreateDatabase(getResources().getString(R.string.database_name), MODE_PRIVATE, null);

        Cursor resultSet = mydatabase.rawQuery("Select * from "+getResources().getString(R.string.table_name) +" where meta_key='"+getResources().getString(R.string.coin_count)+"'", null);
        //Log.d("majid",String.valueOf(resultSet.getCount())+"1");
        if (resultSet.getCount() > 0) {
            resultSet.moveToFirst();
            //   et_guild_name.setText(resultSet.getString(1));
        }
        int
                new_cnt = Integer.parseInt(resultSet.getString(2));
        if(typ.equals("add"))
            new_cnt+=coint_cnt;
        else
            new_cnt-=coint_cnt;
        mydatabase.execSQL("update " + getResources().getString(R.string.table_name) + " set meta_value='"+String.valueOf(new_cnt)+"' where meta_key='"+getResources().getString(R.string.coin_count)+"'");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_game_select);


        fun = new functions();
        font_name = fun.font_name;
        tf = Typeface.createFromAsset(getAssets(),font_name );
        String
                coint_count =get_coint_count();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("bet");
            bet = value;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;


        //    Toast.makeText(getBaseContext(), String.valueOf(screenWidth) + ":" + String.valueOf(screenHeight), Toast.LENGTH_SHORT).show();



///////////////////////////////////////////////////defines///////////////////////////////////////////////////////

        int btn_count = 3;
        int
                btn_height = (int)(screenHeight*0.14);
        int
                btn_width  = (int)(screenWidth*0.56);
        int distance_from_top = (int)((screenHeight-(btn_height*btn_count))/2);
        int x=0,y=0;



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////Top Section/////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////


        int
                img_coin_width = (int)(screenWidth*0.12);
        final ImageView img_coin = (ImageView) findViewById(R.id.img_coin);
        LinearLayout.LayoutParams lp_coin = new LinearLayout.LayoutParams((int)img_coin_width, (int)img_coin_width);
        img_coin.setLayoutParams(lp_coin);
        x = 1;
        y = 1;

        //Toast.makeText(getBaseContext(), String.valueOf(x) + ":" + String.valueOf(y), Toast.LENGTH_SHORT).show();

        lp_coin.setMargins(x, y, 0, 0);
        img_coin.setLayoutParams(lp_coin);

        final TextView txt_coin_count = (TextView) findViewById(R.id.txt_coin_count);
        txt_coin_count.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (screenWidth * 0.08));

        txt_coin_count.setTypeface(tf);
        txt_coin_count.setText(coint_count);




        Rect bounds = new Rect();
        Paint textPaint = txt_coin_count.getPaint();
        String
                ss=txt_coin_count.getText().toString();

        textPaint.getTextBounds(ss, 0, ss.length(), bounds);
        int height_txt_coin_count = bounds.height();
        int width_txt_coin_count = bounds.width();





        textPaint.getTextBounds(ss, 0, ss.length(), bounds);
        int height_txt_member_name = bounds.height();
        int width_txt_member_name  = bounds.width();










        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////Buttons////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////


        //Toast.makeText(getBaseContext(), String.valueOf(x) + ":" + String.valueOf(y), Toast.LENGTH_SHORT).show();









    }
    public void clk_repeat_game(View view) {

        if(Integer.valueOf(get_coint_count())>=(Integer.valueOf(bet)/2)) {
            Intent i = new Intent(after_game_select.this, Board.class);
            i.putExtra("bet" ,String.valueOf((Integer.valueOf(bet) / 2)));

            set_coint_count((Integer.valueOf(bet) / 2), "sub");
            Boolean write_successful = false;
            File root = null;
            String
                    total = "";
            boolean flag = false;
            try {
                // <span id="IL_AD8" class="IL_AD">check for</span> SDcard
                root = Environment.getExternalStorageDirectory();
                //Log.i(TAG, "path.." + root.getAbsolutePath());

                //check sdcard permission
                if (root.canWrite()) {
                    File fileDir = new File(root.getAbsolutePath());
                    fileDir.mkdirs();

                    File file = new File(fileDir, getResources().getString(R.string.file_name));
                    FileWriter filewriter = new FileWriter(file);
                    BufferedWriter out = new BufferedWriter(filewriter);
                    out.write(get_coint_count());
                    out.close();
                    write_successful = true;
                }
            } catch (IOException e) {
                Log.e("ERROR:---", "Could not write file to SDCard" + e.getMessage());
                write_successful = false;
            }
            finish();
            startActivity(i);
        }
        else
        {
            Toast.makeText(getBaseContext(),  R.string.not_enough_coin, Toast.LENGTH_SHORT).show();
        }
    }
    public void clk_new_game(View view) {
        Intent i = new Intent(after_game_select.this, Game_select.class);
        finish();
        startActivity(i);
    }
    public void clk_main_menu(View view) {
        Intent i = new Intent(after_game_select.this, MainActivity.class);
        finish();
        startActivity(i);
    }

}
