package com.example.indecsa_v2.admin.proyecto;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.admin.contratista.ConfirmEliminarDialog;
import com.example.indecsa_v2.models.Domicilio;
import com.example.indecsa_v2.models.Estado;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;
import com.example.indecsa_v2.util.PdfHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
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

    // ─── Estado de edición ───────────────────────────────────────────────────
    private final List<Estado> listaEstados = new ArrayList<>();
    private ProyectoDto proyectoCompleto; // recargado del backend para merge
    private String fechaIniSel = null;
    private String fechaFinSel = null;

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
        args.putString(ARG_ESTATUS,    p.getEstatusProyecto() != null ? p.getEstatusProyecto() : "");
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

        // Botones editar / eliminar
        view.<AppCompatButton>findViewById(R.id.btnEditar)
                .setOnClickListener(v -> mostrarEditar(view));
        view.<AppCompatButton>findViewById(R.id.btnEliminar)
                .setOnClickListener(v -> mostrarConfirmEliminar());
        AppCompatButton btnEstatus = view.findViewById(R.id.btnEstatus);
        if (btnEstatus != null) btnEstatus.setOnClickListener(v -> mostrarCambiarEstatus(view));

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

    // ─── CAMBIAR ESTATUS ─────────────────────────────────────────────────────

    private static final String[] ESTATUS_PROYECTO =
            {"PLANEACION", "EN_CURSO", "PENDIENTE", "FINALIZADO", "CANCELADO"};

    private void mostrarCambiarEstatus(View view) {
        Bundle a = getArguments();
        if (a == null) return;
        final int id = a.getInt(ARG_ID, -1);
        if (id <= 0) {
            Toast.makeText(getContext(), "ID de proyecto inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        String actual = a.getString(ARG_ESTATUS, "");
        int seleccion = 0;
        for (int i = 0; i < ESTATUS_PROYECTO.length; i++) {
            if (ESTATUS_PROYECTO[i].equals(actual)) { seleccion = i; break; }
        }
        final int[] elegido = { seleccion };

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cambiar estatus del proyecto")
                .setSingleChoiceItems(ESTATUS_PROYECTO, seleccion, (d, which) -> elegido[0] = which)
                .setPositiveButton("Aplicar", (d, w) ->
                        aplicarEstatus(view, id, ESTATUS_PROYECTO[elegido[0]]))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void aplicarEstatus(View view, int id, String nuevoEstatus) {
        Map<String, Object> body = new HashMap<>();
        body.put("estatus", nuevoEstatus);
        RetrofitClient.getApiService().patchProyectoEstatus(id, body)
                .enqueue(new Callback<ProyectoDto>() {
                    @Override
                    public void onResponse(Call<ProyectoDto> call, Response<ProyectoDto> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            Bundle a = getArguments();
                            if (a != null) a.putString(ARG_ESTATUS, nuevoEstatus);
                            TextView tvEstatus = view.findViewById(R.id.dialogTvEstatus);
                            if (tvEstatus != null) {
                                boolean activo = "EN_CURSO".equals(nuevoEstatus) || "ACTIVO".equals(nuevoEstatus);
                                tvEstatus.setText("● " + capitalizar(nuevoEstatus));
                                tvEstatus.setBackgroundResource(activo
                                        ? R.drawable.item_disp_verde : R.drawable.item_disp_rojo);
                            }
                            Toast.makeText(getContext(), "Estatus actualizado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                        } else {
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forCode(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ProyectoDto> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── MODO EDITAR ─────────────────────────────────────────────────────────

    private void mostrarEditar(View view) {
        view.findViewById(R.id.panelDetalle).setVisibility(View.GONE);
        view.findViewById(R.id.panelEditar).setVisibility(View.VISIBLE);

        Bundle a = getArguments();
        if (a == null) return;

        // Relleno rápido con lo que ya tenemos
        setEditText(view, R.id.editNombre, a.getString(ARG_NOMBRE));
        setEditText(view, R.id.editTipo,   a.getString(ARG_TIPO));

        AppCompatButton btnFi = view.findViewById(R.id.btnFechaIni);
        AppCompatButton btnFf = view.findViewById(R.id.btnFechaFin);
        if (btnFi != null) btnFi.setOnClickListener(v -> pickFecha(true, btnFi));
        if (btnFf != null) btnFf.setOnClickListener(v -> pickFecha(false, btnFf));

        cargarEstadosYProyecto(view, a.getInt(ARG_ID, -1));

        view.<AppCompatButton>findViewById(R.id.btnCancelarEdicion)
                .setOnClickListener(v -> mostrarDetalle(view));
        view.<AppCompatButton>findViewById(R.id.btnGuardar)
                .setOnClickListener(v -> guardarProyecto(view, a.getInt(ARG_ID, -1)));
    }

    private void pickFecha(boolean inicio, AppCompatButton btn) {
        String actual = inicio ? fechaIniSel : fechaFinSel;
        Calendar cal = Calendar.getInstance();
        if (actual != null && actual.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] p = actual.split("-");
            cal.set(Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]));
        }
        new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
            String f = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
            if (inicio) fechaIniSel = f; else fechaFinSel = f;
            btn.setText(f);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void cargarEstadosYProyecto(View view, int id) {
        RetrofitClient.getApiService().getAllEstados().enqueue(new Callback<List<Estado>>() {
            @Override public void onResponse(Call<List<Estado>> call, Response<List<Estado>> response) {
                if (!isAdded()) return;
                listaEstados.clear();
                if (response.isSuccessful() && response.body() != null) listaEstados.addAll(response.body());
                poblarSpinnerEstadoDom(view);
                if (id > 0) cargarProyectoCompleto(view, id);
            }
            @Override public void onFailure(Call<List<Estado>> call, Throwable t) {
                if (!isAdded()) return;
                poblarSpinnerEstadoDom(view);
                if (id > 0) cargarProyectoCompleto(view, id);
            }
        });
    }

    private void cargarProyectoCompleto(View view, int id) {
        RetrofitClient.getApiService().getProyectoById(id).enqueue(new Callback<ProyectoDto>() {
            @Override public void onResponse(Call<ProyectoDto> call, Response<ProyectoDto> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    proyectoCompleto = response.body();
                    llenarFormCompleto(view, proyectoCompleto);
                }
            }
            @Override public void onFailure(Call<ProyectoDto> call, Throwable t) { }
        });
    }

    private void llenarFormCompleto(View view, ProyectoDto p) {
        setEditText(view, R.id.editNombre,      p.getNombreProyecto());
        setEditText(view, R.id.editTipo,        p.getTipoProyecto());
        setEditText(view, R.id.editCliente,     p.getCliente());
        setEditText(view, R.id.editOferta,      p.getOfertaTrabajo());
        setEditText(view, R.id.editDescripcion, p.getDescripcionProyecto());
        setEditText(view, R.id.editImagen,      p.getImagenProyectoUrl());

        RatingBar rb = view.findViewById(R.id.editRating);
        if (rb != null && p.getCalificacionProyecto() != null) rb.setRating(p.getCalificacionProyecto());

        fechaIniSel = p.getFechaEstimadaInicio();
        fechaFinSel = p.getFechaEstimadaFin();
        AppCompatButton btnFi = view.findViewById(R.id.btnFechaIni);
        AppCompatButton btnFf = view.findViewById(R.id.btnFechaFin);
        if (btnFi != null && fechaIniSel != null && !fechaIniSel.isEmpty()) btnFi.setText(fechaIniSel);
        if (btnFf != null && fechaFinSel != null && !fechaFinSel.isEmpty()) btnFf.setText(fechaFinSel);

        Domicilio dom = p.getDomicilio();
        if (dom != null) {
            setEditText(view, R.id.editCalle,   dom.getCalle());
            setEditText(view, R.id.editNumExt,  dom.getNumExt());
            setEditText(view, R.id.editNumInt,  dom.getNumInt());
            setEditText(view, R.id.editColonia, dom.getColonia());
            setEditText(view, R.id.editCodPost, dom.getCodPost() != null ? String.valueOf(dom.getCodPost()) : "");
            setEditText(view, R.id.editMunAlc,  dom.getMunAlc());
            if (dom.getEstado() != null) seleccionarEstadoDom(view, dom.getEstado().getIdEstado());
        }
    }

    private void poblarSpinnerEstadoDom(View view) {
        Spinner sp = view.findViewById(R.id.spinnerEstadoDom);
        if (sp == null) return;
        List<String> nombres = new ArrayList<>();
        nombres.add("— Selecciona estado —");
        for (Estado e : listaEstados) nombres.add(e.getNombreEst() != null ? e.getNombreEst() : ("Estado " + e.getIdEstado()));
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(ad);
    }

    private void seleccionarEstadoDom(View view, Integer idEstado) {
        if (idEstado == null) return;
        Spinner sp = view.findViewById(R.id.spinnerEstadoDom);
        if (sp == null) return;
        for (int i = 0; i < listaEstados.size(); i++) {
            if (idEstado.equals(listaEstados.get(i).getIdEstado())) { sp.setSelection(i + 1); return; }
        }
    }

    private Integer estadoDomSeleccionado(View view) {
        Spinner sp = view.findViewById(R.id.spinnerEstadoDom);
        if (sp == null) return null;
        int pos = sp.getSelectedItemPosition();
        if (pos <= 0 || pos - 1 >= listaEstados.size()) return null;
        return listaEstados.get(pos - 1).getIdEstado();
    }

    private void guardarProyecto(View view, int id) {
        // Merge sobre el objeto completo recargado para conservar campos NOT NULL
        ProyectoDto p = proyectoCompleto != null ? proyectoCompleto : new ProyectoDto();
        p.setNombreProyecto    (getEditText(view, R.id.editNombre));
        p.setTipoProyecto      (getEditText(view, R.id.editTipo));
        p.setCliente           (getEditText(view, R.id.editCliente));
        p.setOfertaTrabajo     (emptyToNull(getEditText(view, R.id.editOferta)));
        p.setDescripcionProyecto(emptyToNull(getEditText(view, R.id.editDescripcion)));
        p.setImagenProyectoUrl (emptyToNull(getEditText(view, R.id.editImagen)));

        RatingBar rb = view.findViewById(R.id.editRating);
        if (rb != null) p.setCalificacionProyecto((int) rb.getRating());

        if (fechaIniSel != null && !fechaIniSel.isEmpty()) p.setFechaEstimadaInicio(fechaIniSel);
        if (fechaFinSel != null && !fechaFinSel.isEmpty()) p.setFechaEstimadaFin(fechaFinSel);

        // Domicilio (merge para conservar idDomicilio)
        Domicilio dom = p.getDomicilio() != null ? p.getDomicilio() : new Domicilio();
        dom.setCalle  (getEditText(view, R.id.editCalle));
        dom.setNumExt (getEditText(view, R.id.editNumExt));
        dom.setNumInt (emptyToNull(getEditText(view, R.id.editNumInt)));
        dom.setColonia(getEditText(view, R.id.editColonia));
        String cp = getEditText(view, R.id.editCodPost);
        if (!cp.isEmpty()) { try { dom.setCodPost(Integer.parseInt(cp)); } catch (NumberFormatException ignored) {} }
        dom.setMunAlc (getEditText(view, R.id.editMunAlc));
        Integer idEstadoDom = estadoDomSeleccionado(view);
        if (idEstadoDom != null) { Estado est = new Estado(); est.setIdEstado(idEstadoDom); dom.setEstado(est); }
        p.setDomicilio(dom);

        // El estatus se cambia con el botón "Estatus"; aquí se preserva.
        if (p.getEstatusProyecto() == null) {
            Bundle a = getArguments();
            if (a != null) p.setEstatusProyecto(a.getString(ARG_ESTATUS));
        }

        AppCompatButton btnGuardar = view.findViewById(R.id.btnGuardar);
        if (btnGuardar != null) btnGuardar.setEnabled(false);

        RetrofitClient.getApiService().updateProyecto(id, p)
                .enqueue(new Callback<ProyectoDto>() {
                    @Override
                    public void onResponse(Call<ProyectoDto> call, Response<ProyectoDto> response) {
                        if (!isAdded()) return;
                        if (btnGuardar != null) btnGuardar.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Proyecto actualizado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forCode(response.code()),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ProyectoDto> call, Throwable t) {
                        if (!isAdded()) return;
                        if (btnGuardar != null) btnGuardar.setEnabled(true);
                        Toast.makeText(getContext(),
                                ApiErrorMessages.forThrowable(t),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String emptyToNull(String s) { return s == null || s.isEmpty() ? null : s; }

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
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Proyecto eliminado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    com.example.indecsa_v2.util.ApiErrorMessages.forCode(response.code()),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                com.example.indecsa_v2.util.ApiErrorMessages.forThrowable(t),
                                Toast.LENGTH_SHORT).show();
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
