package com.example.indecsa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class c_h_proyecto_especialidad extends Fragment {

    private String estadoSeleccionado = "";
    private static final String TAG = "EspecialidadFragment";

    public c_h_proyecto_especialidad() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView - Inflando layout");
        return inflater.inflate(R.layout.fragment_c_h_proyecto_especialidad, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated - Iniciando");

        // ⭐ CAMBIO AQUÍ: Ahora recibe "ESTADO_SELECCIONADO" en lugar de "PROYECTO_SELECCIONADO"
        if (getArguments() != null) {
            estadoSeleccionado = getArguments().getString("estado_seleccionado", "");  // CAMBIADO a minúsculas
            Log.d(TAG, "Estado recibido: " + estadoSeleccionado);
            actualizarTitulo();
        }

        configurarBotonesEspecialidad();
    }

    private void actualizarTitulo() {
        View view = getView();
        if (view != null) {
            TextView titulo = view.findViewById(R.id.txtTitulo);
            if (titulo != null) {
                titulo.setText("Especialidad - " + estadoSeleccionado);
                Log.d(TAG, "Título actualizado: Especialidad - " + estadoSeleccionado);
            } else {
                Log.e(TAG, "❌ No se encontró R.id.txtTitulo en el layout");
            }
        }
    }

    private void configurarBotonesEspecialidad() {
        View view = getView();
        if (view == null) {
            Log.e(TAG, "❌ View es null en configurarBotonesEspecialidad");
            return;
        }

        // CORREGIR LOS IDs PARA QUE COINCIDAN CON EL XML

        // Botón Obra - XML: btnProObra
        Button btnObra = view.findViewById(R.id.btnProObra);  // CAMBIADO
        if (btnObra != null) {
            btnObra.setOnClickListener(v -> navegarATodaFichas("Obra"));
            Log.d(TAG, "✅ Botón Obra configurado");
        } else {
            Log.e(TAG, "❌ No se encontró R.id.btnProObra");
        }

        // Botón Remodelación - XML: btnProRemodelacion
        Button btnRemodelacion = view.findViewById(R.id.btnProRemodelacion);  // CAMBIADO
        if (btnRemodelacion != null) {
            btnRemodelacion.setOnClickListener(v -> navegarATodaFichas("Remodelación"));
            Log.d(TAG, "✅ Botón Remodelación configurado");
        } else {
            Log.e(TAG, "❌ No se encontró R.id.btnProRemodelacion");
        }

        // Botón Venta de Mobiliario - XML: btnProVenta
        Button btnVentaMobiliario = view.findViewById(R.id.btnProVenta);  // CAMBIADO
        if (btnVentaMobiliario != null) {
            btnVentaMobiliario.setOnClickListener(v -> navegarATodaFichas("Venta de Mobiliario"));
            Log.d(TAG, "✅ Botón Venta de Mobiliario configurado");
        } else {
            Log.e(TAG, "❌ No se encontró R.id.btnProVenta");
        }

        // Botón Instalación de Mobiliario - XML: btnProInstalacion
        Button btnInstalacionMobiliario = view.findViewById(R.id.btnProInstalacion);  // CAMBIADO
        if (btnInstalacionMobiliario != null) {
            btnInstalacionMobiliario.setOnClickListener(v -> navegarATodaFichas("Instalación de Mobiliario"));
            Log.d(TAG, "✅ Botón Instalación de Mobiliario configurado");
        } else {
            Log.e(TAG, "❌ No se encontró R.id.btnProInstalacion");
        }
    }

    private void navegarATodaFichas(String especialidadSeleccionada) {
        Log.d(TAG, "Navegando a fichas - Estado: " + estadoSeleccionado + ", Especialidad: " + especialidadSeleccionada);

        Bundle bundle = new Bundle();
        bundle.putString("ESTADO_SELECCIONADO", estadoSeleccionado);  // ⭐ Ahora usa estadoSeleccionado
        bundle.putString("ESPECIALIDAD_SELECCIONADA", especialidadSeleccionada);

        c_h_proyecto_toda_fichas todaFichasFragment = new c_h_proyecto_toda_fichas();
        todaFichasFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorfragmentos, todaFichasFragment)
                .addToBackStack(null)
                .commit();

        Log.d(TAG, "✅ Transacción de fragmento iniciada");
    }
}