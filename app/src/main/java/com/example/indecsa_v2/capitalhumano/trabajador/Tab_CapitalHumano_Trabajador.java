package com.example.indecsa_v2.capitalhumano.trabajador;

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
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab_CapitalHumano_Trabajador extends Fragment {

    private RecyclerView recyclerView;
    private android.widget.EditText editBuscar;
    private AppCompatButton btnBuscar;

    private TrabajadorCapHumAdapter adapter;
    private List<TrabajadorDto> lista = new ArrayList<>();
    private List<TrabajadorDto> listaFiltrada = new ArrayList<>();

    public Tab_CapitalHumano_Trabajador() {}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_tab__capital_humano__trabajador, container, false);

        recyclerView = vista.findViewById(R.id.recyclerViewAreas);
        editBuscar   = vista.findViewById(R.id.editBuscarArea);
        btnBuscar    = vista.findViewById(R.id.btnBuscar);

        // CapHum solo visualiza — ocultar botón Agregar
        View layoutAgregar = vista.findViewById(R.id.layoutAgregar);
        if (layoutAgregar != null) layoutAgregar.setVisibility(View.GONE);

        adapter = new TrabajadorCapHumAdapter(listaFiltrada);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> filtrar(editBuscar.getText().toString()));
        cargar();
        return vista;
    }

    private void cargar() {
        RetrofitClient.getApiService().getAllTrabajadores().enqueue(new Callback<List<TrabajadorDto>>() {
            @Override
            public void onResponse(Call<List<TrabajadorDto>> call, Response<List<TrabajadorDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lista.clear();
                    lista.addAll(response.body());
                    listaFiltrada.clear();
                    listaFiltrada.addAll(lista);
                    recyclerView.setVisibility(lista.isEmpty() ? View.GONE : View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar trabajadores", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<TrabajadorDto>> call, Throwable t) {
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
            for (TrabajadorDto t : lista) {
                if ((t.getNombreTrabajador()       != null && t.getNombreTrabajador().toLowerCase().contains(q)) ||
                        (t.getEspecialidadTrabajador() != null && t.getEspecialidadTrabajador().toLowerCase().contains(q))) {
                    listaFiltrada.add(t);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // ─── Adapter ─────────────────────────────────────────────────────────────

    private class TrabajadorCapHumAdapter extends RecyclerView.Adapter<TrabajadorCapHumAdapter.VH> {
        private final List<TrabajadorDto> items;
        TrabajadorCapHumAdapter(List<TrabajadorDto> items) { this.items = items; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_caphum_trabajador, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(items.get(pos)); }
        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView textAvatar, textNombreCompleto, textEspecialidad, textNumero,
                    textCorreo, textUbicacion, badgeEstado;
            RatingBar ratingBar;

            VH(View v) {
                super(v);
                textAvatar         = v.findViewById(R.id.textAvatar);
                textNombreCompleto = v.findViewById(R.id.textNombreCompleto);
                textEspecialidad   = v.findViewById(R.id.textEspecialidad);
                textNumero         = v.findViewById(R.id.textNumero);
                textCorreo         = v.findViewById(R.id.textCorreo);
                textUbicacion      = v.findViewById(R.id.textUbicacion);
                badgeEstado        = v.findViewById(R.id.badgeEstado);
                ratingBar          = v.findViewById(R.id.ratingBar);
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
                    case "ACTIVO": case "VACACIONES":
                        badgeEstado.setText("● " + capitalizar(estado));
                        badgeEstado.setBackgroundResource(R.drawable.item_disp_verde);
                        break;
                    default:
                        badgeEstado.setText("● " + capitalizar(estado));
                        badgeEstado.setBackgroundResource(R.drawable.item_disp_rojo);
                        break;
                }

                Integer cal = t.getCalificacionTrabajador();
                if (cal != null && cal > 0) {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(cal.floatValue());
                } else {
                    ratingBar.setVisibility(View.GONE);
                }

                // ── Solo lectura: usa el dialog readonly ──
                itemView.setOnClickListener(v -> {
                    DetalleTrabajadorReadonlyDialog dialog =
                            DetalleTrabajadorReadonlyDialog.newInstance(t);
                    dialog.show(getParentFragmentManager(), "readonly_trabajador");
                });
            }

            private String capitalizar(String s) {
                if (s == null || s.isEmpty()) return "—";
                return s.charAt(0) + s.substring(1).toLowerCase();
            }
        }
    }
}