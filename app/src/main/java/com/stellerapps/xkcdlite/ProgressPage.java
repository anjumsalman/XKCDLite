package com.stellerapps.xkcdlite;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;


public class ProgressPage extends Activity {

    private Typeface aileron,aileronsm;

    private TextView progressPageTV,percentTV;

    private CProgress progressCP;

    private int maxXkcd = 1, count = 0;

    private float percent = 0f;

    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_page);

        progressPageTV = (TextView) findViewById(R.id.progressPageTV);
        percentTV = (TextView) findViewById(R.id.percentTV);
        progressCP = (CProgress) findViewById(R.id.progressCP);

        aileron = Typeface.createFromAsset(getAssets(),"fonts/aileron.otf");
        aileronsm = Typeface.createFromAsset(getAssets(),"fonts/aileronsm.otf");

        progressPageTV.setTypeface(aileron);
        percentTV.setTypeface(aileronsm);

        maxXkcd = getIntent().getIntExtra("XKCD_MAX",1552);

        db = new DBHelper(this);
        count = db.getCount();
        percent = Math.round(((float)count / (float)maxXkcd) * 10000)/100;
        progressCP.setProgress(percent);
        percentTV.setText(percent+"%");
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left,R.anim.push_out_right);
    }

}
