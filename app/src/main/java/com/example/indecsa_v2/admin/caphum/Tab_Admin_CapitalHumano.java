package com.example.indecsa_v2.admin.caphum;

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

/**
 * Tab de Capital Humano — panel administrador.
 *
 * Muestra la lista de proyectos. Al seleccionar uno abre
 * AsignarCapitalHumanoDialog con las reglas:
 *   - Máximo 1 contratista asignado
 *   - Máximo 8 trabajadores asignados
 *   - No se pueden repetir trabajadores
 *
 * Reutiliza el layout fragment_tab__admin__proyecto.xml y
 * el item_card_admin_proyecto.xml para no crear vistas extra.
 */
public class Tab_Admin_CapitalHumano extends Fragment {

    private RecyclerView      recyclerViewAreas;
    private EditText          editBuscarArea;
    private AppCompatButton   btnBuscar;

    private ProyectoCapHumAdapter adapter;
    private List<ProyectoDto>     listaProyectos;
    private List<ProyectoDto>     listaFiltrada;

    public Tab_Admin_CapitalHumano() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Reutilizamos el mismo layout que Tab_Admin_Proyecto
        View vista = inflater.inflate(R.layout.fragment_tab__admin__proyecto, container, false);

        recyclerViewAreas = vista.findViewById(R.id.recyclerViewAreas);
        editBuscarArea    = vista.findViewById(R.id.editBuscarArea);
        btnBuscar         = vista.findViewById(R.id.btnBuscar);

        listaProyectos = new ArrayList<>();
        listaFiltrada  = new ArrayList<>();
        adapter        = new ProyectoCapHumAdapter(listaFiltrada);

        recyclerViewAreas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAreas.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> filtrar(editBuscarArea.getText().toString()));

        cargarProyectos();
        return vista;
    }

    // ─── CARGA ───────────────────────────────────────────────────────────────

    private void cargarProyectos() {
        recyclerViewAreas.setVisibility(View.GONE);

        RetrofitClient.getApiService().getAllProyectos().enqueue(new Callback<List<ProyectoDto>>() {
            @Override
            public void onResponse(Call<List<ProyectoDto>> call, Response<List<ProyectoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProyectos.clear();
                    listaProyectos.addAll(response.body());
                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaProyectos);
                    recyclerViewAreas.setVisibility(listaProyectos.isEmpty() ? View.GONE : View.VISIBLE);
                    adapter.notifyDataSetChanged();
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

    // ─── FILTRO ───────────────────────────────────────────────────────────────

    private void filtrar(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaProyectos);
        } else {
            String q = texto.toLowerCase().trim();
            for (ProyectoDto p : listaProyectos) {
                boolean nombre = p.getNombreProyecto() != null && p.getNombreProyecto().toLowerCase().contains(q);
                boolean tipo   = p.getTipoProyecto()   != null && p.getTipoProyecto().toLowerCase().contains(q);
                boolean lugar  = p.getLugarProyecto()  != null && p.getLugarProyecto().toLowerCase().contains(q);
                if (nombre || tipo || lugar) listaFiltrada.add(p);
            }
        }
        adapter.notifyDataSetChanged();
        recyclerViewAreas.setVisibility(listaFiltrada.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ─── ABRIR DIALOG DE ASIGNACIÓN ──────────────────────────────────────────

    private void abrirAsignacion(ProyectoDto proyecto) {
        AsignarCapitalHumanoDialog dialog = AsignarCapitalHumanoDialog.newInstance(
                proyecto.getIdProyecto(),
                proyecto.getNombreProyecto()
        );
        dialog.show(getParentFragmentManager(), "asignar_capital_humano");
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    private class ProyectoCapHumAdapter extends RecyclerView.Adapter<ProyectoCapHumAdapter.VH> {

        private final List<ProyectoDto> items;

        ProyectoCapHumAdapter(List<ProyectoDto> items) { this.items = items; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_admin_proyecto, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView  textNombreCompleto, textEspecialidad, textUbicacion,
                    textCorreo, textNumero, badgeEstado;
            RatingBar ratingBar;

            VH(View itemView) {
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

                itemView.setOnClickListener(v -> abrirAsignacion(p));
            }
        }
    }
}