package com.sputa.blackjacjs;

/**
 * Created by diego on 20/02/2018.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class functions  extends AppCompatActivity {

    int screenWidth  = 0;
    int screenHeight = 0;
    static int a = 4;
    static int update_message_showed = 0;
    static int protected_message_showed = 0;

    public String
            font_name = "fonts/BYekan.ttf";

    public static String
            u_id="1";
    public static String
            u_name="morteza";
    public static String
            reg_id="0";
    public static String
            shaked_uname="";
    public static String
            shaked_g_id="";

    public static String
            ad_shown="no";
    public static String
            gender="boy";
    public static String
            avatar_name="";

    Typeface tf;

    public static int question_count = 4;
    public static int request_time_out = 10;



    public  void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    public int get_text_with(TextView my_txt)
    {
        Rect bounds = new Rect();
        Paint textPaint = my_txt.getPaint();
        String
                ss=my_txt.getText().toString();

        textPaint.getTextBounds(ss, 0, ss.length(), bounds);

        return bounds.width();
    }
    public int get_text_height(TextView my_txt)
    {
        Rect bounds = new Rect();
        Paint textPaint = my_txt.getPaint();
        String
                ss=my_txt.getText().toString();

        textPaint.getTextBounds(ss, 0, ss.length(), bounds);

        return bounds.height();
    }
    public boolean isConnectingToInternet( Context _context){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
    public  boolean check_need_permission()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            return true;
        }
        else {
            return  false;
        }
    }
    public boolean checkIfAlreadyhavePermission(String perm, Context mcontext) {
        int result = ContextCompat.checkSelfPermission(mcontext, perm);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
