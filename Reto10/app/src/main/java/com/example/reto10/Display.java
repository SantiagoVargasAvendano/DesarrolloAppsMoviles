package com.example.reto10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Display extends AppCompatActivity {

    TextView name ;
    TextView code;
    TextView municipio;
    TextView departamento;
    TextView disciplina;
    TextView area;
    TextView clasificacion;
    TextView programa;

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        code = (TextView) findViewById(R.id.code);
        name = (TextView) findViewById(R.id.name);
        municipio = (TextView) findViewById(R.id.municipio);
        departamento = (TextView) findViewById(R.id.departamento);
        disciplina = (TextView) findViewById(R.id.disciplina);
        area = (TextView) findViewById(R.id.area);
        clasificacion = (TextView) findViewById(R.id.clasificacion);
        programa = (TextView) findViewById(R.id.programa);


        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            String name2 = extras.getString("name");


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.datos.gov.co")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GroupService groupService = retrofit.create(GroupService.class);
            Call<List<Group>> call = groupService.getGroups();

            call.enqueue(new Callback<List<Group>>() {
                @Override
                public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                    for(Group grp : response.body()) {
                        if(grp.getNme_grupo_gr()!=null){
                            if(grp.getNme_grupo_gr().equals(name2)){
                                group = grp;
                                name.setText(group.getNme_grupo_gr());
                                code.setText(group.getCod_grupo_gr());
                                municipio.setText(group.getNme_municipio_gr());
                                departamento.setText(group.getNme_departamento_gr());
                                disciplina.setText(group.getNme_area_esp_gr());
                                area.setText(group.getNme_area_gr());
                                clasificacion.setText(group.getNme_clasificacion_gr());
                                programa.setText(group.getNme_prog_colc1_gr());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Group>> call, Throwable t) {
                }
            });


        }
    }
}