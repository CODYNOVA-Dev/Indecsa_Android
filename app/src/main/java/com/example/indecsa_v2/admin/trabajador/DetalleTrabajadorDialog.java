package com.example.indecsa_v2.admin.trabajador;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.indecsa_v2.models.RendimientoIndicadorDto;
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    // ─── Estado de edición ───────────────────────────────────────────────────
    private static final String[] SEXOS = {"—", "Masculino", "Femenino", "Otro"};
    private final List<Estado> listaEstados = new ArrayList<>();
    private TrabajadorDto trabajadorCompleto; // recargado del backend para merge
    private String fechaIngresoSel = null;

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
        AppCompatButton btnEstado   = view.findViewById(R.id.btnEstado);

        btnEditar.setOnClickListener(v -> mostrarEditar(view));
        btnEliminar.setOnClickListener(v -> mostrarConfirmEliminar());
        if (btnEstado != null) btnEstado.setOnClickListener(v -> mostrarCambiarEstado(view));

        int idT = a.getInt(ARG_ID, -1);
        if (idT > 0) cargarRendimiento(view, idT);
    }

    // ─── RENDIMIENTO ─────────────────────────────────────────────────────────

    private void cargarRendimiento(View view, int idTrabajador) {
        ProgressBar progress   = view.findViewById(R.id.progressRendimiento);
        TextView    tvVacio    = view.findViewById(R.id.tvRendimientoVacio);
        LinearLayout container = view.findViewById(R.id.containerRendimiento);
        if (progress == null || tvVacio == null || container == null) return;

        progress.setVisibility(View.VISIBLE);
        tvVacio.setText("Cargando...");
        tvVacio.setVisibility(View.VISIBLE);
        container.removeAllViews();

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

        TextView tvProyecto  = row.findViewById(R.id.tvNombreProyecto);
        TextView tvSemaforo  = row.findViewById(R.id.tvSemaforo);
        TextView tvHoras     = row.findViewById(R.id.tvHoras);
        TextView tvAvance    = row.findViewById(R.id.tvAvance);
        TextView tvRendReal  = row.findViewById(R.id.tvRendReal);
        TextView tvRendEsp   = row.findViewById(R.id.tvRendEsperado);
        TextView tvDesv      = row.findViewById(R.id.tvDesviacion);

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

    // ─── CAMBIAR ESTADO (baja / vacaciones / activar) ────────────────────────

    private static final String[] ESTADOS_TRABAJADOR = {"ACTIVO", "INACTIVO", "VACACIONES", "BAJA"};

    private void mostrarCambiarEstado(View view) {
        Bundle a = getArguments();
        if (a == null) return;
        final int idT = a.getInt(ARG_ID, -1);
        if (idT <= 0) {
            Toast.makeText(getContext(), "ID de trabajador inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        String actual = a.getString(ARG_ESTADO, "");
        int seleccion = 0;
        for (int i = 0; i < ESTADOS_TRABAJADOR.length; i++) {
            if (ESTADOS_TRABAJADOR[i].equals(actual)) { seleccion = i; break; }
        }
        final int[] elegido = { seleccion };

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cambiar estado del trabajador")
                .setSingleChoiceItems(ESTADOS_TRABAJADOR, seleccion, (d, which) -> elegido[0] = which)
                .setPositiveButton("Aplicar", (d, w) ->
                        aplicarEstado(view, idT, ESTADOS_TRABAJADOR[elegido[0]]))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void aplicarEstado(View view, int idT, String nuevoEstado) {
        Map<String, Object> body = new HashMap<>();
        body.put("estado", nuevoEstado);
        RetrofitClient.getApiService().patchTrabajadorEstado(idT, body)
                .enqueue(new Callback<TrabajadorDto>() {
                    @Override
                    public void onResponse(Call<TrabajadorDto> call, Response<TrabajadorDto> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            Bundle a = getArguments();
                            if (a != null) a.putString(ARG_ESTADO, nuevoEstado);
                            TextView tvEstado = view.findViewById(R.id.dialogTvEstado);
                            if (tvEstado != null) {
                                tvEstado.setText("● " + capitalizar(nuevoEstado));
                                tvEstado.setBackgroundResource(
                                        "ACTIVO".equals(nuevoEstado) || "VACACIONES".equals(nuevoEstado)
                                                ? R.drawable.item_disp_verde
                                                : R.drawable.item_disp_rojo);
                            }
                            Toast.makeText(getContext(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                        } else {
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forCode(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<TrabajadorDto> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── MODO EDITAR ─────────────────────────────────────────────────────────

    private void mostrarEditar(View view) {
        modoActual = MODE_EDITAR;

        view.findViewById(R.id.panelDetalle).setVisibility(View.GONE);
        view.findViewById(R.id.panelEditar).setVisibility(View.VISIBLE);

        Bundle a = getArguments();
        if (a == null) return;

        // Spinner de sexo
        Spinner spSexo = view.findViewById(R.id.spinnerSexo);
        if (spSexo != null) {
            ArrayAdapter<String> adSexo = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, SEXOS);
            adSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSexo.setAdapter(adSexo);
        }

        // Relleno rápido con lo que ya tenemos
        setEditText(view, R.id.editNombre,       a.getString(ARG_NOMBRE));
        setEditText(view, R.id.editEspecialidad, a.getString(ARG_ESPECIALIDAD));
        setEditText(view, R.id.editCorreo,       a.getString(ARG_CORREO));
        setEditText(view, R.id.editTelefono,     a.getString(ARG_TELEFONO));
        setEditText(view, R.id.editExperiencia,  a.getString(ARG_EXPERIENCIA));
        setEditText(view, R.id.editDescripcion,  a.getString(ARG_DESCRIPCION));
        setEditText(view, R.id.editNss,          a.getString(ARG_NSS));
        RatingBar rb = view.findViewById(R.id.editRating);
        if (rb != null) rb.setRating(a.getInt(ARG_CALIFICACION, 0));

        fechaIngresoSel = a.getString(ARG_FECHA_INGRESO);
        AppCompatButton btnFecha = view.findViewById(R.id.btnFechaIngreso);
        if (btnFecha != null) {
            if (fechaIngresoSel != null && !fechaIngresoSel.isEmpty()) btnFecha.setText(fechaIngresoSel);
            btnFecha.setOnClickListener(v -> mostrarDatePicker(btnFecha));
        }

        cargarEstadosYTrabajador(view, a.getInt(ARG_ID, -1));

        AppCompatButton btnGuardar  = view.findViewById(R.id.btnGuardar);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnCancelarEdicion);
        btnCancelar.setOnClickListener(v -> mostrarDetalle(view));
        btnGuardar.setOnClickListener(v -> guardarTrabajador(view, a.getInt(ARG_ID, -1)));
    }

    private void mostrarDatePicker(AppCompatButton btn) {
        Calendar cal = Calendar.getInstance();
        if (fechaIngresoSel != null && fechaIngresoSel.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] p = fechaIngresoSel.split("-");
            cal.set(Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]));
        }
        new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
            fechaIngresoSel = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
            btn.setText(fechaIngresoSel);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void cargarEstadosYTrabajador(View view, int id) {
        RetrofitClient.getApiService().getAllEstados().enqueue(new Callback<List<Estado>>() {
            @Override public void onResponse(Call<List<Estado>> call, Response<List<Estado>> response) {
                if (!isAdded()) return;
                listaEstados.clear();
                if (response.isSuccessful() && response.body() != null) listaEstados.addAll(response.body());
                poblarSpinnerEstadoDom(view);
                if (id > 0) cargarTrabajadorCompleto(view, id);
            }
            @Override public void onFailure(Call<List<Estado>> call, Throwable t) {
                if (!isAdded()) return;
                poblarSpinnerEstadoDom(view);
                if (id > 0) cargarTrabajadorCompleto(view, id);
            }
        });
    }

    private void cargarTrabajadorCompleto(View view, int id) {
        RetrofitClient.getApiService().getTrabajadorById(id).enqueue(new Callback<TrabajadorDto>() {
            @Override public void onResponse(Call<TrabajadorDto> call, Response<TrabajadorDto> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    trabajadorCompleto = response.body();
                    llenarFormCompleto(view, trabajadorCompleto);
                }
            }
            @Override public void onFailure(Call<TrabajadorDto> call, Throwable t) { }
        });
    }

    private void llenarFormCompleto(View view, TrabajadorDto t) {
        setEditText(view, R.id.editNombre,         t.getNombreTrabajador());
        setEditText(view, R.id.editCurp,           t.getCurp());
        setEditText(view, R.id.editRfc,            t.getRfc());
        setEditText(view, R.id.editNss,            t.getNssTrabajador());
        setEditText(view, R.id.editNacionalidad,   t.getNacionalidad());
        setEditText(view, R.id.editEstadoCivil,    t.getEstadoCivil());
        setEditText(view, R.id.editTelefono,       t.getTelefonoTrabajador());
        setEditText(view, R.id.editCorreo,         t.getCorreoTrabajador());
        setEditText(view, R.id.editFoto,           t.getFotoPerfilUrl());
        setEditText(view, R.id.editPuesto,         t.getPuesto());
        setEditText(view, R.id.editEspecialidad,   t.getEspecialidadTrabajador());
        setEditText(view, R.id.editDescripcion,    t.getDescPuesto());
        setEditText(view, R.id.editEscolaridad,    t.getEscolaridad());
        setEditText(view, R.id.editExperiencia,    t.getExperiencia());
        setEditText(view, R.id.editContratacion,   t.getContratacion());
        setEditText(view, R.id.editJornada,        t.getJornada());
        setEditText(view, R.id.editIdiomas,        t.getIdiomas());
        setEditText(view, R.id.editLenguaIndigena, t.getLenguaIndigena());
        setEditText(view, R.id.editAntPenal,       t.getAntPenal());
        setEditText(view, R.id.editDeudorAlim,     t.getDeudorAlim());
        setEditText(view, R.id.editFolioLic,       t.getFolioLicCond());

        RatingBar rb = view.findViewById(R.id.editRating);
        if (rb != null && t.getEvaluacionTrabajador() != null) rb.setRating(t.getEvaluacionTrabajador());

        fechaIngresoSel = t.getFechaIngreso();
        AppCompatButton btnFecha = view.findViewById(R.id.btnFechaIngreso);
        if (btnFecha != null && fechaIngresoSel != null && !fechaIngresoSel.isEmpty()) btnFecha.setText(fechaIngresoSel);

        Spinner spSexo = view.findViewById(R.id.spinnerSexo);
        if (spSexo != null && t.getSexo() != null) {
            for (int i = 0; i < SEXOS.length; i++) {
                if (SEXOS[i].equalsIgnoreCase(t.getSexo())) { spSexo.setSelection(i); break; }
            }
        }

        Domicilio dom = t.getDomicilio();
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

    private void guardarTrabajador(View view, int id) {
        // Merge sobre el objeto completo recargado: no perder campos NOT NULL
        TrabajadorDto t = trabajadorCompleto != null ? trabajadorCompleto : new TrabajadorDto();

        t.setNombreTrabajador      (getEditText(view, R.id.editNombre));
        String curp = getEditText(view, R.id.editCurp); if (!curp.isEmpty()) t.setCurp(curp.toUpperCase());
        String rfc  = getEditText(view, R.id.editRfc);  if (!rfc.isEmpty())  t.setRfc(rfc.toUpperCase());
        t.setNssTrabajador         (emptyToNull(getEditText(view, R.id.editNss)));
        t.setNacionalidad          (getEditText(view, R.id.editNacionalidad));
        t.setEstadoCivil           (emptyToNull(getEditText(view, R.id.editEstadoCivil)));
        t.setTelefonoTrabajador    (getEditText(view, R.id.editTelefono));
        t.setCorreoTrabajador      (getEditText(view, R.id.editCorreo));
        t.setFotoPerfilUrl         (emptyToNull(getEditText(view, R.id.editFoto)));
        t.setPuesto                (getEditText(view, R.id.editPuesto));
        t.setEspecialidadTrabajador(getEditText(view, R.id.editEspecialidad));
        t.setDescPuesto            (getEditText(view, R.id.editDescripcion));
        t.setEscolaridad           (getEditText(view, R.id.editEscolaridad));
        t.setExperiencia           (emptyToNull(getEditText(view, R.id.editExperiencia)));
        t.setContratacion          (getEditText(view, R.id.editContratacion));
        t.setJornada               (getEditText(view, R.id.editJornada));
        t.setIdiomas               (emptyToNull(getEditText(view, R.id.editIdiomas)));
        t.setLenguaIndigena        (emptyToNull(getEditText(view, R.id.editLenguaIndigena)));
        t.setAntPenal              (emptyToNull(getEditText(view, R.id.editAntPenal)));
        t.setDeudorAlim            (emptyToNull(getEditText(view, R.id.editDeudorAlim)));
        t.setFolioLicCond          (emptyToNull(getEditText(view, R.id.editFolioLic)));

        RatingBar rb = view.findViewById(R.id.editRating);
        if (rb != null) t.setEvaluacionTrabajador((int) rb.getRating());

        if (fechaIngresoSel != null && !fechaIngresoSel.isEmpty()) t.setFechaIngreso(fechaIngresoSel);

        Spinner spSexo = view.findViewById(R.id.spinnerSexo);
        if (spSexo != null && spSexo.getSelectedItemPosition() > 0) {
            t.setSexo(SEXOS[spSexo.getSelectedItemPosition()]);
        }

        // Domicilio (merge sobre el existente para conservar idDomicilio)
        Domicilio dom = t.getDomicilio() != null ? t.getDomicilio() : new Domicilio();
        dom.setCalle  (getEditText(view, R.id.editCalle));
        dom.setNumExt (getEditText(view, R.id.editNumExt));
        dom.setNumInt (emptyToNull(getEditText(view, R.id.editNumInt)));
        dom.setColonia(getEditText(view, R.id.editColonia));
        String cp = getEditText(view, R.id.editCodPost);
        if (!cp.isEmpty()) { try { dom.setCodPost(Integer.parseInt(cp)); } catch (NumberFormatException ignored) {} }
        dom.setMunAlc (getEditText(view, R.id.editMunAlc));
        Integer idEstadoDom = estadoDomSeleccionado(view);
        if (idEstadoDom != null) { Estado est = new Estado(); est.setIdEstado(idEstadoDom); dom.setEstado(est); }
        t.setDomicilio(dom);

        // El estado del trabajador se cambia con el botón "Estado"; aquí se preserva.
        if (t.getEstadoTrabajador() == null) {
            Bundle a = getArguments();
            if (a != null) t.setEstadoTrabajador(a.getString(ARG_ESTADO));
        }

        AppCompatButton btnGuardar = view.findViewById(R.id.btnGuardar);
        if (btnGuardar != null) btnGuardar.setEnabled(false);

        RetrofitClient.getApiService().updateTrabajador(id, t)
                .enqueue(new Callback<TrabajadorDto>() {
                    @Override
                    public void onResponse(Call<TrabajadorDto> call, Response<TrabajadorDto> response) {
                        if (!isAdded()) return;
                        if (btnGuardar != null) btnGuardar.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Trabajador actualizado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forCode(response.code()),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<TrabajadorDto> call, Throwable t2) {
                        if (!isAdded()) return;
                        if (btnGuardar != null) btnGuardar.setEnabled(true);
                        Toast.makeText(getContext(),
                                ApiErrorMessages.forThrowable(t2),
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
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Trabajador eliminado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forCode(response.code()),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                ApiErrorMessages.forThrowable(t),
                                Toast.LENGTH_SHORT).show();
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
