package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class AgregarFicha extends Fragment {

    private String estado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agregar_ficha, container, false);

        if (getArguments() != null) {
            estado = getArguments().getString("estadoSeleccionado");
        }

        configurarBotonesEspecialidades(view);

        return view;
    }

    private void configurarBotonesEspecialidades(View view) {

        view.findViewById(R.id.btnObra).setOnClickListener(v -> {
            navegarAFichaFinal("OBRA");
        });

        view.findViewById(R.id.btnRemodelacion).setOnClickListener(v -> {
            navegarAFichaFinal("REMODELACION");
        });

        view.findViewById(R.id.btnVentaMobiliario).setOnClickListener(v -> {
            navegarAFichaFinal("VENTA_MOBILIARIO");
        });

        view.findViewById(R.id.btnInstalacionMobiliario).setOnClickListener(v -> {
            navegarAFichaFinal("INSTALACION_MOBILIARIO");
        });
    }

    private void navegarAFichaFinal(String especialidad) {

        fichaagrega fragment = new fichaagrega();

        Bundle args = new Bundle();
        args.putString("estadoSeleccionado", estado);
        args.putString("especialidadSeleccionada", especialidad);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorfragmentos, fragment)
                .addToBackStack(null)
                .commit();
    }
}
