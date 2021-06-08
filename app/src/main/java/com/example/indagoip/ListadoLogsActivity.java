package com.example.indagoip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.indagoip.modelo.Request;
import com.example.indagoip.modelo.RequestsContract;
import com.example.indagoip.persistencia.DB;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

// esta actividad se encarga simplemente de leer los datos en la base de datos y mostrarselos al usuario
public class ListadoLogsActivity extends AppCompatActivity {

    private static String TAG = ListadoLogsActivity.class.getCanonicalName();

    // listado de peticiones realizadas
    private List<Request> datos = new ArrayList<Request>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_logs);

        // se cofnigura toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Obtenemos los datos gracias al helper de la base de datos
        DB.getInstance().readRequests(this, datos);

        // se crea el adaptador
        AdaptadorRequests adaptador = new AdaptadorRequests(this);

        // se asocia el adaptador a la list view para mostrar los valores
        ListView lstOpciones = (ListView) findViewById(R.id.listViewLogs);
        lstOpciones.setAdapter(adaptador);

        // se configura el click de la petición (solo contra proveedores) para visualizarla en el mapa
        lstOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {

                if (datos.get(arg2).getTipo() == RequestsContract.Request.TIPO_REQUEST_PROVEEDOR) {
                    // se muestra al usuario la opción elegida (de forma temporal en un toast)
                    Toast toast1 = Toast.makeText(getApplicationContext(),
                            "Renderizando url: " + datos.get(arg2).getUrl(), Toast.LENGTH_SHORT);
                    toast1.show();

                    Intent i = new Intent( ListadoLogsActivity.this, VisualizarMapa.class );
                    i.putExtra("url", datos.get(arg2).getUrl());
                    startActivity(i);
                    finish(); // finish and return
                } else {
                    // toast informativos
                    Toast toast1 = Toast.makeText(getApplicationContext(), getString(R.string.aviso_tipo_url_no_renderizable), Toast.LENGTH_SHORT);
                    toast1.show();
                }
            }
        });
    }

    /**
     * Adapter for data views.
     *
     * @author <A HREF="mailto:rmartico@ubu.es">Raúl Marticorena</A>
     * @version 1.0
     *
     */
    private class AdaptadorRequests extends ArrayAdapter {

        /** Context. */
        Activity context;

        /**
         * Constructor.
         *
         * @param context
         *            context
         */
        AdaptadorRequests(Activity context) {
            super(context, R.layout.hosts, datos);
            this.context = context;
        }

        /**
         * Load the views with current data.
         *
         * @param position
         *            position
         * @param convertView
         *            view
         * @param parent
         *            parent
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView;
            ViewHolder holder;

            if (item == null) { // first time
                LayoutInflater inflater = context.getLayoutInflater();
                item = inflater.inflate(R.layout.logs, null);

                holder = new ViewHolder();
                holder.url = (TextView) item.findViewById(R.id.lblUrl);
                holder.fecha = (TextView) item.findViewById(R.id.lblFecha);
                item.setTag(holder);

            } else { // view previously loaded
                holder = (ViewHolder) item.getTag();
            }

            holder.url.setText(datos.get(position).getUrl());
            SimpleDateFormat sdf = new SimpleDateFormat(RequestsContract.Request.DATE_FORMAT);
            holder.fecha.setText(sdf.format(datos.get(position).getFechaHora()));
            return (item);
        }
    }

    /**
     * View holder.
     *
     * @author <A HREF="mailto:rmartico@ubu.es">Raúl Marticorena</A>
     * @version 1.0
     */
    static class ViewHolder {
        TextView url;
        TextView fecha;
    }
}