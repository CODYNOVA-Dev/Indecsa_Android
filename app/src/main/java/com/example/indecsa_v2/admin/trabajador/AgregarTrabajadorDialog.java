package com.example.indecsa_v2.admin.trabajador;

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
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarTrabajadorDialog extends DialogFragment {

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
        return inflater.inflate(R.layout.dialog_agregar_trabajador, container, false);
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
            String nombre       = getText(view, R.id.editNombre);
            String especialidad = getText(view, R.id.editEspecialidad);
            String correo       = getText(view, R.id.editCorreo);
            String telefono     = getText(view, R.id.editTelefono);
            String nss          = getText(view, R.id.editNss);
            String ubicacion    = getText(view, R.id.editUbicacion);
            String experiencia  = getText(view, R.id.editExperiencia);
            String descripcion  = getText(view, R.id.editDescripcion);

            if (nombre.isEmpty()) {
                Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            TrabajadorDto dto = new TrabajadorDto();
            dto.setNombreTrabajador(nombre);
            dto.setEspecialidadTrabajador(especialidad);
            dto.setCorreoTrabajador(correo);
            dto.setTelefonoTrabajador(telefono);
            dto.setNssTrabajador(nss);
            dto.setUbicacionTrabajador(ubicacion);
            dto.setExperiencia(experiencia);
            dto.setDescripcionTrabajador(descripcion);
            dto.setEstadoTrabajador("ACTIVO");
            dto.setCalificacionTrabajador(0);

            RetrofitClient.getApiService().createTrabajador(dto)
                    .enqueue(new Callback<TrabajadorDto>() {
                        @Override
                        public void onResponse(Call<TrabajadorDto> call, Response<TrabajadorDto> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Trabajador agregado", Toast.LENGTH_SHORT).show();
                                if (listener != null) listener.onAgregado();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), "Error al guardar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<TrabajadorDto> call, Throwable t) {
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