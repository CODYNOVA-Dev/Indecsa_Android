package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class Editfichaesp extends Fragment {

    private String estadoSeleccionado;

    public Editfichaesp() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editfichaesp, container, false);

        // Recibir estado del fragmento anterior
        if (getArguments() != null) {
            estadoSeleccionado = getArguments().getString("estado");
        }

        Button btnObra = view.findViewById(R.id.btnObra);
        Button btnRemodelacion = view.findViewById(R.id.btnRemodelacion);
        Button btnVentaMobiliario = view.findViewById(R.id.btnVentaMobiliario);
        Button btnInstalacionMobiliario = view.findViewById(R.id.btnInstalacionMobiliario);

        View.OnClickListener listener = v -> {
            String especialidadSeleccionada = ((Button) v).getText().toString();

            // Crear fragmento FichaObra y pasar estado + especialidad
            FichaObra ficha = new FichaObra();
            Bundle args = new Bundle();
            args.putString("estado", estadoSeleccionado);
            args.putString("especialidad", especialidadSeleccionada);
            ficha.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedorfragmentos, ficha)
                    .addToBackStack(null)
                    .commit();
        };

        btnObra.setOnClickListener(listener);
        btnRemodelacion.setOnClickListener(listener);
        btnVentaMobiliario.setOnClickListener(listener);
        btnInstalacionMobiliario.setOnClickListener(listener);

        return view;
    }
}
