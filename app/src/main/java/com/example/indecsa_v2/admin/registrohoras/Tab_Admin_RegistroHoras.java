package com.example.indecsa_v2.admin.registrohoras;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.example.indecsa_v2.models.AsignacionTrabajadorProyectoDto;
import com.example.indecsa_v2.models.CuadrillaDto;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.models.RegistroHorasDto;
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab_Admin_RegistroHoras extends Fragment {

    private static final TimeZone TZ_MX = TimeZone.getTimeZone("America/Mexico_City");
    private static final String SIN_CUADRILLA = "Sin cuadrilla";
    private static final String SIN_TRABAJADOR = "— Selecciona trabajador —";

    private Spinner         spinnerProyecto;
    private Spinner         spinnerTrabajador;
    private Spinner         spinnerCuadrilla;
    private AppCompatButton btnFecha;
    private EditText        editHoras;
    private Spinner         spinnerTipoPeriodo;
    private EditText        editObservaciones;
    private AppCompatButton btnRegistrar;
    private TextView        textEmpty;
    private RecyclerView    recyclerRegistros;

    private final List<ProyectoDto>                     listaProyectos    = new ArrayList<>();
    private final List<AsignacionTrabajadorProyectoDto> listaAsignaciones = new ArrayList<>();
    private final List<CuadrillaDto>                    listaCuadrillas   = new ArrayList<>();
    private final List<RegistroHorasDto>                listaRegistros    = new ArrayList<>();
    private final Map<Integer, TrabajadorDto>           mapTrabajadores   = new HashMap<>();

    private RegistroHorasAdapter adapter;
    private String fechaSeleccionada = null;
    private boolean proyectoSeleccionado = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_tab__registro_horas, container, false);

        spinnerProyecto   = vista.findViewById(R.id.spinnerProyecto);
        spinnerTrabajador = vista.findViewById(R.id.spinnerTrabajador);
        spinnerCuadrilla  = vista.findViewById(R.id.spinnerCuadrilla);
        btnFecha          = vista.findViewById(R.id.btnFecha);
        editHoras         = vista.findViewById(R.id.editHoras);
        spinnerTipoPeriodo= vista.findViewById(R.id.spinnerTipoPeriodo);
        editObservaciones = vista.findViewById(R.id.editObservaciones);
        btnRegistrar      = vista.findViewById(R.id.btnRegistrar);
        textEmpty         = vista.findViewById(R.id.textEmpty);
        recyclerRegistros = vista.findViewById(R.id.recyclerRegistros);

        adapter = new RegistroHorasAdapter(listaRegistros, mapTrabajadores);
        recyclerRegistros.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRegistros.setAdapter(adapter);

        setupSpinnerTipoPeriodo();
        setupSpinnerCuadrillaVacio();
        setupSpinnerTrabajadorVacio();
        btnFecha.setOnClickListener(v -> mostrarDatePicker());
        btnRegistrar.setOnClickListener(v -> registrarHoras());

        cargarProyectos();
        cargarTrabajadores();

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
                Toast.makeText(getContext(), "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarTrabajadores() {
        RetrofitClient.getApiService().getAllTrabajadores().enqueue(new Callback<List<TrabajadorDto>>() {
            @Override
            public void onResponse(Call<List<TrabajadorDto>> call, Response<List<TrabajadorDto>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    mapTrabajadores.clear();
                    for (TrabajadorDto t : response.body()) {
                        if (t.getIdTrabajador() != null) mapTrabajadores.put(t.getIdTrabajador(), t);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<TrabajadorDto>> call, Throwable t) { }
        });
    }

    private void cargarAsignaciones(Integer idProyecto) {
        RetrofitClient.getApiService().getAsignacionesByProyecto(idProyecto)
                .enqueue(new Callback<List<AsignacionTrabajadorProyectoDto>>() {
            @Override
            public void onResponse(Call<List<AsignacionTrabajadorProyectoDto>> call,
                                   Response<List<AsignacionTrabajadorProyectoDto>> response) {
                if (!isAdded()) return;
                listaAsignaciones.clear();
                if (response.isSuccessful() && response.body() != null) {
                    listaAsignaciones.addAll(response.body());
                }
                setupSpinnerTrabajador();
            }
            @Override
            public void onFailure(Call<List<AsignacionTrabajadorProyectoDto>> call, Throwable t) {
                if (!isAdded()) return;
                setupSpinnerTrabajador();
            }
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

    private void cargarRegistros(Integer idProyecto) {
        // Backend NO expone filtro por proyecto en /registros-horas — traemos
        // todos y filtramos client-side. Si la lista crece mucho, considerar
        // iterar getRegistrosHorasByAsignacion() por cada asignación del proyecto.
        RetrofitClient.getApiService().getAllRegistrosHoras()
                .enqueue(new Callback<List<RegistroHorasDto>>() {
            @Override
            public void onResponse(Call<List<RegistroHorasDto>> call,
                                   Response<List<RegistroHorasDto>> response) {
                if (!isAdded()) return;
                listaRegistros.clear();
                if (response.isSuccessful() && response.body() != null) {
                    int descartadosPorNull = 0;
                    for (RegistroHorasDto r : response.body()) {
                        if (idProyecto == null) {
                            listaRegistros.add(r);
                        } else if (r.getIdProyecto() != null && r.getIdProyecto().equals(idProyecto)) {
                            listaRegistros.add(r);
                        } else if (r.getIdProyecto() == null) {
                            descartadosPorNull++;
                        }
                    }
                    // Si MUCHOS registros vienen sin proyecto anidado, el backend
                    // probablemente cambió el serialization (FK plana en vez de
                    // objeto eager). El filtro client-side queda inservible:
                    // mostraríamos lista vacía cuando en realidad hay datos.
                    if (descartadosPorNull > 0 && idProyecto != null) {
                        Log.w("Tab_Admin_RegistroHoras",
                                "Descartados " + descartadosPorNull + " registros sin proyecto anidado. "
                                        + "¿Backend dejó de incluir asignacionTrabajadorProyecto eager?");
                    }
                }
                adapter.notifyDataSetChanged();
                actualizarVisibilidadLista();
            }
            @Override
            public void onFailure(Call<List<RegistroHorasDto>> call, Throwable t) {
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
                    listaAsignaciones.clear();
                    listaCuadrillas.clear();
                    listaRegistros.clear();
                    adapter.notifyDataSetChanged();
                    setupSpinnerTrabajadorVacio();
                    setupSpinnerCuadrillaVacio();
                    mostrarVacio("Selecciona un proyecto para registrar horas");
                } else {
                    proyectoSeleccionado = true;
                    // Reset SINCRÓNICO de listas dependientes: evita que el
                    // usuario registre con una selección "stale" del proyecto
                    // anterior mientras la nueva data carga async.
                    listaAsignaciones.clear();
                    listaCuadrillas.clear();
                    setupSpinnerTrabajadorVacio();
                    setupSpinnerCuadrillaVacio();

                    ProyectoDto p = listaProyectos.get(position - 1);
                    cargarAsignaciones(p.getIdProyecto());
                    cargarCuadrillas(p.getIdProyecto());
                    cargarRegistros(p.getIdProyecto());
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupSpinnerTrabajadorVacio() {
        List<String> opciones = new ArrayList<>();
        opciones.add(SIN_TRABAJADOR);
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, opciones);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrabajador.setAdapter(ad);
    }

    private void setupSpinnerTrabajador() {
        List<String> nombres = new ArrayList<>();
        nombres.add(SIN_TRABAJADOR);
        for (AsignacionTrabajadorProyectoDto a : listaAsignaciones) {
            TrabajadorDto t = a.getIdTrabajador() != null ? mapTrabajadores.get(a.getIdTrabajador()) : null;
            String nombre = t != null && t.getNombreTrabajador() != null
                    ? t.getNombreTrabajador() : "Trabajador #" + a.getIdTrabajador();
            nombres.add(nombre);
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrabajador.setAdapter(ad);
    }

    private void setupSpinnerCuadrillaVacio() {
        List<String> opciones = new ArrayList<>();
        opciones.add(SIN_CUADRILLA);
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, opciones);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuadrilla.setAdapter(ad);
    }

    private void setupSpinnerCuadrilla() {
        List<String> nombres = new ArrayList<>();
        nombres.add(SIN_CUADRILLA);
        for (CuadrillaDto c : listaCuadrillas) {
            nombres.add(c.getNombreCuadrilla() != null ? c.getNombreCuadrilla() : "Cuadrilla #" + c.getIdCuadrilla());
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuadrilla.setAdapter(ad);
    }

    private void setupSpinnerTipoPeriodo() {
        List<String> opciones = new ArrayList<>();
        opciones.add("DIARIO");
        opciones.add("SEMANAL");
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, opciones);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPeriodo.setAdapter(ad);
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

    private void registrarHoras() {
        if (!proyectoSeleccionado) {
            Toast.makeText(getContext(), "Selecciona un proyecto", Toast.LENGTH_SHORT).show();
            return;
        }

        int posTrabajador = spinnerTrabajador.getSelectedItemPosition();
        if (posTrabajador == 0) {
            Toast.makeText(getContext(), "Selecciona un trabajador", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaSeleccionada == null) {
            Toast.makeText(getContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        String horasStr = editHoras.getText() != null ? editHoras.getText().toString().trim() : "";
        if (horasStr.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa las horas trabajadas", Toast.LENGTH_SHORT).show();
            return;
        }

        double horas;
        try {
            horas = Double.parseDouble(horasStr);
            if (horas <= 0 || horas > 24) {
                Toast.makeText(getContext(), "Las horas deben estar entre 0 y 24", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Formato de horas inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // El usuario pudo cambiar de proyecto entre seleccionar trabajador y
        // pulsar Registrar; validamos contra el tamaño actual de la lista.
        int idxTrabajador = posTrabajador - 1;
        if (idxTrabajador < 0 || idxTrabajador >= listaAsignaciones.size()) {
            Toast.makeText(getContext(),
                    "La selección de trabajador ya no es válida. Vuelve a seleccionarlo.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AsignacionTrabajadorProyectoDto asignacion = listaAsignaciones.get(idxTrabajador);

        RegistroHorasDto dto = new RegistroHorasDto();
        dto.setIdAsignacionTp(asignacion.getIdAsignacionTp());
        dto.setFechaRegistro(fechaSeleccionada);
        dto.setHorasTrabajadas(horas);
        dto.setTipoPeriodo(spinnerTipoPeriodo.getSelectedItem().toString());

        int posCuadrilla = spinnerCuadrilla.getSelectedItemPosition();
        if (posCuadrilla > 0 && posCuadrilla - 1 < listaCuadrillas.size()) {
            dto.setIdCuadrilla(listaCuadrillas.get(posCuadrilla - 1).getIdCuadrilla());
        }

        String obs = editObservaciones.getText() != null ? editObservaciones.getText().toString().trim() : "";
        if (!obs.isEmpty()) dto.setObservaciones(obs);

        btnRegistrar.setEnabled(false);
        RetrofitClient.getApiService().createRegistroHoras(dto).enqueue(new Callback<RegistroHorasDto>() {
            @Override
            public void onResponse(Call<RegistroHorasDto> call, Response<RegistroHorasDto> response) {
                if (!isAdded()) return;
                btnRegistrar.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Horas registradas correctamente", Toast.LENGTH_SHORT).show();
                    limpiarFormulario();
                    // Recargar lista
                    int posProyecto = spinnerProyecto.getSelectedItemPosition();
                    if (posProyecto > 0 && posProyecto - 1 < listaProyectos.size()) {
                        cargarRegistros(listaProyectos.get(posProyecto - 1).getIdProyecto());
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Error al registrar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RegistroHorasDto> call, Throwable t) {
                if (!isAdded()) return;
                btnRegistrar.setEnabled(true);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarFormulario() {
        spinnerTrabajador.setSelection(0);
        spinnerCuadrilla.setSelection(0);
        btnFecha.setText("Seleccionar fecha");
        fechaSeleccionada = null;
        editHoras.setText("");
        spinnerTipoPeriodo.setSelection(0);
        editObservaciones.setText("");
    }

    // ─── VISIBILIDAD LISTA ────────────────────────────────────────────────────

    private void actualizarVisibilidadLista() {
        if (listaRegistros.isEmpty()) {
            mostrarVacio(proyectoSeleccionado
                    ? "No hay registros de horas para este proyecto"
                    : "Selecciona un proyecto para ver los registros");
        } else {
            ocultarVacio();
        }
    }

    private void mostrarVacio(String msg) {
        textEmpty.setText(msg);
        textEmpty.setVisibility(View.VISIBLE);
        recyclerRegistros.setVisibility(View.GONE);
    }

    private void ocultarVacio() {
        textEmpty.setVisibility(View.GONE);
        recyclerRegistros.setVisibility(View.VISIBLE);
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    static class RegistroHorasAdapter extends RecyclerView.Adapter<RegistroHorasAdapter.VH> {

        private final List<RegistroHorasDto>          data;
        private final Map<Integer, TrabajadorDto>     mapT;

        RegistroHorasAdapter(List<RegistroHorasDto> data, Map<Integer, TrabajadorDto> mapT) {
            this.data = data;
            this.mapT = mapT;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_registro_horas, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            h.bind(data.get(position));
        }

        @Override
        public int getItemCount() { return data.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView textAvatar, textNombre, textCuadrilla, textFecha, textHoras, textTipoPeriodo;

            VH(View v) {
                super(v);
                textAvatar      = v.findViewById(R.id.textAvatar);
                textNombre      = v.findViewById(R.id.textNombreTrabajador);
                textCuadrilla   = v.findViewById(R.id.textCuadrilla);
                textFecha       = v.findViewById(R.id.textFecha);
                textHoras       = v.findViewById(R.id.textHoras);
                textTipoPeriodo = v.findViewById(R.id.textTipoPeriodo);
            }

            void bind(RegistroHorasDto r) {
                String nombre = r.getNombreTrabajador() != null
                        ? r.getNombreTrabajador()
                        : (r.getIdTrabajador() != null && mapT.containsKey(r.getIdTrabajador())
                                ? mapT.get(r.getIdTrabajador()).getNombreTrabajador()
                                : "Trabajador");
                textAvatar.setText(nombre.isEmpty() ? "?" : String.valueOf(nombre.charAt(0)).toUpperCase());
                textNombre.setText(nombre);
                textCuadrilla.setText(r.getNombreCuadrilla() != null ? r.getNombreCuadrilla() : itemView.getContext().getString(R.string.sin_cuadrilla));
                textFecha.setText(r.getFechaRegistro() != null ? r.getFechaRegistro() : "—");
                textHoras.setText(r.getHorasTrabajadas() != null
                        ? String.format(Locale.US, "%.1f h", r.getHorasTrabajadas()) : "—");
                textTipoPeriodo.setText(r.getTipoPeriodo() != null ? r.getTipoPeriodo() : "");
            }
        }
    }
}
