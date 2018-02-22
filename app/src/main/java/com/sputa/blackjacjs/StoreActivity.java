package com.sputa.blackjacjs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.sputa.blackjacjs.util.IabHelper;
import com.sputa.blackjacjs.util.IabResult;
import com.sputa.blackjacjs.util.Inventory;
import com.sputa.blackjacjs.util.Purchase;

import org.json.JSONObject;

import ir.adad.client.Adad;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellRewardListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class StoreActivity extends AppCompatActivity {

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
    public static Context mContext;

    // Debug tag, for logging
    String  gift_coin_count="200";
    static final String TAG = "bazar";

    // SKUs for our products: the premium upgrade (non-consumable)
    static final String SKU_CONS1000 = "coin1000";
    static final String SKU_CONS2000 = "cons2000";
    static final String SKU_removeAds = "removeAds";
    static final String SKU_fullVersion = "FullVersion";

    // Does the user have the premium upgrade?
    boolean mIsPremium = false;
    boolean mIsFullPermisson = false;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 100;

    // The helper object
    IabHelper mHelper;
    int saw_ads=0;
    Inventory in;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            in=inventory;
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;

            }
            else {
                Log.d(TAG, "Query inventory was successful.");
                // does the user have the premium upgrade?
                mIsPremium = inventory.hasPurchase(SKU_removeAds);
                mIsFullPermisson = inventory.hasPurchase(SKU_fullVersion);



                //  Toast.makeText(getBaseContext(),String.valueOf(mIsPremium),Toast.LENGTH_SHORT).show();
                // update UI accordingly

                Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
                Log.d(TAG, "User is " + (mIsFullPermisson ? "fullVersion" : "NOT fullVersion"));

            }

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result+"----");
                Toast.makeText(getBaseContext(),"مشکل در خرید",Toast.LENGTH_SHORT).show();
                return;
            }
            else if (purchase.getSku().equals(SKU_CONS1000)) {
                Log.d(TAG, "Purchas successfull"+purchase.getSku());
                mHelper.consumeAsync(purchase,
                        mConsumeFinishedListener);
                purchase.getDeveloperPayload();
                Toast.makeText(getBaseContext(),"هوراااا... خرید با موفقیت انجام شد...1000 سکه به سکه های شما افزوده شد",Toast.LENGTH_LONG).show();
                set_coint_count(1000,"add");
                Boolean write_successful = false;
                File root=null;
                String
                        total="";
                boolean flag=false;
                try {
                    // <span id="IL_AD8" class="IL_AD">check for</span> SDcard
                    root = Environment.getExternalStorageDirectory();
                    //Log.i(TAG, "path.." + root.getAbsolutePath());

                    //check sdcard permission
                    if (root.canWrite()){
                        File fileDir = new File(root.getAbsolutePath());
                        fileDir.mkdirs();

                        File file= new File(fileDir, getResources().getString(R.string.file_name));
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
                String
                        coin_cnt=get_coint_count();
                TextView txt_coin_count = (TextView) findViewById(R.id.txt_coin_count);
                txt_coin_count.setText(coin_cnt);
                // give user access to premium content and update the UI
            }
            else if (purchase.getSku().equals(SKU_CONS2000)) {
                Log.d(TAG, "Purchas successfull"+purchase.getSku());
                mHelper.consumeAsync(purchase,
                        mConsumeFinishedListener);
                Toast.makeText(getBaseContext(),"هورااااا... خرید با موفقیت انجام شد...2000 سکه به سکه های شما افزوده شد",Toast.LENGTH_LONG).show();
                set_coint_count(2000,"add");
                Boolean write_successful = false;
                File root=null;
                String
                        total="";
                boolean flag=false;
                try {
                    // <span id="IL_AD8" class="IL_AD">check for</span> SDcard
                    root = Environment.getExternalStorageDirectory();
                    //Log.i(TAG, "path.." + root.getAbsolutePath());

                    //check sdcard permission
                    if (root.canWrite()){
                        File fileDir = new File(root.getAbsolutePath());
                        fileDir.mkdirs();

                        File file= new File(fileDir, getResources().getString(R.string.file_name));
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
                String
                        coin_cnt=get_coint_count();
                TextView txt_coin_count = (TextView) findViewById(R.id.txt_coin_count);
                txt_coin_count.setText(coin_cnt);
                // give user access to premium content and update the UI
            }
            else if (purchase.getSku().equals(SKU_removeAds)) {
                Log.d(TAG, "Purchas successfull"+purchase.getSku());
                // mHelper.consumeAsync(purchase,mConsumeFinishedListener);
                //mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                mIsPremium =true;
                Toast.makeText(getBaseContext(),"هورااااا... خرید با موفقیت انجام شد.. تبلیغات برای شما حذف شد",Toast.LENGTH_LONG).show();
                //set_coint_count(2000,"add");

                // give user access to premium content and update the UI
            }
            else if (purchase.getSku().equals(SKU_fullVersion)) {
                Log.d(TAG, "Purchas successfull"+purchase.getSku());
                // mHelper.consumeAsync(purchase,mConsumeFinishedListener);
                //mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                mIsFullPermisson=true;
                Toast.makeText(getBaseContext(),"هورااااا... خرید با موفقیت انجام شد.. نسخه شما به نسخه کامل ارتقا یافت - اکنون می توانید از صفحه اصلی برنامه تعداد سکه های خود را افزایش دهید",Toast.LENGTH_LONG).show();
                //set_coint_count(2000,"add");

                // give user access to premium content and update the UI
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Tapsell.initialize(this, "sjbjiqblnggmhkafbihhbdmjhrbmsgqqblapgkmobgagnqrdnrnprmnqcqbpmhsspsolcs");
        TapsellAdRequestOptions aa = new TapsellAdRequestOptions(2);
        Tapsell.requestAd(this, null, aa, new TapsellAdRequestListener() {
            @Override
            public void onError(String error) {
                Toast.makeText(StoreActivity.this, "بروز خطا", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdAvailable(TapsellAd ad) {
                isAvailable =1;
                ad1=ad;
            //    Toast.makeText(StoreActivity.this, "ویدیو آماده است", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNoAdAvailable() {
                Toast.makeText(StoreActivity.this, "تبلیغات در دسترس نیست", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoNetwork() {
                Toast.makeText(StoreActivity.this, "خطای شبکه", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExpiring(TapsellAd ad) {
                Toast.makeText(StoreActivity.this, "تبلیغات منقضی شده است", Toast.LENGTH_SHORT).show();
            }
        });







        try {
            String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC919lWH+Pk1XY8KOwEBXZnzSiUkXitreWZ1Kbuo4787M9dQlZ9wmSjpr1b1fbII8epkb0pvwmnjgnF+XBdf+bsv5eIKqR9TfYlnwgU5lcksQ7nrPxSoXwd2A6pnJhFEQzP7KRjLU9E33vemwLe/zssOXhvHrGgYOKfR6MVppyMTM+ArSKkv7EKhvwwYm/xweYF0jqyrP2yutyBFByg4FhAxFtVhbBAUbukrERz2p0CAwEAAQ==";
// You can find it in your Bazaar console, in the Dealers section.
// It is recommended to add more security than just pasting it in your source code;
            mHelper = new IabHelper(this, base64EncodedPublicKey);

            Log.d(TAG, "Starting setup.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    }
                    // Hooray, IAB is fully set up!
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
            });
        }
        catch (Exception e2){
            Toast.makeText(getBaseContext(),"خطا در ارتباط با پرداخت درون برنامه ای",Toast.LENGTH_SHORT).show();
        }
//

//
//        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + getResources().getString(R.string.table_name) + " (Id INT,meta_key VARCHAR,meta_value VARCHAR");
//
//
//        Cursor resultSet = mydatabase.rawQuery("Select * from "+getResources().getString(R.string.table_name), null);
//        //Log.d("majid",String.valueOf(resultSet.getCount())+"1");
//        int
//            v_id=0;
//        if (resultSet.getCount() > 0) {
//            resultSet.moveToLast();
//            String vc_id = resultSet.getString(0);
//            v_id = Integer.valueOf(vc_id);
//
//
//        }
//        v_id++;
//        SQLiteDatabase mydatabase = openOrCreateDatabase(getResources().getString(R.string.database_name), MODE_PRIVATE, null);
//        mydatabase.execSQL("INSERT INTO " + getResources().getString(R.string.table_name) + " VALUES(" + String.valueOf(v_id) + ",'coin_count','500');");
//
//         resultSet = mydatabase.rawQuery("Select * from "+getResources().getString(R.string.table_name) +" where meta_key='coin_count'", null);
//        //Log.d("majid",String.valueOf(resultSet.getCount())+"1");
//        if (resultSet.getCount() > 0) {
//            resultSet.moveToFirst();
//         //   et_guild_name.setText(resultSet.getString(1));
//        }
//

        String
                coint_count =get_coint_count();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;


        //     Toast.makeText(getBaseContext(), String.valueOf(screenWidth) + ":" + String.valueOf(screenHeight), Toast.LENGTH_SHORT).show();



///////////////////////////////////////////////////defines///////////////////////////////////////////////////////

        int btn_count = 4;
        int
                btn_height = (int)(screenHeight*0.14);
        int
                btn_width  = (int)(screenWidth*0.6);
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


        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////Buttons////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////



        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(btn_width, btn_height);








        final MediaPlayer mp = new MediaPlayer();
//        img.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
////                    RelativeLayout  layout = (RelativeLayout) findViewById(R.id.img_new_game);
////                    layout.setBackgroundResource(R.drawable.new_game_l);
//                    img.setBackgroundDrawable(getResources().getDrawable(R.drawable.coin1000_l));
//                    //Toast.makeText(getBaseContext(), String.valueOf(width) + ":" + String.valueOf(height), Toast.LENGTH_SHORT).show();
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
////                    RelativeLayout  layout = (RelativeLayout ) findViewById(R.id.img_new_game);
////                    layout.setBackgroundResource(R.drawable.new_game);
//                    img.setBackgroundDrawable(getResources().getDrawable(R.drawable.coin1000));
////                    MyAsyncTask mm1 =  new MyAsyncTask();
////
////
////                    mm1.url =  getResources().getString(R.string.site_url) +"runSP.php?param=action&mac_id="+m_wlanMacAdd+"&action_type=new_card";
////                    mm1.execute("");
////                    Intent i = new Intent(MainActivity.this, NewCardDetail.class);
////
//
//                        set_coint_count(1000,"add");
//                        String
//                                coin_cnt=get_coint_count();
//                    txt_coin_count.setText(coin_cnt);
//
//                    if(mp.isPlaying())
//                    {
//                        mp.stop();
//                    }
//
//                    try {
//                        mp.reset();
//                        AssetFileDescriptor afd;
//                        afd = getAssets().openFd("click.wav");
//                        mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
//                        mp.prepare();
//                        mp.start();
//                    } catch (IllegalStateException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                return true;
//            }
//        });


        final ImageView img2 = (ImageView) findViewById(R.id.fullversion);


        final ImageView img_removeAds = (ImageView) findViewById(R.id.removeAds);


//        final ImageView img_coin100_day_gift = (ImageView) findViewById(R.id.coin100_day_gift);
//        LinearLayout.LayoutParams lp_img_coin100_day_gift = new LinearLayout.LayoutParams(btn_width, btn_height);
//        img_coin100_day_gift.setLayoutParams(lp);
//        x = (int) (-btn_width);
//        y = (int) (distance_from_top) + (btn_height * 3);
//        lp_img_coin100_day_gift.setMargins(x, y, 0, 0);
//        img_coin100_day_gift.setLayoutParams(lp_img_coin100_day_gift);







//        final ImageView img_coin100_see_ads = (ImageView) findViewById(R.id.coin100_see_ads);
//        LinearLayout.LayoutParams lp_img_coin100_see_ads = new LinearLayout.LayoutParams(btn_width, btn_height);
//        img_coin100_see_ads.setLayoutParams(lp);
//        x = (int) (-btn_width);
//        y = (int) (distance_from_top) + (btn_height * 4);
//        lp_img_coin100_see_ads.setMargins(x, y, 0, 0);
//        img_coin100_see_ads.setLayoutParams(lp_img_coin100_see_ads);







//        img2.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
////                    RelativeLayout  layout = (RelativeLayout) findViewById(R.id.img_new_game);
////                    layout.setBackgroundResource(R.drawable.new_game_l);
//                    img2.setBackgroundDrawable(getResources().getDrawable(R.drawable.coin2000_l));
//                    //Toast.makeText(getBaseContext(), String.valueOf(width) + ":" + String.valueOf(height), Toast.LENGTH_SHORT).show();
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
////                    RelativeLayout  layout = (RelativeLayout ) findViewById(R.id.img_new_game);
////                    layout.setBackgroundResource(R.drawable.new_game);
//                    img2.setBackgroundDrawable(getResources().getDrawable(R.drawable.coin2000));
////                    MyAsyncTask mm1 =  new MyAsyncTask();
////
////
////                    mm1.url =  getResources().getString(R.string.site_url) +"runSP.php?param=action&mac_id="+m_wlanMacAdd+"&action_type=new_card";
////                    mm1.execute("");
////                    Intent i = new Intent(MainActivity.this, NewCardDetail.class);
////
//                    set_coint_count(2000,"add");
//                    String
//                            coin_cnt=get_coint_count();
//                    txt_coin_count.setText(coin_cnt);
//                    if (mp.isPlaying()) {
//                        mp.stop();
//                    }
//
//                    try {
//                        mp.reset();
//                        AssetFileDescriptor afd;
//                        afd = getAssets().openFd("click.wav");
//                        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//                        mp.prepare();
//                        mp.start();
//                    } catch (IllegalStateException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                return true;
//            }
//        });


        // mHelper.queryInventoryAsync(mGotInventoryListener);

    }
    public void clk_1000(View view)
    {
        try {
            if (mHelper != null) mHelper.flagEndAsync();
            mHelper.launchPurchaseFlow(this, SKU_CONS1000, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
        }catch (Exception e2){
            Toast.makeText(getBaseContext(),"خطا در ارتباط با پرداخت درون برنامه ای",Toast.LENGTH_SHORT).show();
        }


    }
    public void clk_2000(View view)
    {
        try {
            if (mHelper != null) mHelper.flagEndAsync();
            mHelper.launchPurchaseFlow(this, SKU_CONS2000, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
        }catch (Exception e2){
            Toast.makeText(getBaseContext(),"خطا در ارتباط با پرداخت درون برنامه ای",Toast.LENGTH_SHORT).show();
        }


    }
    public void clk_removeAds(View view)
    {

        if(!mIsPremium) {
            try {
                if (mHelper != null) mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(this, SKU_removeAds, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
            } catch (Exception e2) {
                Toast.makeText(getBaseContext(), "خطا در ارتباط با پرداخت درون برنامه ای", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getBaseContext(), R.string.remove_ads, Toast.LENGTH_SHORT).show();
        }


    }
    public void clk_coin100_daygift(View view)
    {
        SQLiteDatabase mydatabase = openOrCreateDatabase(getResources().getString(R.string.database_name), MODE_PRIVATE, null);
        Calendar c = Calendar.getInstance();
        int year = c.get((Calendar.YEAR));
        int month = c.get((Calendar.MONTH));
        int day = c.get((Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
//        String
//                date=String.valueOf(year)+":"+String.valueOf(month)+":"+String.valueOf(day);
        //Toast.makeText(getBaseContext(),date,Toast.LENGTH_SHORT).show();
        Cursor resultSet = mydatabase.rawQuery("Select * from "+getResources().getString(R.string.table_name) +" where meta_key='gift_date' and meta_value='"+date+"'", null);
        //Log.d("majid",String.valueOf(resultSet.getCount())+"1");
        if (resultSet.getCount() > 0) {
            resultSet.moveToFirst();
            Toast.makeText(getBaseContext(),"شما سهمیه امروز را استفاده کرده اید",Toast.LENGTH_SHORT).show();
            //   et_guild_name.setText(resultSet.getString(1));
        }
        else
        {
            resultSet = mydatabase.rawQuery("Select * from "+getResources().getString(R.string.table_name) +" where meta_key='gift_date' and meta_value>'"+date+"'", null);
            //Log.d("majid",String.valueOf(resultSet.getCount())+"1");
            if (resultSet.getCount() == 0) {
                Cursor resultSet1 = mydatabase.rawQuery("Select * from " + getResources().getString(R.string.table_name), null);
                //Log.d("majid",String.valueOf(resultSet.getCount())+"1");
                int
                        v_id = 0;
                if (resultSet1.getCount() > 0) {
                    resultSet1.moveToLast();
                    String vc_id = resultSet1.getString(0);
                    v_id = Integer.valueOf(vc_id);
                }
                v_id++;
                mydatabase.execSQL("INSERT INTO " + getResources().getString(R.string.table_name) + " VALUES(" + String.valueOf(v_id) + ",'gift_date','" + date + "')");
                set_coint_count(Integer.valueOf(gift_coin_count), "add");
                TextView txt_coint_count = (TextView) findViewById(R.id.txt_coin_count);
                txt_coint_count.setText(get_coint_count());
                //   Toast.makeText(getBaseContext(),"",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("تعداد " + gift_coin_count + " سکه به سکه های شما افزوده شد")
                        .setCancelable(false)
                        .setPositiveButton("ممنونم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else
            {
                Toast.makeText(getBaseContext(),"تاریخ دستگاه مشکل دارد",Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void clk_coin100_see_ads(View view)
    {


    }

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if (result.isSuccess()) {
                        // provision the in-app purchase to the user
                        // (for example, credit 50 gold coins to player's character)
                    }
                    else {
                        // handle error
                    }
                }
            };



    public void onBackPressed() {

        //Intent i=new Intent(StoreActivity.this,MainActivity.class);
        finish();
        //startActivity(i);

    }

    public void clk_full_version(View view) {

        if(!mIsFullPermisson) {
            try {
                if (mHelper != null) mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(this, SKU_fullVersion, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
            } catch (Exception e2) {
                Toast.makeText(getBaseContext(), "خطا در ارتباط با پرداخت درون برنامه ای", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getBaseContext(), "شما قبلا نسخه کامل بازی را خریداری کرده اید - برای شما تبلیغات حذف شده است و از صفحه اصلی برنامه می توانید تعداد سکه های خود را افزایش دهید", Toast.LENGTH_SHORT).show();
        }
    }
    private TapsellAd ad1;
    public int isAvailable =0;
    public void clk_See_ads(View view) {
        TapsellAdRequestOptions aa = new TapsellAdRequestOptions(2);
        Tapsell.requestAd(this, null, aa, new TapsellAdRequestListener() {
            @Override
            public void onError(String error) {
                Toast.makeText(StoreActivity.this, "بروز خطا", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdAvailable(TapsellAd ad) {
                isAvailable =1;
                ad1=ad;
                Toast.makeText(StoreActivity.this, "ویدیو آماده است", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNoAdAvailable() {
                Toast.makeText(StoreActivity.this, "تبلیغات در دسترس نیست", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoNetwork() {
                Toast.makeText(StoreActivity.this, "خطای شبکه", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExpiring(TapsellAd ad) {
                Toast.makeText(StoreActivity.this, "تبلیغات منقضی شده است", Toast.LENGTH_SHORT).show();
            }
        });
        TapsellShowOptions ss = new TapsellShowOptions();
        ss.setBackDisabled(false);
        ss.setRotationMode(TapsellShowOptions.ROTATION_LOCKED_LANDSCAPE);
        ss.setShowDialog(true);
        ad1.show(StoreActivity.this, ss, new TapsellAdShowListener() {
            @Override
            public void onOpened(TapsellAd ad) {
          //      Toast.makeText(StoreActivity.this, "opened", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onClosed(TapsellAd ad) {
         //       Toast.makeText(StoreActivity.this, "closed", Toast.LENGTH_SHORT).show();

            }
        });
        Tapsell.setRewardListener(new TapsellRewardListener() {
            @Override
            public void onAdShowFinished(TapsellAd ad, boolean completed) {
                // store user reward if ad.isRewardedAd() and completed is true
                if(completed) {
                    Toast.makeText(StoreActivity.this, "ممنون که ویدیو را نگاه کردید", Toast.LENGTH_SHORT).show();


                    set_coint_count(50, "add");
                    TextView txt_coint_count = (TextView) findViewById(R.id.txt_coin_count);
                    txt_coint_count.setText(get_coint_count());
                    //   Toast.makeText(getBaseContext(),"",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                    builder.setMessage("تعداد " + "50" + " سکه به سکه های شما افزوده شد")
                            .setCancelable(false)
                            .setPositiveButton("ممنونم", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });
    }
}
