package com.example.indecsa_v2.capitalhumano.contratista;

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
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab_CapitalHumano_Contratista extends Fragment {

    private RecyclerView recyclerView;
    private android.widget.EditText editBuscar;
    private AppCompatButton btnBuscar;

    private ContratistaCapHumAdapter adapter;
    private List<Contratista> lista = new ArrayList<>();
    private List<Contratista> listaFiltrada = new ArrayList<>();

    public Tab_CapitalHumano_Contratista() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_tab__admin__contratista, container, false);

        recyclerView = vista.findViewById(R.id.recyclerViewAreas);
        editBuscar   = vista.findViewById(R.id.editBuscarArea);
        btnBuscar    = vista.findViewById(R.id.btnBuscar);

        // CapHum solo visualiza — ocultar botón Agregar
        View layoutAgregar = vista.findViewById(R.id.layoutAgregar);
        if (layoutAgregar != null) layoutAgregar.setVisibility(View.GONE);

        adapter = new ContratistaCapHumAdapter(listaFiltrada);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> filtrar(editBuscar.getText().toString()));
        cargar();
        return vista;
    }

    private void cargar() {
        RetrofitClient.getApiService().getAllContratistas().enqueue(new Callback<List<Contratista>>() {
            @Override
            public void onResponse(Call<List<Contratista>> call, Response<List<Contratista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lista.clear();
                    lista.addAll(response.body());
                    listaFiltrada.clear();
                    listaFiltrada.addAll(lista);
                    recyclerView.setVisibility(lista.isEmpty() ? View.GONE : View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar contratistas", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Contratista>> call, Throwable t) {
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
            for (Contratista c : lista) {
                if ((c.getNombreContratista() != null && c.getNombreContratista().toLowerCase().contains(q)) ||
                        (c.getRfcContratista()    != null && c.getRfcContratista().toLowerCase().contains(q))) {
                    listaFiltrada.add(c);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // ─── Adapter ─────────────────────────────────────────────────────────────

    private class ContratistaCapHumAdapter extends RecyclerView.Adapter<ContratistaCapHumAdapter.VH> {
        private final List<Contratista> items;
        ContratistaCapHumAdapter(List<Contratista> items) { this.items = items; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_admin_contratista, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(items.get(pos)); }
        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView textAvatar, textNombreCompleto, textUbicacion, textNumero,
                    textCorreo, textEspecialidad, badgeEstado;
            RatingBar ratingBar;

            VH(View v) {
                super(v);
                textAvatar         = v.findViewById(R.id.textAvatar);
                textNombreCompleto = v.findViewById(R.id.textNombreCompleto);
                textUbicacion      = v.findViewById(R.id.textUbicacion);
                textNumero         = v.findViewById(R.id.textNumero);
                textCorreo         = v.findViewById(R.id.textCorreo);
                textEspecialidad   = v.findViewById(R.id.textEspecialidad);
                badgeEstado        = v.findViewById(R.id.badgeEstado);
                ratingBar          = v.findViewById(R.id.ratingBar);
            }

            void bind(Contratista c) {
                String nombre = c.getNombreContratista();
                textAvatar.setText(nombre != null && !nombre.isEmpty()
                        ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?");
                textNombreCompleto.setText(nombre);
                textEspecialidad.setText(c.getRfcContratista());
                textUbicacion.setText(c.getUbicacionContratista());
                textNumero.setText(c.getTelefonoContratista());
                textCorreo.setText(c.getCorreoContratista());

                if ("ACTIVO".equals(c.getEstadoContratista())) {
                    badgeEstado.setText("● Activo");
                    badgeEstado.setBackgroundResource(R.drawable.item_disp_verde);
                } else {
                    badgeEstado.setText("● Inactivo");
                    badgeEstado.setBackgroundResource(R.drawable.item_disp_rojo);
                }

                if (c.getCalificacionContratista() != null && c.getCalificacionContratista() > 0)
                    ratingBar.setRating(c.getCalificacionContratista().floatValue());

                // ── Solo lectura: usa el dialog readonly ──
                itemView.setOnClickListener(v -> {
                    DetalleContratistaReadonlyDialog dialog =
                            DetalleContratistaReadonlyDialog.newInstance(c);
                    dialog.show(getParentFragmentManager(), "readonly_contratista");
                });
            }
        }
    }
}