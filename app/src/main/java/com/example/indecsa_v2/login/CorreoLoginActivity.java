package com.example.indecsa_v2.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.indecsa_v2.R;

public class CorreoLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correo_login);

        // Arranca siempre con el fragment de correo
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_correo_login, new IngresarCorreoFragment())
                    .commit();
        }
    }

    // Llamado desde IngresarCorreoFragment al presionar "Siguiente"
    public void irAContrasena(String correo) {
        Bundle args = new Bundle();
        args.putString("correo", correo); // pasamos el correo al siguiente fragment

        IngresarContrasenaFragment frag = new IngresarContrasenaFragment();
        frag.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                )
                .replace(R.id.frame_correo_login, frag)
                .addToBackStack(null)
                .commit();
    }
}