package com.example.indecsa_v2.admin.personalobra;

import android.os.Bundle;
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
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tab de Personal en Obra para el panel ADMIN.
 *
 * Flujo:
 *   1) Carga la lista de proyectos en el spinner.
 *   2) Carga la lista de trabajadores una sola vez (mapa por id).
 *   3) Al seleccionar proyecto, carga sus asignaciones y sus cuadrillas.
 *   4) Renderiza tarjetas con nombre, puesto, cuadrilla y estatus.
 *   5) Filtra localmente por estatus y por texto.
 */
public class Tab_Admin_PersonalObra extends Fragment {

    private static final String ESTATUS_TODOS = "Todos";

    private Spinner         spinnerProyecto;
    private Spinner         spinnerEstatus;
    private EditText        editBuscar;
    private AppCompatButton btnBuscar;
    private TextView        textEmpty;
    private RecyclerView    recyclerPersonalObra;

    private final List<ProyectoDto>                       listaProyectos    = new ArrayList<>();
    private final Map<Integer, TrabajadorDto>             mapTrabajadores   = new HashMap<>();
    private final List<CuadrillaDto>                      listaCuadrillas   = new ArrayList<>();
    private final List<AsignacionTrabajadorProyectoDto>   listaAsignaciones = new ArrayList<>();
    private final List<AsignacionTrabajadorProyectoDto>   listaFiltrada     = new ArrayList<>();

    private PersonalObraAdapter adapter;
    private boolean             proyectoSeleccionado = false;

