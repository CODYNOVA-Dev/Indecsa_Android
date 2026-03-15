package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CapitalHumano extends Fragment {

    public CapitalHumano() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capital_humano, container, false);

        // Referencias a los botones
        ImageButton btnProyectos = view.findViewById(R.id.btnproyectos);
        ImageButton btnTrabajadores = view.findViewById(R.id.btnTrabajadores);
        ImageButton btnFichas = view.findViewById(R.id.btnFichas);

        // 1. BOTÓN PROYECTOS
        if (btnProyectos != null) {
            btnProyectos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Verifica que esta clase exista en tu proyecto
                    Fragment fragment = new Fragment_CH_proyecto_estado();
                    navegarAFragmento(fragment);
                }
            });
        }

        // 2. BOTÓN TRABAJADORES
        if (btnTrabajadores != null) {
            btnTrabajadores.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Verifica que esta clase exista en tu proyecto
                    Fragment fragment = new TrabajadoresEstadoCapitalHumano();
                    navegarAFragmento(fragment);
                }
            });
        }

        // 3. BOTÓN FICHAS - ¡PROBLEMA AQUÍ!
        if (btnFichas != null) {
            // Botón Fichas - VUELVE AL FLUJO NORMAL
            btnFichas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Flujo original
                    Fragment fragment = new FichasEstadoCapitalHumano();
                    navegarAFragmento(fragment);
                }
            });
        }

        return view;
    }

    // MÉTODO COMÚN PARA NAVEGACIÓN
    private void navegarAFragmento(Fragment fragment) {
        try {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.contenedorfragmentos, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            // Si hay error, verifica en Logcat
            System.out.println("Error en navegación: " + e.getMessage());
        }
    }
}