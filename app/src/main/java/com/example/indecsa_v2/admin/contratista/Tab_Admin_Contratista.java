package com.example.indecsa_v2.admin.contratista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
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

public class Tab_Admin_Contratista extends Fragment {

    private RecyclerView recyclerViewAreas;
    private EditText editBuscarArea;
    private AppCompatButton btnBuscar;

    private ContratistaAdapter contratistaAdapter;
    private List<Contratista> listaContratistas;
    private List<Contratista> listaContratistasFiltrada;

    public Tab_Admin_Contratista() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_tab__admin__contratista, container, false);

        recyclerViewAreas = vista.findViewById(R.id.recyclerViewAreas);
        editBuscarArea    = vista.findViewById(R.id.editBuscarArea);
        btnBuscar         = vista.findViewById(R.id.btnBuscar);

        listaContratistas         = new ArrayList<>();
        listaContratistasFiltrada = new ArrayList<>();
        contratistaAdapter        = new ContratistaAdapter(listaContratistasFiltrada);
        recyclerViewAreas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAreas.setAdapter(contratistaAdapter);

        btnBuscar.setOnClickListener(v -> filtrarContratistas(editBuscarArea.getText().toString()));

        cargarContratistas();
        return vista;
    }

    private void cargarContratistas() {
        recyclerViewAreas.setVisibility(View.GONE);

        RetrofitClient.getApiService().getAllContratistas().enqueue(new Callback<List<Contratista>>() {
            @Override
            public void onResponse(Call<List<Contratista>> call, Response<List<Contratista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaContratistas.clear();
                    listaContratistas.addAll(response.body());
                    listaContratistasFiltrada.clear();
                    listaContratistasFiltrada.addAll(listaContratistas);
                    recyclerViewAreas.setVisibility(listaContratistas.isEmpty() ? View.GONE : View.VISIBLE);
                    contratistaAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar contratistas", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Contratista>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarContratistas(String texto) {
        listaContratistasFiltrada.clear();
        if (texto.isEmpty()) {
            listaContratistasFiltrada.addAll(listaContratistas);
        } else {
            String q = texto.toLowerCase().trim();
            for (Contratista c : listaContratistas) {
                boolean nombre    = c.getNombreContratista() != null && c.getNombreContratista().toLowerCase().contains(q);
                boolean rfc       = c.getRfcContratista()   != null && c.getRfcContratista().toLowerCase().contains(q);
                boolean ubicacion = c.getUbicacionContratista() != null && c.getUbicacionContratista().toLowerCase().contains(q);
                boolean desc      = c.getDescripcionContratista() != null && c.getDescripcionContratista().toLowerCase().contains(q);
                if (nombre || rfc || ubicacion || desc) listaContratistasFiltrada.add(c);
            }
        }
        contratistaAdapter.notifyDataSetChanged();
        recyclerViewAreas.setVisibility(listaContratistasFiltrada.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ─── ABRIR DIALOG ────────────────────────────────────────────────────────

    private void abrirDetalleContratista(Contratista contratista) {
        DetalleContratistaDialog dialog = DetalleContratistaDialog.newInstance(contratista);
        dialog.setOnCambioListener(this::cargarContratistas);
        dialog.show(getParentFragmentManager(), "detalle_contratista");
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    private class ContratistaAdapter extends RecyclerView.Adapter<ContratistaAdapter.ViewHolder> {

        private final List<Contratista> contratistas;

        ContratistaAdapter(List<Contratista> contratistas) { this.contratistas = contratistas; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_admin_contratista, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(contratistas.get(position));
        }

        @Override
        public int getItemCount() { return contratistas.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView textAvatar;
            android.widget.TextView textNombreCompleto;
            android.widget.TextView textUbicacion;
            android.widget.TextView textNumero;
            android.widget.TextView textCorreo;
            android.widget.TextView textEspecialidad;
            android.widget.TextView badgeEstado;
            RatingBar               ratingBar;
            CardView                cardContratista;

            ViewHolder(View itemView) {
                super(itemView);
                textAvatar         = itemView.findViewById(R.id.textAvatar);
                textNombreCompleto = itemView.findViewById(R.id.textNombreCompleto);
                textUbicacion      = itemView.findViewById(R.id.textUbicacion);
                textNumero         = itemView.findViewById(R.id.textNumero);
                textCorreo         = itemView.findViewById(R.id.textCorreo);
                textEspecialidad   = itemView.findViewById(R.id.textEspecialidad);
                badgeEstado        = itemView.findViewById(R.id.badgeEstado);
                ratingBar          = itemView.findViewById(R.id.ratingBar);
                cardContratista    = (CardView) itemView;
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

                if (c.getCalificacionContratista() != null && c.getCalificacionContratista() > 0) {
                    ratingBar.setRating(c.getCalificacionContratista().floatValue());
                }

                // ✅ Ahora abre el dialog real
                cardContratista.setOnClickListener(v -> abrirDetalleContratista(c));
            }
        }
    }
}