package com.dzt.mywaterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Estadistica extends ActionBarActivity implements View.OnClickListener{
    private static final int LLAVEABIERTA_X_MINUTO = 10;
    private static final int REGADERA_X_MINUTO = 20;
    private static final int LAVARDIENTES_SINCERRARLLAVE = 20;
    private static final int INODORO_X_USO = 20;
    private static final int LAVARPLATOS_X_MINUTO = 10;
    private static final int GOTERA_X_DIA = 150;
    private static final int GASTO_X_LAVADORA = 200;
    private static final int LAVAR_CARRO_MANGUERA = 500;
    private static final int MANGERA_REGANDO_X_MINUTO = 30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistica);

        TextView estadistica = (TextView)findViewById(R.id.Txt_tiempoCalculado);
        SharedPreferences misPreferencias = getSharedPreferences("infoUsuario", Context.MODE_PRIVATE);
        String sNumeroHabitantes = misPreferencias.getString("numeroHabitantes", "0");
        String sLitrosAlmacenados = misPreferencias.getString("litrosAlmacenados","0");
        String sCantidadCarros = misPreferencias.getString("cantidadCarros","0");

        int iNumeroHabitantes = Integer.parseInt(sNumeroHabitantes);
        int iLitrosAlmacenados = Integer.parseInt(sLitrosAlmacenados);
        int iCantidadCarros = Integer.parseInt(sCantidadCarros);

        Log.i("Numero Habitantes: ", "" + iNumeroHabitantes);
        Log.i("Litros Almacenados: ", ""+iLitrosAlmacenados);
        Log.i("Cantidad Carros: ", ""+iCantidadCarros);

        int gastoXDia = 0;
        gastoXDia+= llaveAbierta(iNumeroHabitantes, 10);
        gastoXDia+= regaderaAbierta(iNumeroHabitantes, 10);
        gastoXDia+= lavarDientes(iNumeroHabitantes, 2);
        gastoXDia+= inodoroUso(iNumeroHabitantes, 5);
        gastoXDia+= lavarPlatos(10);
        gastoXDia+= gastoGotera(iNumeroHabitantes, 0);
        gastoXDia+= gastoLavadora(iNumeroHabitantes, 1);
        gastoXDia+= lavarCarro(iNumeroHabitantes, iCantidadCarros);
        gastoXDia+= mangeraRegando(iNumeroHabitantes, 10);

        int diasConAgua = iLitrosAlmacenados / gastoXDia;
        estadistica.setText("Dias aproximados con agua: "+diasConAgua+" dias.");

        Button Btn_volverCalcular = (Button)findViewById(R.id.Btn_volverCalcular);
        Btn_volverCalcular.setOnClickListener(this);

        Button Btn_BorrarPreferences = (Button)findViewById(R.id.Btn_BorrarPreferences);
        Btn_BorrarPreferences.setOnClickListener(this);
    }
    private int llaveAbierta(int numeroHabitantes, int minutosXPersona){
        return LLAVEABIERTA_X_MINUTO * numeroHabitantes * minutosXPersona;
    }
    private int regaderaAbierta(int numeroHabitantes, int minutosXPersona){
        return REGADERA_X_MINUTO * numeroHabitantes * 10;
    }
    private int lavarDientes(int numeroHabitantes, int vecesXPersona){
        return LAVARDIENTES_SINCERRARLLAVE * numeroHabitantes * vecesXPersona;
    }
    private int inodoroUso(int numeroHabitantes, int vecesXPersona){
        return INODORO_X_USO * numeroHabitantes * vecesXPersona;
    }
    private int lavarPlatos(int minutosLavando){
        return LAVARPLATOS_X_MINUTO * minutosLavando;
    }
    private int gastoLavadora(int numeroHabitantes, int cantidadLavadas){
        return GASTO_X_LAVADORA * numeroHabitantes * cantidadLavadas;
    }
    private int gastoGotera(int numeroHabitantes, int numeroGoteras){
        return GOTERA_X_DIA * numeroGoteras;
    }
    private int lavarCarro(int numeroHabitantes, int cantidadCarros){
        return LAVAR_CARRO_MANGUERA * cantidadCarros;
    }
    private int mangeraRegando(int numeroHabitantes, int minutosRegando){
        return MANGERA_REGANDO_X_MINUTO * minutosRegando;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_estadistica, menu);
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
            case R.id.Btn_volverCalcular:

                startActivity(new Intent(this, AltaUsuario.class));
                this.finish();
                break;

            case R.id.Btn_BorrarPreferences:
                SharedPreferences.Editor editor = getSharedPreferences("infoUsuario", MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();

                startActivity(new Intent(this, MainActivity.class));
                this.finish();
                break;
        }
    }
}
