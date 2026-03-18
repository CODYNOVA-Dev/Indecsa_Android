package com.example.indecsa_v2.admin.contratista;

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
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dialog flotante para ver, editar y eliminar un Contratista.
 *
 * Uso desde Tab_Admin_Contratista:
 *   DetalleContratistaDialog dialog = DetalleContratistaDialog.newInstance(contratista);
 *   dialog.setOnCambioListener(() -> cargarContratistas());
 *   dialog.show(getParentFragmentManager(), "detalle_contratista");
 */
public class DetalleContratistaDialog extends DialogFragment {

    // ─── Args ────────────────────────────────────────────────────────────────
    private static final String ARG_ID           = "id";
    private static final String ARG_NOMBRE       = "nombre";
    private static final String ARG_RFC          = "rfc";
    private static final String ARG_CORREO       = "correo";
    private static final String ARG_TELEFONO     = "telefono";
    private static final String ARG_ESTADO       = "estado";
    private static final String ARG_CALIFICACION = "calificacion";
    private static final String ARG_DESCRIPCION  = "descripcion";
    private static final String ARG_EXPERIENCIA  = "experiencia";
    private static final String ARG_UBICACION    = "ubicacion";

    // ─── Callback ────────────────────────────────────────────────────────────
    public interface OnCambioListener { void onCambio(); }
    private OnCambioListener onCambioListener;
    public void setOnCambioListener(OnCambioListener l) { this.onCambioListener = l; }

    // ─── Factory ─────────────────────────────────────────────────────────────
    public static DetalleContratistaDialog newInstance(Contratista c) {
        DetalleContratistaDialog d = new DetalleContratistaDialog();
        Bundle args = new Bundle();
        args.putInt   (ARG_ID,           c.getIdContratista()          != null ? c.getIdContratista()          : -1);
        args.putString(ARG_NOMBRE,       c.getNombreContratista());
        args.putString(ARG_RFC,          c.getRfcContratista());
        args.putString(ARG_CORREO,       c.getCorreoContratista());
        args.putString(ARG_TELEFONO,     c.getTelefonoContratista());
        args.putString(ARG_ESTADO,       c.getEstadoContratista());
        args.putInt   (ARG_CALIFICACION, c.getCalificacionContratista() != null ? c.getCalificacionContratista() : 0);
        args.putString(ARG_DESCRIPCION,  c.getDescripcionContratista());
        args.putString(ARG_EXPERIENCIA,  c.getExperiencia());
        args.putString(ARG_UBICACION,    c.getUbicacionContratista());
        d.setArguments(args);
        return d;
    }

