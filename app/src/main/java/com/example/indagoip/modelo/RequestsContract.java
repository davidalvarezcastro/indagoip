package com.example.indagoip.modelo;

import android.provider.BaseColumns;

public final class RequestsContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private RequestsContract() {}

    /* Inner class that defines the table contents */
    public static class Request implements BaseColumns {
        public static final String TABLE_NAME = "REQUESTS";

        public static final String COLUMN_NAME_ID = "idRequest";
        public static final String COLUMN_NAME_FECHA = "fechaHora";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_TIPO = "tipo";

        public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
        public static final Integer TIPO_REQUEST_HOST = 1; // indica el host de búsqueda en la aplicación
        public static final Integer TIPO_REQUEST_PROVEEDOR = 2; // indica la petición realizada a los proveedores de servicio
    }
}