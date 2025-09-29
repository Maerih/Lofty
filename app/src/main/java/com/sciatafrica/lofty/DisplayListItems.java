package com.sciatafrica.lofty;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayListItems extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items);

        Intent intent = getIntent();
        String itemValue = intent.getStringExtra(MainActivity.EXTRA_ITEM);
        TextView tv = (TextView) findViewById(R.id.textViewListItem);
        //tv.setText(itemValue);
        tv.setText(getListItems());
    }
    private String getListItems(){
        String URL = "content://com.sciatafrica.lofty.listprovider";
        Uri listitems = Uri.parse(URL);
        Cursor c = getContentResolver().query(listitems,null,null,null,"listitem");
        String results = "";
        if(c.moveToFirst()){
            do{
                int idIndex = c.getColumnIndex(ListProvider._ID);
                int itemIndex = c.getColumnIndex(ListProvider.LISTITEM);

                results += c.getString(idIndex) + ", " + c.getString(itemIndex) + "\n";



            }while(c.moveToNext());
        }
        return results;
    }
}
