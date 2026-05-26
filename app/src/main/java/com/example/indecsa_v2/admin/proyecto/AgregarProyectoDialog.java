package com.example.indecsa_v2.admin.proyecto;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.Domicilio;
import com.example.indecsa_v2.models.Estado;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.network.CatalogosCache;
import com.example.indecsa_v2.network.DomicilioHelper;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarProyectoDialog extends DialogFragment {

    public interface OnAgregadoListener { void onAgregado(); }
    private OnAgregadoListener listener;
    public void setOnAgregadoListener(OnAgregadoListener l) { this.listener = l; }

    /** Enum del backend: Proyecto.TipoProyecto. Debe coincidir literalmente. */
    private static final String[] TIPOS_PROYECTO = {
            "Construccion", "Remodelacion", "Venta_mobiliaria", "Instalacion_de_mobiliario"
    };

    private List<Estado> estadosCache;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        if (d.getWindow() != null) d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return d;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_agregar_proyecto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            getDialog().setCanceledOnTouchOutside(false);
        }

        Spinner spinnerTipo = view.findViewById(R.id.spinnerTipo);
        ArrayAdapter<String> tipoAdp = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, TIPOS_PROYECTO);
        tipoAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdp);

        Spinner spinnerEstadoGeo = view.findViewById(R.id.spinnerEstadoGeo);
        cargarEstadosEnSpinner(spinnerEstadoGeo);

        AppCompatButton btnGuardar  = view.findViewById(R.id.btnGuardar);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre    = getText(view, R.id.editNombre);
            String cliente   = getText(view, R.id.editCliente);
            String lugar     = getText(view, R.id.editLugar);
            String municipio = getText(view, R.id.editMunicipio);
            String fechaIni  = getText(view, R.id.editFechaIni);
            String fechaFin  = getText(view, R.id.editFechaFin);
            String tipo      = (String) spinnerTipo.getSelectedItem();

            if (nombre.isEmpty() || cliente.isEmpty() || lugar.isEmpty() || municipio.isEmpty()) {
                Toast.makeText(getContext(),
                        "Completa nombre, cliente, calle y municipio",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Estado estadoGeo = obtenerEstadoSeleccionado(spinnerEstadoGeo);
            if (estadoGeo == null) {
                Toast.makeText(getContext(),
                        "No hay estados disponibles. Crea uno en /api/estados primero",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // 1) Crear domicilio inline → 2) crear proyecto referenciándolo.
            DomicilioHelper.crear(lugar, null, null, null, municipio, estadoGeo.getIdEstado(),
                    new DomicilioHelper.Callback() {
                        @Override public void onCreated(@NonNull Domicilio dom) {
                            if (!isAdded()) return;
                            crearProyecto(nombre, cliente, tipo, fechaIni, fechaFin, dom);
                        }
                        @Override public void onError(@NonNull String msg) {
                            if (!isAdded()) return;
                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void crearProyecto(String nombre, String cliente, String tipo,
                               String fechaIni, String fechaFin, Domicilio dom) {
        ProyectoDto dto = new ProyectoDto();
        dto.setNombreProyecto(nombre);
        dto.setCliente(cliente);
        dto.setTipoProyecto(tipo);
        dto.setDomicilio(dom);
        if (!fechaIni.isEmpty()) dto.setFechaEstimadaInicio(fechaIni);
        if (!fechaFin.isEmpty()) dto.setFechaEstimadaFin(fechaFin);
        dto.setEstatusProyecto("PLANEACION");

        RetrofitClient.getApiService().createProyecto(dto)
                .enqueue(new Callback<ProyectoDto>() {
                    @Override
                    public void onResponse(@NonNull Call<ProyectoDto> call, @NonNull Response<ProyectoDto> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Proyecto agregado", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onAgregado();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al guardar (código " + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ProyectoDto> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarEstadosEnSpinner(Spinner spinner) {
        CatalogosCache.getEstados(items -> {
            estadosCache = items;
            String[] labels = new String[items.size()];
            for (int i = 0; i < items.size(); i++) {
                Estado e = items.get(i);
                labels[i] = e.getNombreEst() != null ? e.getNombreEst() : ("Estado #" + e.getIdEstado());
            }
            ArrayAdapter<String> adp = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, labels);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adp);
        });
    }

    @Nullable
    private Estado obtenerEstadoSeleccionado(Spinner spinner) {
        if (estadosCache == null || estadosCache.isEmpty()) return null;
        int pos = spinner.getSelectedItemPosition();
        if (pos < 0 || pos >= estadosCache.size()) return null;
        return estadosCache.get(pos);
    }

    private String getText(View root, int id) {
        EditText et = root.findViewById(id);
        return et != null ? et.getText().toString().trim() : "";
    }
}
