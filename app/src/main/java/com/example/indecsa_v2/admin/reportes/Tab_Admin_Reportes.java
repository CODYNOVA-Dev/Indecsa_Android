package com.example.indecsa_v2.admin.reportes;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab_Admin_Reportes extends Fragment {

    // ─── Tipos de reporte ────────────────────────────────────────────────────
    private static final int TIPO_RENDIMIENTO = 0;
    private static final int TIPO_HORAS       = 1;
    private static final int TIPO_AVANCE      = 2;

    private static final String[] TIPOS_DISPLAY = {
            "Rendimiento por Trabajador",
            "Horas por Proyecto",
            "Avance de Obra"
    };

    // ─── Views ───────────────────────────────────────────────────────────────
    private Spinner         spinnerTipoReporte;
    private LinearLayout    layoutTrabajador;
    private LinearLayout    layoutProyecto;
    private Spinner         spinnerTrabajador;
    private Spinner         spinnerProyecto;
    private AppCompatButton btnFechaInicio;
    private AppCompatButton btnFechaFin;
    private ProgressBar     progressReporte;
    private TextView        tvEstado;

    // ─── Estado ──────────────────────────────────────────────────────────────
    private final List<TrabajadorDto> listaTrabajadores = new ArrayList<>();
    private final List<ProyectoDto>   listaProyectos    = new ArrayList<>();

    private String fechaInicioStr = null;
    private String fechaFinStr    = null;
    private int    tipoSeleccionado = TIPO_HORAS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_reportes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerTipoReporte = view.findViewById(R.id.spinnerTipoReporte);
        layoutTrabajador   = view.findViewById(R.id.layoutSeleccionTrabajador);
        layoutProyecto     = view.findViewById(R.id.layoutSeleccionProyecto);
        spinnerTrabajador  = view.findViewById(R.id.spinnerTrabajador);
        spinnerProyecto    = view.findViewById(R.id.spinnerProyecto);
        btnFechaInicio     = view.findViewById(R.id.btnFechaInicio);
        btnFechaFin        = view.findViewById(R.id.btnFechaFin);
        progressReporte    = view.findViewById(R.id.progressReporte);
        tvEstado           = view.findViewById(R.id.tvEstadoReporte);

        setupTipoSpinner();
        setupFechasPorDefecto();
        cargarDatos();

        view.<android.widget.Button>findViewById(R.id.btnGenerarPdf)
                .setOnClickListener(v -> generarReporte());
    }

    // ─── Tipo de reporte ─────────────────────────────────────────────────────

    private void setupTipoSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, TIPOS_DISPLAY);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoReporte.setAdapter(adapter);
        spinnerTipoReporte.setSelection(TIPO_HORAS);

        spinnerTipoReporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                tipoSeleccionado = pos;
                layoutTrabajador.setVisibility(pos == TIPO_RENDIMIENTO ? View.VISIBLE : View.GONE);
                layoutProyecto.setVisibility(pos != TIPO_RENDIMIENTO ? View.VISIBLE : View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    // ─── Fechas ──────────────────────────────────────────────────────────────

    private void setupFechasPorDefecto() {
        Calendar cal = Calendar.getInstance();
        fechaFinStr = String.format(Locale.US, "%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        btnFechaFin.setText(fechaFinStr);

        cal.add(Calendar.MONTH, -1);
        fechaInicioStr = String.format(Locale.US, "%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        btnFechaInicio.setText(fechaInicioStr);

        btnFechaInicio.setOnClickListener(v -> mostrarDatePicker(true));
        btnFechaFin.setOnClickListener(v -> mostrarDatePicker(false));
    }

    private void mostrarDatePicker(boolean esInicio) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (dp, y, m, d) -> {
                    String fecha = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
                    if (esInicio) { fechaInicioStr = fecha; btnFechaInicio.setText(fecha); }
                    else          { fechaFinStr    = fecha; btnFechaFin.setText(fecha); }
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // ─── Carga de combos ─────────────────────────────────────────────────────

    private void cargarDatos() {
        cargarProyectos();
        cargarTrabajadores();
    }

    private void cargarProyectos() {
        RetrofitClient.getApiService().getAllProyectos().enqueue(new Callback<List<ProyectoDto>>() {
            @Override
            public void onResponse(Call<List<ProyectoDto>> call, Response<List<ProyectoDto>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    listaProyectos.clear();
                    listaProyectos.addAll(response.body());
                    List<String> nombres = new ArrayList<>();
                    for (ProyectoDto p : listaProyectos) nombres.add(p.getNombreProyecto());
                    ArrayAdapter<String> a = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, nombres);
                    a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProyecto.setAdapter(a);
                }
            }
            @Override public void onFailure(Call<List<ProyectoDto>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "No se pudo cargar la lista de proyectos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarTrabajadores() {
        RetrofitClient.getApiService().getAllTrabajadores().enqueue(new Callback<List<TrabajadorDto>>() {
            @Override
            public void onResponse(Call<List<TrabajadorDto>> call, Response<List<TrabajadorDto>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    listaTrabajadores.clear();
                    listaTrabajadores.addAll(response.body());
                    List<String> nombres = new ArrayList<>();
                    for (TrabajadorDto t : listaTrabajadores) nombres.add(t.getNombreTrabajador());
                    ArrayAdapter<String> a = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, nombres);
                    a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTrabajador.setAdapter(a);
                }
            }
            @Override public void onFailure(Call<List<TrabajadorDto>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "No se pudo cargar la lista de trabajadores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Generación ──────────────────────────────────────────────────────────

    private void generarReporte() {
        if (fechaInicioStr == null || fechaFinStr == null) {
            Toast.makeText(requireContext(), "Selecciona ambas fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseBody> call;
        String nombreArchivo;

        switch (tipoSeleccionado) {
            case TIPO_RENDIMIENTO: {
                if (listaTrabajadores.isEmpty()) {
                    Toast.makeText(requireContext(), "Cargando trabajadores…", Toast.LENGTH_SHORT).show();
                    return;
                }
                int pos = spinnerTrabajador.getSelectedItemPosition();
                if (pos < 0 || pos >= listaTrabajadores.size()) {
                    Toast.makeText(requireContext(), "Selecciona un trabajador", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer idT = listaTrabajadores.get(pos).getIdTrabajador();
                call = RetrofitClient.getApiService().descargarRendimientoTrabajador(idT, fechaInicioStr, fechaFinStr);
                nombreArchivo = "rendimiento_trabajador_" + idT + "_" + fechaInicioStr + ".pdf";
                break;
            }
            case TIPO_HORAS: {
                if (listaProyectos.isEmpty()) {
                    Toast.makeText(requireContext(), "Cargando proyectos…", Toast.LENGTH_SHORT).show();
                    return;
                }
                int pos = spinnerProyecto.getSelectedItemPosition();
                if (pos < 0 || pos >= listaProyectos.size()) {
                    Toast.makeText(requireContext(), "Selecciona un proyecto", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer idP = listaProyectos.get(pos).getIdProyecto();
                call = RetrofitClient.getApiService().descargarHorasProyecto(idP, fechaInicioStr, fechaFinStr);
                nombreArchivo = "horas_proyecto_" + idP + "_" + fechaInicioStr + ".pdf";
                break;
            }
            default: {  // TIPO_AVANCE
                if (listaProyectos.isEmpty()) {
                    Toast.makeText(requireContext(), "Cargando proyectos…", Toast.LENGTH_SHORT).show();
                    return;
                }
                int pos = spinnerProyecto.getSelectedItemPosition();
                if (pos < 0 || pos >= listaProyectos.size()) {
                    Toast.makeText(requireContext(), "Selecciona un proyecto", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer idP = listaProyectos.get(pos).getIdProyecto();
                call = RetrofitClient.getApiService().descargarAvanceObra(idP, fechaInicioStr, fechaFinStr);
                nombreArchivo = "avance_obra_" + idP + "_" + fechaInicioStr + ".pdf";
                break;
            }
        }

        setLoading(true, "Generando PDF…");
        final String archivoDef = nombreArchivo;

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> c, Response<ResponseBody> r) {
                if (!isAdded()) return;
                if (r.isSuccessful() && r.body() != null) {
                    guardarYAbrirPdf(r.body(), archivoDef);
                } else {
                    setLoading(false, "Error: " + r.code());
                    Toast.makeText(requireContext(), "Error generando el reporte (" + r.code() + ")", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> c, Throwable t) {
                if (!isAdded()) return;
                setLoading(false, "Sin conexión");
                Toast.makeText(requireContext(), "No se pudo conectar al servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    // ─── Guardar y abrir PDF ─────────────────────────────────────────────────

    private void guardarYAbrirPdf(ResponseBody body, String nombreArchivo) {
        new Thread(() -> {
            try {
                File dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                if (dir == null) throw new IOException("Almacenamiento externo no disponible");
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, nombreArchivo);

                try (InputStream is = body.byteStream();
                     FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = is.read(buf)) != -1) fos.write(buf, 0, len);
                }

                requireActivity().runOnUiThread(() -> {
                    setLoading(false, "PDF guardado ✓");
                    abrirPdf(file);
                });

            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> {
                    setLoading(false, "Error al guardar");
                    Toast.makeText(requireContext(), "No se pudo guardar el PDF", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void abrirPdf(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "Abrir PDF con…"));
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "PDF guardado. Instala un visor de PDF para abrirlo.", Toast.LENGTH_LONG).show();
        }
    }

    // ─── UI helpers ──────────────────────────────────────────────────────────

    private void setLoading(boolean loading, String msg) {
        progressReporte.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvEstado.setText(msg);
        View btnGenerar = getView() != null ? getView().findViewById(R.id.btnGenerarPdf) : null;
        if (btnGenerar != null) btnGenerar.setEnabled(!loading);
    }
}
