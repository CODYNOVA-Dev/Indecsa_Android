package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class EditarEstadoAdmin extends Fragment {

    public EditarEstadoAdmin() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asignaredoadmin, container, false);

        view.findViewById(R.id.btnImagen1).setOnClickListener(v -> navegarAFichaEspecialidad("HIDALGO"));
        view.findViewById(R.id.btnImagen2).setOnClickListener(v -> navegarAFichaEspecialidad("CDMX"));
        view.findViewById(R.id.btnImagen3).setOnClickListener(v -> navegarAFichaEspecialidad("PUEBLA"));

        return view;
    }

    private void navegarAFichaEspecialidad(String estado) {
        Editfichaesp ficha = new Editfichaesp();
        Bundle args = new Bundle();
        args.putString("estado", estado); // Pasamos el estado
        ficha.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorfragmentos, ficha)
                .addToBackStack(null)
                .commit();
    }
}
