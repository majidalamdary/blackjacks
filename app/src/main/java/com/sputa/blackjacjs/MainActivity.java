package com.sputa.blackjacjs;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

//import ir.adad.client.Adad;
//
import com.google.firebase.messaging.FirebaseMessaging;
import com.sputa.blackjacjs.app.Config;
import com.sputa.blackjacjs.util.IabHelper;
import com.sputa.blackjacjs.util.IabResult;
import com.sputa.blackjacjs.util.Inventory;
import com.sputa.blackjacjs.util.NotificationUtils;

import ir.adad.client.Adad;


public class MainActivity extends AppCompatActivity {

    functions fun;
    String
            font_name = "";
    Typeface tf;

    String m_wlanMacAdd ="";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public MyAsyncTask mm;

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        model += ("---"+sdkVersion + " (" + release +")");
        if (model.startsWith(manufacturer)) {
            return (model);
        } else {
            return (manufacturer) + " " + model;
        }
    }
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
    static final String TAG = "bazar";

    // SKUs for our products: the premium upgrade (non-consumable)
    static final String SKU_CONS1000 = "cons1000";
    static final String SKU_CONS2000 = "cons2000";
    static final String SKU_removeAds = "removeAds";
    static final String SKU_fullVersion = "FullVersion";


    // Does the user have the premium upgrade?
    boolean mIsPremium = false;
    boolean mIsFullVersion = false;


    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 100;

    // The helper object
    IabHelper mHelper;

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
                mIsFullVersion= inventory.hasPurchase(SKU_fullVersion);

              //  Toast.makeText(MainActivity.this, String.valueOf(mIsPremium), Toast.LENGTH_SHORT).show();

                //  Toast.makeText(getBaseContext(), String.valueOf(mIsPremium), Toast.LENGTH_SHORT).show();
                // update UI accordingly
                if(mIsPremium) {
                    RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.lay_ad);
                    mainLayout.setVisibility(LinearLayout.GONE);

                    SharedPreferences prefs = getSharedPreferences("m1", Context.MODE_PRIVATE);
                    prefs.edit().putString("removeAds", "true").apply();


                }
                else
                {
                    SharedPreferences prefs = getSharedPreferences("m1", Context.MODE_PRIVATE);
                    prefs.edit().putString("removeAds", "false").apply();
                    RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.lay_ad);
                    mainLayout.setVisibility(LinearLayout.VISIBLE);
                }
                if(mIsFullVersion) {
                    ImageView img1=findViewById(R.id.img_plus);
                    img1.setVisibility(View.VISIBLE);
                    RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.lay_ad);
                    mainLayout.setVisibility(LinearLayout.GONE);
                    SharedPreferences prefs = getSharedPreferences("m1", Context.MODE_PRIVATE);
                    prefs.edit().putString("fullVersion", "true").apply();


                }
                else
                {
                    SharedPreferences prefs = getSharedPreferences("m1", Context.MODE_PRIVATE);
                    prefs.edit().putString("fullVersion", "false").apply();
                    ImageView img1=findViewById(R.id.img_plus);
                    img1.setVisibility(View.INVISIBLE);

                }

                Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            }

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
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
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Adad.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        fun = new functions();
        font_name = fun.font_name;
        tf = Typeface.createFromAsset(getAssets(),font_name );
        // if (MySigCheck(getApplicationContext()) != "18976453"){
        //      Toast.makeText(getApplicationContext(),MySigCheck(getApplicationContext()), Toast.LENGTH_SHORT).show();
        //
        // }


