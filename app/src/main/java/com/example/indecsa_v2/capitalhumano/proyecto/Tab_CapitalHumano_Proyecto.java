package com.example.indecsa_v2.capitalhumano.proyecto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
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

public class Tab_CapitalHumano_Proyecto extends Fragment {

    private RecyclerView recyclerView;
    private android.widget.EditText editBuscar;
    private AppCompatButton btnBuscar;

    private ProyectoCapHumAdapter adapter;
    private List<ProyectoDto> lista = new ArrayList<>();
    private List<ProyectoDto> listaFiltrada = new ArrayList<>();

    public Tab_CapitalHumano_Proyecto() {}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_tab__capital_humano__proyecto, container, false);

        recyclerView = vista.findViewById(R.id.recyclerViewAreas);
        editBuscar   = vista.findViewById(R.id.editBuscarArea);
        btnBuscar    = vista.findViewById(R.id.btnBuscar);

        // CapHum solo visualiza — ocultar botón Agregar
        View layoutAgregar = vista.findViewById(R.id.layoutAgregar);
        if (layoutAgregar != null) layoutAgregar.setVisibility(View.GONE);

        adapter = new ProyectoCapHumAdapter(listaFiltrada);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> filtrar(editBuscar.getText().toString()));
        cargar();
        return vista;
    }

    private void cargar() {
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

    private void filtrar(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(lista);
        } else {
            String q = texto.toLowerCase().trim();
            for (ProyectoDto p : lista) {
                if ((p.getNombreProyecto() != null && p.getNombreProyecto().toLowerCase().contains(q)) ||
                        (p.getTipoProyecto()   != null && p.getTipoProyecto().toLowerCase().contains(q))   ||
                        (p.getLugarProyecto()  != null && p.getLugarProyecto().toLowerCase().contains(q))) {
                    listaFiltrada.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // ─── Adapter ─────────────────────────────────────────────────────────────

    private class ProyectoCapHumAdapter extends RecyclerView.Adapter<ProyectoCapHumAdapter.VH> {
        private final List<ProyectoDto> items;
        ProyectoCapHumAdapter(List<ProyectoDto> items) { this.items = items; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_caphum_proyecto, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(items.get(pos)); }
        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView textNombreCompleto, textUbicacion, badgeEstado;
            RatingBar ratingBar;

            VH(View v) {
                super(v);
                textNombreCompleto = v.findViewById(R.id.textNombreCompleto);
                textUbicacion      = v.findViewById(R.id.textUbicacion);
                badgeEstado        = v.findViewById(R.id.badgeEstado);
                ratingBar          = v.findViewById(R.id.ratingBar);
            }

            void bind(ProyectoDto p) {
                textNombreCompleto.setText(p.getNombreProyecto());
                textUbicacion.setText(p.getLugarProyecto());
                badgeEstado.setText("● Activo");
                badgeEstado.setBackgroundResource(R.drawable.item_disp_verde);
                if (ratingBar != null) ratingBar.setVisibility(View.GONE);

                // ── Solo lectura: usa el dialog readonly ──
                itemView.setOnClickListener(v -> {
                    DetalleProyectoReadonlyDialog dialog =
                            DetalleProyectoReadonlyDialog.newInstance(p);
                    dialog.show(getParentFragmentManager(), "readonly_proyecto");
                });
            }
        }
    }
}