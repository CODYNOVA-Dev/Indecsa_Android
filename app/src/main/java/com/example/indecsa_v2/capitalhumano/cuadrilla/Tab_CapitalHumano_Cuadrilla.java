package com.example.indecsa_v2.capitalhumano.cuadrilla;

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
import com.example.indecsa_v2.models.CuadrillaDto;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Gestión de cuadrillas por proyecto (equivalente a la pantalla web
 * CapitalHumano/Cuadrilla/Cuadrillas.html):
 *   - Seleccionar proyecto.
 *   - Crear cuadrilla (nombre + frente de trabajo).
 *   - Listar cuadrillas del proyecto.
 *   - Activar / desactivar cada cuadrilla.
 *
 * Modelado sobre el patrón de {@code Tab_Admin_AvanceObra}.
 */
public class Tab_CapitalHumano_Cuadrilla extends Fragment {

    private Spinner         spinnerProyecto;
    private EditText        editNombreCuadrilla;
    private EditText        editFrenteTrabajo;
    private AppCompatButton btnCrear;
    private TextView        textEmpty;
    private RecyclerView    recyclerCuadrillas;

    private final List<ProyectoDto>  listaProyectos  = new ArrayList<>();
    private final List<CuadrillaDto> listaCuadrillas = new ArrayList<>();

