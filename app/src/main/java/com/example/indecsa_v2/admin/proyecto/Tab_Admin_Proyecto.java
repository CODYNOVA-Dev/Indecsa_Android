package com.example.indecsa_v2.admin.proyecto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class Tab_Admin_Proyecto extends Fragment {

    private RecyclerView      recyclerViewAreas;
    private EditText          editBuscarArea;
    private AppCompatButton   btnBuscar;
    private AppCompatButton   btnAgregar;

    private ProyectoAdapter   proyectoAdapter;
    private List<ProyectoDto> listaProyectos;
    private List<ProyectoDto> listaProyectosFiltrada;

    public Tab_Admin_Proyecto() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_tab__admin__proyecto, container, false);

        recyclerViewAreas = vista.findViewById(R.id.recyclerViewAreas);
        editBuscarArea    = vista.findViewById(R.id.editBuscarArea);
        btnBuscar         = vista.findViewById(R.id.btnBuscar);
        btnAgregar        = vista.findViewById(R.id.btnAgregar);

        listaProyectos         = new ArrayList<>();
        listaProyectosFiltrada = new ArrayList<>();
        proyectoAdapter        = new ProyectoAdapter(listaProyectosFiltrada);

        recyclerViewAreas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAreas.setAdapter(proyectoAdapter);

        btnBuscar.setOnClickListener(v -> filtrarProyectos(editBuscarArea.getText().toString()));

        // ✅ Configurar botón Agregar
        AppCompatButton btnAgregar = vista.findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            AgregarProyectoDialog dialog = new AgregarProyectoDialog();
            dialog.setOnAgregadoListener(this::cargarProyectos);
            dialog.show(getParentFragmentManager(), "agregar_proyecto");
        });

        cargarProyectos();
        return vista;
    }

    private void cargarProyectos() {
        recyclerViewAreas.setVisibility(View.GONE);

        RetrofitClient.getApiService().getAllProyectos().enqueue(new Callback<List<ProyectoDto>>() {
            @Override
            public void onResponse(Call<List<ProyectoDto>> call, Response<List<ProyectoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProyectos.clear();
                    listaProyectos.addAll(response.body());
                    listaProyectosFiltrada.clear();
                    listaProyectosFiltrada.addAll(listaProyectos);
                    recyclerViewAreas.setVisibility(listaProyectos.isEmpty() ? View.GONE : View.VISIBLE);
                    proyectoAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<ProyectoDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarProyectos(String texto) {
        listaProyectosFiltrada.clear();
        if (texto.isEmpty()) {
            listaProyectosFiltrada.addAll(listaProyectos);
        } else {
            String q = texto.toLowerCase().trim();
            for (ProyectoDto p : listaProyectos) {
                boolean nombre  = p.getNombreProyecto() != null && p.getNombreProyecto().toLowerCase().contains(q);
                boolean tipo    = p.getTipoProyecto()   != null && p.getTipoProyecto().toLowerCase().contains(q);
                boolean lugar   = p.getLugarProyecto()  != null && p.getLugarProyecto().toLowerCase().contains(q);
                if (nombre || tipo || lugar) listaProyectosFiltrada.add(p);
            }
        }
        proyectoAdapter.notifyDataSetChanged();
        recyclerViewAreas.setVisibility(listaProyectosFiltrada.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ─── ABRIR DIALOG ────────────────────────────────────────────────────────

    private void abrirDetalleProyecto(ProyectoDto proyecto) {
        DetalleProyectoDialog dialog = DetalleProyectoDialog.newInstance(proyecto);
        dialog.setOnCambioListener(this::cargarProyectos);
        dialog.show(getParentFragmentManager(), "detalle_proyecto");
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    private class ProyectoAdapter extends RecyclerView.Adapter<ProyectoAdapter.ViewHolder> {

        private final List<ProyectoDto> proyectos;

        ProyectoAdapter(List<ProyectoDto> proyectos) { this.proyectos = proyectos; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_admin_proyecto, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(proyectos.get(position));
        }

        @Override
        public int getItemCount() { return proyectos.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView  textNombreCompleto;
            TextView  textEspecialidad;
            TextView  textUbicacion;
            TextView  textCorreo;
            TextView  textNumero;
            TextView  badgeEstado;
            RatingBar ratingBar;

            ViewHolder(View itemView) {
                super(itemView);
                textNombreCompleto = itemView.findViewById(R.id.textNombreCompleto);
                textEspecialidad   = itemView.findViewById(R.id.textEspecialidad);
                textUbicacion      = itemView.findViewById(R.id.textUbicacion);
                textCorreo         = itemView.findViewById(R.id.textCorreo);
                textNumero         = itemView.findViewById(R.id.textNumero);
                badgeEstado        = itemView.findViewById(R.id.badgeEstado);
                ratingBar          = itemView.findViewById(R.id.ratingBar);
            }

            void bind(ProyectoDto p) {
                textNombreCompleto.setText(p.getNombreProyecto());
                textEspecialidad.setText(p.getTipoProyecto());
                textUbicacion.setText(p.getLugarProyecto());
                textCorreo.setText("Tipo:");
                textNumero.setText(p.getTipoProyecto() != null ? p.getTipoProyecto() : "—");
                badgeEstado.setText("● Activo");
                badgeEstado.setBackgroundResource(R.drawable.item_disp_verde);
                ratingBar.setVisibility(View.GONE);

                // ✅ Ahora abre el dialog real
                itemView.setOnClickListener(v -> abrirDetalleProyecto(p));
            }
        }
    }
}