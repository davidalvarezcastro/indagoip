package com.example.indagoip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.indagoip.modelo.Request;
import com.example.indagoip.modelo.RequestsContract;
import com.example.indagoip.persistencia.DB;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    // variables para los proveedores de servicio utilizados
    private static String coordenadasService = "https://freegeoip.app/json/";

    // referencia al EditText
    private EditText urlText;

    // request code
    private static final int REQUEST_HOST = 0;

    // expresiones regular
    /**
     * NOTA: se han probado diferentes expresiones regulares, pero ninguna es fiable al 100%
     */
    private static String hostnameRegex = "[a-z0-9]+[\\.]{1}[a-z0-9]+[\\.]{1}[a-z0-9]+[\\.]{1}[a-z0-9]+";
    private static Pattern patternHostname = Pattern.compile(hostnameRegex, Pattern.CASE_INSENSITIVE);
    private static String ipsHostsRegex = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)+([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$";
    private static Pattern patternIpsHosts = Pattern.compile(ipsHostsRegex, Pattern.CASE_INSENSITIVE);

    private static final String TAG = MainActivity.class
            .getName();

    // se alamcena la latitud y longitud en caso de tener que cambiar de proveedor de servicios
    private String latitude = "";
    private String longitude = "";

    // referencias a los elementos de la vista
    private Button btnC1;
    private Button btnC2;

    // listado de los hosts identificados
    private Set<String> hosts;

    // preferencias
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // se premite realizar peticiones en el hilo principal (evitar android.os.NetworkOnMainThreadException)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // configuración toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // se inicializa la gestión de preferencias
        sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);

        // gestión y configuración del edittext
        urlText = (EditText) findViewById(R.id.editURL);

        // se obtiene la referencia a los botones
        btnC1 = findViewById(R.id.buttonCaso1);
        btnC2 = findViewById(R.id.buttonCaso2);

        // gestión de la acción al pulsar el btnC1
        btnC1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "lozalizando por ip (CASO I)");
                // se comprueba el valor escrito por el usuario
                String host = checkHost();
                if (host == null || host.isEmpty()) return;

                // se resetean las variables y se llama a la función de gestión
                resetVariables();
                handleSearchByHost(host);
            }
        });

        // gestión de la acción al pulsar el btnC1
        btnC2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "lozalizando por listado de hosts (CASO II)");
                // se comprueba el valor escrito por el usuario
                String host = checkHost();
                if (host == null || host.isEmpty()) return;

                // se resetea variables y se llama a la función de gestión
                resetVariables();
                handleSearchHostsByHost(host);
            }
        });
    }

    /**
     * Función para resetear variables entre búsqueda y búsqueda
     */
    private void resetVariables() {
        latitude = "";
        longitude = "";
        hosts = new HashSet<>(); // set con los hostnames encontrados (no duplicados)
    }

    /**
     * Función para comprobar si hay conexión en el dispositivo
     */
    private boolean checkNetwork() {
        try {
            // se comprueba la conectividad contra google
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Función para realizar la lectura del EditText y comprobar que sea correcto (ip or hostname)-
     *
     * Devuelve el host en caso de cumplir con las condiciones o un null en caso contrario
     */
    private String checkHost() {
        // se obtiene el valor del campo
        String host = urlText.getText().toString().trim();

        // en caso de vacío, se muestra un mensaje de error
        if (host.isEmpty()) {
            Utils.showToast(getApplicationContext(), getString(R.string.aviso_no_host));
            return null;
        }

        Matcher matcher = patternIpsHosts.matcher(host);
        // en caso de que no se haya especificado un host correcto, se muestra un mensaje de error
        if (!matcher.find()) {
            Utils.showToast(getApplicationContext(), getString(R.string.aviso_formato_host));
            return null;
        }

        return host;
    }

    /**
     * Obtiene información de latitud-longitud de un host/ip dado
     *
     * @param host
     */
    private boolean handleSearchByHost(String host) {
        Log.v(TAG, "handleSearchByHost");

        // primero se comprueba si hay conexión a Internet
        if (!checkNetwork()) {
            Utils.showToast(getApplicationContext(), getString(R.string.aviso_no_conexion));
            return false;
        }

        StringBuffer content;

        try {
            // se llama al servicio de coordenadas geográficas
            URL url = new URL(coordenadasService+host);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            content = new StringBuffer();
            // se recorre el contenido y se almacena en una variable temporal
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // se almacenan en los logs (la función comprueba si está activo o no)
            storeLog(host);
        } catch (Exception error) {
            Log.e(TAG, error.toString());
            Utils.showToast(getApplicationContext(), String.format(getString(R.string.aviso_fallo_coordenadas), coordenadasService));
            return false;
        }

        String output = null;
        try {
            // se parsea el contenido a un JSON
            JSONObject obj = new JSONObject(content.toString());
            // se obtiene las variables que nos interesan
            latitude = obj.getString("latitude");
            longitude = obj.getString("longitude");
        } catch (Throwable t) {
            Log.e(TAG, "Errores parseando el json: " + t.toString());
            Utils.showToast(getApplicationContext(), getString(R.string.aviso_fallo_parsear_json_coordenadas));
            return false;
        }

        try {
            if (!latitude.isEmpty() && !longitude.isEmpty()) {
                openIntentVisualizarMapa();
            }
        } catch (Exception error) {
            Log.e(TAG, error.toString());
            return false;
        }

        return true;
    }

    /**
     * Obtiene información de los hosts contenidos en el HTML de un host dato
     *
     * @param host
     */
    private boolean handleSearchHostsByHost(String host) {
        Log.v(TAG, "handleSearchHostsByHost");

        // primero se comprueba si hay conexión a Internet
        if (!checkNetwork()) {
            Utils.showToast(getApplicationContext(), getString(R.string.aviso_no_conexion));
            return false;
        }

        try {
            // se llama al host pasado por el usuario
            URL url = new URL("https://" + host);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine = "";

            // se recorre el resultado línea a linea
            while ((inputLine = in.readLine()) != null) {
                // se ejecuta el parser obteniendo los hostnames (función group())
                Matcher matcher = patternHostname.matcher(inputLine);
                boolean matchFound = matcher.find();
                if(matchFound) {
                    hosts.add(matcher.group());
                }
            }
            in.close();
        } catch (Exception error) {
            Utils.showToast(getApplicationContext(), getString(R.string.aviso_problemas_buscar_hosts));
            Log.e(TAG, error.toString());
            return false;
        }

        // solo se cambia de actividad en caso de haber identificado hosts
        if (hosts.size() > 0) {
            openIntenListadoHosts();
        } else {
            Utils.showToast(getApplicationContext(), getString(R.string.aviso_no_hosts));
        }

        return true;
    }

    // funciones para gestionar los Intents que gestionan las actividades
    /**
     * Inicia la actividad ({@link VisualizarMapa})
     */
    private void openIntentVisualizarMapa() {
        Intent i = new Intent(MainActivity.this, VisualizarMapa.class);
        // parámetros para indicarle a la actividad qué coordenadas renderizar
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        startActivity(i);
    }

    /**
     * Inicia la actividad ({@link ListadoHostsActivity})
     */
    private void openIntenListadoHosts() {
        Intent i = new Intent(MainActivity.this, ListadoHostsActivity.class);
        // se convierte el Set a un ArrayList y se le pasa a la actividad
        ArrayList<String> datos = new ArrayList<String>(hosts);
        i.putExtra("hosts", datos);
        // se espera como resultado el hosts seleccionado
        startActivityForResult(i, REQUEST_HOST);
    }

    /**
     * Se obtiene el resultado de la subactividad ({@link ListadoHostsActivity})
     *
     * @param requestCode
     *            code sent to the subactivity
     * @param resultCode
     *            result returned
     * @param data
     *            additional data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "onActivityResult");
        if (requestCode == REQUEST_HOST) {
            if (resultCode == Activity.RESULT_OK) {
                handleSearchByHost(data.getExtras().get("host").toString());
            }
        }
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
            DB.getInstance().insertRequest(this, new Request(RequestsContract.Request.TIPO_REQUEST_HOST, new Date(), host));
        }
    }

    /**
     * Añade elementos al menú
     *
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Gestión de las acciones del toolbar
     *
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // gestión de las acciones de los items del menú superior
        if (id == R.id.submenu_settings) {
            try {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            } catch (Exception err) {
                Log.e(TAG, err.toString());
            }
            return true;
        } else if  (id == R.id.submenu_developer) {
            try {
                Intent i = new Intent(MainActivity.this, AcercaDeActivity.class);
                startActivity(i);
            } catch (Exception err) {
                Log.e(TAG, err.toString());
            }
            return true;
        } else if  (id == R.id.submenu_logs) { // se crea este menú (por defecto oculto, necesario compilar de nuevo el proyecto) para comprobar el listado de logs (en una aplicación real no estaría)
            try {
                Intent i = new Intent(MainActivity.this, ListadoLogsActivity.class);
                startActivity(i);
            } catch (Exception err) {
                Log.e(TAG, err.toString());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}