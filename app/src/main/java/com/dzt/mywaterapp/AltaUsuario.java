package com.dzt.mywaterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AltaUsuario extends ActionBarActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuario);

        SharedPreferences myPreferences = getSharedPreferences("infoUsuario", Context.MODE_PRIVATE);
        String sNombreUsuario = myPreferences.getString("nombreUsuario", "");
        String sHabitantes = myPreferences.getString("numeroHabitantes", "");
        String sLitrosAlmacenados = myPreferences.getString("litrosAlmacenados", "");
        String sCantidadCarros = myPreferences.getString("cantidadCarros", "");


        TextView edt_Usuario = (TextView)findViewById(R.id.Edt_Usuario);
        edt_Usuario.setText(sNombreUsuario);


        TextView edt_Habitantes = (TextView)findViewById(R.id.Edt_Habitantes);
        edt_Habitantes.setText(sHabitantes);

        TextView edt_LitrosAlmacenados = (TextView)findViewById(R.id.Edt_LitrosAlmacenables);
        edt_LitrosAlmacenados.setText(sLitrosAlmacenados);

        TextView edt_CantidadCarros = (TextView)findViewById(R.id.Edt_CantidadCarros);
        edt_CantidadCarros.setText(sCantidadCarros);

        Button Btn_Ok = (Button)findViewById(R.id.Btn_Ok);
        Btn_Ok.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alta_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Btn_Ok:
                TextView edt_Usuario = (TextView)findViewById(R.id.Edt_Usuario);
                String sNombreUsuario = edt_Usuario.getText().toString();

                TextView edt_Habitantes = (TextView)findViewById(R.id.Edt_Habitantes);
                String sHabitantes = edt_Habitantes.getText().toString();

                TextView edt_LitrosAlmacenados = (TextView)findViewById(R.id.Edt_LitrosAlmacenables);
                String sLitrosAlmacenados = edt_LitrosAlmacenados.getText().toString();

                TextView edt_CantidadCarros = (TextView)findViewById(R.id.Edt_CantidadCarros);
                String sCantidadCarros = edt_CantidadCarros.getText().toString();

                SharedPreferences.Editor editor = getSharedPreferences("infoUsuario", MODE_PRIVATE).edit();
                editor.putString("nombreUsuario",sNombreUsuario);
                editor.putString("numeroHabitantes", sHabitantes );
                editor.putString("litrosAlmacenados", sLitrosAlmacenados );
                editor.putString("cantidadCarros", sCantidadCarros );
                editor.commit();

                Toast.makeText(this, "sNombreUsuario" + " " + sHabitantes + " " + sLitrosAlmacenados, Toast.LENGTH_LONG).show();

                startActivity(new Intent(this, Estadistica.class));
                this.finish();

                break;


        }
    }
}
