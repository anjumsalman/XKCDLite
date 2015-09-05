package com.stellerapps.xkcdlite;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class ExplainPage extends Activity {

    private TextView explainTextTV;
    private TextView explainTV;

    private int xkcdNumber;

    private static final String TAG = "XKCD Browser";

    private Typeface aileron,aileronr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_page);

        explainTextTV = (TextView) findViewById(R.id.explainTextTV);
        explainTV = (TextView) findViewById(R.id.progressPageTV);

        aileron = Typeface.createFromAsset(getAssets(),"fonts/aileron.otf");
        aileronr = Typeface.createFromAsset(getAssets(),"fonts/aileronr.otf");

        explainTextTV.setTypeface(aileronr);
        explainTV.setTypeface(aileron);

        xkcdNumber = getIntent().getIntExtra("XKCD_NUM",1551);

        new AsyncExp().execute("http://www.explainxkcd.com/wiki/index.php/"+xkcdNumber);
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left,R.anim.push_out_right);
    }

    private class AsyncExp extends AsyncTask<String,Void,String> {
        Document doc;
        Elements elXkcdExp;
        String outExp="";

        @Override
        protected String doInBackground(String... arg) {
            try {
                doc = Jsoup.connect(arg[0]).timeout(0).get();
                elXkcdExp=doc.select("#mw-content-text table~p");
                for(int i=0;i<elXkcdExp.size();i++){
                    outExp+=elXkcdExp.eq(i).text()+"\n";
                }
            } catch (IOException e) {
                Log.e(TAG, "Error in Jsoup");
                //refresh();
            }
            return outExp;
        }

        protected void onPostExecute(String result) {
            Log.d("Explain",result);
            showExp(result);
        }
    }

    public void showExp(String msg){
        explainTextTV.setText(msg);
    }

}
