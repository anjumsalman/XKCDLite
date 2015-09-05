package com.stellerapps.xkcdlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class DisplayPage extends Activity {

    private int xkcdNumber, maxXkcd;

    private boolean isLoading,fromFav = false;

    private Random random;

    private String xkcdTitle, xkcdImgUrl;
    private static final String TAG = "XKCD Browser";
    private String[] actions;

    private Typeface aileronsm;

    private TextView titleTV;

    private ImageButton menuB;

    private TouchImageView xkcdImgIV;

    private ListPopupWindow listPopupWindow;

    private DBHelper db;

    private AlertDialog.Builder alert;

    private BitmapDrawable drawable;
    private Bitmap bitmap;
    private File imgFile,cacheDir,cacheFile,imgDir;
    public FileOutputStream outStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_page);

        aileronsm = Typeface.createFromAsset(getAssets(),"fonts/aileronsm.otf");

        xkcdImgIV=(TouchImageView) findViewById(R.id.xkcdImgIV);
        titleTV=(TextView) findViewById(R.id.titleTV);
        menuB=(ImageButton) findViewById(R.id.menuB);

        titleTV.setTypeface(aileronsm);

        fromFav = getIntent().getBooleanExtra("FROM_FAV",false);

        if(fromFav){
            maxXkcd = getIntent().getIntExtra("XKCD_MAX",1551);
            xkcdNumber = getIntent().getIntExtra("XKCD_NUM",1);
        }else{
            maxXkcd = getIntent().getIntExtra("XKCD_MAX",1551);
            xkcdNumber = maxXkcd;
        }

        db = new DBHelper(this);

        actions = new String[]{"Goto","Explain","Save","Share"};
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow .setAdapter(new ArrayAdapter<String>(this, R.layout.menu_list, actions));
        listPopupWindow.setAnchorView(menuB);
        listPopupWindow.setWidth(250);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DisplayPage.this,ExplainPage.class);
                intent.putExtra("XKCD_NUM",xkcdNumber);
                switch (i){
                    case 0:
                        showAlert();
                        break;
                    case 1:
                        if(!isLoading){
                            startActivity(intent);
                            overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
                        }else{ Toast.makeText(DisplayPage.this,R.string.xk_wait,Toast.LENGTH_SHORT).show();}
                        break;
                    case 2:
                        if(!isLoading){
                            saveImage();
                            listPopupWindow.dismiss();
                        }
                        break;
                    case 3:
                        if(!isLoading){
                            share();
                            listPopupWindow.dismiss();
                        }
                        break;
                }
            }
        });

        if(isNetworkAvailable()){
            if(fromFav){
                new AsyncImg().execute(new String[]{"http://xkcd.com/"+xkcdNumber});
            }else{
                random=new Random();
                xkcdNumber=random.nextInt(maxXkcd);
                new AsyncImg().execute(new String[]{"http://xkcd.com/"+xkcdNumber});
            }
        }
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left,R.anim.push_out_right);
    }

    public void onResume(){
        super.onResume();
        listPopupWindow.dismiss();
    }

    /* ---------------- Async Class Begins ------------------*/
    private class AsyncImg extends AsyncTask<String,Void,String[]>{
        Document doc;
        Elements elXkcdTitle;
        Elements elXkcdImgUrl;
        String []out=new String[2];

        protected void onPreExecute() {
            xkcdImgIV.setImageDrawable(null);
            isLoading = true;
        }
        @Override
        protected String[] doInBackground(String... arg) {
            try {
                doc = Jsoup.connect(arg[0]).timeout(0).get();
                elXkcdTitle=doc.select("#ctitle");
                elXkcdImgUrl=doc.select("#comic img");
                out[0]=elXkcdTitle.text();
                out[1]=elXkcdImgUrl.first().attr("src");
            } catch (IOException f) {
                Log.e(TAG,"Error in Jsoup");
            }
            return out;
        }

        protected void onPostExecute(String[] result) {
            xkcdTitle=result[0];
            xkcdImgUrl="http:"+result[1];

            titleTV.setText(xkcdNumber+" : "+xkcdTitle);

            db.addCount(xkcdNumber);

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
            imageLoader.displayImage(xkcdImgUrl, xkcdImgIV,null, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    Toast.makeText(DisplayPage.this,"Failed to load. Check your connection.",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    isLoading = false;
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
            
        }
    }

	/* ---------------- Async Class Ends ------------------*/

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void navLeft(View v){
        xkcdNumber--;
        if(xkcdNumber<1){
            Toast.makeText(this,R.string.xk_reached_end,Toast.LENGTH_SHORT).show();
            xkcdNumber++;
        }else{
            titleTV.setText(R.string.xk_loading);
            new AsyncImg().execute(new String[]{"http://xkcd.com/"+xkcdNumber});
        }
    }

    public void navRight(View v){
        xkcdNumber++;
        if(xkcdNumber>maxXkcd){
            Toast.makeText(this,R.string.xk_reached_end,Toast.LENGTH_SHORT).show();
            xkcdNumber--;
        }else{
            titleTV.setText(R.string.xk_loading);
            new AsyncImg().execute(new String[]{"http://xkcd.com/"+xkcdNumber});
        }
    }

    public void rand (View v){
        titleTV.setText(R.string.xk_loading);
        xkcdNumber = new Random().nextInt(maxXkcd);
        new AsyncImg().execute(new String[]{"http://xkcd.com/"+xkcdNumber});
    }

    public void addFav (View v){
        if(!isLoading){
            XKCD xkcd=new XKCD(xkcdNumber,xkcdTitle,xkcdImgUrl);
            if(db.isPresent(xkcdNumber)){
                Toast.makeText(this,R.string.xk_already_fav,Toast.LENGTH_SHORT).show();
            }else{
                db.addXKCD(xkcd);
                Toast.makeText(this,R.string.xk_fav_added,Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,R.string.xk_wait,Toast.LENGTH_SHORT).show();
        }
    }

    public void showMenu(View v){
        listPopupWindow.show();
    }

    public void showAlert(){
        alert= new AlertDialog.Builder(this);

        alert.setTitle("Goto");
        alert.setMessage("Enter XKCD number");

        LinearLayout alertLayout=new LinearLayout(this);
        alertLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText numberT = new EditText(this);
        numberT.setPadding(20,10,20,10);

        alertLayout.addView(numberT);
        alert.setView(alertLayout);

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                try{if(Integer.parseInt(numberT.getText().toString())<maxXkcd && Integer.parseInt(numberT.getText().toString())>0 && numberT.getText().toString()!=null){
                    xkcdNumber=Integer.parseInt(numberT.getText().toString());
                    new AsyncImg().execute(new String[]{"http://xkcd.com/"+xkcdNumber});
                    listPopupWindow.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(), "Enter a valid number", Toast.LENGTH_SHORT).show();
                    showAlert();
                }}
                catch(NumberFormatException e){
                    Toast.makeText(getApplicationContext(), "Enter a valid number", Toast.LENGTH_SHORT).show();
                    showAlert();
                }
            }
        });
        alert.show();
    }

    public void saveImage(){
        if(!isLoading) {
            drawable = (BitmapDrawable) xkcdImgIV.getDrawable();
            bitmap = drawable.getBitmap();
            imgDir = new File(Environment.getExternalStorageDirectory() + File.separator + "XKCDLite");
            imgDir.mkdir();
            imgFile = new File(imgDir, xkcdNumber + ".jpg");
            try {
                outStream = new FileOutputStream(imgFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                Toast.makeText(DisplayPage.this, R.string.xk_saved, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.d(TAG, "Not Saved");
                Toast.makeText(DisplayPage.this, R.string.xk_save_failed, Toast.LENGTH_SHORT).show();
            }

            db.addSave(xkcdNumber, xkcdTitle);
        }else {
            Toast.makeText(this, R.string.xk_wait, Toast.LENGTH_SHORT).show();
        }
    }

    public void share(){
        if(!isLoading) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            xkcdImgIV.buildDrawingCache();
            bitmap = xkcdImgIV.getDrawingCache();
            OutputStream outStream = null;
            cacheDir = getExternalCacheDir();
            cacheFile = new File(cacheDir, "Share.jpg");
            try {
                outStream = new FileOutputStream(cacheFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                Log.d(TAG, "Not Saved");
                Toast.makeText(DisplayPage.this, R.string.xk_share_failed, Toast.LENGTH_SHORT).show();
            }

            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cacheFile));
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }else{
            Toast.makeText(this, R.string.xk_wait, Toast.LENGTH_SHORT).show();
        }
    }

}
