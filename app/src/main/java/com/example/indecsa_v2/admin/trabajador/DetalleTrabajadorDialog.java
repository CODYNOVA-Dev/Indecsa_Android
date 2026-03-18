package com.example.indecsa_v2.admin.trabajador;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.admin.contratista.ConfirmEliminarDialog;
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dialog flotante para ver, editar y eliminar un Trabajador.
 *
 * Modos:
 *   MODE_DETALLE → muestra la info del trabajador + botones Editar / Eliminar
 *   MODE_EDITAR  → reemplaza el contenido por un formulario editable
 *
 * Uso desde Tab_Admin_Trabajador:
 *   DetalleTrabajadorDialog dialog = DetalleTrabajadorDialog.newInstance(trabajadorDto);
 *   dialog.setOnCambioListener(() -> cargarTrabajadores());
 *   dialog.show(getParentFragmentManager(), "detalle_trabajador");
 */
public class DetalleTrabajadorDialog extends DialogFragment {

    // ─── Constantes ──────────────────────────────────────────────────────────
    private static final String ARG_ID            = "id";
    private static final String ARG_NOMBRE        = "nombre";
    private static final String ARG_ESPECIALIDAD  = "especialidad";
    private static final String ARG_CORREO        = "correo";
    private static final String ARG_TELEFONO      = "telefono";
    private static final String ARG_EXPERIENCIA   = "experiencia";
    private static final String ARG_CALIFICACION  = "calificacion";
    private static final String ARG_DESCRIPCION   = "descripcion";
    private static final String ARG_ESTADO        = "estado";
    private static final String ARG_UBICACION     = "ubicacion";
    private static final String ARG_NSS           = "nss";
    private static final String ARG_FECHA_INGRESO = "fechaIngreso";

    private static final int MODE_DETALLE = 0;
    private static final int MODE_EDITAR  = 1;

    private int modoActual = MODE_DETALLE;

    // ─── Callback ────────────────────────────────────────────────────────────
    public interface OnCambioListener { void onCambio(); }
    private OnCambioListener onCambioListener;
    public void setOnCambioListener(OnCambioListener l) { this.onCambioListener = l; }

    // ─── Factory ─────────────────────────────────────────────────────────────
    public static DetalleTrabajadorDialog newInstance(TrabajadorDto t) {
        DetalleTrabajadorDialog d = new DetalleTrabajadorDialog();
        Bundle args = new Bundle();
        args.putInt   (ARG_ID,           t.getIdTrabajador()          != null ? t.getIdTrabajador()          : -1);
        args.putString(ARG_NOMBRE,       t.getNombreTrabajador());
        args.putString(ARG_ESPECIALIDAD, t.getEspecialidadTrabajador());
        args.putString(ARG_CORREO,       t.getCorreoTrabajador());
        args.putString(ARG_TELEFONO,     t.getTelefonoTrabajador());
        args.putString(ARG_EXPERIENCIA,  t.getExperiencia());
        args.putInt   (ARG_CALIFICACION, t.getCalificacionTrabajador() != null ? t.getCalificacionTrabajador() : 0);
        args.putString(ARG_DESCRIPCION,  t.getDescripcionTrabajador());
        args.putString(ARG_ESTADO,       t.getEstadoTrabajador());
        args.putString(ARG_UBICACION,    t.getUbicacionTrabajador());
        args.putString(ARG_NSS,          t.getNssTrabajador());
        args.putString(ARG_FECHA_INGRESO,t.getFechaIngreso());
        d.setArguments(args);
        return d;
    }

