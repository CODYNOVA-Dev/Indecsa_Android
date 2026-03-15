package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class AsignarEstadoAdmin extends Fragment {

    public AsignarEstadoAdmin() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_asignaredoadmin, container, false);

        configurarBotonesEstados(view);

        return view;
    }

    private void configurarBotonesEstados(View view) {

        // HIDALGO
        view.findViewById(R.id.btnImagen1).setOnClickListener(v -> {
            abrirEspecialidades("Hidalgo");
        });

        // CDMX
        view.findViewById(R.id.btnImagen2).setOnClickListener(v -> {
            abrirEspecialidades("CDMX");
        });

        // PUEBLA
        view.findViewById(R.id.btnImagen3).setOnClickListener(v -> {
            abrirEspecialidades("Puebla");
        });
    }

    private void abrirEspecialidades(String estado) {

        AgregarFicha fragment = new AgregarFicha();

        Bundle args = new Bundle();
        args.putString("estadoSeleccionado", estado);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorfragmentos, fragment)
                .addToBackStack(null)
                .commit();
    }
}