    private CuadrillaAdapter adapter;
    private boolean proyectoSeleccionado = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_tab__cuadrilla, container, false);

        spinnerProyecto     = vista.findViewById(R.id.spinnerProyecto);
        editNombreCuadrilla = vista.findViewById(R.id.editNombreCuadrilla);
        editFrenteTrabajo   = vista.findViewById(R.id.editFrenteTrabajo);
        btnCrear            = vista.findViewById(R.id.btnCrear);
        textEmpty           = vista.findViewById(R.id.textEmpty);
        recyclerCuadrillas  = vista.findViewById(R.id.recyclerCuadrillas);

        adapter = new CuadrillaAdapter(listaCuadrillas, this::togglarEstatus);
        recyclerCuadrillas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCuadrillas.setAdapter(adapter);

        btnCrear.setOnClickListener(v -> crearCuadrilla());

        cargarProyectos();

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
                adapter.notifyDataSetChanged();
                actualizarVisibilidadLista();
            }
            @Override
            public void onFailure(Call<List<CuadrillaDto>> call, Throwable t) {
                if (!isAdded()) return;
                actualizarVisibilidadLista();
            }
        });
    }

    // ─── SPINNER PROYECTO ────────────────────────────────────────────────────

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
                    adapter.notifyDataSetChanged();
                    mostrarVacio("Selecciona un proyecto para ver sus cuadrillas");
                } else {
                    proyectoSeleccionado = true;
                    ProyectoDto p = listaProyectos.get(position - 1);
                    cargarCuadrillas(p.getIdProyecto());
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private ProyectoDto proyectoActual() {
        int pos = spinnerProyecto.getSelectedItemPosition();
        if (pos <= 0 || pos - 1 >= listaProyectos.size()) return null;
        return listaProyectos.get(pos - 1);
    }

    // ─── CREAR ───────────────────────────────────────────────────────────────

    private void crearCuadrilla() {
        ProyectoDto proyecto = proyectoActual();
        if (proyecto == null) {
            Toast.makeText(getContext(), "Selecciona un proyecto", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = editNombreCuadrilla.getText() != null
                ? editNombreCuadrilla.getText().toString().trim() : "";
        if (nombre.isEmpty()) {
            Toast.makeText(getContext(), "El nombre de la cuadrilla es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        String frente = editFrenteTrabajo.getText() != null
                ? editFrenteTrabajo.getText().toString().trim() : "";

        CuadrillaDto dto = new CuadrillaDto();
        dto.setIdProyecto(proyecto.getIdProyecto());
        dto.setNombreCuadrilla(nombre);
        if (!frente.isEmpty()) dto.setFrenteTrabajo(frente);
        dto.setEstatusCuadrilla("ACTIVO");

        btnCrear.setEnabled(false);
        RetrofitClient.getApiService().createCuadrilla(dto).enqueue(new Callback<CuadrillaDto>() {
            @Override
            public void onResponse(Call<CuadrillaDto> call, Response<CuadrillaDto> response) {
                if (!isAdded()) return;
                btnCrear.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Cuadrilla creada correctamente", Toast.LENGTH_SHORT).show();
                    editNombreCuadrilla.setText("");
                    editFrenteTrabajo.setText("");
                    cargarCuadrillas(proyecto.getIdProyecto());
                } else {
                    Toast.makeText(getContext(),
                            ApiErrorMessages.forCode(response.code()), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CuadrillaDto> call, Throwable t) {
                if (!isAdded()) return;
                btnCrear.setEnabled(true);
                Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── ACTIVAR / DESACTIVAR ────────────────────────────────────────────────

    private void togglarEstatus(CuadrillaDto c) {
        if (c == null || c.getIdCuadrilla() == null) return;
        final String nuevoEstatus = "ACTIVO".equals(c.getEstatusCuadrilla()) ? "INACTIVO" : "ACTIVO";

        Map<String, Object> body = new HashMap<>();
        body.put("estatus", nuevoEstatus);

        RetrofitClient.getApiService().patchCuadrillaEstatus(c.getIdCuadrilla(), body)
                .enqueue(new Callback<CuadrillaDto>() {
            @Override
            public void onResponse(Call<CuadrillaDto> call, Response<CuadrillaDto> response) {
                if (!isAdded()) return;
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "ACTIVO".equals(nuevoEstatus) ? "Cuadrilla activada" : "Cuadrilla desactivada",
                            Toast.LENGTH_SHORT).show();
                    ProyectoDto p = proyectoActual();
                    if (p != null) cargarCuadrillas(p.getIdProyecto());
                } else {
                    Toast.makeText(getContext(),
                            ApiErrorMessages.forCode(response.code()), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CuadrillaDto> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── VISIBILIDAD ─────────────────────────────────────────────────────────

    private void actualizarVisibilidadLista() {
        if (listaCuadrillas.isEmpty()) {
            mostrarVacio(proyectoSeleccionado
                    ? "No hay cuadrillas creadas para este proyecto"
                    : "Selecciona un proyecto para ver sus cuadrillas");
        } else {
            textEmpty.setVisibility(View.GONE);
            recyclerCuadrillas.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarVacio(String msg) {
        textEmpty.setText(msg);
        textEmpty.setVisibility(View.VISIBLE);
        recyclerCuadrillas.setVisibility(View.GONE);
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    interface OnToggleListener { void onToggle(CuadrillaDto c); }

    static class CuadrillaAdapter extends RecyclerView.Adapter<CuadrillaAdapter.VH> {

        private final List<CuadrillaDto> data;
        private final OnToggleListener toggleListener;

        CuadrillaAdapter(List<CuadrillaDto> data, OnToggleListener toggleListener) {
            this.data = data;
            this.toggleListener = toggleListener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_cuadrilla, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) { h.bind(data.get(position)); }

        @Override
        public int getItemCount() { return data.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView textNombre, textFrente, textEstatus;
            AppCompatButton btnToggle;

            VH(View v) {
                super(v);
                textNombre  = v.findViewById(R.id.textNombreCuadrilla);
                textFrente  = v.findViewById(R.id.textFrenteTrabajo);
                textEstatus = v.findViewById(R.id.textEstatus);
                btnToggle   = v.findViewById(R.id.btnToggleEstatus);
            }

            void bind(CuadrillaDto c) {
                textNombre.setText(c.getNombreCuadrilla() != null
                        ? c.getNombreCuadrilla() : "Cuadrilla #" + c.getIdCuadrilla());
                textFrente.setText(c.getFrenteTrabajo() != null && !c.getFrenteTrabajo().isEmpty()
                        ? c.getFrenteTrabajo() : "Sin frente asignado");

                boolean activo = "ACTIVO".equals(c.getEstatusCuadrilla());
                textEstatus.setText(activo ? "Activo" : "Inactivo");
                textEstatus.setBackgroundResource(activo
                        ? R.drawable.item_disp_verde : R.drawable.item_disp_rojo);

                btnToggle.setText(activo ? "Desactivar" : "Activar");
                btnToggle.setOnClickListener(v -> {
                    if (toggleListener != null) toggleListener.onToggle(c);
                });
            }
        }
    }
}
