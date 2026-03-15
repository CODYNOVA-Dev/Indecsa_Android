package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class EspecialidadTrabajadoresCapitalHumano extends Fragment {

    private String estadoSeleccionado = "";

    public EspecialidadTrabajadoresCapitalHumano() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_especialidad_trabajadores_capital_humano, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            estadoSeleccionado = getArguments().getString("ESTADO_SELECCIONADO", "");
            actualizarTitulo();
        }

        configurarBotonesEspecialidad();
    }

    private void actualizarTitulo() {
        View view = getView();
        if (view != null) {
            // CAMBIO AQUÍ: textViewTitulo → txtTitulo
            TextView titulo = view.findViewById(R.id.txtTitulo);
            if (titulo != null) {
                titulo.setText("Especialidad - " + estadoSeleccionado);
            }
        }
    }

    private void configurarBotonesEspecialidad() {
        View view = getView();
        if (view == null) return;

        // CAMBIO AQUÍ: Ahora son Buttons, no ImageButtons
        view.findViewById(R.id.btnObra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegarATrabajadores("Obra");
            }
        });

        view.findViewById(R.id.btnRemodelacion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegarATrabajadores("Remodelación");
            }
        });

        view.findViewById(R.id.btnVentaMobiliario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegarATrabajadores("Venta de Mobiliario");
            }
        });

        view.findViewById(R.id.btnInstalacionMobiliario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegarATrabajadores("Instalación de Mobiliario");
            }
        });
    }

    private void navegarATrabajadores(String especialidadSeleccionada) {
        Bundle bundle = new Bundle();
        bundle.putString("ESTADO_SELECCIONADO", estadoSeleccionado);
        bundle.putString("ESPECIALIDAD_SELECCIONADA", especialidadSeleccionada);

        TrabajadoresCapitalHumano trabajadoresFragment = new TrabajadoresCapitalHumano();
        trabajadoresFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorfragmentos, trabajadoresFragment)
                .addToBackStack(null)
                .commit();
    }
}