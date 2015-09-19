package com.dzt.mywaterapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class listViewHorarios extends ActionBarActivity implements View.OnClickListener {

    private int zonaId = 0;
    private String zonaName = "";
    private ArrayList array_horarios = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_horarios);

        Bundle b = getIntent().getExtras();
        zonaId = b.getInt("zonaId");
        zonaName = b.getString("zonaName");

        TextView title = (TextView) findViewById(R.id.listViewTitle);
        title.setText("Los horarios para la zona "+ zonaName + " son: ");

        callAPI();

        //rellenarArrayList(zonaId);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_view_horarios, menu);
        return true;
    }

    private void rellenarArrayList(ArrayList array_Horarios) {

        horarioAdapter adapter;
// Inicializamos el adapter.
        adapter = new horarioAdapter(this, array_Horarios);
// Asignamos el Adapter al ListView, en este punto hacemos que el
// ListView muestre los datos que queremos.
        ListView listaHorario = (ListView) findViewById(R.id.zonasListView);
        listaHorario.setAdapter(adapter);

    }

    private void callAPI() {
        URLpetition petition = new URLpetition("get horarios by zonaId");
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.ip));
        sb.append("horario_by_zonaId/");
        sb.append(zonaId);
        petition.execute(sb.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.home_view_horarios:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    private class URLpetition extends AsyncTask<String, Void, String> {
        String action;

        public URLpetition(String action) {
            this.action = action;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            Log.d("url = ", params[0]);
            HttpGet get = new HttpGet(params[0]);
            String retorno = "";
            StringBuilder stringBuilder = new StringBuilder();
            try {
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                //InputStream stream = new InputStream(entity.getContent(),"UTF-8");
                InputStream stream = entity.getContent();
                BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String line;
                while ((line = r.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();

            } catch (IOException e) {
                Log.d("Error: ", e.getMessage());
            }
            Log.d("Return text = ", retorno);
            return retorno;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (action) {
                default:
                    break;
                case "get horarios by zonaId":
                    llenarLista(result);

                    break;
            }
        }

    }

    private void llenarLista(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);

            int l = jsonArray.length();
            Log.i("JSONArray.length()", "" + l);

            ArrayList<Horario> array_Horarios = new ArrayList<Horario>();
            for (int i = 0; i < l; i++) {
                // int zonaId = jsonArray.getJSONObject(i).getInt("ZonaId");
                //  String name = jsonArray.getJSONObject(i).getString("Nombre");
                //  String inicioAgua = jsonArray.getJSONObject(i).getJSONObject("ZonaHorarioRelation").getString("FechaInicial");
                //    String finalAgua = jsonArray.getJSONObject(i).getJSONObject("ZonaHorarioRelation").getString("FechaFinal");
                //  String disp = jsonArray.getJSONObject(i).getString("Disponible");
                String fechaInicial = jsonArray.getJSONObject(i).getString("FechaInicial");
                String fechaFinal = jsonArray.getJSONObject(i).getString("FechaFinal");
                Horario horario = new Horario(fechaInicial, fechaFinal);
                array_Horarios.add(horario);

            }

            rellenarArrayList(array_Horarios);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


class horarioAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Horario> datos;


    public horarioAdapter(Context context, ArrayList<Horario> datos) {
        super(context,R.layout.layoutlistviewhorarios,datos);
        // Guardamos los parámetros en variables de clase.
        this.context = context;
        this.datos = datos;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // En primer lugar "inflamos" una nueva vista, que será la que se
        // mostrará en la celda del ListView. Para ello primero creamos el
        // inflater, y después inflamos la vista.
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.layoutlistviewhorarios, null);

        if(position % 2 == 1) {
            item.setBackgroundColor(Color.LTGRAY);
        } else {
            item.setBackgroundColor(Color.WHITE);
        }

        // A partir de la vista, recogeremos los controles que contiene para
        // poder manipularlos.
        // Recogemos el ImageView y le asignamos una foto.
        // ImageView imagen = (ImageView) item.findViewById(R.id.imgAnimal);
        //  imagen.setImageResource(datos.get(position).getDrawableImageID());

        // Recogemos el TextView para mostrar el nombre y establecemos el
        // nombre.
        TextView fechaInicial = (TextView) item.findViewById(R.id.txt_fechaInicial);

        String timezone = datos.get(position).getStringfechaInicial();
        String [] separatedTimezone = timezone.split("T");
        String [] separatedTime = separatedTimezone[1].split("Z");
        fechaInicial.setText(separatedTime[0] + " de " + separatedTimezone[0] + " a ");

        //fechaInicial.setText("fecha Inicial");

        //Date d = new Date(datos.get(position).getStringfechaInicial());
        //Log.i("date ",datos.get(position).getStringfechaInicial());
        //fechaInicial.setText(d.toString());

        // Recogemos el TextView para mostrar el número de celda y lo
        // establecemos.
        TextView fechaFinal = (TextView) item.findViewById(R.id.txt_fechaFinal);

        timezone = datos.get(position).getStringsfechaFinal();
        separatedTimezone = timezone.split("T");
        separatedTime = separatedTimezone[1].split("Z");
        fechaFinal.setText(separatedTime[0] + " de " + separatedTimezone[0]);

        // Devolvemos la vista para que se muestre en el ListView.
        return item;
    }

}



