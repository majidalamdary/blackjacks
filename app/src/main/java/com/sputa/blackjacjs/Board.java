package com.sputa.blackjacjs;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Board extends AppCompatActivity {


    int[][] cards = new int[5][12];
    int[][] cards_used = new int[5][12];
    int     card_value =0;
    int bet = 0;
    int screenWidth = 0;
    int screenHeight = 0;

    LinearLayout lay_player_board ;

    LinearLayout.LayoutParams lp_lay_player_board ;
    ImageView[] img_player_card ;

    LinearLayout lay_opponent_board ;

    LinearLayout.LayoutParams lp_lay_opponent_board ;
    ImageView[] img_opponent_card ;


    int player_card_index = 5;
    int opponent_card_index = 6;
    String
            play_turn = "player";
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
    private int get_random_card(int typ)
    {
        boolean
                flag=true;
        int
                nxt_num=0,card_type=0;
        while(flag)
        {
            Random rnd=new Random();
            nxt_num=rnd.nextInt(10)+2;
            if(nxt_num != 5)
                flag = false;

            card_type = rnd.nextInt(4)+1;
            if(cards_used[card_type][nxt_num] > 0)
                flag = true;

        }
        cards_used[card_type][nxt_num] = typ;
        card_value = nxt_num;
        return cards[card_type][nxt_num];

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("bet");
            bet = (Integer.valueOf(value))*2;
        }

        cards[1][2] = R.drawable.kheshtsarbaz;
        cards[1][3] = R.drawable.kheshtbibi;
        cards[1][4] = R.drawable.kheshtshah;
        cards[1][6] = R.drawable.khesht6;
        cards[1][7] = R.drawable.khesht7;
        cards[1][8] = R.drawable.khesht8;
        cards[1][9] = R.drawable.khesht9;
        cards[1][10] = R.drawable.khesht10;
        cards[1][11] = R.drawable.khesht1;


        cards[2][2] = R.drawable.piksarbaz;
        cards[2][3] = R.drawable.pikbibi;
        cards[2][4] = R.drawable.pikshah;
        cards[2][6] = R.drawable.pik6;
        cards[2][7] = R.drawable.pik7;
        cards[2][8] = R.drawable.pik8;
        cards[2][9] = R.drawable.pik9;
        cards[2][10] = R.drawable.pik10;
        cards[2][11] = R.drawable.pik1;

        cards[3][2] = R.drawable.khajsarbaz;
        cards[3][3] = R.drawable.khajbibi;
        cards[3][4] = R.drawable.khajshah;
        cards[3][6] = R.drawable.khaj6;
        cards[3][7] = R.drawable.khaj7;
        cards[3][8] = R.drawable.khaj8;
        cards[3][9] = R.drawable.khaj9;
        cards[3][10] = R.drawable.khaj10;
        cards[3][11] = R.drawable.khaj1;

        cards[4][2] = R.drawable.delsarbaz;
        cards[4][3] = R.drawable.delbibi;
        cards[4][4] = R.drawable.delshah;
        cards[4][6] = R.drawable.del6;
        cards[4][7] = R.drawable.del7;
        cards[4][8] = R.drawable.del8;
        cards[4][9] = R.drawable.del9;
        cards[4][10] = R.drawable.del10;
        cards[4][11] = R.drawable.del1;

        for(int i=1;i<=4;i++)
            for(int j=1;j<=11;j++)
                cards_used[i][j] = 0;



        String
                coint_count =get_coint_count();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;


///////////////////////////////////////////////////defines///////////////////////////////////////////////////////

        int btn_count = 4;
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


        txt_coin_count.setText(coint_count);

        final  TextView txt_member_name = (TextView) findViewById(R.id.txt_member_name);
        txt_member_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screenWidth*0.08));

        txt_member_name.setText("مهمان");


        Rect bounds = new Rect();
        Paint textPaint = txt_coin_count.getPaint();
        String
                ss=txt_coin_count.getText().toString();

        textPaint.getTextBounds(ss, 0, ss.length(), bounds);
        int height_txt_coin_count = bounds.height();
        int width_txt_coin_count = bounds.width();



        bounds = new Rect();
        textPaint = txt_member_name.getPaint();
        ss=txt_member_name.getText().toString();

        textPaint.getTextBounds(ss, 0, ss.length(), bounds);
        int height_txt_member_name = bounds.height();
        int width_txt_member_name  = bounds.width();





        int
                img_new_member_width = (int)(screenWidth*0.1);
        final ImageView img_new_member = (ImageView) findViewById(R.id.img_new_member);
        LinearLayout.LayoutParams lp_new_member = new LinearLayout.LayoutParams((int)img_new_member_width, (int)img_new_member_width);
        img_new_member.setLayoutParams(lp_new_member);
        x = screenWidth - img_new_member_width - width_txt_coin_count-img_coin_width-((int)(img_new_member_width*0.3));
        y = 1;

        //Toast.makeText(getBaseContext(), String.valueOf(x) + ":" + String.valueOf(y), Toast.LENGTH_SHORT).show();

        lp_new_member.setMargins(x, y, 0, 0);
        img_new_member.setLayoutParams(lp_new_member);





        LinearLayout.LayoutParams lp_member_name = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        x =   (img_new_member_width + ((int)(img_new_member_width*0.3)) + width_txt_member_name)*-1;
        y = 1;

        //Toast.makeText(getBaseContext(), String.valueOf(x) + ":" + String.valueOf(y), Toast.LENGTH_SHORT).show();

        lp_member_name.setMargins(x, y, 0, 0);
        txt_member_name.setLayoutParams(lp_member_name);


        LinearLayout lay_top = (LinearLayout) findViewById(R.id.lay_top);

        LinearLayout.LayoutParams lp_lay_top = new LinearLayout.LayoutParams(screenWidth, (int)img_coin_width);
        lay_top.setLayoutParams(lp_lay_top);





        final RelativeLayout lay_ad = (RelativeLayout) findViewById(R.id.lay_ad);
        LinearLayout.LayoutParams lp_lay_ad = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(screenHeight*0.09));
        lay_ad.setLayoutParams(lp_lay_ad);



        ///////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////opponent name///////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////

        final  TextView txt_opponent = (TextView) findViewById(R.id.txt_opponent);
        txt_opponent.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screenWidth*0.06));


        RelativeLayout lay_opponent_text = (RelativeLayout) findViewById(R.id.lay_opponent_text);

        LinearLayout.LayoutParams lp_lay_opponent_text = new LinearLayout.LayoutParams(screenWidth, (int)(screenHeight*0.08));
        lay_opponent_text.setLayoutParams(lp_lay_opponent_text);

        ///////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////opponent Board///////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////

        lay_opponent_board = (LinearLayout) findViewById(R.id.lay_opponent_board);

        lp_lay_opponent_board = new LinearLayout.LayoutParams(screenWidth, (int)(screenHeight*0.23));

        lay_opponent_board.setLayoutParams(lp_lay_opponent_board);



        // Now the layout parameters, these are a little tricky at first
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(screenWidth*0.23),(int)(screenHeight*0.18));

        img_opponent_card = new ImageView[11];
        img_opponent_card[1] = new ImageView(this);
        img_opponent_card[1].setScaleType(ImageView.ScaleType.MATRIX);
        // img_player_card[1].setBackgroundDrawable(getResources().getDrawable(get_random_card(1)));



        // Let's get the root layout and add our ImageView
        //LinearLayout layout = (LinearLayout) findViewById(R.id.lay_opponent_board);
        params2.setMargins(0, ((int) (screenWidth * 0.08)), 0, 0);


        lay_opponent_board.addView(img_opponent_card[1], 0, params2);

        for(int i=2;i<=6;i++)
        {
            img_opponent_card[i] = new ImageView(this);
            img_opponent_card[i].setScaleType(ImageView.ScaleType.MATRIX);
            if(i==6)
                img_opponent_card[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.card_back));



            // Let's get the root layout and add our ImageView
            //LinearLayout layout = (LinearLayout) findViewById(R.id.lay_opponent_board);
            params2.setMargins(((int)(screenWidth*0.08)), ((int)(screenWidth*0.08)), 0, 0);


            lay_opponent_board.addView(img_opponent_card[i], 0, params2);

        }
        img_opponent_card[7] = new ImageView(this);
        img_opponent_card[7].setScaleType(ImageView.ScaleType.MATRIX);
        //  img_player_card[3].setBackgroundDrawable(getResources().getDrawable(get_random_card(1)));



        // Let's get the root layout and add our ImageView
        //LinearLayout layout = (LinearLayout) findViewById(R.id.lay_opponent_board);
        params2.setMargins(((int)(screenWidth*0.06))*-1, ((int)(screenWidth*0.08)), 0, 0);


        lay_opponent_board.addView(img_opponent_card[7], 0, params2);

        ///////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////Player Board///////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////


        lay_player_board = (LinearLayout) findViewById(R.id.lay_player_board);

        lp_lay_player_board = new LinearLayout.LayoutParams(screenWidth, (int)(screenHeight*0.23));

        lay_player_board.setLayoutParams(lp_lay_player_board);



        // Now the layout parameters, these are a little tricky at first
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams((int)(screenWidth*0.23),(int)(screenHeight*0.18));

        img_player_card = new ImageView[11];
        img_player_card[1] = new ImageView(this);
        img_player_card[1].setScaleType(ImageView.ScaleType.MATRIX);
        // img_player_card[1].setBackgroundDrawable(getResources().getDrawable(get_random_card(1)));



        // Let's get the root layout and add our ImageView
        //LinearLayout layout = (LinearLayout) findViewById(R.id.lay_opponent_board);
        params1.setMargins(0, ((int) (screenWidth * 0.08)), 0, 0);


        lay_player_board.addView(img_player_card[1], 0, params1);

        for(int i=2;i<=6;i++)
        {
            img_player_card[i] = new ImageView(this);
            img_player_card[i].setScaleType(ImageView.ScaleType.MATRIX);
            if(i==6)
                img_player_card[i].setBackgroundDrawable(getResources().getDrawable(get_random_card(1)));



            // Let's get the root layout and add our ImageView
            //LinearLayout layout = (LinearLayout) findViewById(R.id.lay_opponent_board);
            params1.setMargins(((int)(screenWidth*0.08)), ((int)(screenWidth*0.08)), 0, 0);


            lay_player_board.addView(img_player_card[i], 0, params1);

        }
        img_player_card[7] = new ImageView(this);
        img_player_card[7].setScaleType(ImageView.ScaleType.MATRIX);
        //  img_player_card[3].setBackgroundDrawable(getResources().getDrawable(get_random_card(1)));



        // Let's get the root layout and add our ImageView
        //LinearLayout layout = (LinearLayout) findViewById(R.id.lay_opponent_board);
        params1.setMargins(((int)(screenWidth*0.06))*-1, ((int)(screenWidth*0.08)), 0, 0);


        lay_player_board.addView(img_player_card[7], 0, params1);



        ///////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////opponent name///////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////

        final  TextView txt_player = (TextView) findViewById(R.id.txt_player);
        txt_player.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (screenWidth * 0.06));


        RelativeLayout lay_txt_player = (RelativeLayout) findViewById(R.id.lay_player_text);

        LinearLayout.LayoutParams lp_lay_txt_player = new LinearLayout.LayoutParams(screenWidth, (int)(screenHeight*0.08));
        lay_txt_player.setLayoutParams(lp_lay_txt_player);

        final  TextView lbl_card_sum = (TextView) findViewById(R.id.lbl_card_sum);
        lbl_card_sum.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (screenWidth * 0.06));

        final  TextView txt_card_sum = (TextView) findViewById(R.id.txt_card_sum);
        txt_card_sum.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (screenWidth * 0.06));










        final ImageView img_enough_card = (ImageView) findViewById(R.id.img_enough_card);
        ConstraintLayout.LayoutParams lp_img_enough_card = new ConstraintLayout.LayoutParams((int)(screenWidth*0.3), (int)(screenWidth*0.3));
        lp_img_enough_card = (ConstraintLayout.LayoutParams) img_enough_card.getLayoutParams();
        lp_img_enough_card.width = (int)(screenWidth*0.3);
        lp_img_enough_card.height = (int)(screenWidth*0.26);

        img_enough_card.setLayoutParams(lp_img_enough_card);


        final ImageView img_get_card = (ImageView) findViewById(R.id.img_get_card);
        ConstraintLayout.LayoutParams lp_img_get_card = new ConstraintLayout.LayoutParams((int)(screenWidth*0.30), (int)(screenWidth*0.30));
        lp_img_get_card = (ConstraintLayout.LayoutParams) img_get_card.getLayoutParams();
        lp_img_get_card.width = (int)(screenWidth*0.3);
        lp_img_get_card.height = (int)(screenWidth*0.26);

        img_get_card.setLayoutParams(lp_img_get_card);



        txt_card_sum.setText(String.valueOf(palyer_score(1)));


        SharedPreferences prefs = getSharedPreferences("m1", Context.MODE_PRIVATE);
        String restoredText = prefs.getString("removeAds", "");
        if (restoredText.equals("true"))
        {
            RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.lay_ad);
            mainLayout.setVisibility(LinearLayout.GONE);
        }


    }


    public void clk_get_card(View view) {

//        lay_player_board = (LinearLayout) findViewById(R.id.lay_player_board);
//
//        lp_lay_player_board = new LinearLayout.LayoutParams(screenWidth, (int)(screenHeight*0.2));
//        lay_player_board.setLayoutParams(lp_lay_player_board);
//        lay_player_board = (LinearLayout) findViewById(R.id.lay_player_board);
//
//        lp_lay_player_board = new LinearLayout.LayoutParams(screenWidth, (int)(screenHeight*0.2));
//        lay_player_board.setLayoutParams(lp_lay_player_board);
//        ImageView image1 = new ImageView(this);
//
//        // Now the layout parameters, these are a little tricky at first
//        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams((int)(screenWidth*0.2),(int)(screenHeight*0.15));
//
//
//
//        image1.setScaleType(ImageView.ScaleType.MATRIX);
//        image1.setBackgroundDrawable(getResources().getDrawable(R.drawable.pik10));
//
//
//
//        // Let's get the root layout and add our ImageView
//        //LinearLayout layout = (LinearLayout) findViewById(R.id.lay_opponent_board);
//        params1.setMargins(((int)(screenWidth*0.15)), ((int)(screenWidth*0.08)), 0, 0);
//        lay_player_board.addView(image1, 0, params1);
        if(play_turn.equals("player")) {
            if (player_card_index > 0) {

                img_player_card[player_card_index].setBackgroundDrawable(getResources().getDrawable(get_random_card(1)));
                player_card_index--;
                TextView txt_card_sum = (TextView) findViewById(R.id.txt_card_sum);
                txt_card_sum.setText(String.valueOf(palyer_score(1)));
                //Toast.makeText(getBaseContext(), String.valueOf(), Toast.LENGTH_SHORT).show();
                if (burned_palyer(1) == 2)
                    won();

                if (burned_palyer(1) == 1)
                    lose();

            } else {
                Toast.makeText(getBaseContext(), "دیگر نمی توانید کارت  دریافت کنید", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void won()
    {
        play_turn = "none";

        set_coint_count(bet, "add");

        TextView txt_coin = (TextView) findViewById(R.id.txt_coin_count);
        txt_coin.setText(get_coint_count());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("تبریک! شما برنده شدید")
                .setCancelable(false)
                .setPositiveButton("اوکی", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        Intent i=new Intent(Board.this,after_game_select.class);
//                        i.putExtra("bet",String.valueOf(bet));
//                        finish();
//                        startActivity(i);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void lose()
    {
        play_turn = "none";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("اووپس... متاسفانه شما باختید")
                .setCancelable(false)
                .setPositiveButton("اوکی", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        Intent i=new Intent(Board.this,after_game_select.class);
//                        i.putExtra("bet",String.valueOf(bet));
//                        finish();
//                        startActivity(i);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


    public void clk_enough_card(View view) {

//        lay_player_board = (LinearLayout) findViewById(R.id.lay_player_board);
//
//        lp_lay_player_board = new LinearLayout.LayoutParams(screenWidth, (int)(screenHeight*0.2));
//        lay_player_board.setLayoutParams(lp_lay_player_board);
//        lay_player_board = (LinearLayout) findViewById(R.id.lay_player_board);
//
//        lp_lay_player_board = new LinearLayout.LayoutParams(screenWidth, (int)(screenHeight*0.2));
//        lay_player_board.setLayoutParams(lp_lay_player_board);
//        ImageView image1 = new ImageView(this);
//
//        // Now the layout parameters, these are a little tricky at first
//        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams((int)(screenWidth*0.2),(int)(screenHeight*0.15));
//
//
//
//        image1.setScaleType(ImageView.ScaleType.MATRIX);
//        image1.setBackgroundDrawable(getResources().getDrawable(R.drawable.pik10));
//
//
//
//        // Let's get the root layout and add our ImageView
//        //LinearLayout layout = (LinearLayout) findViewById(R.id.lay_opponent_board);
//        params1.setMargins(((int)(screenWidth*0.15)), ((int)(screenWidth*0.08)), 0, 0);
//        lay_player_board.addView(image1, 0, params1);
//        if(player_card_index>0) {
//
//            img_player_card[player_card_index].setBackgroundDrawable(getResources().getDrawable(get_random_card(1)));
//            player_card_index--;
//            //   Toast.makeText(getBaseContext(), "majid", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(getBaseContext(), "دیگر نمی توانید کارت  دریافت کنید", Toast.LENGTH_SHORT).show();
//        }
        if(play_turn.equals("player")) {
            play_turn = "opponent";

            Boolean
                    flag = true;
            while (flag) {
                img_opponent_card[opponent_card_index].setBackgroundDrawable(getResources().getDrawable(get_random_card(2)));
                opponent_card_index--;
                if (opponent_card_index == 0) {
                    flag = false;
                }
                int
                        score = palyer_score(2);
                if (burned_palyer(2) == 0) {
                    if (score > 12) {
                        int nxt_num = new Random().nextInt(7) + 1;
                        int diff = 21 - score;
                        if (nxt_num <= diff) {

                        }
                        else
                        {
                            flag = false;
                            if (palyer_score(1) > score)
                                won();
                            else
                                lose();
                        }
                    }
                } else {
                    flag = false;
                    if (burned_palyer(2) == 2)
                        lose();
                    else
                        won();
                }

            }
        }

    }
    private int burned_palyer(int typ) {

        int score =0;
        for(int i=1;i<=4;i++)
            for(int j=1;j<=11;j++)
                if(cards_used[i][j] ==typ)
                {
                    score+=j;
                }
        if(score>21)
            return 1;
        if(score<21)
            return 0;
        if(score==21)
            return 2;
        return 0;

    }
    private int palyer_score(int typ) {

        int score =0;
        for(int i=1;i<=4;i++)
            for(int j=1;j<=11;j++)
                if(cards_used[i][j] ==typ)
                {
                    score+=j;
                }

        return score;

    }

    @Override
    public void onBackPressed() {
        //  Toast.makeText(getBaseContext(),"back",Toast.LENGTH_SHORT).show();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        Intent i=new Intent(Board.this,MainActivity.class);
                        finish();
                        startActivity(i);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("آیا می خواهید به منوی اصلی برگردید؟").setPositiveButton("بله", dialogClickListener)
                .setNegativeButton("خیر", dialogClickListener).show();
    }

}
