package com.stellerapps.xkcdlite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FavPage extends Activity {

    private Typeface aileron,aileronr;

    private TextView favPageTV;

    private ListView comicListLV;

    private ImageButton delB;

    private DBHelper db;
    private SQLiteDatabase dbSQL;
    private Cursor cursor;

    private int maxXkcd;

    ComicListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_page);

        favPageTV = (TextView) findViewById(R.id.favPageTV);
        comicListLV = (ListView) findViewById(R.id.comicListLV);
        delB = (ImageButton) findViewById(R.id.delB);

        aileron = Typeface.createFromAsset(getAssets(), "fonts/aileron.otf");
        aileronr = Typeface.createFromAsset(getAssets(), "fonts/aileronr.otf");

        favPageTV.setTypeface(aileron);

        maxXkcd = getIntent().getIntExtra("XKCD_MAX", 1551);

        listPopulator();
        comicListLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        comicListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] text = ((TextView) view.findViewById(R.id.listTextTV)).getText().toString().split(" : ");
                Intent intent = new Intent(FavPage.this, DisplayPage.class);
                intent.putExtra("FROM_FAV",true);
                intent.putExtra("XKCD_NUM",Integer.parseInt(text[0]));
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
            }
        });

        comicListLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.listCheckCB);
                checkBox.toggle();
                return true;
            }
        });
    }

    public void onResume(){
        listPopulator();
        super.onResume();
    }

    public void onDestroy(){
        cursor.close();
        super.onDestroy();
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public void listPopulator(){
        db = new DBHelper(this);
        dbSQL=db.getWritableDatabase();
        cursor=dbSQL.rawQuery("SELECT  * FROM favTable", null);

        adapter = new ComicListAdapter(FavPage.this, cursor);
        comicListLV.setAdapter(adapter);

        if(cursor.getCount()==0){
            Toast.makeText(this, "No favourites exist",Toast.LENGTH_SHORT).show();
            delB.setVisibility(View.INVISIBLE);
        }else{
            delB.setVisibility(View.VISIBLE);
        }

    }

    public void delete(View v){

        int childCount = cursor.getCount();
        for (int i=0;i<childCount;i++){

            CheckBox checkBox = (CheckBox) comicListLV.getChildAt(i).findViewById(R.id.listCheckCB);
            TextView textView = (TextView) comicListLV.getChildAt(i).findViewById(R.id.listTextTV);

            String[] text = textView.getText().toString().split(" : ");

            if(checkBox.isChecked()){
                db.deletexkcd(Integer.parseInt(text[0]));
            }
        }
        listPopulator();
        int childCountNow = cursor.getCount();
        if (childCount!=childCountNow){
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
