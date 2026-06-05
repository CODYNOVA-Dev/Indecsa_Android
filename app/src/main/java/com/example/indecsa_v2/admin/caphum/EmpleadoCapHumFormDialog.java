package com.example.indecsa_v2.admin.caphum;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.EmpleadoDto;
import com.example.indecsa_v2.models.Rol;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Alta y edición de usuarios de Capital Humano (equivalente a las pantallas web
 * AltaCap.html y EditCap.html).
 *
 * El rol queda fijado a CAPITAL_HUMANO (se resuelve su idRol desde /roles).
 * En modo edición la contraseña es opcional: si se deja vacía, no se modifica.
 */
public class EmpleadoCapHumFormDialog extends DialogFragment {

    public interface OnGuardadoListener { void onGuardado(); }

    private static final String ARG_MODO   = "modo";   // "crear" | "editar"
    private static final String ARG_ID     = "id";
    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_CORREO = "correo";
    private static final String ARG_CURP   = "curp";
    private static final String ARG_FOTO   = "foto";

    private EditText editNombre, editCorreo, editCurp, editContrasena, editFoto;
    private AppCompatButton btnGuardar, btnCancelar;
    private TextView tvTitulo;

    private boolean esEdicion = false;
    private Integer idEmpleado = null;
    private Integer idRolCapHum = null;

    private OnGuardadoListener listener;

    public static EmpleadoCapHumFormDialog paraCrear() {
        EmpleadoCapHumFormDialog d = new EmpleadoCapHumFormDialog();
        Bundle a = new Bundle();
        a.putString(ARG_MODO, "crear");
        d.setArguments(a);
        return d;
    }

    public static EmpleadoCapHumFormDialog paraEditar(EmpleadoDto e) {
        EmpleadoCapHumFormDialog d = new EmpleadoCapHumFormDialog();
        Bundle a = new Bundle();
        a.putString(ARG_MODO, "editar");
        if (e.getIdEmpleado() != null) a.putInt(ARG_ID, e.getIdEmpleado());
        a.putString(ARG_NOMBRE, e.getNombreEmpleado());
        a.putString(ARG_CORREO, e.getCorreoEmpleado());
        a.putString(ARG_CURP,   e.getCurp());
        a.putString(ARG_FOTO,   e.getFotoPerfilUrl());
        d.setArguments(a);
        return d;
    }

    public void setOnGuardadoListener(OnGuardadoListener l) { this.listener = l; }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_empleado_caphum, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95f);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            getDialog().setCanceledOnTouchOutside(false);
        }

        tvTitulo       = view.findViewById(R.id.tvTitulo);
        editNombre     = view.findViewById(R.id.editNombre);
        editCorreo     = view.findViewById(R.id.editCorreo);
        editCurp       = view.findViewById(R.id.editCurp);
        editContrasena = view.findViewById(R.id.editContrasena);
        editFoto       = view.findViewById(R.id.editFoto);
        btnGuardar     = view.findViewById(R.id.btnGuardar);
        btnCancelar    = view.findViewById(R.id.btnCancelar);

        Bundle a = getArguments();
        esEdicion = a != null && "editar".equals(a.getString(ARG_MODO));

        if (esEdicion && a != null) {
            idEmpleado = a.containsKey(ARG_ID) ? a.getInt(ARG_ID) : null;
            tvTitulo.setText("Editar empleado");
            editNombre.setText(a.getString(ARG_NOMBRE, ""));
            editCorreo.setText(a.getString(ARG_CORREO, ""));
            editCurp.setText(a.getString(ARG_CURP, ""));
            editFoto.setText(a.getString(ARG_FOTO, ""));
            editContrasena.setHint("Dejar vacío para mantener");
        } else {
            tvTitulo.setText("Nuevo empleado de Capital Humano");
            editContrasena.setHint("Mínimo 8 caracteres");
        }

        btnCancelar.setOnClickListener(v -> dismiss());
        btnGuardar.setOnClickListener(v -> guardar());

        cargarRolCapHum();
    }

    /** Resuelve el idRol de CAPITAL_HUMANO desde /roles (igual que la web). */
    private void cargarRolCapHum() {
        RetrofitClient.getApiService().getAllRoles().enqueue(new Callback<List<Rol>>() {
            @Override
            public void onResponse(Call<List<Rol>> call, Response<List<Rol>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    for (Rol r : response.body()) {
                        String n = r.getNombreRol() != null ? r.getNombreRol().toUpperCase() : "";
                        if (n.contains("CAP")) { idRolCapHum = r.getIdRol(); break; }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Rol>> call, Throwable t) { }
        });
    }

    private void guardar() {
        String nombre = txt(editNombre);
        String correo = txt(editCorreo);
        String curp   = txt(editCurp).toUpperCase();
        String pass   = editContrasena.getText() != null ? editContrasena.getText().toString() : "";
        String foto   = txt(editFoto);

        if (nombre.isEmpty() || nombre.length() > 100) {
            Toast.makeText(getContext(), "El nombre es obligatorio (máx. 100)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(getContext(), "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (curp.length() != 18) {
            Toast.makeText(getContext(), "La CURP debe tener 18 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!esEdicion && pass.length() < 8) {
            Toast.makeText(getContext(), "La contraseña debe tener mínimo 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (esEdicion && !pass.isEmpty() && pass.length() < 8) {
            Toast.makeText(getContext(), "Si cambias la contraseña debe tener mínimo 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (idRolCapHum == null) {
            Toast.makeText(getContext(), "No se pudo cargar el rol de Capital Humano. Reintenta.", Toast.LENGTH_SHORT).show();
            cargarRolCapHum();
            return;
        }

        EmpleadoDto dto = new EmpleadoDto();
        dto.setNombreEmpleado(nombre);
        dto.setCorreoEmpleado(correo);
        dto.setCurp(curp);
        dto.setFotoPerfilUrl(foto.isEmpty() ? null : foto);
        dto.setRol(new Rol(idRolCapHum));
        if (!pass.isEmpty()) dto.setContrasena(pass);

        btnGuardar.setEnabled(false);

        Callback<EmpleadoDto> cb = new Callback<EmpleadoDto>() {
            @Override
            public void onResponse(Call<EmpleadoDto> call, Response<EmpleadoDto> response) {
                if (!isAdded()) return;
                btnGuardar.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),
                            esEdicion ? "Empleado actualizado correctamente"
                                      : "Empleado registrado correctamente",
                            Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onGuardado();
                    dismiss();
                } else {
                    Toast.makeText(getContext(),
                            ApiErrorMessages.forCode(response.code()), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<EmpleadoDto> call, Throwable t) {
                if (!isAdded()) return;
                btnGuardar.setEnabled(true);
                Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
            }
        };

        if (esEdicion && idEmpleado != null) {
            RetrofitClient.getApiService().updateEmpleado(idEmpleado, dto).enqueue(cb);
        } else {
            RetrofitClient.getApiService().createEmpleado(dto).enqueue(cb);
        }
    }

    private String txt(EditText e) {
        return e.getText() != null ? e.getText().toString().trim() : "";
    }
}