    // ─── onCreateDialog ──────────────────────────────────────────────────────
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    // ─── onCreateView ────────────────────────────────────────────────────────
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detalle_trabajador, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tamaño del dialog: 92% del ancho de pantalla
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            getDialog().setCanceledOnTouchOutside(false); // no cerrar al tocar fuera
        }

        mostrarDetalle(view);
    }

    // ─── MODO DETALLE ────────────────────────────────────────────────────────

    private void mostrarDetalle(View view) {
        modoActual = MODE_DETALLE;

        // Contenedores
        View panelDetalle = view.findViewById(R.id.panelDetalle);
        View panelEditar  = view.findViewById(R.id.panelEditar);
        panelDetalle.setVisibility(View.VISIBLE);
        panelEditar.setVisibility(View.GONE);

        Bundle a = getArguments();
        if (a == null) return;

        // Cabecera
        TextView tvAvatar   = view.findViewById(R.id.dialogTvAvatar);
        TextView tvNombre   = view.findViewById(R.id.dialogTvNombre);
        TextView tvEstado   = view.findViewById(R.id.dialogTvEstado);

        String nombre = a.getString(ARG_NOMBRE, "");
        tvAvatar.setText(!nombre.isEmpty() ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?");
        tvNombre.setText(nombre);

        String estado = a.getString(ARG_ESTADO, "");
        tvEstado.setText("● " + capitalizar(estado));
        tvEstado.setBackgroundResource(
                "ACTIVO".equals(estado) || "VACACIONES".equals(estado)
                        ? R.drawable.item_disp_verde
                        : R.drawable.item_disp_rojo);

        // Campos info
        setTexto(view, R.id.dialogTvEspecialidad, a.getString(ARG_ESPECIALIDAD));
        setTexto(view, R.id.dialogTvCorreo,       a.getString(ARG_CORREO));
        setTexto(view, R.id.dialogTvTelefono,     a.getString(ARG_TELEFONO));
        setTexto(view, R.id.dialogTvExperiencia,  a.getString(ARG_EXPERIENCIA));
        setTexto(view, R.id.dialogTvDescripcion,  a.getString(ARG_DESCRIPCION));

        // Rating
        RatingBar ratingBar = view.findViewById(R.id.dialogRatingBar);
        ratingBar.setRating(a.getInt(ARG_CALIFICACION, 0));

        // Botones
        AppCompatButton btnEditar   = view.findViewById(R.id.btnEditar);
        AppCompatButton btnEliminar = view.findViewById(R.id.btnEliminar);

        btnEditar.setOnClickListener(v -> mostrarEditar(view));
        btnEliminar.setOnClickListener(v -> mostrarConfirmEliminar());
    }

    // ─── MODO EDITAR ─────────────────────────────────────────────────────────

    private void mostrarEditar(View view) {
        modoActual = MODE_EDITAR;

        View panelDetalle = view.findViewById(R.id.panelDetalle);
        View panelEditar  = view.findViewById(R.id.panelEditar);
        panelDetalle.setVisibility(View.GONE);
        panelEditar.setVisibility(View.VISIBLE);

        Bundle a = getArguments();
        if (a == null) return;

        // Pre-llenar campos editables
        setEditText(view, R.id.editNombre,       a.getString(ARG_NOMBRE));
        setEditText(view, R.id.editEspecialidad, a.getString(ARG_ESPECIALIDAD));
        setEditText(view, R.id.editCorreo,       a.getString(ARG_CORREO));
        setEditText(view, R.id.editTelefono,     a.getString(ARG_TELEFONO));
        setEditText(view, R.id.editExperiencia,  a.getString(ARG_EXPERIENCIA));
        setEditText(view, R.id.editDescripcion,  a.getString(ARG_DESCRIPCION));
        setEditText(view, R.id.editNss,          a.getString(ARG_NSS));
        setEditText(view, R.id.editUbicacion,    a.getString(ARG_UBICACION));

        // Botones del panel editar
        AppCompatButton btnGuardar   = view.findViewById(R.id.btnGuardar);
        AppCompatButton btnCancelar  = view.findViewById(R.id.btnCancelarEdicion);

        btnCancelar.setOnClickListener(v -> mostrarDetalle(view));

        btnGuardar.setOnClickListener(v -> {
            TrabajadorDto dto = new TrabajadorDto();
            dto.setNombreTrabajador      (getEditText(view, R.id.editNombre));
            dto.setEspecialidadTrabajador(getEditText(view, R.id.editEspecialidad));
            dto.setCorreoTrabajador      (getEditText(view, R.id.editCorreo));
            dto.setTelefonoTrabajador    (getEditText(view, R.id.editTelefono));
            dto.setExperiencia           (getEditText(view, R.id.editExperiencia));
            dto.setDescripcionTrabajador (getEditText(view, R.id.editDescripcion));
            dto.setNssTrabajador         (getEditText(view, R.id.editNss));
            dto.setUbicacionTrabajador   (getEditText(view, R.id.editUbicacion));
            // Mantener estado y calificacion actuales
            dto.setEstadoTrabajador      (a.getString(ARG_ESTADO));
            dto.setCalificacionTrabajador(a.getInt(ARG_CALIFICACION, 0));
            dto.setFechaIngreso          (a.getString(ARG_FECHA_INGRESO));

            guardarTrabajador(a.getInt(ARG_ID, -1), dto, view);
        });
    }

    private void guardarTrabajador(int id, TrabajadorDto dto, View view) {
        RetrofitClient.getApiService().updateTrabajador(id, dto)
                .enqueue(new Callback<TrabajadorDto>() {
                    @Override
                    public void onResponse(Call<TrabajadorDto> call, Response<TrabajadorDto> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Trabajador actualizado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<TrabajadorDto> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── CONFIRMAR ELIMINAR ──────────────────────────────────────────────────

    private void mostrarConfirmEliminar() {
        Bundle a = getArguments();
        if (a == null) return;

        ConfirmEliminarDialog confirm = ConfirmEliminarDialog.newInstance(
                "¿Eliminar trabajador?",
                "Esta acción no se puede deshacer.\n\"" + a.getString(ARG_NOMBRE) + "\" será eliminado permanentemente."
        );
        confirm.setOnConfirmListener(() -> eliminarTrabajador(a.getInt(ARG_ID, -1)));
        confirm.show(getParentFragmentManager(), "confirm_eliminar_trabajador");
    }

    private void eliminarTrabajador(int id) {
        RetrofitClient.getApiService().deleteTrabajador(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Trabajador eliminado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── Utilidades ──────────────────────────────────────────────────────────

    private void setTexto(View root, int id, String texto) {
        TextView tv = root.findViewById(id);
        if (tv != null) tv.setText(texto != null ? texto : "—");
    }

    private void setEditText(View root, int id, String texto) {
        EditText et = root.findViewById(id);
        if (et != null) et.setText(texto != null ? texto : "");
    }

    private String getEditText(View root, int id) {
        EditText et = root.findViewById(id);
        return et != null ? et.getText().toString().trim() : "";
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return "—";
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
