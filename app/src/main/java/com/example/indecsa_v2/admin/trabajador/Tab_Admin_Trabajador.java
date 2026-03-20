package com.example.indecsa_v2.admin.trabajador;

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
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab_Admin_Trabajador extends Fragment {

    private RecyclerView        recyclerViewAreas;
    private EditText            editBuscarArea;
    private AppCompatButton     btnBuscar;
    private AppCompatButton     btnAgregar;

    private TrabajadorAdapter   trabajadorAdapter;
    private List<TrabajadorDto> listaTrabajadores;
    private List<TrabajadorDto> listaTrabajadoresFiltrada;

    public Tab_Admin_Trabajador() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_tab__admin__trabajador, container, false);

        recyclerViewAreas = vista.findViewById(R.id.recyclerViewAreas);
        editBuscarArea    = vista.findViewById(R.id.editBuscarArea);
        btnBuscar         = vista.findViewById(R.id.btnBuscar);
        btnAgregar        = vista.findViewById(R.id.btnAgregar);

        listaTrabajadores         = new ArrayList<>();
        listaTrabajadoresFiltrada = new ArrayList<>();
        trabajadorAdapter         = new TrabajadorAdapter(listaTrabajadoresFiltrada);

        recyclerViewAreas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAreas.setAdapter(trabajadorAdapter);

        btnBuscar.setOnClickListener(v -> filtrarTrabajadores(editBuscarArea.getText().toString()));

        // ✅ Configurar botón Agregar
        AppCompatButton btnAgregar = vista.findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            AgregarTrabajadorDialog dialog = new AgregarTrabajadorDialog();
            dialog.setOnAgregadoListener(this::cargarTrabajadores);
            dialog.show(getParentFragmentManager(), "agregar_trabajador");
        });
        cargarTrabajadores();
        return vista;
    }

    private void cargarTrabajadores() {
        recyclerViewAreas.setVisibility(View.GONE);

        RetrofitClient.getApiService().getAllTrabajadores().enqueue(new Callback<List<TrabajadorDto>>() {
            @Override
            public void onResponse(Call<List<TrabajadorDto>> call, Response<List<TrabajadorDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTrabajadores.clear();
                    listaTrabajadores.addAll(response.body());
                    listaTrabajadoresFiltrada.clear();
                    listaTrabajadoresFiltrada.addAll(listaTrabajadores);
                    recyclerViewAreas.setVisibility(listaTrabajadores.isEmpty() ? View.GONE : View.VISIBLE);
                    trabajadorAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar trabajadores", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<TrabajadorDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarTrabajadores(String texto) {
        listaTrabajadoresFiltrada.clear();
        if (texto.isEmpty()) {
            listaTrabajadoresFiltrada.addAll(listaTrabajadores);
        } else {
            String q = texto.toLowerCase().trim();
            for (TrabajadorDto t : listaTrabajadores) {
                boolean nombre       = t.getNombreTrabajador()       != null && t.getNombreTrabajador().toLowerCase().contains(q);
                boolean especialidad = t.getEspecialidadTrabajador() != null && t.getEspecialidadTrabajador().toLowerCase().contains(q);
                boolean correo       = t.getCorreoTrabajador()       != null && t.getCorreoTrabajador().toLowerCase().contains(q);
                boolean telefono     = t.getTelefonoTrabajador()     != null && t.getTelefonoTrabajador().toLowerCase().contains(q);
                boolean ubicacion    = t.getUbicacionTrabajador()    != null && t.getUbicacionTrabajador().toLowerCase().contains(q);
                boolean experiencia  = t.getExperiencia()            != null && t.getExperiencia().toLowerCase().contains(q);
                if (nombre || especialidad || correo || telefono || ubicacion || experiencia) {
                    listaTrabajadoresFiltrada.add(t);
                }
            }
        }
        trabajadorAdapter.notifyDataSetChanged();
        recyclerViewAreas.setVisibility(listaTrabajadoresFiltrada.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ─── ABRIR DIALOG ────────────────────────────────────────────────────────

    private void abrirDetalleTrabajador(TrabajadorDto trabajador) {
        DetalleTrabajadorDialog dialog = DetalleTrabajadorDialog.newInstance(trabajador);
        dialog.setOnCambioListener(this::cargarTrabajadores);
        dialog.show(getParentFragmentManager(), "detalle_trabajador");
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    private class TrabajadorAdapter extends RecyclerView.Adapter<TrabajadorAdapter.ViewHolder> {

        private final List<TrabajadorDto> trabajadores;

        TrabajadorAdapter(List<TrabajadorDto> trabajadores) { this.trabajadores = trabajadores; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_admin_trabajador, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(trabajadores.get(position));
        }

        @Override
        public int getItemCount() { return trabajadores.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView  textAvatar;
            TextView  textNombreCompleto;
            TextView  textEspecialidad;
            TextView  textNumero;
            TextView  textCorreo;
            TextView  textUbicacion;
            TextView  badgeEstado;
            RatingBar ratingBar;

            ViewHolder(View itemView) {
                super(itemView);
                textAvatar         = itemView.findViewById(R.id.textAvatar);
                textNombreCompleto = itemView.findViewById(R.id.textNombreCompleto);
                textEspecialidad   = itemView.findViewById(R.id.textEspecialidad);
                textNumero         = itemView.findViewById(R.id.textNumero);
                textCorreo         = itemView.findViewById(R.id.textCorreo);
                textUbicacion      = itemView.findViewById(R.id.textUbicacion);
                badgeEstado        = itemView.findViewById(R.id.badgeEstado);
                ratingBar          = itemView.findViewById(R.id.ratingBar);
            }

            void bind(TrabajadorDto t) {
                String nombre = t.getNombreTrabajador();
                textAvatar.setText(nombre != null && !nombre.isEmpty()
                        ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?");
                textNombreCompleto.setText(nombre);
                textEspecialidad.setText(t.getEspecialidadTrabajador());
                textNumero.setText(t.getTelefonoTrabajador());
                textCorreo.setText(t.getCorreoTrabajador());
                textUbicacion.setText(t.getUbicacionTrabajador());

                String estado = t.getEstadoTrabajador() != null ? t.getEstadoTrabajador() : "";
                switch (estado) {
                    case "ACTIVO":
                        badgeEstado.setText("● Activo");
                        badgeEstado.setBackgroundResource(R.drawable.item_disp_verde);
                        break;
                    case "VACACIONES":
                        badgeEstado.setText("● Vacaciones");
                        badgeEstado.setBackgroundResource(R.drawable.item_disp_verde);
                        break;
                    case "INACTIVO":
                        badgeEstado.setText("● Inactivo");
                        badgeEstado.setBackgroundResource(R.drawable.item_disp_rojo);
                        break;
                    case "BAJA":
                        badgeEstado.setText("● Baja");
                        badgeEstado.setBackgroundResource(R.drawable.item_disp_rojo);
                        break;
                    default:
                        badgeEstado.setText("● —");
                        badgeEstado.setBackgroundResource(R.drawable.item_disp_rojo);
                        break;
                }

                Integer calificacion = t.getCalificacionTrabajador();
                if (calificacion != null && calificacion > 0) {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(calificacion.floatValue());
                } else {
                    ratingBar.setVisibility(View.GONE);
                }

                // ✅ Ahora abre el dialog real
                itemView.setOnClickListener(v -> abrirDetalleTrabajador(t));
            }
        }
    }
}