//        try {
//            String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC919lWH+Pk1XY8KOwEBXZnzSiUkXitreWZ1Kbuo4787M9dQlZ9wmSjpr1b1fbII8epkb0pvwmnjgnF+XBdf+bsv5eIKqR9TfYlnwgU5lcksQ7nrPxSoXwd2A6pnJhFEQzP7KRjLU9E33vemwLe/zssOXhvHrGgYOKfR6MVppyMTM+ArSKkv7EKhvwwYm/xweYF0jqyrP2yutyBFByg4FhAxFtVhbBAUbukrERz2p0CAwEAAQ==";
//// You can find it in your Bazaar console, in the Dealers section.
//// It is recommended to add more security than just pasting it in your source code;
//            mHelper = new IabHelper(this, base64EncodedPublicKey);
//
//            Log.d(TAG, "Starting setup.");
//            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//                public void onIabSetupFinished(IabResult result) {
//                    Log.d(TAG, "Setup finished.");
//
//                    if (!result.isSuccess()) {
//                        // Oh noes, there was a problem.
//                        Log.d(TAG, "Problem setting up In-app Billing: " + result);
//                    }
//                    // Hooray, IAB is fully set up!
//                    mHelper.queryInventoryAsync(mGotInventoryListener);
//                }
//            });
//        }
//        catch (Exception e2){
//            //   Toast.makeText(getBaseContext(),"خطا در ارتباط با پرداخت درون برنامه ای",Toast.LENGTH_SHORT).show();
//        }


        SQLiteDatabase mydatabase = openOrCreateDatabase(getResources().getString(R.string.database_name), MODE_PRIVATE, null);

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + getResources().getString(R.string.table_name) + " (Id INT,meta_key VARCHAR,meta_value VARCHAR)");


        Cursor resultSet = mydatabase.rawQuery("Select * from "+getResources().getString(R.string.table_name), null);
        //Log.d("majid",String.valueOf(resultSet.getCount())+"1");
        int
                v_id=0;
        if (resultSet.getCount() > 0) {
            resultSet.moveToLast();
            String vc_id = resultSet.getString(0);
            v_id = Integer.valueOf(vc_id);
        }
        v_id++;
//        mydatabase.execSQL("delete from " + getResources().getString(R.string.table_name) + " ");
        resultSet = mydatabase.rawQuery("Select * from "+getResources().getString(R.string.table_name)+ " where meta_key='"+getResources().getString(R.string.coin_count)+"'", null);
        if(resultSet.getCount()==0) {

            Boolean write_successful = false;
            File root=null;
            String
                    total="";
            boolean flag=false;
            try {
                // <span id="IL_AD8" class="IL_AD">check for</span> SDcard
                root = Environment.getExternalStorageDirectory();
                Log.i(TAG, "path.." + root.getAbsolutePath());

                //check sdcard permission
                if (root.canWrite()){
                    File fileDir = new File(root.getAbsolutePath());
                    fileDir.mkdirs();

                    File file= new File(fileDir, getResources().getString(R.string.file_name));
                    FileReader filewriter = new FileReader(file);
                    BufferedReader in = new BufferedReader(filewriter);

                    total = in.readLine();
                    //  Toast.makeText(getBaseContext(), total, Toast.LENGTH_SHORT).show();
                    in.close();
                    write_successful = true;
                }
            } catch (IOException e) {
                Log.e("majid", "Could not write file to SDCard1" + e.getMessage());
                write_successful = false;

            }
            if(write_successful) {
                mydatabase.execSQL("INSERT INTO " + getResources().getString(R.string.table_name) + " VALUES(" + String.valueOf(v_id) + ",'" + getResources().getString(R.string.coin_count) + "','"+total+"');");
            }
            else {
                mydatabase.execSQL("INSERT INTO " + getResources().getString(R.string.table_name) + " VALUES(" + String.valueOf(v_id) + ",'" + getResources().getString(R.string.coin_count) + "','" + getResources().getString(R.string.coin_count_int) + "');");
                try {
                    // <span id="IL_AD8" class="IL_AD">check for</span> SDcard
                    root = Environment.getExternalStorageDirectory();
                    Log.i(TAG, "path.." + root.getAbsolutePath());

                    //check sdcard permission
                    if (root.canWrite()){
                        File fileDir = new File(root.getAbsolutePath());
                        fileDir.mkdirs();

                        File file= new File(fileDir, getResources().getString(R.string.file_name));
                        FileWriter filewriter = new FileWriter(file);
                        BufferedWriter out = new BufferedWriter(filewriter);
                        out.write( getResources().getString(R.string.coin_count) );
                        out.close();
                        write_successful = true;
                    }
                } catch (IOException e) {
                    Log.e("majid", "Could not write file to SDCard2" + e.getMessage());
                    write_successful = false;
                }
            }



        }
        else
        {
            Boolean write_successful = false;
            File root=null;
            String
                    total="";
            boolean flag=false;
            try {
                // <span id="IL_AD8" class="IL_AD">check for</span> SDcard
                root = Environment.getExternalStorageDirectory();
                Log.i(TAG, "path.." + root.getAbsolutePath());

                //check sdcard permission
                if (root.canWrite()){
                    File fileDir = new File(root.getAbsolutePath());
                    fileDir.mkdirs();

                    File file= new File(fileDir, getResources().getString(R.string.file_name));
                    FileReader filewriter = new FileReader(file);
                    BufferedReader in = new BufferedReader(filewriter);

                    total = in.readLine();
                    //  Toast.makeText(getBaseContext(), total, Toast.LENGTH_SHORT).show();
                    in.close();
                    write_successful = true;
                }
                else
                {
                }
            } catch (IOException e) {
                Log.e("majid", "Could not write file to SDCard3" + e.getMessage());
                write_successful = false;

            }
            if(write_successful) {
            }
            else {

                try {
                    // <span id="IL_AD8" class="IL_AD">check for</span> SDcard
                    root = Environment.getExternalStorageDirectory();
                    Log.i(TAG, "path.." + root.getAbsolutePath());

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
                    Log.e("majid", "Could not write file to SDCard4" + e.getMessage());
                    write_successful = false;
                }
            }
        }


        // Toast.makeText(getBaseContext(), String.valueOf(write_successful), Toast.LENGTH_SHORT).show();



        //  mydatabase.execSQL("update " + getResources().getString(R.string.table_name) + " set meta_value='500' where meta_key='"+getResources().getString(R.string.coin_count)+"'");



        //Log.d("majid",resultSet.getString(2));
        String
                coint_count =get_coint_count();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;


        // Toast.makeText(getBaseContext(), String.valueOf(screenWidth) + ":" + String.valueOf(screenHeight), Toast.LENGTH_SHORT).show();



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


        //Toast.makeText(getBaseContext(), String.valueOf(x) + ":" + String.valueOf(y), Toast.LENGTH_SHORT).show();


        final  TextView txt_coin_count = (TextView) findViewById(R.id.txt_coin_count);
        txt_coin_count.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (screenWidth * 0.08));


        txt_coin_count.setText(coint_count);

        txt_coin_count.setTypeface(tf);




        int
                img_heart_width = (int)(screenWidth*0.12);
        final ImageView img_hear = (ImageView) findViewById(R.id.img_heart);



