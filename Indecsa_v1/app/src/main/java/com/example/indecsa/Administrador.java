package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class Administrador extends Fragment {

    public Administrador() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del menú principal
        View view = inflater.inflate(R.layout.fragment_administrador, container, false);

        // Configurar los botones
        configurarBotonesMenu(view);

        return view;
    }

    private void configurarBotonesMenu(View view) {
        // Botón "Agregar" (btnImagen1) - Va a AgregarFicha
        view.findViewById(R.id.btnImagen1).setOnClickListener(v -> {
            navegarAAgregarFicha();
        });

        // Botón "Editar" (btnImagen2) - Va a Editfichaesp
        view.findViewById(R.id.btnImagen2).setOnClickListener(v -> {
            navegarAEditarFicha();
        });
    }

    private void navegarAAgregarFicha() {
        // Crear instancia del fragmento AgregarFicha
        AsignarEstadoAdmin agregarFichaFragment = new AsignarEstadoAdmin();

        // Reemplazar el fragment actual
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorfragmentos, agregarFichaFragment)
                .addToBackStack(null) // Para poder volver al menú principal
                .commit();
    }

    private void navegarAEditarFicha() {
        // Crear instancia del fragmento Editfichaesp
        EditarEstadoAdmin editarFichaFragment = new EditarEstadoAdmin();

        // Reemplazar el fragment actual
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorfragmentos, editarFichaFragment)
                .addToBackStack(null) // Para poder volver al menú principal
                .commit();
    }
}