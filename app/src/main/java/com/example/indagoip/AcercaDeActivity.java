package com.example.indagoip;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class AcercaDeActivity extends AppCompatActivity {

    // referencias a los elementos de la vista
    private ImageButton btnEmail;
    private ImageButton btnFacebook;
    private ImageButton btnGithub;

    private static final String TAG = MainActivity.class
            .getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);

        // configuración toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // se obtiene la referencia a los botones
        btnEmail = findViewById(R.id.emailButton);
        btnFacebook = findViewById(R.id.facebookButton);
        btnGithub = findViewById(R.id.githubButton);

        // configuración de las acciones de los botones
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.v(TAG, "enviando email ...");
                    sendEmail(getString(R.string.email_desarrollador));
                } catch (Exception error) {
                    Log.e(TAG, error.toString());
                }
            }
        });

        btnGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.v(TAG, "abriendo github ...");
                    goToUrl(getString(R.string.github_desarrollador));
                } catch (Exception error) {
                    Log.e(TAG, error.toString());
                }
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.v(TAG, "abriendo facebook ...");
                    goToUrl(getString(R.string.facebook_desarrollador));
                } catch (Exception error) {
                    Log.e(TAG, error.toString());
                }
            }
        });

    }

    // función para abrir una url determinada en el navegador de Android
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent i = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(i);
    }

    // función para abrir la app del email y enviar un email
    private void sendEmail (String email) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:"+ email);
        intent.setData(data);
        startActivity(intent);
    }
}