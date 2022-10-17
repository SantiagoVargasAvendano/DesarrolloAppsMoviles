package com.example.reto10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    /*ListView list;
    ArrayList<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, titles);
        list = findViewById(R.id.list);

        list.setAdapter(arrayAdapter);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PostService postService = retrofit.create(PostService.class);
        Call<List<Post>> call = postService.getPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                for(Post post : response.body()) {
                    titles.add(post.getTitle());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
            }
        });

    }*/

    ListView list;
    ArrayList<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, titles);
        list = findViewById(R.id.list);

        list.setAdapter(arrayAdapter);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.datos.gov.co")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GroupService groupService = retrofit.create(GroupService.class);
        Call<List<Group>> call = groupService.getGroups();

        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                for(Group group : response.body()) {
                    titles.add(group.getNme_grupo_gr());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                String name = titles.get(arg2);

                Bundle dataBundle = new Bundle();
                dataBundle.putString("name", name);

                Intent intent = new Intent(getApplicationContext(), Display.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });

    }
}