package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;

public class TrabajadoresEstadoCapitalHumano extends Fragment {

    public TrabajadoresEstadoCapitalHumano() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trabajadores_estado_capital_humano, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configurarBotonesEstado();
    }

    private void configurarBotonesEstado() {
        View view = getView();
        if (view == null) return;

        view.findViewById(R.id.btnImagen1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegarAEspecialidad("Hidalgo");
            }
        });

        view.findViewById(R.id.btnImagen2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegarAEspecialidad("CDMX");
            }
        });

        view.findViewById(R.id.btnImagen3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegarAEspecialidad("Puebla");
            }
        });
    }

    private void navegarAEspecialidad(String estadoSeleccionado) {
        Bundle bundle = new Bundle();
        bundle.putString("ESTADO_SELECCIONADO", estadoSeleccionado);

        EspecialidadTrabajadoresCapitalHumano especialidadFragment = new EspecialidadTrabajadoresCapitalHumano();
        especialidadFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorfragmentos, especialidadFragment)
                .addToBackStack(null)
                .commit();
    }
}