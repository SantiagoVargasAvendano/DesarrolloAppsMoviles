package com.example.reto8;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayContact extends AppCompatActivity {
    private DBHelper mydb ;

    TextView name ;
    TextView phone;
    TextView email;
    TextView url;
    TextView prod_serv;
    Spinner classification;
    int id_To_Update = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);
        name = (TextView) findViewById(R.id.editTextName);
        phone = (TextView) findViewById(R.id.editTextPhone);
        email = (TextView) findViewById(R.id.editTextEmail);
        url = (TextView) findViewById(R.id.editTextUrl);
        prod_serv = (TextView) findViewById(R.id.editTextProdServ);
        classification = (Spinner) findViewById(R.id.editTextClassification);

        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            int Value = extras.getInt("id");

            if(Value>0){
                //means this is the view part not the add contact part.
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();

                @SuppressLint("Range") String nam = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_NAME));
                @SuppressLint("Range") String phon = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_PHONE));
                @SuppressLint("Range") String emai = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_EMAIL));
                @SuppressLint("Range") String url1 = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_URL));
                @SuppressLint("Range") String prodServ1 = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_PAS));
                @SuppressLint("Range") String classif1 = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_CLASSIF));

                if (!rs.isClosed())  {
                    rs.close();
                }
                Button b = (Button)findViewById(R.id.button1);
                b.setVisibility(View.INVISIBLE);

                name.setText((CharSequence)nam);
                name.setFocusable(false);
                name.setClickable(false);

                phone.setText((CharSequence)phon);
                phone.setFocusable(false);
                phone.setClickable(false);

                email.setText((CharSequence)emai);
                email.setFocusable(false);
                email.setClickable(false);

                url.setText((CharSequence)url1);
                url.setFocusable(false);
                url.setClickable(false);

                prod_serv.setText((CharSequence)prodServ1);
                prod_serv.setFocusable(false);
                prod_serv.setClickable(false);

                //Toast.makeText(getApplicationContext(), classif1, Toast.LENGTH_SHORT).show();
                if(classif1.equals("consultoría")){
                    classification.setSelection(0);
                }else if (classif1.equals("desarrollo a la medida")){
                    classification.setSelection(1);
                }else {
                    classification.setSelection(2);
                }
                classification.setEnabled(false);
                classification.setClickable(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Bundle extras = getIntent().getExtras();

        if(extras !=null) {
            int Value = extras.getInt("id");
            if(Value>0){
                getMenuInflater().inflate(R.menu.display_contact, menu);
            } else{
                getMenuInflater().inflate(R.menu.menu_main, menu);
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.Edit_Contact:
                Button b = (Button)findViewById(R.id.button1);
                b.setVisibility(View.VISIBLE);
                name.setEnabled(true);
                name.setFocusableInTouchMode(true);
                name.setClickable(true);

                phone.setEnabled(true);
                phone.setFocusableInTouchMode(true);
                phone.setClickable(true);

                email.setEnabled(true);
                email.setFocusableInTouchMode(true);
                email.setClickable(true);

                url.setEnabled(true);
                url.setFocusableInTouchMode(true);
                url.setClickable(true);

                prod_serv.setEnabled(true);
                prod_serv.setFocusableInTouchMode(true);
                prod_serv.setClickable(true);

                classification.setEnabled(true);
                classification.setFocusableInTouchMode(true);
                classification.setClickable(true);

                return true;
            case R.id.Delete_Contact:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContact)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mydb.deleteContact(id_To_Update);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void run(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            int Value = extras.getInt("id");
            if(Value>0){
                if(mydb.updateContact(id_To_Update,name.getText().toString(),
                        phone.getText().toString(), email.getText().toString(),
                        url.getText().toString(), prod_serv.getText().toString(), classification.getSelectedItem().toString())){
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            } else{
                if(mydb.insertContact(name.getText().toString(), phone.getText().toString(),
                        email.getText().toString(), url.getText().toString(), prod_serv.getText().toString(), classification.getSelectedItem().toString())){
                    Toast.makeText(getApplicationContext(), "done",
                            Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "not done",
                            Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        }
    }
}