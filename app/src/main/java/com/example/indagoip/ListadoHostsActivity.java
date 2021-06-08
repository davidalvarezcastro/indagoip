package com.example.indagoip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListadoHostsActivity extends AppCompatActivity {

    private static final String TAG = ListadoHostsActivity.class
            .getName();

    /** Lista de hosts. */
    private List<String> datos = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_hosts);

        // se configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        datos = (ArrayList<String>) getIntent().getSerializableExtra("hosts");;

        AdaptadorHosts adaptador = new AdaptadorHosts(this);

        ListView lstOpciones = (ListView) findViewById(R.id.listViewHosts);
        lstOpciones.setAdapter(adaptador);

        // se configura el click sobre un host para visualizar las coordenadas
        lstOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // se muestra al usuario la opción elegida (de forma temporal en un toast)
                Toast toast1 = Toast.makeText(getApplicationContext(),
                        "Seleccionado: " + datos.get(arg2), Toast.LENGTH_SHORT);
                toast1.show();

                Intent i = new Intent( ListadoHostsActivity.this, ListadoHostsActivity.class );
                i.putExtra("host", datos.get(arg2));
                // se especifica el resultado (tipo de resultado OK)
                setResult( Activity.RESULT_OK, i );
                finish(); // finish and return

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
    private class AdaptadorHosts extends ArrayAdapter {

        /** Context. */
        Activity context;

        /**
         * Constructor.
         *
         * @param context
         *            context
         */
        AdaptadorHosts(Activity context) {
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
                item = inflater.inflate(R.layout.hosts, null);

                holder = new ViewHolder();
                holder.url = (TextView) item.findViewById(R.id.lblUrl);
                item.setTag(holder);
            } else { // view previously loaded
                holder = (ViewHolder) item.getTag();
            }

            holder.url.setText(datos.get(position));
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
    }
}