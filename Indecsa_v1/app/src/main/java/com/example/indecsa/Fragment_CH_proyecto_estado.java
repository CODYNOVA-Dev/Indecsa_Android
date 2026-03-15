package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Fragment_CH_proyecto_estado extends Fragment {

    // Clave para enviar el nombre del estado al fragmento destino
    private static final String KEY_ESTADO = "estado_seleccionado";

    public Fragment_CH_proyecto_estado() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Asume que el nombre del layout es fragment_ch_proyecto_estado.xml
        return inflater.inflate(R.layout.fragment__c_h_proyecto_estado, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Encontrar los botones en el diseño
        ImageButton btnHidalgo = view.findViewById(R.id.btnHidalgo);
        ImageButton btnCDMX = view.findViewById(R.id.btnCDMX);
        ImageButton btnPuebla = view.findViewById(R.id.btnPuebla);

        // 2. Asignar los eventos de clic para navegar, enviando el nombre del estado
        btnHidalgo.setOnClickListener(v -> irAProyectos("Hidalgo"));
        btnCDMX.setOnClickListener(v -> irAProyectos("CDMX"));
        btnPuebla.setOnClickListener(v -> irAProyectos("Puebla"));
    }

    /**
     * Realiza la transición al fragmento de especialidad correspondiente, enviando el estado seleccionado.
     * @param nombreEstado El nombre del estado que se pulsó (ej: "Hidalgo").
     */
    private void irAProyectos(String nombreEstado) {

        // 1. Determinar qué fragmento instanciar basado en el nombre del estado
        Fragment siguienteFragment;

        switch (nombreEstado) {
            case "CDMX":
                siguienteFragment = new c_h_proyecto_especialidad();
                break;
            case "Puebla":
                siguienteFragment = new c_h_proyecto_especialidad();
                break;
            case "Hidalgo":
                siguienteFragment = new c_h_proyecto_especialidad();
                break;
            default:
                // Opcional: Manejar un caso por defecto si el nombre del estado no coincide
                // Por ahora, asumiremos que solo se presionan los 3 botones
                return;
        }

        // 2. Empaquetar el nombre del estado en un Bundle (Esto sigue siendo útil)
        Bundle args = new Bundle();
        args.putString(KEY_ESTADO, nombreEstado);
        siguienteFragment.setArguments(args);

        // 3. Realizar la transacción de fragmentos
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedorfragmentos, siguienteFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }
}