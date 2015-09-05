package com.stellerapps.xkcdlite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LaunchPage extends Activity {

    private Typeface aileron;

    private TextView browseTV;
    private TextView favsTV;
    private TextView progressTV;
    private TextView stashTV;

    private int maxXkcd = 1;

    private static final String TAG = "XKCD Browser";

    private boolean browseEnabled = false;

    private ImageButton menuB;

    private ListPopupWindow listPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_page);

        aileron = Typeface.createFromAsset(getAssets(),"fonts/aileron.otf");

        browseTV = (TextView) findViewById(R.id.browseTV);
        favsTV = (TextView) findViewById(R.id.favsTV);
        progressTV = (TextView) findViewById(R.id.progressTV);
        stashTV = (TextView) findViewById(R.id.stashTV);
        menuB = (ImageButton) findViewById(R.id.menuB);

        browseTV.setTypeface(aileron);
        favsTV.setTypeface(aileron);
        progressTV.setTypeface(aileron);
        stashTV.setTypeface(aileron);

        browseTV.setTextColor(Color.GRAY);
        progressTV.setTextColor(Color.GRAY);
        favsTV.setTextColor(Color.GRAY);

        String[] actions = {"About","Exit"};
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow .setAdapter(new ArrayAdapter<String>(this, R.layout.menu_list, actions));
        listPopupWindow.setAnchorView(menuB);
        listPopupWindow.setWidth(250);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent aboutIntent = new Intent(LaunchPage.this, AboutPage.class);
                        startActivity(aboutIntent);
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        break;
                    case 1:
                        System.exit(1);
                        break;
                }
            }
        });

        if(isNetworkAvailable())
            new AsyncMax().execute("http://www.xkcd.com");
        else
            Toast.makeText(LaunchPage.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();

    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left,R.anim.push_out_right);
    }

    public void onResume(){
        super.onResume();
        listPopupWindow.dismiss();

        if(browseEnabled){
            browseTV.setTextColor(Color.BLACK);
            favsTV.setTextColor(Color.BLACK);
            progressTV.setTextColor(Color.BLACK);
        }

        stashTV.setTextColor(Color.BLACK);
    }

    public void browseXK(View v){
        if(browseEnabled) {
            browseTV.setTextColor(getResources().getColor(R.color.app_orange));
            Intent intent = new Intent(this, DisplayPage.class);
            intent.putExtra("XKCD_MAX", maxXkcd);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
        }else if(isNetworkAvailable()){
            Toast.makeText(this, "Initialising...", Toast.LENGTH_SHORT).show();
        }
    }

    public void showMenu(View v){
        listPopupWindow.show();
    }

    public void favsXK(View v){
        if(browseEnabled) {
            favsTV.setTextColor(getResources().getColor(R.color.app_orange));
            Intent intent = new Intent(this, FavPage.class);
            intent.putExtra("XKCD_MAX", maxXkcd);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
        }else if(isNetworkAvailable()){
            Toast.makeText(this, "Initialising...", Toast.LENGTH_SHORT).show();
        }
    }

    public void progressXK(View v){
        if(browseEnabled) {
            progressTV.setTextColor(getResources().getColor(R.color.app_orange));
            Intent intent = new Intent(this, ProgressPage.class);
            intent.putExtra("XKCD_MAX", maxXkcd);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
        }else if(isNetworkAvailable()){
            Toast.makeText(this,"Initialising...",Toast.LENGTH_SHORT).show();
        }

    }

    public void stashXK(View v){
        stashTV.setTextColor(getResources().getColor(R.color.app_orange));
        Intent intent = new Intent(this, Stash.class);
        startActivity(intent);
        overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
    }

    private class AsyncMax extends AsyncTask<String,Void,String> {
        Document doc;
        Elements elXkcdMax;
        String outMax="";

        @Override
        protected String doInBackground(String... arg) {
            try {
                doc = Jsoup.connect(arg[0]).timeout(0).get();
                elXkcdMax=doc.select("a[rel=prev]");
                outMax=elXkcdMax.attr("href").substring(1,5);
                Log.e(TAG, outMax);
            } catch (IOException e) {
                Log.e(TAG, "Error in Jsoup");
                return "1551";
            }
            return outMax;
        }

        protected void onPostExecute(String result) {
            maxXkcd=Integer.parseInt(result)+1;
            browseEnabled = true;
            browseTV.setTextColor(Color.BLACK);
            progressTV.setTextColor(Color.BLACK);
            favsTV.setTextColor(Color.BLACK);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
