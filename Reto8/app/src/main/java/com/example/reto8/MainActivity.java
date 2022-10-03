package com.example.reto8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.content.Intent;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView obj;
    private Spinner filterName;
    private CheckBox filterCons;
    private CheckBox filterDesa;
    private CheckBox filterFabr;
    private Button button;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context ctx = this;
        mydb = new DBHelper(this);
        ArrayList array_list = mydb.getAllContacts();
        ArrayAdapter arrayAdapter = new ArrayAdapter(ctx, android.R.layout.simple_list_item_1, array_list);
        ArrayList array_list2 = mydb.getAllContacts();
        array_list2.add(0, "");
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(ctx, android.R.layout.simple_list_item_1, array_list2);

        filterName = (Spinner)findViewById(R.id.filterClassification);
        filterName.setAdapter(arrayAdapter2);
        filterCons = (CheckBox)findViewById(R.id.checkBox1);
        filterDesa = (CheckBox)findViewById(R.id.checkBox2);
        filterFabr = (CheckBox)findViewById(R.id.checkBox3);
        obj = (ListView)findViewById(R.id.listView1);
        obj.setAdapter(arrayAdapter);
        button = (Button)findViewById(R.id.button);
        
        obj.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                // TODO Auto-generated method stub
                int id_To_Search = arg2 + 1;

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);

                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = filterName.getSelectedItem().toString();
                Boolean cons = filterCons.isChecked();
                Boolean desa = filterDesa.isChecked();
                Boolean fabr = filterFabr.isChecked();

                ArrayList array_list = mydb.filters(text,cons,desa,fabr);
                ArrayAdapter arrayAdapter = new ArrayAdapter(ctx, android.R.layout.simple_list_item_1, array_list);
                obj.setAdapter(arrayAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.item1:Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);

                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);
                intent.putExtras(dataBundle);

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }
}