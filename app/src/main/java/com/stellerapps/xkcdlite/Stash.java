package com.stellerapps.xkcdlite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Stash extends Activity {

    private StashListAdapter adapter;

    private ListView stashListLV;

    private ImageButton delStashB;

    private Typeface aileron,aileronr;

    private TextView stashPageTV;

    private DBHelper db;
    private SQLiteDatabase dbSQL;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stash);

        stashPageTV = (TextView) findViewById(R.id.stashPageTV);
        stashListLV = (ListView) findViewById(R.id.stashListLV);
        delStashB = (ImageButton) findViewById(R.id.delStashB);

        aileron = Typeface.createFromAsset(getAssets(), "fonts/aileron.otf");
        aileronr = Typeface.createFromAsset(getAssets(), "fonts/aileronr.otf");

        stashPageTV.setTypeface(aileron);

        listPopulator();
        stashListLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        stashListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView title = (TextView)view.findViewById(R.id.listTextTV);
                String[] text = title.getText().toString().split(" : ");
                Intent intent = new Intent(Stash.this,DisplayStashed.class);
                intent.putExtra("XKCD_NUM",Integer.parseInt(text[0]));
                intent.putExtra("XKCD_TITLE",text[1]);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
            }
        });

        stashListLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
        cursor=dbSQL.rawQuery("SELECT  * FROM saveTable", null);

        adapter = new StashListAdapter(Stash.this, cursor);
        stashListLV.setAdapter(adapter);

        if(cursor.getCount()==0){
            Toast.makeText(this, "Nothing saved", Toast.LENGTH_SHORT).show();
            delStashB.setVisibility(View.INVISIBLE);
        }else{
            delStashB.setVisibility(View.VISIBLE);
        }
    }

    public void deleteStashItem(View v){

        int childCount = cursor.getCount();
        for (int i=0;i<childCount;i++){

            CheckBox checkBox = (CheckBox) stashListLV.getChildAt(i).findViewById(R.id.listCheckCB);
            TextView textView = (TextView) stashListLV.getChildAt(i).findViewById(R.id.listTextTV);

            String[] text = textView.getText().toString().split(" : ");

            if(checkBox.isChecked()){
                db.deleteSave(Integer.parseInt(text[0]));
            }
        }
        listPopulator();
        int childCountNow = cursor.getCount();
        if (childCount!=childCountNow){
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
