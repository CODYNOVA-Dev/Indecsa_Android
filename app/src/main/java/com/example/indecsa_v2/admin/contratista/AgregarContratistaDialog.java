package com.example.indecsa_v2.admin.contratista;

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
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.Estado;
import com.example.indecsa_v2.network.CatalogosCache;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarContratistaDialog extends DialogFragment {

    public interface OnAgregadoListener { void onAgregado(); }
    private OnAgregadoListener listener;
    public void setOnAgregadoListener(OnAgregadoListener l) { this.listener = l; }

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
        return inflater.inflate(R.layout.dialog_agregar_contratista, container, false);
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

        Spinner spinnerEstadoOperacion = view.findViewById(R.id.spinnerEstadoOperacion);
        cargarEstadosEnSpinner(spinnerEstadoOperacion);

        AppCompatButton btnGuardar  = view.findViewById(R.id.btnGuardar);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre   = getText(view, R.id.editNombre);
            String curp     = getText(view, R.id.editCurp);
            String rfc      = getText(view, R.id.editRfc);
            String correo   = getText(view, R.id.editCorreo);
            String telefono = getText(view, R.id.editTelefono);
            String exp      = getText(view, R.id.editExperiencia);
            String desc     = getText(view, R.id.editDescripcion);

            if (nombre.isEmpty() || curp.isEmpty() || rfc.isEmpty()
                    || correo.isEmpty() || telefono.isEmpty() || desc.isEmpty()) {
                Toast.makeText(getContext(),
                        "Completa nombre, CURP, RFC, correo, teléfono y descripción",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Estado estadoOp = obtenerEstadoSeleccionado(spinnerEstadoOperacion);
            if (estadoOp == null) {
                Toast.makeText(getContext(),
                        "No hay estados disponibles. Crea uno en /api/estados primero",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Contratista c = new Contratista();
            c.setNombreContratista(nombre);
            c.setCurp(curp);
            c.setRfcContratista(rfc);
            c.setCorreoContratista(correo);
            c.setTelefonoContratista(telefono);
            c.setExperiencia(exp);
            c.setDescripcionContratista(desc);
            c.setEstadoContratista("ACTIVO");
            c.setCalificacionContratista(0);
            c.setEstadoOperacion(estadoOp);

            RetrofitClient.getApiService().createContratista(c)
                    .enqueue(new Callback<Contratista>() {
                        @Override
                        public void onResponse(@NonNull Call<Contratista> call, @NonNull Response<Contratista> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Contratista agregado", Toast.LENGTH_SHORT).show();
                                if (listener != null) listener.onAgregado();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(),
                                        ApiErrorMessages.forCode(response.code()),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Contratista> call, @NonNull Throwable t) {
                            if (!isAdded()) return;
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forThrowable(t),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
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
