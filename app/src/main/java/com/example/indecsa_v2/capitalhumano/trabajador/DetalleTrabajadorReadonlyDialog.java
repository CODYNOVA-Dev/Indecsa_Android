package com.example.indecsa_v2.capitalhumano.trabajador;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.RendimientoIndicadorDto;
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleTrabajadorReadonlyDialog extends DialogFragment {

    private static final String ARG_ID           = "id";
    private static final String ARG_NOMBRE       = "nombre";
    private static final String ARG_ESPECIALIDAD = "especialidad";
    private static final String ARG_CORREO       = "correo";
    private static final String ARG_TELEFONO     = "telefono";
    private static final String ARG_EXPERIENCIA  = "experiencia";
    private static final String ARG_CALIFICACION = "calificacion";
    private static final String ARG_DESCRIPCION  = "descripcion";
    private static final String ARG_ESTADO       = "estado";

    // ─── Factory ─────────────────────────────────────────────────────────────
    public static DetalleTrabajadorReadonlyDialog newInstance(TrabajadorDto t) {
        DetalleTrabajadorReadonlyDialog d = new DetalleTrabajadorReadonlyDialog();
        Bundle args = new Bundle();
        args.putInt   (ARG_ID,           t.getIdTrabajador() != null ? t.getIdTrabajador() : -1);
        args.putString(ARG_NOMBRE,       t.getNombreTrabajador());
        args.putString(ARG_ESPECIALIDAD, t.getEspecialidadTrabajador());
        args.putString(ARG_CORREO,       t.getCorreoTrabajador());
        args.putString(ARG_TELEFONO,     t.getTelefonoTrabajador());
        args.putString(ARG_EXPERIENCIA,  t.getExperiencia());
        args.putInt   (ARG_CALIFICACION, t.getCalificacionTrabajador() != null ? t.getCalificacionTrabajador() : 0);
        args.putString(ARG_DESCRIPCION,  t.getDescripcionTrabajador());
        args.putString(ARG_ESTADO,       t.getEstadoTrabajador());
        d.setArguments(args);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null)
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detalle_trabajador_readonly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            getDialog().setCanceledOnTouchOutside(true);
        }

        Bundle a = getArguments();
        if (a == null) return;

        // Avatar
        String nombre = a.getString(ARG_NOMBRE, "");
        TextView tvAvatar = view.findViewById(R.id.dialogTvAvatar);
        tvAvatar.setText(!nombre.isEmpty()
                ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?");

        // Nombre
        setTexto(view, R.id.dialogTvNombre, nombre);

        // Estado badge
        String estado = a.getString(ARG_ESTADO, "");
        TextView tvEstado = view.findViewById(R.id.dialogTvEstado);
        tvEstado.setText("● " + capitalizar(estado));
        tvEstado.setBackgroundResource(
                "ACTIVO".equals(estado) || "VACACIONES".equals(estado)
                        ? R.drawable.item_disp_verde
                        : R.drawable.item_disp_rojo);

        // Campos
        setTexto(view, R.id.dialogTvEspecialidad, a.getString(ARG_ESPECIALIDAD));
        setTexto(view, R.id.dialogTvCorreo,       a.getString(ARG_CORREO));
        setTexto(view, R.id.dialogTvTelefono,     a.getString(ARG_TELEFONO));
        setTexto(view, R.id.dialogTvExperiencia,  a.getString(ARG_EXPERIENCIA));
        setTexto(view, R.id.dialogTvDescripcion,  a.getString(ARG_DESCRIPCION));

        // Rating
        RatingBar ratingBar = view.findViewById(R.id.dialogRatingBar);
        ratingBar.setRating(a.getInt(ARG_CALIFICACION, 0));

        // Botón cerrar
        view.<Button>findViewById(R.id.btnCerrar)
                .setOnClickListener(v -> dismiss());

        // Rendimiento
        int idT = a.getInt(ARG_ID, -1);
        if (idT > 0) cargarRendimiento(view, idT);
    }

    // ─── RENDIMIENTO ─────────────────────────────────────────────────────────

    private void cargarRendimiento(View view, int idTrabajador) {
        ProgressBar  progress  = view.findViewById(R.id.progressRendimiento);
        TextView     tvVacio   = view.findViewById(R.id.tvRendimientoVacio);
        LinearLayout container = view.findViewById(R.id.containerRendimiento);
        if (progress == null || tvVacio == null || container == null) return;

        progress.setVisibility(View.VISIBLE);
        tvVacio.setText("Cargando...");
        tvVacio.setVisibility(View.VISIBLE);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String fechaFin = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -30);
        String fechaInicio = sdf.format(cal.getTime());

        RetrofitClient.getApiService()
                .getRendimientoTrabajador(idTrabajador, fechaInicio, fechaFin)
                .enqueue(new Callback<List<RendimientoIndicadorDto>>() {
            @Override
            public void onResponse(Call<List<RendimientoIndicadorDto>> call,
                                   Response<List<RendimientoIndicadorDto>> response) {
                if (!isAdded() || getView() == null) return;
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    tvVacio.setVisibility(View.GONE);
                    for (RendimientoIndicadorDto r : response.body()) {
                        agregarIndicadorRow(container, r);
                    }
                } else {
                    tvVacio.setText("Sin datos de rendimiento en el período");
                    tvVacio.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<RendimientoIndicadorDto>> call, Throwable t) {
                if (!isAdded() || getView() == null) return;
                progress.setVisibility(View.GONE);
                tvVacio.setText("No se pudo cargar el rendimiento");
                tvVacio.setVisibility(View.VISIBLE);
            }
        });
    }

    private void agregarIndicadorRow(LinearLayout container, RendimientoIndicadorDto r) {
        View row = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_rendimiento_indicador, container, false);

        TextView tvProyecto = row.findViewById(R.id.tvNombreProyecto);
        TextView tvSemaforo = row.findViewById(R.id.tvSemaforo);
        TextView tvHoras    = row.findViewById(R.id.tvHoras);
        TextView tvAvance   = row.findViewById(R.id.tvAvance);
        TextView tvRendReal = row.findViewById(R.id.tvRendReal);
        TextView tvRendEsp  = row.findViewById(R.id.tvRendEsperado);
        TextView tvDesv     = row.findViewById(R.id.tvDesviacion);

        tvProyecto.setText(r.getNombreProyecto() != null ? r.getNombreProyecto() : "Proyecto");

        String semaforo = r.getIndicadorSemaforo() != null ? r.getIndicadorSemaforo() : "SIN_ESTANDAR";
        switch (semaforo) {
            case "VERDE":
                tvSemaforo.setText("● VERDE");
                tvSemaforo.setBackgroundResource(R.drawable.item_disp_verde);
                break;
            case "AMARILLO":
                tvSemaforo.setText("● AMARILLO");
                tvSemaforo.setBackgroundResource(R.drawable.item_disp_amarillo);
                break;
            case "ROJO":
                tvSemaforo.setText("● ROJO");
                tvSemaforo.setBackgroundResource(R.drawable.item_disp_rojo);
                break;
            default:
                tvSemaforo.setText("● S/D");
                tvSemaforo.setBackgroundResource(R.drawable.item_disp_rojo);
                break;
        }

        tvHoras.setText(r.getTotalHorasTrabajadas() != null
                ? String.format(Locale.US, "%.1f h", r.getTotalHorasTrabajadas()) : "—");
        String unidad = r.getUnidadMedida() != null ? r.getUnidadMedida() : "";
        tvAvance.setText(r.getTotalAvanceEjecutado() != null
                ? String.format(Locale.US, "%.2f %s", r.getTotalAvanceEjecutado(), unidad) : "—");
        tvRendReal.setText(r.getRendimientoReal() != null
                ? String.format(Locale.US, "%.4f", r.getRendimientoReal()) : "—");
        tvRendEsp.setText(r.getRendimientoEsperado() != null
                ? String.format(Locale.US, "%.4f", r.getRendimientoEsperado()) : "—");

        if (r.getPorcentajeDesviacion() != null) {
            double d = r.getPorcentajeDesviacion();
            tvDesv.setText(String.format(Locale.US, "%+.1f%%", d));
            tvDesv.setTextColor(d >= -10 ? 0xFF22C55E : (d >= -30 ? 0xFFEAB308 : 0xFFEF4444));
        } else {
            tvDesv.setText("—");
            tvDesv.setTextColor(0xFF6B7280);
        }

        container.addView(row);
    }

    private void setTexto(View root, int id, String texto) {
        TextView tv = root.findViewById(id);
        if (tv != null) tv.setText(texto != null ? texto : "—");
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return "—";
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}