    public Tab_Admin_PersonalObra() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_tab__personal_obra, container, false);

        spinnerProyecto      = vista.findViewById(R.id.spinnerProyecto);
        spinnerEstatus       = vista.findViewById(R.id.spinnerEstatus);
        editBuscar           = vista.findViewById(R.id.editBuscar);
        btnBuscar            = vista.findViewById(R.id.btnBuscar);
        textEmpty            = vista.findViewById(R.id.textEmpty);
        recyclerPersonalObra = vista.findViewById(R.id.recyclerPersonalObra);

        adapter = new PersonalObraAdapter(listaFiltrada);
        recyclerPersonalObra.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPersonalObra.setAdapter(adapter);

        setupSpinnerEstatus();
        btnBuscar.setOnClickListener(v -> aplicarFiltros());

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
                } else {
                    Toast.makeText(getContext(), "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<ProyectoDto>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                        if (t.getIdTrabajador() != null) {
                            mapTrabajadores.put(t.getIdTrabajador(), t);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<TrabajadorDto>> call, Throwable t) { /* silencioso */ }
        });
    }

    private void cargarAsignaciones(Integer idProyecto) {
        RetrofitClient.getApiService().getAsignacionesByProyecto(idProyecto).enqueue(
            new Callback<List<AsignacionTrabajadorProyectoDto>>() {
                @Override
                public void onResponse(Call<List<AsignacionTrabajadorProyectoDto>> call,
                                       Response<List<AsignacionTrabajadorProyectoDto>> response) {
                    if (!isAdded()) return;
                    listaAsignaciones.clear();
                    if (response.isSuccessful() && response.body() != null) {
                        listaAsignaciones.addAll(response.body());
                    }
                    aplicarFiltros();
                }
                @Override
                public void onFailure(Call<List<AsignacionTrabajadorProyectoDto>> call, Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Error al cargar asignaciones: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void cargarCuadrillas(Integer idProyecto) {
        RetrofitClient.getApiService().getCuadrillasByProyecto(idProyecto).enqueue(
            new Callback<List<CuadrillaDto>>() {
                @Override
                public void onResponse(Call<List<CuadrillaDto>> call, Response<List<CuadrillaDto>> response) {
                    if (!isAdded()) return;
                    listaCuadrillas.clear();
                    if (response.isSuccessful() && response.body() != null) {
                        listaCuadrillas.addAll(response.body());
                    }
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onFailure(Call<List<CuadrillaDto>> call, Throwable t) { /* silencioso */ }
            }
        );
    }

    // ─── SPINNERS ────────────────────────────────────────────────────────────

    private void setupSpinnerProyecto() {
        List<String> nombres = new ArrayList<>();
        nombres.add("— Selecciona un proyecto —");
        for (ProyectoDto p : listaProyectos) nombres.add(p.getNombreProyecto());

        ArrayAdapter<String> adapterNombres = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, nombres);
        adapterNombres.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProyecto.setAdapter(adapterNombres);

        spinnerProyecto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    proyectoSeleccionado = false;
                    listaAsignaciones.clear();
                    listaCuadrillas.clear();
                    listaFiltrada.clear();
                    adapter.notifyDataSetChanged();
                    mostrarVacio("Selecciona un proyecto para ver su personal en obra");
                } else {
                    proyectoSeleccionado = true;
                    ProyectoDto p = listaProyectos.get(position - 1);
                    cargarAsignaciones(p.getIdProyecto());
                    cargarCuadrillas(p.getIdProyecto());
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupSpinnerEstatus() {
        List<String> opciones = new ArrayList<>();
        opciones.add(ESTATUS_TODOS);
        opciones.add("ACTIVO");
        opciones.add("SUSPENDIDO");
        opciones.add("INCAPACIDAD");
        opciones.add("VACACIONES");
        opciones.add("FINALIZADO");
        opciones.add("CANCELADO");

        ArrayAdapter<String> adapterEstatus = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, opciones);
        adapterEstatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstatus.setAdapter(adapterEstatus);

        spinnerEstatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { aplicarFiltros(); }
            @Override public void onNothingSelected(AdapterView<?> p) { }
        });
    }

    // ─── FILTROS ─────────────────────────────────────────────────────────────

    private void aplicarFiltros() {
        listaFiltrada.clear();
        String estatusSel = spinnerEstatus.getSelectedItem() != null
                ? spinnerEstatus.getSelectedItem().toString() : ESTATUS_TODOS;
        String texto = editBuscar.getText() != null ? editBuscar.getText().toString().toLowerCase().trim() : "";

        for (AsignacionTrabajadorProyectoDto a : listaAsignaciones) {
            boolean okEstatus = ESTATUS_TODOS.equals(estatusSel)
                    || (a.getEstatusAsignacion() != null && a.getEstatusAsignacion().equalsIgnoreCase(estatusSel));
            boolean okTexto = texto.isEmpty() || coincideTexto(a, texto);
            if (okEstatus && okTexto) listaFiltrada.add(a);
        }

        adapter.notifyDataSetChanged();
        if (listaFiltrada.isEmpty()) {
            String msg;
            if (!proyectoSeleccionado) {
                msg = "Selecciona un proyecto para ver su personal en obra";
            } else if (listaAsignaciones.isEmpty()) {
                msg = "No hay personal asignado a este proyecto";
            } else {
                msg = "Ningún registro coincide con el filtro";
            }
            mostrarVacio(msg);
        } else {
            ocultarVacio();
        }
    }

    private boolean coincideTexto(AsignacionTrabajadorProyectoDto a, String q) {
        TrabajadorDto t = a.getIdTrabajador() != null ? mapTrabajadores.get(a.getIdTrabajador()) : null;
        boolean nombre = t != null && t.getNombreTrabajador() != null
                && t.getNombreTrabajador().toLowerCase().contains(q);
        boolean puesto = a.getPuestoEnProyecto() != null
                && a.getPuestoEnProyecto().toLowerCase().contains(q);
        boolean cuadrilla = false;
        for (CuadrillaDto c : listaCuadrillas) {
            if (c.getNombreCuadrilla() != null && c.getNombreCuadrilla().toLowerCase().contains(q)) {
                cuadrilla = true;
                break;
            }
        }
        return nombre || puesto || cuadrilla;
    }

    private void mostrarVacio(String mensaje) {
        textEmpty.setText(mensaje);
        textEmpty.setVisibility(View.VISIBLE);
        recyclerPersonalObra.setVisibility(View.GONE);
    }

    private void ocultarVacio() {
        textEmpty.setVisibility(View.GONE);
        recyclerPersonalObra.setVisibility(View.VISIBLE);
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    private class PersonalObraAdapter extends RecyclerView.Adapter<PersonalObraAdapter.ViewHolder> {

        private final List<AsignacionTrabajadorProyectoDto> data;

        PersonalObraAdapter(List<AsignacionTrabajadorProyectoDto> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_personal_obra, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int position) {
            h.bind(data.get(position));
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textAvatar, textNombre, textPuesto, textCuadrilla, textPeriodo, badgeEstatus;

            ViewHolder(View itemView) {
                super(itemView);
                textAvatar    = itemView.findViewById(R.id.textAvatar);
                textNombre    = itemView.findViewById(R.id.textNombreCompleto);
                textPuesto    = itemView.findViewById(R.id.textPuesto);
                textCuadrilla = itemView.findViewById(R.id.textCuadrilla);
                textPeriodo   = itemView.findViewById(R.id.textPeriodo);
                badgeEstatus  = itemView.findViewById(R.id.badgeEstatus);
            }

            void bind(AsignacionTrabajadorProyectoDto a) {
                TrabajadorDto t = a.getIdTrabajador() != null
                        ? mapTrabajadores.get(a.getIdTrabajador()) : null;
                String nombre = t != null && t.getNombreTrabajador() != null
                        ? t.getNombreTrabajador() : "Trabajador #" + a.getIdTrabajador();

                textAvatar.setText(nombre.isEmpty()
                        ? "?" : String.valueOf(nombre.charAt(0)).toUpperCase());
                textNombre.setText(nombre);
                textPuesto.setText(a.getPuestoEnProyecto() != null
                        ? a.getPuestoEnProyecto() : "Sin puesto asignado");

                // Cuadrilla: por ahora no hay relación 1-1 con asignación, así que se muestra
                // el conjunto de cuadrillas activas del proyecto como referencia.
                String cuadrillaTexto = listaCuadrillas.isEmpty()
                        ? "Sin cuadrilla registrada"
                        : "Cuadrillas: " + listaCuadrillas.size();
                textCuadrilla.setText(cuadrillaTexto);

                String inicio = a.getFechaInicio() != null ? a.getFechaInicio() : "—";
                String fin    = a.getFechaFinEstimada() != null ? a.getFechaFinEstimada() : "—";
                textPeriodo.setText(inicio + "  →  " + fin);

                String estatus = a.getEstatusAsignacion() != null ? a.getEstatusAsignacion() : "";
                pintarBadge(estatus);
            }

            private void pintarBadge(String estatus) {
                switch (estatus) {
                    case "ACTIVO":
                        badgeEstatus.setText("● Activo");
                        badgeEstatus.setBackgroundResource(R.drawable.item_disp_verde);
                        break;
                    case "VACACIONES":
                        badgeEstatus.setText("● Vacaciones");
                        badgeEstatus.setBackgroundResource(R.drawable.item_disp_verde);
                        break;
                    case "INCAPACIDAD":
                        badgeEstatus.setText("● Incapacidad");
                        badgeEstatus.setBackgroundResource(R.drawable.item_disp_naranja);
                        break;
                    case "SUSPENDIDO":
                        badgeEstatus.setText("● Suspendido");
                        badgeEstatus.setBackgroundResource(R.drawable.item_disp_amarillo);
                        break;
                    case "FINALIZADO":
                        badgeEstatus.setText("● Finalizado");
                        badgeEstatus.setBackgroundResource(R.drawable.item_disp_rojo);
                        break;
                    case "CANCELADO":
                        badgeEstatus.setText("● Cancelado");
                        badgeEstatus.setBackgroundResource(R.drawable.item_disp_rojo);
                        break;
                    default:
                        badgeEstatus.setText("● —");
                        badgeEstatus.setBackgroundResource(R.drawable.item_disp_rojo);
                        break;
                }
            }
        }
    }
}
