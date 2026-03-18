package com.example.indecsa_v2.admin.contratista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.network.ApiService;
import com.example.indecsa_v2.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Tab_Admin_Contratista extends Fragment {

    private RecyclerView recyclerViewAreas;
    private ProgressBar progressBar;
    private TextView textNoContratistas;
    private EditText editBuscarArea;
    private AppCompatButton btnBuscar;

    private ContratistaAdapter contratistaAdapter;
    private List<Contratista> listaContratistas;
    private List<Contratista> listaContratistasFiltrada;
    private ApiService apiService;

    public Tab_Admin_Contratista() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_tab__admin__contratista, container, false);

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Inicializar vistas
        recyclerViewAreas       = vista.findViewById(R.id.recyclerViewAreas);
        editBuscarArea          = vista.findViewById(R.id.editBuscarArea);
        btnBuscar               = vista.findViewById(R.id.btnBuscar);
        // Si tu layout tiene ProgressBar y TextView de estado vacío, enlázalos aquí:
        // progressBar          = vista.findViewById(R.id.progressBar);
        // textNoContratistas   = vista.findViewById(R.id.textNoContratistas);

        // Configurar RecyclerView
        listaContratistas        = new ArrayList<>();
        listaContratistasFiltrada = new ArrayList<>();
        contratistaAdapter       = new ContratistaAdapter(listaContratistasFiltrada);
        recyclerViewAreas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAreas.setAdapter(contratistaAdapter);

        // Configurar botón buscar
        btnBuscar.setOnClickListener(v -> filtrarContratistas(editBuscarArea.getText().toString()));

        // Cargar contratistas al iniciar
        cargarContratistas();

        return vista;
    }

    // -------------------------------------------------------------------------
    // Carga de datos desde la API
    // -------------------------------------------------------------------------
    private void cargarContratistas() {
        // progressBar.setVisibility(View.VISIBLE);
        recyclerViewAreas.setVisibility(View.GONE);

        apiService.obtenerContratistas().enqueue(new Callback<List<Contratista>>() {
            @Override
            public void onResponse(Call<List<Contratista>> call, Response<List<Contratista>> response) {
                // progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    listaContratistas.clear();
                    listaContratistas.addAll(response.body());

                    listaContratistasFiltrada.clear();
                    listaContratistasFiltrada.addAll(listaContratistas);

                    if (listaContratistas.isEmpty()) {
                        // textNoContratistas.setVisibility(View.VISIBLE);
                        recyclerViewAreas.setVisibility(View.GONE);
                    } else {
                        // textNoContratistas.setVisibility(View.GONE);
                        recyclerViewAreas.setVisibility(View.VISIBLE);
                        contratistaAdapter.notifyDataSetChanged();
                    }
                } else {
                    // textNoContratistas.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Error al cargar contratistas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Contratista>> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                // textNoContratistas.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Filtrado por texto
    // -------------------------------------------------------------------------
    private void filtrarContratistas(String texto) {
        listaContratistasFiltrada.clear();

        if (texto.isEmpty()) {
            listaContratistasFiltrada.addAll(listaContratistas);
        } else {
            String textoBusqueda = texto.toLowerCase().trim();
            for (Contratista contratista : listaContratistas) {
                if (contratista.getNombreContratista().toLowerCase().contains(textoBusqueda) ||
                        contratista.getRfc().toLowerCase().contains(textoBusqueda)            ||
                        contratista.getUbicacion().toLowerCase().contains(textoBusqueda)      ||
                        contratista.getEspecialidad().toLowerCase().contains(textoBusqueda)) {
                    listaContratistasFiltrada.add(contratista);
                }
            }
        }

        contratistaAdapter.notifyDataSetChanged();

        if (listaContratistasFiltrada.isEmpty()) {
            // textNoContratistas.setText("No se encontraron contratistas con \"" + texto + "\"");
            // textNoContratistas.setVisibility(View.VISIBLE);
            recyclerViewAreas.setVisibility(View.GONE);
        } else {
            // textNoContratistas.setVisibility(View.GONE);
            recyclerViewAreas.setVisibility(View.VISIBLE);
        }
    }

    // -------------------------------------------------------------------------
    // ADAPTER (clase interna)
    // -------------------------------------------------------------------------
    private class ContratistaAdapter extends RecyclerView.Adapter<ContratistaAdapter.ViewHolder> {

        private final List<Contratista> contratistas;

        public ContratistaAdapter(List<Contratista> contratistas) {
            this.contratistas = contratistas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_contratista, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(contratistas.get(position));
        }

        @Override
        public int getItemCount() {
            return contratistas.size();
        }

        // -----------------------------------------------------------------
        // ViewHolder
        // -----------------------------------------------------------------
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView     textAvatar;
            TextView     textNombreCompleto;
            TextView     textUbicacion;
            TextView     textNumero;
            TextView     textCorreo;
            TextView     textEspecialidad;
            TextView     badgeEstado;
            RatingBar    ratingBar;
            CardView     cardContratista;

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
                cardContratista    = (CardView) itemView; // raíz del item
            }

            void bind(Contratista contratista) {
                // Avatar: inicial del nombre
                if (contratista.getNombreContratista() != null && !contratista.getNombreContratista().isEmpty()) {
                    textAvatar.setText(String.valueOf(contratista.getNombreContratista().charAt(0)).toUpperCase());
                }

                // RFC + nombre completo
                textNombreCompleto.setText(contratista.getRfc());

                // Demás campos
                textUbicacion.setText(contratista.getUbicacion());
                textNumero.setText(contratista.getTelefono());
                textCorreo.setText(contratista.getCorreo());
                textEspecialidad.setText(contratista.getEspecialidad());

                // Badge de estado
                if ("Activo".equals(contratista.getEstadoContratista())) {
                    badgeEstado.setText("● Activo");
                    badgeEstado.setBackgroundResource(R.drawable.item_disp_verde);
                } else {
                    badgeEstado.setText("● Inactivo");
                    badgeEstado.setBackgroundResource(R.drawable.item_disp_rojo); // ajusta el drawable si es distinto
                }

                // Rating
                if (contratista.getCalificacion() > 0) {
                    ratingBar.setRating(contratista.getCalificacion());
                }

                // Click en la tarjeta
                cardContratista.setOnClickListener(v -> abrirDetalleContratista(contratista));
            }
        }
    }

    // -------------------------------------------------------------------------
    // Navegación al detalle (adapta el ID de navegación según tu nav_graph)
    // -------------------------------------------------------------------------
    private void abrirDetalleContratista(Contratista contratista) {
        Bundle bundle = new Bundle();
        bundle.putInt("contratistaId", contratista.getIdContratista());
        bundle.putString("contratistaNombre", contratista.getNombreContratista());

        // Ejemplo de navegación — cambia el ID de destino según tu nav_graph:
        // Navigation.findNavController(requireView())
        //         .navigate(R.id.action_tab_admin_contratista_to_detalle, bundle);

        Toast.makeText(getContext(), "Contratista: " + contratista.getNombreContratista(), Toast.LENGTH_SHORT).show();
    }
}