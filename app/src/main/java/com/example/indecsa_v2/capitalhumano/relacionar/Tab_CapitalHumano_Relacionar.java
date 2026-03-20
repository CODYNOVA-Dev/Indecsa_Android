package com.example.indecsa_v2.capitalhumano.relacionar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab_CapitalHumano_Relacionar extends Fragment {

    private RecyclerView recyclerView;
    private EditText editBuscar;
    private AppCompatButton btnBuscar;

    private RelacionarAdapter adapter;
    private List<ProyectoDto> lista = new ArrayList<>();
    private List<ProyectoDto> listaFiltrada = new ArrayList<>();

    public Tab_CapitalHumano_Relacionar() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_tab__capital_humano__relacionar, container, false);

        recyclerView = vista.findViewById(R.id.recyclerViewAreas);
        editBuscar   = vista.findViewById(R.id.editBuscarArea);
        btnBuscar    = vista.findViewById(R.id.btnBuscar);

        // CapHum solo asigna, no agrega proyectos nuevos
        View layoutAgregar = vista.findViewById(R.id.layoutAgregar);
        if (layoutAgregar != null) layoutAgregar.setVisibility(View.GONE);

        adapter = new RelacionarAdapter(listaFiltrada);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> filtrar(editBuscar.getText().toString()));

        cargar();
        return vista;
    }

    // ─── Carga ───────────────────────────────────────────────────────────────

    private void cargar() {
        recyclerView.setVisibility(View.GONE);

        RetrofitClient.getApiService().getAllProyectos().enqueue(new Callback<List<ProyectoDto>>() {
            @Override
            public void onResponse(Call<List<ProyectoDto>> call, Response<List<ProyectoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lista.clear();
                    lista.addAll(response.body());
                    listaFiltrada.clear();
                    listaFiltrada.addAll(lista);
                    recyclerView.setVisibility(lista.isEmpty() ? View.GONE : View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<ProyectoDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Filtro ───────────────────────────────────────────────────────────────

    private void filtrar(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(lista);
        } else {
            String q = texto.toLowerCase().trim();
            for (ProyectoDto p : lista) {
                boolean nombre = p.getNombreProyecto() != null && p.getNombreProyecto().toLowerCase().contains(q);
                boolean tipo   = p.getTipoProyecto()   != null && p.getTipoProyecto().toLowerCase().contains(q);
                boolean lugar  = p.getLugarProyecto()  != null && p.getLugarProyecto().toLowerCase().contains(q);
                if (nombre || tipo || lugar) listaFiltrada.add(p);
            }
        }
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(listaFiltrada.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ─── Adapter ─────────────────────────────────────────────────────────────

    private class RelacionarAdapter extends RecyclerView.Adapter<RelacionarAdapter.VH> {

        private final List<ProyectoDto> items;

        RelacionarAdapter(List<ProyectoDto> items) { this.items = items; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_caphum_relacionar, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(items.get(pos)); }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView textNombreCompleto;
            TextView textEstado;
            TextView textMunicipio;
            TextView badgeEstado;

            VH(View v) {
                super(v);
                textNombreCompleto = v.findViewById(R.id.textNombreCompleto);
                textEstado         = v.findViewById(R.id.textEstado);
                textMunicipio      = v.findViewById(R.id.textMunicipio);
                badgeEstado        = v.findViewById(R.id.badgeEstado);
            }

            void bind(ProyectoDto p) {
                textNombreCompleto.setText(p.getNombreProyecto());

                // Reutilizamos textEstado para mostrar el tipo de proyecto
                if (textEstado != null)
                    textEstado.setText(p.getTipoProyecto() != null ? p.getTipoProyecto() : "—");

                // Reutilizamos textMunicipio para mostrar el lugar
                if (textMunicipio != null)
                    textMunicipio.setText(p.getLugarProyecto() != null ? p.getLugarProyecto() : "—");

                // Badge fijo mientras el DTO no exponga estatus
                if (badgeEstado != null) {
                    badgeEstado.setText("● En curso");
                    badgeEstado.setBackgroundResource(R.drawable.item_disp_verde);
                }

                // ── Al tocar el proyecto → abre AsignacionProyectoDialog ──
                itemView.setOnClickListener(v ->
                        com.example.indecsa_v2.capitalhumano.relacionar.AsignacionProyectoDialog.newInstance(p)
                                .show(getParentFragmentManager(), "asignacion_" + p.getIdProyecto()));
            }
        }
    }
}