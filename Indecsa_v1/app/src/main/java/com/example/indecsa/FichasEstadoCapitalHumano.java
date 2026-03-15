package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FichasEstadoCapitalHumano extends Fragment {

    public FichasEstadoCapitalHumano() {
        // Required empty public constructor
    }

    // Método para crear instancia (opcional, puedes usar constructor vacío)
    public static FichasEstadoCapitalHumano newInstance() {
        return new FichasEstadoCapitalHumano();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fichas_estado_capital_humano, container, false);

        // Botones para cada estado
        ImageButton btnHidalgo = view.findViewById(R.id.btnImagen1);
        ImageButton btnCDMX = view.findViewById(R.id.btnImagen2);
        ImageButton btnPuebla = view.findViewById(R.id.btnImagen3);

        btnHidalgo.setOnClickListener(v -> navegarAEspecialidad("Hidalgo"));
        btnCDMX.setOnClickListener(v -> navegarAEspecialidad("CDMX"));
        btnPuebla.setOnClickListener(v -> navegarAEspecialidad("Puebla"));

        return view;
    }

    private void navegarAEspecialidad(String estado) {
        // Crear fragmento de Especialidad y pasar el estado
        Fragment fragment = FichasEspecialidadCapitalHumano.newInstance(estado);

        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.contenedorfragmentos, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}