package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FichasEspecialidadCapitalHumano extends Fragment {

    private static final String ARG_ESTADO = "estado";
    private String estadoSeleccionado;

    public FichasEspecialidadCapitalHumano() {
        // Required empty public constructor
    }

    public static FichasEspecialidadCapitalHumano newInstance(String estado) {
        FichasEspecialidadCapitalHumano fragment = new FichasEspecialidadCapitalHumano();
        Bundle args = new Bundle();
        args.putString(ARG_ESTADO, estado);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            estadoSeleccionado = getArguments().getString(ARG_ESTADO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fichas_especialidad_capital_humano, container, false);

        // Mostrar el estado seleccionado
        TextView txtTitulo = view.findViewById(R.id.txtTitulo);
        if (estadoSeleccionado != null) {
            txtTitulo.setText("FICHAS POR ESPECIALIDAD - " + estadoSeleccionado);
        }

        // Botones de especialidades
        Button btnObra = view.findViewById(R.id.btnObra);
        Button btnRemodelacion = view.findViewById(R.id.btnRemodelacion);
        Button btnVentaMobiliario = view.findViewById(R.id.btnVentaMobiliario);
        Button btnInstalacionMobiliario = view.findViewById(R.id.btnInstalacionMobiliario);

        btnObra.setOnClickListener(v -> navegarAFichas("Obra"));
        btnRemodelacion.setOnClickListener(v -> navegarAFichas("Remodelacion"));
        btnVentaMobiliario.setOnClickListener(v -> navegarAFichas("Venta Mobiliario"));
        btnInstalacionMobiliario.setOnClickListener(v -> navegarAFichas("Instalacion Mobiliario"));

        return view;
    }

    private void navegarAFichas(String especialidad) {
        // Crear fragmento de Lista de Fichas y pasar estado + especialidad
        Fragment fragment = FichasCapitalHumano.newInstance(estadoSeleccionado, especialidad);

        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.contenedorfragmentos, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}