//
//        final  TextView txt_member_name = (TextView) findViewById(R.id.txt_member_name);
//        txt_member_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screenWidth*0.08));
//
//        txt_member_name.setText("مهمان");
//
//
//         bounds = new Rect();
//         textPaint = txt_coin_count.getPaint();
//
//                ss=txt_coin_count.getText().toString();
//
//        textPaint.getTextBounds(ss, 0, ss.length(), bounds);
//        int height_txt_coin_count = bounds.height();
//        int width_txt_coin_count = bounds.width();
//
//
//
//         bounds = new Rect();
//         textPaint = txt_member_name.getPaint();
//         ss=txt_member_name.getText().toString();
//
//        textPaint.getTextBounds(ss, 0, ss.length(), bounds);
//        int height_txt_member_name = bounds.height();
//        int width_txt_member_name  = bounds.width();
//
//
//
//
//
//        int
//                img_new_member_width = (int)(screenWidth*0.1);
//        final ImageView img_new_member = (ImageView) findViewById(R.id.img_new_member);
//        LinearLayout.LayoutParams lp_new_member = new LinearLayout.LayoutParams((int)img_new_member_width, (int)img_new_member_width);
//        img_new_member.setLayoutParams(lp_new_member);
//        x = screenWidth - img_new_member_width - width_txt_coin_count-img_coin_width-((int)(img_new_member_width*0.3));
//        y = 1;
//
//        //Toast.makeText(getBaseContext(), String.valueOf(x) + ":" + String.valueOf(y), Toast.LENGTH_SHORT).show();
//
//        lp_new_member.setMargins(x, y, 0, 0);
//        img_new_member.setLayoutParams(lp_new_member);
//
//
//
//
//
//        LinearLayout.LayoutParams lp_member_name = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        x =   (img_new_member_width + ((int)(img_new_member_width*0.3)) + width_txt_member_name)*-1;
//        y = 1;
//
//        //Toast.makeText(getBaseContext(), String.valueOf(x) + ":" + String.valueOf(y), Toast.LENGTH_SHORT).show();
//
//        lp_member_name.setMargins(x, y, 0, 0);
//        txt_member_name.setLayoutParams(lp_member_name);
//

        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////Buttons////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////


        final ImageView img = (ImageView) findViewById(R.id.img_new_game);

        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    MyAsyncTask mm1 =  new MyAsyncTask();
