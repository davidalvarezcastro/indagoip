package com.example.indagoip;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.example.indagoip.modelo.Request;
import com.example.indagoip.modelo.RequestsContract;
import com.example.indagoip.persistencia.DB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class VisualizarMapa extends AppCompatActivity implements SensorEventListener {

    // iframe para visualizar una url
    private WebView webView;

    // proveedores de servicios
    private static String googleMapsService = "https://www.google.com/maps/search/?api=1&query=%s,%s";
    private static String openStreetService = "http://www.openstreetmap.org/index.html?mlat=%s&mlon=%s&zoom=10";

    // se almacena la latitud y longitud en caso de tener que cambiar de proveedor de servicios
    private String latitude = "";
    private String longitude = "";

    // se almacena el proveedor utilizado
    private String proveedor;
    private ArrayList<String> proveedores; // se guardan los proveedores disponibles

    // preferencias
    private SharedPreferences sharedPref;

    private static final String TAG = VisualizarMapa.class
            .getName();

    // variables para gestionar el acelerómetro (sesión 05)
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private boolean availableAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_TIMEOUT = 200;
    private static final float SHAKE_THRESHOLD = 5.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_mapa);

        // configuración toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // se inicializa gestión de preferencias
        sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);

        // se obtiene el proveedor predefinido y se crea el listado de proveedores
        proveedor = sharedPref.getString(getString(R.string.prefs_proveedor), getString(R.string.proveedor_google_key));
        proveedores = new ArrayList<String>();
        Collections.addAll(proveedores, getResources().getStringArray(R.array.entryValues_proveedor)); //  se obtiene el listado de los resources

        // referencia al indicador informativo de agitar
        FloatingActionButton agitar = findViewById(R.id.agitar);

        /**
         * esta actividad permite pasarle directamente una url o  las latitudes y longitudes y seleccionar el proveedor correspondiente
         */
        if (this.getIntent().getExtras().getString("url") != null && !this.getIntent().getExtras().getString("url").isEmpty()) {
            /**
             * NOTA: en esta opción no se configura el acelerómetro porque se trata de una
             * previsualización, no se permite el cambio de proveedor de mapas. Simplemente se
             * renderiza la url pasada
             */
            // configuración del webview (url obtenida de los extras del intent)
            setUpebView(this.getIntent().getExtras().getString("url"));

            // se oculta el indicador
            agitar.hide();
        } else {
            // se gestiona los parámetros recibidos en el Intent
            latitude = this.getIntent().getExtras().getString("latitude");
            longitude = this.getIntent().getExtras().getString("longitude");

            // configuración del webview (se genera la url de visualización)
            setUpebView(formatMapProvider());

            // configuración del acelerómetro
            setUpAccelerometer();

            // se configura la notificación
            agitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, getString(R.string.aviso_agitar_smartphone), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    protected void onResume() {
        super.onResume();
        if (availableAccelerometer) {
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void onPause() {
        super.onPause();
        if (availableAccelerometer) {
            senSensorManager.unregisterListener(this);
        }
    }

    /**
     * Formatear la url de visualización (tiene en cuenta el proveedor de servicios)
     */
    private String formatMapProvider() {
        // se genera la url teniendo en cuenta el proveedor
        if (proveedor.equals(getString(R.string.proveedor_google_key))) {
            return String.format(googleMapsService, latitude, longitude);
        } else if (proveedor.equals(getString(R.string.proveedor_openstreet_key))) {
            return String.format(openStreetService, latitude, longitude);
        } else {
            Utils.showToast(getApplicationContext(), String.format(getString(R.string.aviso_proveedor_mapas), proveedor));
            this.finish(); // se finaliza la actividad porque el proveedor recibido es desconocido para esta actividad
        }

        return null;
    }

    /**
     * Función para configurar el acerómetro
     */
    private void setUpAccelerometer() {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (senAccelerometer != null){
            availableAccelerometer = true;
        }

        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Función para configurar el webview que carga la url pasada
     *
     * @param url
     */
    private void setUpebView(String url) {
        // se almacenan en los logs (la función comprueba si está activo o no)
        storeLog(url);

        // se obtiene la referencia a los elementos
        webView = (WebView) findViewById(R.id.webview);
        // configuración del webview para mostrar contenido en la misma app
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        Log.v(TAG, "cargando url " + url);
        // se muestra el contenido en el webview
        webView.loadUrl(url);
    }

    /**
     * Función para cambiar de proveedor de servicios
     */
    private void changeMapProvider() {
        Log.v(TAG, "[changeMapProvider] cambiando de proveedor de mapas");

        int actualIndex = proveedores.indexOf(proveedor.trim());
        int nextIndex = (actualIndex+1) %  proveedores.size();

        // se obtiene el siguiente provedor de la lista
        proveedor = proveedores.get(nextIndex);

        Utils.showToast(getApplicationContext(), String.format(getString(R.string.aviso_cambio_proveedor), proveedor));
        setUpebView(formatMapProvider());
    }


    /**
     * Función para gestionar las acciones a llevar a cabo cuando se agita el dispositivo
     */
    private void handleShake() {
        changeMapProvider();
    }

    /**
     * Permite almacenar los logs indicando el host de la búsqueda
     *
     * FIXME: se duplica debido a la inicialización de sharedPref y la gestión de los logs
     *
     * @param host
     */
    private void storeLog(String host) {
        // se obtienen los valores de la preferencia de logs
        Boolean logs = sharedPref.getBoolean(getString(R.string.prefs_guardar_logs), false);

        Log.v(TAG, "log: " + String.valueOf(logs));

        if (logs) {
            Log.v(TAG, "almacenando en los logs el host " + host);
            // se inserta un nuevo registro de petición
            DB.getInstance().insertRequest(this, new Request(RequestsContract.Request.TIPO_REQUEST_PROVEEDOR, new Date(), host));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > SHAKE_TIMEOUT) {
                Log.v(TAG, "[onSensorChanged] curTime: " + String.valueOf(curTime));
                Log.v(TAG, "[onSensorChanged] lastUpdate: " + String.valueOf(lastUpdate));
                Log.v(TAG, "[onSensorChanged] curTime - lastUpdate: " + String.valueOf(curTime - lastUpdate));
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                double speed = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                Log.v(TAG, "[onSensorChanged] speed: " + String.valueOf(speed));

                if (speed > SHAKE_THRESHOLD) {
                    handleShake();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this sample
    }
}