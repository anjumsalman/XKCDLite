package com.stellerapps.xkcdlite;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class AboutPage extends Activity {
    private Typeface aileron,aileronr;

    private TextView aboutTV,releaseDateTV,versionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        aboutTV = (TextView) findViewById(R.id.aboutTV);
        releaseDateTV = (TextView) findViewById(R.id.releaseDateTV);
        versionTV = (TextView) findViewById(R.id.versionTV);

        aileron = Typeface.createFromAsset(getAssets(), "fonts/aileron.otf");
        aileronr = Typeface.createFromAsset(getAssets(),"fonts/aileronr.otf");

        aboutTV.setTypeface(aileron);
        releaseDateTV.setTypeface(aileronr);
        versionTV.setTypeface(aileronr);
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left,R.anim.push_out_right);
    }

}