    // ─── onCreateDialog ──────────────────────────────────────────────────────
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    // ─── onCreateView ────────────────────────────────────────────────────────
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detalle_contratista, container, false);
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

        mostrarDetalle(view);
    }

    // ─── MODO DETALLE ────────────────────────────────────────────────────────

    private void mostrarDetalle(View view) {
        view.findViewById(R.id.panelDetalle).setVisibility(View.VISIBLE);
        view.findViewById(R.id.panelEditar).setVisibility(View.GONE);

        Bundle a = getArguments();
        if (a == null) return;

        // Cabecera
        String nombre = a.getString(ARG_NOMBRE, "");
        TextView tvAvatar = view.findViewById(R.id.dialogTvAvatar);
        tvAvatar.setText(!nombre.isEmpty() ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?");
        setTexto(view, R.id.dialogTvNombre, nombre);

        String estado = a.getString(ARG_ESTADO, "");
        TextView tvEstado = view.findViewById(R.id.dialogTvEstado);
        tvEstado.setText("● " + capitalizar(estado));
        tvEstado.setBackgroundResource("ACTIVO".equals(estado)
                ? R.drawable.item_disp_verde : R.drawable.item_disp_rojo);

        // Campos info
        setTexto(view, R.id.dialogTvRfc,         a.getString(ARG_RFC));
        setTexto(view, R.id.dialogTvCorreo,       a.getString(ARG_CORREO));
        setTexto(view, R.id.dialogTvTelefono,     a.getString(ARG_TELEFONO));
        setTexto(view, R.id.dialogTvExperiencia,  a.getString(ARG_EXPERIENCIA));
        setTexto(view, R.id.dialogTvDescripcion,  a.getString(ARG_DESCRIPCION));

        RatingBar ratingBar = view.findViewById(R.id.dialogRatingBar);
        ratingBar.setRating(a.getInt(ARG_CALIFICACION, 0));

        // Botones
        view.<AppCompatButton>findViewById(R.id.btnEditar)
                .setOnClickListener(v -> mostrarEditar(view));
        view.<AppCompatButton>findViewById(R.id.btnEliminar)
                .setOnClickListener(v -> mostrarConfirmEliminar());
    }

    // ─── MODO EDITAR ─────────────────────────────────────────────────────────

    private void mostrarEditar(View view) {
        view.findViewById(R.id.panelDetalle).setVisibility(View.GONE);
        view.findViewById(R.id.panelEditar).setVisibility(View.VISIBLE);

        Bundle a = getArguments();
        if (a == null) return;

        setEditText(view, R.id.editNombre,      a.getString(ARG_NOMBRE));
        setEditText(view, R.id.editRfc,         a.getString(ARG_RFC));
        setEditText(view, R.id.editCorreo,      a.getString(ARG_CORREO));
        setEditText(view, R.id.editTelefono,    a.getString(ARG_TELEFONO));
        setEditText(view, R.id.editExperiencia, a.getString(ARG_EXPERIENCIA));
        setEditText(view, R.id.editDescripcion, a.getString(ARG_DESCRIPCION));
        setEditText(view, R.id.editUbicacion,   a.getString(ARG_UBICACION));

        view.<AppCompatButton>findViewById(R.id.btnCancelarEdicion)
                .setOnClickListener(v -> mostrarDetalle(view));

        view.<AppCompatButton>findViewById(R.id.btnGuardar)
                .setOnClickListener(v -> {
                    Contratista c = new Contratista();
                    c.setNombreContratista      (getEditText(view, R.id.editNombre));
                    c.setRfcContratista         (getEditText(view, R.id.editRfc));
                    c.setCorreoContratista      (getEditText(view, R.id.editCorreo));
                    c.setTelefonoContratista    (getEditText(view, R.id.editTelefono));
                    c.setExperiencia            (getEditText(view, R.id.editExperiencia));
                    c.setDescripcionContratista (getEditText(view, R.id.editDescripcion));
                    c.setUbicacionContratista   (getEditText(view, R.id.editUbicacion));
                    // Mantener estado y calificación actuales
                    c.setEstadoContratista      (a.getString(ARG_ESTADO));
                    c.setCalificacionContratista(a.getInt(ARG_CALIFICACION, 0));

                    guardarContratista(a.getInt(ARG_ID, -1), c);
                });
    }

    private void guardarContratista(int id, Contratista c) {
        RetrofitClient.getApiService().updateContratista(id, c)
                .enqueue(new Callback<Contratista>() {
                    @Override
                    public void onResponse(Call<Contratista> call, Response<Contratista> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Contratista actualizado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Contratista> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── CONFIRMAR ELIMINAR ──────────────────────────────────────────────────

    private void mostrarConfirmEliminar() {
        Bundle a = getArguments();
        if (a == null) return;

        ConfirmEliminarDialog confirm = ConfirmEliminarDialog.newInstance(
                "¿Eliminar contratista?",
                "Esta acción no se puede deshacer.\n\"" + a.getString(ARG_NOMBRE) + "\" será eliminado permanentemente."
        );
        confirm.setOnConfirmListener(() -> eliminarContratista(a.getInt(ARG_ID, -1)));
        confirm.show(getParentFragmentManager(), "confirm_eliminar_contratista");
    }

    private void eliminarContratista(int id) {
        RetrofitClient.getApiService().deleteContratista(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Contratista eliminado", Toast.LENGTH_SHORT).show();
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
