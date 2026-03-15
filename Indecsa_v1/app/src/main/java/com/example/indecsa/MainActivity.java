package com.example.indecsa;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Este debe ser tu segundo XML con el FrameLayout

        // Cargar el fragmento de login automáticamente
        cargarLoginFragment();
    }

    private void cargarLoginFragment() {
        // Crear una instancia del fragmento de login
        Inicio_sesion loginFragment = new Inicio_sesion();



        // Iniciar la transacción del fragmento
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedorfragmentos, loginFragment);
        transaction.addToBackStack(null); // Opcional: para poder volver atrás
        transaction.commit();
    }
}