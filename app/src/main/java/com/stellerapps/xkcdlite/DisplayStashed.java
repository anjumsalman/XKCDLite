package com.stellerapps.xkcdlite;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;


public class DisplayStashed extends Activity {

    TextView displayStashTitleTV;
    TouchImageView stashxkcdImgIV;

    Typeface aileronsm;

    private int xkcdNum;

    private String xkcdTitle, saveLoc;

    DBHelper db;
    SQLiteDatabase sqldb;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stashed);

        displayStashTitleTV = (TextView) findViewById(R.id.displayStashTitleTV);
        stashxkcdImgIV = (TouchImageView) findViewById(R.id.stashxkcdImgIV);

        aileronsm = Typeface.createFromAsset(getAssets(),"fonts/aileronsm.otf");

        displayStashTitleTV.setTypeface(aileronsm);

        xkcdNum = getIntent().getIntExtra("XKCD_NUM",1);
        xkcdTitle = getIntent().getStringExtra("XKCD_TITLE");

        db = new DBHelper(this);
        sqldb = db.getReadableDatabase();

        displayStashTitleTV.setText(xkcdNum + " : " +xkcdTitle);

        saveLoc = Environment.getExternalStorageDirectory() + File.separator + "XKCDLite" + File.separator + xkcdNum +".jpg";
        ImageLoader imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        imgLoader.displayImage("file://"+saveLoc, stashxkcdImgIV);
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

}