////                    mm1.url =  getResources().getString(R.string.site_url) +"runSP.php?param=action&mac_id="+m_wlanMacAdd+"&action_type=new_game&coin_value="+get_coint_count()+"&version_value="+BuildConfig.VERSION_NAME;
//                    mm1.url =  getResources().getString(R.string.site_url) +"test.php";
//
//                    mm1.execute("");
                    Intent i=new Intent(MainActivity.this,Game_select.class);
                    finish();
                    startActivity(i);

                }

                return true;
            }
        });


        final ImageView img1 = (ImageView) findViewById(R.id.img_store);



        final ImageView img2 = (ImageView) findViewById(R.id.img_help);



        final ImageView img3 = (ImageView) findViewById(R.id.img_exit);



        img1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                } else if (event.getAction() == MotionEvent.ACTION_UP) {

//                    MyAsyncTask mm1 =  new MyAsyncTask();
//                    mm1.url =  getResources().getString(R.string.site_url) +"runSP.php?param=action&mac_id="+m_wlanMacAdd+"&action_type=store";
//                    mm1.execute("");

                    Intent i=new Intent(MainActivity.this,StoreActivity.class);
                 //   finish();
                    startActivity(i);
                }

                return true;
            }
        });

        img2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    MyAsyncTask mm1 =  new MyAsyncTask();
//                    mm1.url =  getResources().getString(R.string.site_url) +"runSP.php?param=action&mac_id="+m_wlanMacAdd+"&action_type=help";
//                    mm1.execute("");
                    Intent i=new Intent(MainActivity.this,Help.class);

                    startActivity(i);
                }

                return true;
            }
        });
        img3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    onBackPressed();

                }

                return true;
            }
        });


        SharedPreferences prefs = getSharedPreferences("m1", Context.MODE_PRIVATE);
        String restoredText = prefs.getString("removeAds", "");
        if (restoredText.equals("true"))
        {
            RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.lay_ad);
            mainLayout.setVisibility(LinearLayout.GONE);
        }
        String restoredText1 = prefs.getString("fullVersion", "");
        if (restoredText1.equals("true"))
        {
            ImageView img11=findViewById(R.id.img_plus);
            img11.setVisibility(View.VISIBLE);
            RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.lay_ad);
            mainLayout.setVisibility(LinearLayout.GONE);
        }

//        mm =  new MyAsyncTask();
//
//        {
//
//            mm.url =  getResources().getString(R.string.site_url) +"runSP.php?param=get_version";
//            mm.execute("");
//        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                  //  Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                  //  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                }
            }
        };

        displayFirebaseRegId();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);
        EditText ed1 = findViewById(R.id.editText2);
        ed1.setText(regId);
        if (!TextUtils.isEmpty(regId)) {
          //  Toast.makeText(this, "Firebase Reg Id: " + regId, Toast.LENGTH_SHORT).show();
            mm =  new MyAsyncTask();

            {

                mm.url =  getResources().getString(R.string.site_url) +"do.php?param=new_user&gcm_id="+ URLEncoder.encode(regId)+"&user_info="+URLEncoder.encode(getDeviceName());

                mm.execute("");
            }
        }
