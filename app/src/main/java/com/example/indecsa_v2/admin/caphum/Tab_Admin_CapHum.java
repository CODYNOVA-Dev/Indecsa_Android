package com.example.indecsa_v2.admin.caphum;

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
import com.example.indecsa_v2.admin.contratista.ConfirmEliminarDialog;
import com.example.indecsa_v2.models.EmpleadoDto;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab_Admin_CapHum extends Fragment {

    private RecyclerView            recyclerViewAreas;
    private EditText                editBuscarArea;
    private AppCompatButton         btnBuscar;
    private AppCompatButton         btnNuevoEmpleado;

    private EmpleadoCapHumAdapter   adapter;
    private final List<EmpleadoDto> lista         = new ArrayList<>();
    private final List<EmpleadoDto> listaFiltrada = new ArrayList<>();

    public Tab_Admin_CapHum() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_tab__admin__cap_hum, container, false);

        recyclerViewAreas = vista.findViewById(R.id.recyclerViewAreas);
        editBuscarArea    = vista.findViewById(R.id.editBuscarArea);
        btnBuscar         = vista.findViewById(R.id.btnBuscar);
        btnNuevoEmpleado  = vista.findViewById(R.id.btnNuevoEmpleado);

        adapter = new EmpleadoCapHumAdapter(listaFiltrada);
        recyclerViewAreas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAreas.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> filtrar(editBuscarArea.getText().toString()));

        if (btnNuevoEmpleado != null)
            btnNuevoEmpleado.setOnClickListener(v -> {
                EmpleadoCapHumFormDialog dialog = EmpleadoCapHumFormDialog.paraCrear();
                dialog.setOnGuardadoListener(this::cargar);
                dialog.show(getParentFragmentManager(), "crear_empleado_caphum");
            });

        cargar();
        return vista;
    }

    // ─── CARGA ───────────────────────────────────────────────────────────────

    private void cargar() {
        recyclerViewAreas.setVisibility(View.GONE);

        // Backend no expone /empleados/rol/{id}; traemos todos y filtramos por rol.
        RetrofitClient.getApiService().getAllEmpleados()
                .enqueue(new Callback<List<EmpleadoDto>>() {
                    @Override
                    public void onResponse(Call<List<EmpleadoDto>> call,
                                           Response<List<EmpleadoDto>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            lista.clear();
                            for (EmpleadoDto e : response.body()) {
                                if ("CAPITAL_HUMANO".equals(e.getNombreRol())) {
                                    lista.add(e);
                                }
                            }
                            listaFiltrada.clear();
                            listaFiltrada.addAll(lista);
                            recyclerViewAreas.setVisibility(
                                    lista.isEmpty() ? View.GONE : View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forCode(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<EmpleadoDto>> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── FILTRO ───────────────────────────────────────────────────────────────

    private void filtrar(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(lista);
        } else {
            String q = texto.toLowerCase().trim();
            for (EmpleadoDto e : lista) {
                boolean nombre = e.getNombreEmpleado() != null
                        && e.getNombreEmpleado().toLowerCase().contains(q);
                boolean correo = e.getCorreoEmpleado() != null
                        && e.getCorreoEmpleado().toLowerCase().contains(q);
                if (nombre || correo) listaFiltrada.add(e);
            }
        }
        adapter.notifyDataSetChanged();
        recyclerViewAreas.setVisibility(listaFiltrada.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    private class EmpleadoCapHumAdapter
            extends RecyclerView.Adapter<EmpleadoCapHumAdapter.VH> {

        private final List<EmpleadoDto> items;

        EmpleadoCapHumAdapter(List<EmpleadoDto> items) { this.items = items; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_admin_empleado_caphum, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(items.get(pos)); }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView        textAvatar, textNombreCompleto, textCorreo,
                    textId, textDescripcion, badgeRol;
            AppCompatButton btnEditar, btnEliminar;

            VH(View v) {
                super(v);
                textAvatar         = v.findViewById(R.id.textAvatar);
                textNombreCompleto = v.findViewById(R.id.textNombreCompleto);
                textCorreo         = v.findViewById(R.id.textCorreo);
                textId             = v.findViewById(R.id.textId);
                textDescripcion    = v.findViewById(R.id.textDescripcion);
                badgeRol           = v.findViewById(R.id.badgeRol);
                btnEditar          = v.findViewById(R.id.btnEditar);
                btnEliminar        = v.findViewById(R.id.btnEliminar);
            }

            void bind(EmpleadoDto e) {
                String nombre = e.getNombreEmpleado();

                // ✅ Null-checks en todos los campos para evitar crash
                if (textAvatar != null)
                    textAvatar.setText(nombre != null && !nombre.isEmpty()
                            ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?");

                if (textNombreCompleto != null)
                    textNombreCompleto.setText(nombre != null ? nombre : "—");

                if (textCorreo != null)
                    textCorreo.setText(e.getCorreoEmpleado() != null
                            ? e.getCorreoEmpleado() : "—");

                if (textId != null)
                    textId.setText("ID: " + (e.getIdEmpleado() != null
                            ? e.getIdEmpleado() : "—"));

                if (textDescripcion != null)
                    textDescripcion.setText("Gestión de asignaciones de personal");

                if (badgeRol != null) {
                    badgeRol.setText("CAPITAL_HUMANO");
                    badgeRol.setBackgroundResource(R.drawable.item_disp_verde);
                }

                if (btnEditar != null)
                    btnEditar.setOnClickListener(v -> {
                        EmpleadoCapHumFormDialog dialog = EmpleadoCapHumFormDialog.paraEditar(e);
                        dialog.setOnGuardadoListener(Tab_Admin_CapHum.this::cargar);
                        dialog.show(getParentFragmentManager(), "editar_empleado_caphum");
                    });

                if (btnEliminar != null)
                    btnEliminar.setOnClickListener(v -> {
                        if (e.getIdEmpleado() == null) return;
                        // Antes: borrado inmediato sin confirmación → toque accidental
                        // en pantalla táctil eliminaba al empleado para siempre.
                        ConfirmEliminarDialog confirm = ConfirmEliminarDialog.newInstance(
                                "¿Eliminar empleado?",
                                "Esta acción no se puede deshacer.\n\""
                                        + (nombre != null ? nombre : "—")
                                        + "\" será eliminado permanentemente."
                        );
                        confirm.setOnConfirmListener(() -> ejecutarEliminarEmpleado(e, nombre, v));
                        confirm.show(getParentFragmentManager(), "confirm_eliminar_empleado");
                    });
            }
        }

        private void ejecutarEliminarEmpleado(EmpleadoDto e, String nombre, View v) {
            RetrofitClient.getApiService().deleteEmpleado(e.getIdEmpleado())
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful()) {
                                lista.remove(e);
                                listaFiltrada.remove(e);
                                notifyDataSetChanged();
                                Toast.makeText(v.getContext(),
                                        nombre + " eliminado",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(v.getContext(),
                                        ApiErrorMessages.forCode(response.code()),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            if (!isAdded()) return;
                            Toast.makeText(v.getContext(),
                                    ApiErrorMessages.forThrowable(t),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}