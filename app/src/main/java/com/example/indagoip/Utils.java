package com.example.indagoip;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.indagoip.modelo.Request;
import com.example.indagoip.persistencia.DB;

import java.util.Date;

/**
 * Se crea esta clase para no duplicar código innecesario entre clases.
 *  - Mostrar los toast de información al usuario
 */
public final class Utils {

    /**
     * Inicializa un {@link Toast} y muestra por pantalla el mensaje recibido
     *
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        Toast toast1 = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast1.show();
    }
}
