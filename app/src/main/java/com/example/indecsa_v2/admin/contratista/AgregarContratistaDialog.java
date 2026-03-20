package com.example.indecsa_v2.admin.contratista;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarContratistaDialog extends DialogFragment {

    public interface OnAgregadoListener { void onAgregado(); }
    private OnAgregadoListener listener;
    public void setOnAgregadoListener(OnAgregadoListener l) { this.listener = l; }

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

        AppCompatButton btnGuardar  = view.findViewById(R.id.btnGuardar);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre    = getText(view, R.id.editNombre);
            String rfc       = getText(view, R.id.editRfc);
            String correo    = getText(view, R.id.editCorreo);
            String telefono  = getText(view, R.id.editTelefono);
            String ubicacion = getText(view, R.id.editUbicacion);
            String exp       = getText(view, R.id.editExperiencia);
            String desc      = getText(view, R.id.editDescripcion);

            if (nombre.isEmpty() || rfc.isEmpty()) {
                Toast.makeText(getContext(), "Nombre y RFC son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            Contratista c = new Contratista();
            c.setNombreContratista(nombre);
            c.setRfcContratista(rfc);
            c.setCorreoContratista(correo);
            c.setTelefonoContratista(telefono);
            c.setUbicacionContratista(ubicacion);
            c.setExperiencia(exp);
            c.setDescripcionContratista(desc);
            c.setEstadoContratista("ACTIVO");

            RetrofitClient.getApiService().createContratista(c)
                    .enqueue(new Callback<Contratista>() {
                        @Override
                        public void onResponse(Call<Contratista> call, Response<Contratista> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Contratista agregado", Toast.LENGTH_SHORT).show();
                                if (listener != null) listener.onAgregado();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), "Error al guardar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Contratista> call, Throwable t) {
                            Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private String getText(View root, int id) {
        EditText et = root.findViewById(id);
        return et != null ? et.getText().toString().trim() : "";
    }
}