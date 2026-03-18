package com.example.indecsa_v2.admin.proyecto;

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
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dialog flotante para ver, editar y eliminar un Proyecto.
 *
 * Uso desde Tab_Admin_Proyecto:
 *   DetalleProyectoDialog dialog = DetalleProyectoDialog.newInstance(proyectoDto);
 *   dialog.setOnCambioListener(() -> cargarProyectos());
 *   dialog.show(getParentFragmentManager(), "detalle_proyecto");
 *
 * Nota: ProyectoDto solo expone idProyecto, nombreProyecto, tipoProyecto, lugarProyecto.
 * Los campos estado, municipio, fechas y calificacion no están en el DTO actual.
 * Cuando el backend los exponga, agregar los ARG correspondientes aquí.
 */
public class DetalleProyectoDialog extends DialogFragment {

    // ─── Args ────────────────────────────────────────────────────────────────
    private static final String ARG_ID         = "id";
    private static final String ARG_NOMBRE     = "nombre";
    private static final String ARG_TIPO       = "tipo";
    private static final String ARG_LUGAR      = "lugar";
    // Campos pendientes de exposición en el DTO — se pasan vacíos por ahora:
    private static final String ARG_MUNICIPIO  = "municipio";
    private static final String ARG_ESTADO_GEO = "estadoGeo";
    private static final String ARG_ESTATUS    = "estatus";
    private static final String ARG_FECHA_INI  = "fechaInicio";
    private static final String ARG_FECHA_FIN  = "fechaFin";
    private static final String ARG_CALIFIC    = "calificacion";

    // ─── Callback ────────────────────────────────────────────────────────────
    public interface OnCambioListener { void onCambio(); }
    private OnCambioListener onCambioListener;
    public void setOnCambioListener(OnCambioListener l) { this.onCambioListener = l; }

    // ─── Factory ─────────────────────────────────────────────────────────────
    public static DetalleProyectoDialog newInstance(ProyectoDto p) {
        DetalleProyectoDialog d = new DetalleProyectoDialog();
        Bundle args = new Bundle();
        args.putInt   (ARG_ID,     p.getIdProyecto()     != null ? p.getIdProyecto()     : -1);
        args.putString(ARG_NOMBRE, p.getNombreProyecto());
        args.putString(ARG_TIPO,   p.getTipoProyecto());
        args.putString(ARG_LUGAR,  p.getLugarProyecto());
        // Cuando el DTO exponga más campos, agregarlos aquí:
        args.putString(ARG_MUNICIPIO,  "");
        args.putString(ARG_ESTADO_GEO, "");
        args.putString(ARG_ESTATUS,    "");
        args.putString(ARG_FECHA_INI,  "");
        args.putString(ARG_FECHA_FIN,  "");
        args.putInt   (ARG_CALIFIC,    0);
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
        return inflater.inflate(R.layout.dialog_detalle_proyecto, container, false);
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

        // Nombre del proyecto como título
        setTexto(view, R.id.dialogTvNombre,    a.getString(ARG_NOMBRE));
        setTexto(view, R.id.dialogTvTipo,      a.getString(ARG_TIPO));
        setTexto(view, R.id.dialogTvLugar,     a.getString(ARG_LUGAR));
        setTexto(view, R.id.dialogTvMunicipio, a.getString(ARG_MUNICIPIO));
        setTexto(view, R.id.dialogTvEstadoGeo, a.getString(ARG_ESTADO_GEO));
        setTexto(view, R.id.dialogTvFechaIni,  a.getString(ARG_FECHA_INI));
        setTexto(view, R.id.dialogTvFechaFin,  a.getString(ARG_FECHA_FIN));

        // Estatus badge
        String estatus = a.getString(ARG_ESTATUS, "");
        TextView tvEstatus = view.findViewById(R.id.dialogTvEstatus);
        if (tvEstatus != null) {
            boolean activo = "EN_CURSO".equals(estatus) || "ACTIVO".equals(estatus);
            tvEstatus.setText("● " + (estatus.isEmpty() ? "Sin estatus" : capitalizar(estatus)));
            tvEstatus.setBackgroundResource(activo
                    ? R.drawable.item_disp_verde : R.drawable.item_disp_rojo);
        }

        // Rating
        RatingBar ratingBar = view.findViewById(R.id.dialogRatingBar);
        if (ratingBar != null) ratingBar.setRating(a.getInt(ARG_CALIFIC, 0));

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

        setEditText(view, R.id.editNombre,    a.getString(ARG_NOMBRE));
        setEditText(view, R.id.editTipo,      a.getString(ARG_TIPO));
        setEditText(view, R.id.editLugar,     a.getString(ARG_LUGAR));
        setEditText(view, R.id.editMunicipio, a.getString(ARG_MUNICIPIO));
        // Fechas — cuando el DTO las exponga:
        setEditText(view, R.id.editFechaIni,  a.getString(ARG_FECHA_INI));
        setEditText(view, R.id.editFechaFin,  a.getString(ARG_FECHA_FIN));

        view.<AppCompatButton>findViewById(R.id.btnCancelarEdicion)
                .setOnClickListener(v -> mostrarDetalle(view));

        view.<AppCompatButton>findViewById(R.id.btnGuardar)
                .setOnClickListener(v -> {
                    ProyectoDto dto = new ProyectoDto(
                            getEditText(view, R.id.editNombre),
                            getEditText(view, R.id.editTipo),
                            getEditText(view, R.id.editLugar)
                    );
                    guardarProyecto(a.getInt(ARG_ID, -1), dto);
                });
    }

    private void guardarProyecto(int id, ProyectoDto dto) {
        RetrofitClient.getApiService().updateProyecto(id, dto)
                .enqueue(new Callback<ProyectoDto>() {
                    @Override
                    public void onResponse(Call<ProyectoDto> call, Response<ProyectoDto> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Proyecto actualizado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ProyectoDto> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── CONFIRMAR ELIMINAR ──────────────────────────────────────────────────

    private void mostrarConfirmEliminar() {
        Bundle a = getArguments();
        if (a == null) return;

        ConfirmEliminarDialog confirm = ConfirmEliminarDialog.newInstance(
                "¿Eliminar proyecto?",
                "Esta acción no se puede deshacer.\n\"" + a.getString(ARG_NOMBRE) + "\" será eliminado permanentemente."
        );
        confirm.setOnConfirmListener(() -> eliminarProyecto(a.getInt(ARG_ID, -1)));
        confirm.show(getParentFragmentManager(), "confirm_eliminar_proyecto");
    }

    private void eliminarProyecto(int id) {
        RetrofitClient.getApiService().deleteProyecto(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Proyecto eliminado", Toast.LENGTH_SHORT).show();
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
        if (tv != null) tv.setText(texto != null && !texto.isEmpty() ? texto : "—");
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
        String lower = s.replace("_", " ").toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