//        else
//            Toast.makeText(this,"Firebase Reg Id is not received yet!", Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        TextView txt = (TextView) findViewById(R.id.txt_coin_count);
        txt.setText(get_coint_count());
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
            //   Toast.makeText(getBaseContext(),"خطا در ارتباط با پرداخت درون برنامه ای",Toast.LENGTH_SHORT).show();
        }








        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
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
                        finish();



                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked

                        break;

                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("آیا می خواهید خارج شوید").setPositiveButton("بله", dialogClickListener)
                .setNegativeButton("خیر", dialogClickListener).show();


    }
    public void clk_coin(View view)
    {


//        String ns = Context.NOTIFICATION_SERVICE;
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
//
//        int icon = R.drawable.coin;
//        CharSequence tickerText = "hoy"; // ticker-text
//        long when = System.currentTimeMillis();
//        Context context = getApplicationContext();
//        CharSequence contentTitle = "Hello";
//        CharSequence contentText = "hi";
//        Intent notificationIntent = new Intent(this, StoreActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        Notification notification = new Notification(icon, tickerText, when);
//        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//
//
//// and this
//
//
//         HELLO_ID++;
//        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound_in);
//        notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
//        mNotificationManager.notify(HELLO_ID, notification);

//        startService(new Intent(MainActivity.this,MainService.class));
//        Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(startMain);
//        TapsellDeveloperInfo.getInstance().setDeveloperKey("khqtblhtbapbfqbtmbbegmkmcdniqeqkrikoeejdophralrrdhrerqmsfdordfpkiqodgd", MainActivity.this);

        //DeveloperCtaInterface.
//        DeveloperCtaInterface.getInstance().showNewCta(DeveloperCtaInterface.PAY_PER_INSTALL, 150, MainActivity.this);
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////

//        Intent myIntent = new Intent(MainActivity.this, MainService.class);
//        MainActivity.this.startService(myIntent);
    }

    int HELLO_ID = 0;
    public String MySigCheck(Context context) {
        String sigChk = "";

        Signature[] signature = new Signature[0];

        try {
            signature = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sigChk = String.valueOf(signature[0].hashCode());

        return sigChk;
    }

    public void clk_heart(View v)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("دوست گرامی این برنامه در حال تکمیل شدن می باشد لطفا با نظرات سازنده و پنج ستاره دادن از ما حمایت کنید، ممنون")
                .setCancelable(false)
                .setPositiveButton("اوکی", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setData(Uri.parse("bazaar://details?id=com.sputa.blackjacjs"));
                        intent.setPackage("com.farsitel.bazaar");
                        startActivity(intent);
                        //   finish();
                    }
                });
        AlertDialog alert1 = builder1.create();
        alert1.show();
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
    public void clk_add_coin(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE: { //Yes button clicked
                        set_coint_count(100, "add");

                        TextView txt_coint_count = (TextView) findViewById(R.id.txt_coin_count);
                        txt_coint_count.setText(get_coint_count());
                        break;

                    }
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                    {
                        set_coint_count(200, "add");
                        TextView txt_coint_count = (TextView) findViewById(R.id.txt_coin_count);
                        txt_coint_count.setText(get_coint_count());
                        break;
                    }
                    case DialogInterface.BUTTON_NEUTRAL:
                        //No button clicked
                    {
                        set_coint_count(500, "add");
                        TextView txt_coint_count = (TextView) findViewById(R.id.txt_coin_count);
                        txt_coint_count.setText(get_coint_count());
                        break;
                    }

            }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("چه تعداد می خواهید به سکه هایتان اضافه شود").setPositiveButton("100", dialogClickListener)
                .setNegativeButton("200", dialogClickListener).setNeutralButton("500",dialogClickListener).show();
    }


    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {



        public  String ss="",url="";





        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub

            //  dd=params[0];
            try {
                postData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Double result){

            //  pb.setVisibility(View.GONE);
         //   Toast.makeText(MainActivity.this, ss, Toast.LENGTH_SHORT).show();
            int
                    start=ss.indexOf("<output>");
            int
                    end=ss.indexOf("</output>");
            String
                    output_str="";
            if(end>0 && ss.length()>0) {
                output_str = ss.substring(start + 8, end);
                int
                        start1 = ss.indexOf("<param>");
                int
                        end1 = ss.indexOf("</param>");
                String
                        param_str = "";
                param_str = ss.substring(start1 + 7, end1);

//                if (param_str.equals("get_version")) {
//                    int
//                            i = 0;
//                    try {
//                        i = Integer.valueOf(output_str);
//                    } catch (Exception e1) {
//                        //    Log.d("majid", e1.getMessage()+"---"+ss+"---");
//                    }
//                    // Log.d("majid",String.valueOf(i));
//                    if (i > 0) {
//
//                        if (i != BuildConfig.VERSION_CODE) {
//                            Toast.makeText(getBaseContext(), getResources().getString(R.string.need_update_message), Toast.LENGTH_LONG).show();
//
//                        }
//                    }
//                }
                if (param_str.equals("new_user")) {
                          start1 = ss.indexOf("<result>");
                          end1 = ss.indexOf("</result>");
                    String
                            result1 = "";
                    result1 = ss.substring(start1 + 8, end1);
               //     Toast.makeText(getBaseContext(),result1,Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(getBaseContext(),param_str+"////"+output_str,Toast.LENGTH_SHORT).show();


            }





            //Toast.makeText(getBaseContext(),"ver="+http_result,Toast.LENGTH_SHORT).show();
//            AlertDialog.Builder builder1 = new AlertDialog.Builder(getBaseContext());
//            builder1.setTitle(getResources().getString(R.string.update));
//            builder1.setMessage(getResources().getString(R.string.need_update_message));
//            builder1.setCancelable(true);
//            builder1.setNeutralButton(android.R.string.ok,
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    });
//



//            AlertDialog alert11 = builder1.create();
//            alert11.show();


        }

        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        public void postData() throws IOException {
            HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
            HttpGet httpget = new HttpGet(url); // Set the action you want to do
            HttpResponse response = httpclient.execute(httpget); // Executeit
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent(); // Create an InputStream with the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) // Read line by line
                sb.append(line);

            String resString = sb.toString(); // Result is here
            ss = resString;
            //Log.d("majid", resString);
            is.close();
        }

    }

}
