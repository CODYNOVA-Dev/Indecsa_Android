package com.example.indecsa_v2.capitalhumano.proyecto;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;
import com.example.indecsa_v2.util.PdfHelper;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleProyectoReadonlyDialog extends DialogFragment {

    private static final String ARG_ID         = "id";
    private static final String ARG_NOMBRE     = "nombre";
    private static final String ARG_TIPO       = "tipo";
    private static final String ARG_LUGAR      = "lugar";
    private static final String ARG_MUNICIPIO  = "municipio";
    private static final String ARG_ESTADO_GEO = "estadoGeo";
    private static final String ARG_ESTATUS    = "estatus";
    private static final String ARG_FECHA_INI  = "fechaInicio";
    private static final String ARG_FECHA_FIN  = "fechaFin";
    private static final String ARG_CALIFIC    = "calificacion";

    // ─── Factory ─────────────────────────────────────────────────────────────
    public static DetalleProyectoReadonlyDialog newInstance(ProyectoDto p) {
        DetalleProyectoReadonlyDialog d = new DetalleProyectoReadonlyDialog();
        Bundle args = new Bundle();
        args.putInt   (ARG_ID,      p.getIdProyecto() != null ? p.getIdProyecto() : -1);
        args.putString(ARG_NOMBRE,  p.getNombreProyecto());
        args.putString(ARG_TIPO,    p.getTipoProyecto());
        args.putString(ARG_LUGAR,   p.getLugarProyecto());
        // Campos pendientes del DTO — vacíos por ahora
        args.putString(ARG_MUNICIPIO,  "");
        args.putString(ARG_ESTADO_GEO, "");
        args.putString(ARG_ESTATUS,    "");
        args.putString(ARG_FECHA_INI,  "");
        args.putString(ARG_FECHA_FIN,  "");
        args.putInt   (ARG_CALIFIC,    0);
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
        return inflater.inflate(R.layout.dialog_detalle_proyecto_readonly, container, false);
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

        // Nombre del proyecto
        setTexto(view, R.id.dialogTvNombre,    a.getString(ARG_NOMBRE));
        setTexto(view, R.id.dialogTvTipo,      a.getString(ARG_TIPO));
        setTexto(view, R.id.dialogTvLugar,     a.getString(ARG_LUGAR));
        setTexto(view, R.id.dialogTvMunicipio, a.getString(ARG_MUNICIPIO));
        setTexto(view, R.id.dialogTvEstadoGeo, a.getString(ARG_ESTADO_GEO));
        setTexto(view, R.id.dialogTvFechaIni,  a.getString(ARG_FECHA_INI));
        setTexto(view, R.id.dialogTvFechaFin,  a.getString(ARG_FECHA_FIN));

        // Badge estatus
        String estatus = a.getString(ARG_ESTATUS, "");
        TextView tvEstatus = view.findViewById(R.id.dialogTvEstatus);
        if (tvEstatus != null) {
            boolean activo = "EN_CURSO".equals(estatus) || "ACTIVO".equals(estatus);
            tvEstatus.setText("● " + (estatus.isEmpty() ? "Sin estatus" : capitalizar(estatus)));
            tvEstatus.setBackgroundResource(activo
                    ? R.drawable.item_disp_verde
                    : R.drawable.item_disp_rojo);
        }

        // Rating
        RatingBar ratingBar = view.findViewById(R.id.dialogRatingBar);
        if (ratingBar != null) ratingBar.setRating(a.getInt(ARG_CALIFIC, 0));

        // Botón cerrar
        view.<Button>findViewById(R.id.btnCerrar)
                .setOnClickListener(v -> dismiss());

        // Botones de reporte
        view.findViewById(R.id.btnReporteHoras)
                .setOnClickListener(v -> mostrarPickerFechas(view, false));
        view.findViewById(R.id.btnReporteAvance)
                .setOnClickListener(v -> mostrarPickerFechas(view, true));
    }

    // ─── REPORTES PDF ────────────────────────────────────────────────────────

    private void mostrarPickerFechas(View rootView, boolean esAvance) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (dp, y, m, d) -> {
                    String ini = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
                    Calendar cal2 = Calendar.getInstance();
                    new DatePickerDialog(requireContext(),
                            (dp2, y2, m2, d2) -> {
                                String fin = String.format(Locale.US, "%04d-%02d-%02d", y2, m2 + 1, d2);
                                int idP = getArguments() != null ? getArguments().getInt(ARG_ID, -1) : -1;
                                ejecutarReporte(rootView, esAvance, idP, ini, fin);
                            },
                            cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH))
                            .show();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void ejecutarReporte(View rootView, boolean esAvance, int idProyecto, String ini, String fin) {
        if (idProyecto <= 0) {
            Toast.makeText(getContext(), "ID de proyecto inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<ResponseBody> call;
        String nombre;
        if (esAvance) {
            call = RetrofitClient.getApiService().descargarAvanceObra(idProyecto, ini, fin);
            nombre = "avance_obra_" + idProyecto + "_" + ini + ".pdf";
        } else {
            call = RetrofitClient.getApiService().descargarHorasProyecto(idProyecto, ini, fin);
            nombre = "horas_proyecto_" + idProyecto + "_" + ini + ".pdf";
        }
        setLoadingReporte(rootView, true, "Generando PDF…");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> c, Response<ResponseBody> r) {
                if (!isAdded()) return;
                if (r.isSuccessful() && r.body() != null) {
                    guardarYAbrirPdf(rootView, r.body(), nombre);
                } else {
                    setLoadingReporte(rootView, false, "Error: " + r.code());
                    Toast.makeText(getContext(), ApiErrorMessages.forCode(r.code()), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> c, Throwable t) {
                if (!isAdded()) return;
                setLoadingReporte(rootView, false, "Sin conexión");
                Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardarYAbrirPdf(View rootView, ResponseBody body, String nombreArchivo) {
        PdfHelper.guardarYAbrir(
                requireContext().getApplicationContext(),
                body,
                nombreArchivo,
                new PdfHelper.Callback() {
                    @Override public void onSuccess(@NonNull File file) {
                        if (!isAdded()) return;
                        setLoadingReporte(rootView, false, "PDF guardado ✓");
                        PdfHelper.abrir(requireContext(), file);
                    }

                    @Override public void onError(@NonNull String msg) {
                        if (!isAdded()) return;
                        setLoadingReporte(rootView, false, "Error al guardar");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoadingReporte(View rootView, boolean loading, String msg) {
        ProgressBar pb = rootView.findViewById(R.id.progressReporteDialog);
        TextView tv = rootView.findViewById(R.id.tvEstadoReporteDialog);
        if (pb != null) pb.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (tv != null) tv.setText(msg);
        View btnH = rootView.findViewById(R.id.btnReporteHoras);
        View btnA = rootView.findViewById(R.id.btnReporteAvance);
        if (btnH != null) btnH.setEnabled(!loading);
        if (btnA != null) btnA.setEnabled(!loading);
    }

    private void setTexto(View root, int id, String texto) {
        TextView tv = root.findViewById(id);
        if (tv != null) tv.setText(texto != null && !texto.isEmpty() ? texto : "—");
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return "—";
        String lower = s.replace("_", " ").toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}