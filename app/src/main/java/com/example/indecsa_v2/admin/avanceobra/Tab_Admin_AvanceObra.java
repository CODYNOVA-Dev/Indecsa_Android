package com.example.indecsa_v2.admin.avanceobra;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.AvancePartidaDto;
import com.example.indecsa_v2.models.CuadrillaDto;
import com.example.indecsa_v2.models.EstandarRendimientoDto;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab_Admin_AvanceObra extends Fragment {

    private static final TimeZone TZ_MX = TimeZone.getTimeZone("America/Mexico_City");
    private static final String[] UNIDADES = {"m2", "m3", "ml", "piezas", "porcentaje"};

    // Sentinelas de "sin selección" para los spinners. Inicializados desde
    // strings.xml en onCreateView para que i18n / cambios de copia sean una
    // sola fuente de verdad. No son static porque getString() necesita Context.
    private String sinCuadrilla;
    private String sinEstandar;

    private Spinner         spinnerProyecto;
    private Spinner         spinnerCuadrilla;
    private Spinner         spinnerEstandar;
    private EditText        editNombrePartida;
    private AppCompatButton btnFecha;
    private EditText        editCantidadEjecutada;
    private Spinner         spinnerUnidad;
    private EditText        editCantidadProgramada;
    private EditText        editObservaciones;
    private AppCompatButton btnRegistrar;
    private TextView        textEmpty;
    private RecyclerView    recyclerAvances;

    private final List<ProyectoDto>          listaProyectos  = new ArrayList<>();
    private final List<CuadrillaDto>         listaCuadrillas = new ArrayList<>();
    private final List<EstandarRendimientoDto> listaEstandares = new ArrayList<>();
    private final List<AvancePartidaDto>     listaAvances    = new ArrayList<>();

    private AvanceObraAdapter adapter;
    private String fechaSeleccionada = null;
    private boolean proyectoSeleccionado = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_tab__avance_obra, container, false);

        sinCuadrilla = getString(R.string.sin_cuadrilla);
        sinEstandar  = getString(R.string.sin_estandar);

        spinnerProyecto       = vista.findViewById(R.id.spinnerProyecto);
        spinnerCuadrilla      = vista.findViewById(R.id.spinnerCuadrilla);
        spinnerEstandar       = vista.findViewById(R.id.spinnerEstandar);
        editNombrePartida     = vista.findViewById(R.id.editNombrePartida);
        btnFecha              = vista.findViewById(R.id.btnFecha);
        editCantidadEjecutada = vista.findViewById(R.id.editCantidadEjecutada);
        spinnerUnidad         = vista.findViewById(R.id.spinnerUnidad);
        editCantidadProgramada= vista.findViewById(R.id.editCantidadProgramada);
        editObservaciones     = vista.findViewById(R.id.editObservaciones);
        btnRegistrar          = vista.findViewById(R.id.btnRegistrar);
        textEmpty             = vista.findViewById(R.id.textEmpty);
        recyclerAvances       = vista.findViewById(R.id.recyclerAvances);

        adapter = new AvanceObraAdapter(listaAvances);
        recyclerAvances.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAvances.setAdapter(adapter);

        setupSpinnerUnidad();
        setupSpinnerCuadrillaVacio();
        setupSpinnerEstandarVacio();
        btnFecha.setOnClickListener(v -> mostrarDatePicker());
        btnRegistrar.setOnClickListener(v -> registrarAvance());

        cargarProyectos();
        cargarEstandares();

        return vista;
    }

    // ─── CARGA DE DATOS ──────────────────────────────────────────────────────

    private void cargarProyectos() {
        RetrofitClient.getApiService().getAllProyectos().enqueue(new Callback<List<ProyectoDto>>() {
            @Override
            public void onResponse(Call<List<ProyectoDto>> call, Response<List<ProyectoDto>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    listaProyectos.clear();
                    listaProyectos.addAll(response.body());
                    setupSpinnerProyecto();
                }
            }
            @Override
            public void onFailure(Call<List<ProyectoDto>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarEstandares() {
        RetrofitClient.getApiService().getAllEstandares().enqueue(new Callback<List<EstandarRendimientoDto>>() {
            @Override
            public void onResponse(Call<List<EstandarRendimientoDto>> call,
                                   Response<List<EstandarRendimientoDto>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    listaEstandares.clear();
                    listaEstandares.addAll(response.body());
                }
                setupSpinnerEstandar();
            }
            @Override
            public void onFailure(Call<List<EstandarRendimientoDto>> call, Throwable t) { }
        });
    }

    private void cargarCuadrillas(Integer idProyecto) {
        RetrofitClient.getApiService().getCuadrillasByProyecto(idProyecto)
                .enqueue(new Callback<List<CuadrillaDto>>() {
            @Override
            public void onResponse(Call<List<CuadrillaDto>> call, Response<List<CuadrillaDto>> response) {
                if (!isAdded()) return;
                listaCuadrillas.clear();
                if (response.isSuccessful() && response.body() != null) {
                    listaCuadrillas.addAll(response.body());
                }
                setupSpinnerCuadrilla();
            }
            @Override
            public void onFailure(Call<List<CuadrillaDto>> call, Throwable t) {
                if (!isAdded()) return;
                setupSpinnerCuadrilla();
            }
        });
    }

    private void cargarAvances(Integer idProyecto) {
        RetrofitClient.getApiService().getAvancesByProyecto(idProyecto)
                .enqueue(new Callback<List<AvancePartidaDto>>() {
            @Override
            public void onResponse(Call<List<AvancePartidaDto>> call,
                                   Response<List<AvancePartidaDto>> response) {
                if (!isAdded()) return;
                listaAvances.clear();
                if (response.isSuccessful() && response.body() != null) {
                    listaAvances.addAll(response.body());
                }
                adapter.notifyDataSetChanged();
                actualizarVisibilidadLista();
            }
            @Override
            public void onFailure(Call<List<AvancePartidaDto>> call, Throwable t) {
                if (!isAdded()) return;
                actualizarVisibilidadLista();
            }
        });
    }

    // ─── SPINNERS ────────────────────────────────────────────────────────────

    private void setupSpinnerProyecto() {
        List<String> nombres = new ArrayList<>();
        nombres.add("— Selecciona un proyecto —");
        for (ProyectoDto p : listaProyectos) nombres.add(p.getNombreProyecto());

        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProyecto.setAdapter(ad);

        spinnerProyecto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    proyectoSeleccionado = false;
                    listaCuadrillas.clear();
                    listaAvances.clear();
                    adapter.notifyDataSetChanged();
                    setupSpinnerCuadrillaVacio();
                    mostrarVacio("Selecciona un proyecto para registrar avances");
                } else {
                    proyectoSeleccionado = true;
                    // Reset síncrono de cuadrillas para que el usuario no pueda
                    // registrar con cuadrilla "stale" del proyecto anterior.
                    listaCuadrillas.clear();
                    setupSpinnerCuadrillaVacio();

                    ProyectoDto p = listaProyectos.get(position - 1);
                    cargarCuadrillas(p.getIdProyecto());
                    cargarAvances(p.getIdProyecto());
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupSpinnerCuadrillaVacio() {
        List<String> opciones = new ArrayList<>();
        opciones.add(sinCuadrilla);
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, opciones);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuadrilla.setAdapter(ad);
    }

    private void setupSpinnerCuadrilla() {
        List<String> nombres = new ArrayList<>();
        nombres.add(sinCuadrilla);
        for (CuadrillaDto c : listaCuadrillas) {
            nombres.add(c.getNombreCuadrilla() != null ? c.getNombreCuadrilla() : "Cuadrilla #" + c.getIdCuadrilla());
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuadrilla.setAdapter(ad);
    }

    private void setupSpinnerEstandarVacio() {
        List<String> opciones = new ArrayList<>();
        opciones.add(sinEstandar);
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, opciones);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstandar.setAdapter(ad);
    }

    private void setupSpinnerEstandar() {
        List<String> nombres = new ArrayList<>();
        nombres.add(sinEstandar);
        for (EstandarRendimientoDto e : listaEstandares) nombres.add(e.toString());
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstandar.setAdapter(ad);
    }

    private void setupSpinnerUnidad() {
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, UNIDADES);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidad.setAdapter(ad);
    }

    // ─── DATE PICKER ─────────────────────────────────────────────────────────

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance(TZ_MX);
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            fechaSeleccionada = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
            btnFecha.setText(fechaSeleccionada);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ─── REGISTRAR ───────────────────────────────────────────────────────────

    private void registrarAvance() {
        if (!proyectoSeleccionado) {
            Toast.makeText(getContext(), "Selecciona un proyecto", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombrePartida = editNombrePartida.getText() != null
                ? editNombrePartida.getText().toString().trim() : "";
        if (nombrePartida.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa el nombre de la partida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaSeleccionada == null) {
            Toast.makeText(getContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantStr = editCantidadEjecutada.getText() != null
                ? editCantidadEjecutada.getText().toString().trim() : "";
        if (cantStr.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa la cantidad ejecutada", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantStr);
            if (cantidad <= 0) {
                Toast.makeText(getContext(), "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Formato de cantidad inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        int posProyecto = spinnerProyecto.getSelectedItemPosition();
        if (posProyecto <= 0 || posProyecto - 1 >= listaProyectos.size()) {
            Toast.makeText(getContext(),
                    "La selección de proyecto ya no es válida.", Toast.LENGTH_SHORT).show();
            return;
        }
        ProyectoDto proyecto = listaProyectos.get(posProyecto - 1);

        AvancePartidaDto dto = new AvancePartidaDto();
        dto.setIdProyecto(proyecto.getIdProyecto());
        dto.setNombrePartida(nombrePartida);
        dto.setFechaRegistro(fechaSeleccionada);
        dto.setCantidadEjecutada(cantidad);
        dto.setUnidadMedida(spinnerUnidad.getSelectedItem().toString());

        int posCuadrilla = spinnerCuadrilla.getSelectedItemPosition();
        if (posCuadrilla > 0 && posCuadrilla - 1 < listaCuadrillas.size()) {
            dto.setIdCuadrilla(listaCuadrillas.get(posCuadrilla - 1).getIdCuadrilla());
        }

        int posEstandar = spinnerEstandar.getSelectedItemPosition();
        if (posEstandar > 0 && posEstandar - 1 < listaEstandares.size()) {
            dto.setIdEstandar(listaEstandares.get(posEstandar - 1).getIdEstandar());
        }

        String cantProg = editCantidadProgramada.getText() != null
                ? editCantidadProgramada.getText().toString().trim() : "";
        if (!cantProg.isEmpty()) {
            try { dto.setCantidadProgramada(Double.parseDouble(cantProg)); }
            catch (NumberFormatException ignored) { }
        }

        String obs = editObservaciones.getText() != null ? editObservaciones.getText().toString().trim() : "";
        if (!obs.isEmpty()) dto.setObservaciones(obs);

        btnRegistrar.setEnabled(false);
        RetrofitClient.getApiService().createAvancePartida(dto).enqueue(new Callback<AvancePartidaDto>() {
            @Override
            public void onResponse(Call<AvancePartidaDto> call, Response<AvancePartidaDto> response) {
                if (!isAdded()) return;
                btnRegistrar.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Avance registrado correctamente", Toast.LENGTH_SHORT).show();
                    limpiarFormulario();
                    cargarAvances(proyecto.getIdProyecto());
                } else {
                    Toast.makeText(getContext(),
                            ApiErrorMessages.forCode(response.code()), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AvancePartidaDto> call, Throwable t) {
                if (!isAdded()) return;
                btnRegistrar.setEnabled(true);
                Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarFormulario() {
        spinnerCuadrilla.setSelection(0);
        spinnerEstandar.setSelection(0);
        editNombrePartida.setText("");
        btnFecha.setText("Seleccionar fecha");
        fechaSeleccionada = null;
        editCantidadEjecutada.setText("");
        spinnerUnidad.setSelection(0);
        editCantidadProgramada.setText("");
        editObservaciones.setText("");
    }

    // ─── VISIBILIDAD ─────────────────────────────────────────────────────────

    private void actualizarVisibilidadLista() {
        if (listaAvances.isEmpty()) {
            mostrarVacio(proyectoSeleccionado
                    ? "No hay avances registrados para este proyecto"
                    : "Selecciona un proyecto para ver los avances");
        } else {
            ocultarVacio();
        }
    }

    private void mostrarVacio(String msg) {
        textEmpty.setText(msg);
        textEmpty.setVisibility(View.VISIBLE);
        recyclerAvances.setVisibility(View.GONE);
    }

    private void ocultarVacio() {
        textEmpty.setVisibility(View.GONE);
        recyclerAvances.setVisibility(View.VISIBLE);
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    static class AvanceObraAdapter extends RecyclerView.Adapter<AvanceObraAdapter.VH> {

        private final List<AvancePartidaDto> data;

        AvanceObraAdapter(List<AvancePartidaDto> data) { this.data = data; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_avance_obra, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) { h.bind(data.get(position)); }

        @Override
        public int getItemCount() { return data.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView textNombrePartida, textActividad, textCuadrilla, textCantidad, textUnidad,
                     textFecha, textPorcentaje;
            ProgressBar progressAvance;
            View layoutProgreso;

            VH(View v) {
                super(v);
                textNombrePartida = v.findViewById(R.id.textNombrePartida);
                textActividad     = v.findViewById(R.id.textActividad);
                textCuadrilla     = v.findViewById(R.id.textCuadrilla);
                textCantidad      = v.findViewById(R.id.textCantidad);
                textUnidad        = v.findViewById(R.id.textUnidad);
                textFecha         = v.findViewById(R.id.textFecha);
                textPorcentaje    = v.findViewById(R.id.textPorcentaje);
                progressAvance    = v.findViewById(R.id.progressAvance);
                layoutProgreso    = v.findViewById(R.id.layoutProgreso);
            }

            void bind(AvancePartidaDto a) {
                textNombrePartida.setText(a.getNombrePartida() != null ? a.getNombrePartida() : "Partida");
                textActividad.setText(a.getNombreActividad() != null ? a.getNombreActividad() : itemView.getContext().getString(R.string.sin_estandar));
                textCuadrilla.setText(a.getNombreCuadrilla() != null ? a.getNombreCuadrilla() : itemView.getContext().getString(R.string.sin_cuadrilla));
                textFecha.setText(a.getFechaRegistro() != null ? a.getFechaRegistro() : "—");

                String cantStr = a.getCantidadEjecutada() != null
                        ? String.format(Locale.US, "%.2f", a.getCantidadEjecutada()) : "—";
                textCantidad.setText(cantStr);
                textUnidad.setText(a.getUnidadMedida() != null ? a.getUnidadMedida() : "");

                if (a.getCantidadProgramada() != null && a.getCantidadProgramada() > 0
                        && a.getCantidadEjecutada() != null) {
                    layoutProgreso.setVisibility(View.VISIBLE);
                    double ratio = (a.getCantidadEjecutada() / a.getCantidadProgramada()) * 100;
                    int pct = (int) Math.max(0, Math.min(100, ratio));
                    progressAvance.setProgress(pct);
                    textPorcentaje.setText(pct + "%");
                } else {
                    layoutProgreso.setVisibility(View.GONE);
                }
            }
        }
    }
}
