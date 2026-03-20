package com.example.indecsa_v2.admin.caphum;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dialog para asignar Capital Humano a un proyecto.
 *
 * Reglas de negocio:
 *   - Máximo 1 contratista por proyecto
 *   - Máximo 8 trabajadores por proyecto
 *   - No se pueden repetir trabajadores (validado por ID)
 *
 * Uso desde Tab_Admin_CapitalHumano:
 *   AsignarCapitalHumanoDialog dialog =
 *       AsignarCapitalHumanoDialog.newInstance(proyectoId, proyectoNombre);
 *   dialog.show(getParentFragmentManager(), "asignar_capital_humano");
 *
 * NOTA: Este dialog gestiona el estado local de la asignación.
 * Cuando el backend exponga endpoints para asignar/desasignar,
 * reemplaza los métodos guardarAsignacion() con las llamadas Retrofit.
 */
public class AsignarCapitalHumanoDialog extends DialogFragment {

    // ─── Límites ─────────────────────────────────────────────────────────────
    private static final int MAX_CONTRATISTAS = 1;
    private static final int MAX_TRABAJADORES = 8;

    // ─── Args ────────────────────────────────────────────────────────────────
    private static final String ARG_PROYECTO_ID     = "proyectoId";
    private static final String ARG_PROYECTO_NOMBRE = "proyectoNombre";

    // ─── Estado de asignación ────────────────────────────────────────────────
    private final List<Contratista>   contratistasAsignados  = new ArrayList<>();
    private final List<TrabajadorDto> trabajadoresAsignados  = new ArrayList<>();

    // ─── Listas completas cargadas de la API ─────────────────────────────────
    private List<Contratista>   todosContratistas  = new ArrayList<>();
    private List<TrabajadorDto> todosTrabajadores  = new ArrayList<>();

    // ─── Adapters ────────────────────────────────────────────────────────────
    private AsignadosAdapter<Contratista>   adapterContratistasAsignados;
    private AsignadosAdapter<TrabajadorDto> adapterTrabajadoresAsignados;

    // ─── Views ───────────────────────────────────────────────────────────────
    private TextView tvContadorContratistas;
    private TextView tvContadorTrabajadores;

    // ─── Factory ─────────────────────────────────────────────────────────────
    public static AsignarCapitalHumanoDialog newInstance(int proyectoId, String proyectoNombre) {
        AsignarCapitalHumanoDialog d = new AsignarCapitalHumanoDialog();
        Bundle args = new Bundle();
        args.putInt   (ARG_PROYECTO_ID,     proyectoId);
        args.putString(ARG_PROYECTO_NOMBRE, proyectoNombre);
        d.setArguments(args);
        return d;
    }

    // ─── Lifecycle ───────────────────────────────────────────────────────────

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_asignar_capital_humano, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95f);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            getDialog().setCanceledOnTouchOutside(false);
        }

        Bundle a = getArguments();
        String nombreProyecto = a != null ? a.getString(ARG_PROYECTO_NOMBRE, "Proyecto") : "Proyecto";

        // Título
        TextView tvTitulo = view.findViewById(R.id.tvTituloProyecto);
        if (tvTitulo != null) tvTitulo.setText(nombreProyecto);

        // Contadores
        tvContadorContratistas = view.findViewById(R.id.tvContadorContratistas);
        tvContadorTrabajadores = view.findViewById(R.id.tvContadorTrabajadores);

        // RecyclerViews de asignados
        RecyclerView rvContratistasAsignados = view.findViewById(R.id.rvContratistasAsignados);
        RecyclerView rvTrabajadoresAsignados = view.findViewById(R.id.rvTrabajadoresAsignados);

        adapterContratistasAsignados = new AsignadosAdapter<>(
                contratistasAsignados,
                this::getNombreContratista,
                this::quitarContratista
        );
        adapterTrabajadoresAsignados = new AsignadosAdapter<>(
                trabajadoresAsignados,
                this::getNombreTrabajador,
                this::quitarTrabajador
        );

        rvContratistasAsignados.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTrabajadoresAsignados.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContratistasAsignados.setAdapter(adapterContratistasAsignados);
        rvTrabajadoresAsignados.setAdapter(adapterTrabajadoresAsignados);

        // Botones para agregar
        AppCompatButton btnAgregarContratista = view.findViewById(R.id.btnAgregarContratista);
        AppCompatButton btnAgregarTrabajador  = view.findViewById(R.id.btnAgregarTrabajador);
        AppCompatButton btnGuardar            = view.findViewById(R.id.btnGuardarAsignacion);
        AppCompatButton btnCerrar             = view.findViewById(R.id.btnCerrarAsignacion);

        btnAgregarContratista.setOnClickListener(v -> mostrarSelectorContratista());
        btnAgregarTrabajador.setOnClickListener(v -> mostrarSelectorTrabajador());
        btnGuardar.setOnClickListener(v -> guardarAsignacion());
        btnCerrar.setOnClickListener(v -> dismiss());

        // Cargar datos
        cargarContratistas();
        cargarTrabajadores();
        actualizarContadores();
    }

    // ─── CARGA DE DATOS ──────────────────────────────────────────────────────

    private void cargarContratistas() {
        RetrofitClient.getApiService().getAllContratistas().enqueue(new Callback<List<Contratista>>() {
            @Override
            public void onResponse(Call<List<Contratista>> call, Response<List<Contratista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todosContratistas = response.body();
                }
            }
            @Override
            public void onFailure(Call<List<Contratista>> call, Throwable t) {
                Toast.makeText(getContext(), "No se pudieron cargar los contratistas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarTrabajadores() {
        RetrofitClient.getApiService().getAllTrabajadores().enqueue(new Callback<List<TrabajadorDto>>() {
            @Override
            public void onResponse(Call<List<TrabajadorDto>> call, Response<List<TrabajadorDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todosTrabajadores = response.body();
                }
            }
            @Override
            public void onFailure(Call<List<TrabajadorDto>> call, Throwable t) {
                Toast.makeText(getContext(), "No se pudieron cargar los trabajadores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── SELECTOR DE CONTRATISTA ─────────────────────────────────────────────

    private void mostrarSelectorContratista() {
        if (contratistasAsignados.size() >= MAX_CONTRATISTAS) {
            Toast.makeText(getContext(),
                    "Solo se puede asignar 1 contratista por proyecto.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (todosContratistas.isEmpty()) {
            Toast.makeText(getContext(), "Cargando contratistas, intenta de nuevo.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filtra los que ya están asignados
        List<String> opciones = new ArrayList<>();
        List<Contratista> disponibles = new ArrayList<>();
        for (Contratista c : todosContratistas) {
            boolean yaAsignado = false;
            for (Contratista a : contratistasAsignados) {
                if (a.getIdContratista() != null && a.getIdContratista().equals(c.getIdContratista())) {
                    yaAsignado = true;
                    break;
                }
            }
            if (!yaAsignado) {
                disponibles.add(c);
                opciones.add(c.getNombreContratista());
            }
        }

        if (disponibles.isEmpty()) {
            Toast.makeText(getContext(), "No hay contratistas disponibles.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Muestra un AlertDialog con la lista
        String[] items = opciones.toArray(new String[0]);
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Seleccionar contratista")
                .setItems(items, (dialog, which) -> {
                    Contratista seleccionado = disponibles.get(which);
                    contratistasAsignados.add(seleccionado);
                    adapterContratistasAsignados.notifyDataSetChanged();
                    actualizarContadores();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ─── SELECTOR DE TRABAJADOR ───────────────────────────────────────────────

    private void mostrarSelectorTrabajador() {
        if (trabajadoresAsignados.size() >= MAX_TRABAJADORES) {
            Toast.makeText(getContext(),
                    "Máximo " + MAX_TRABAJADORES + " trabajadores por proyecto.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (todosTrabajadores.isEmpty()) {
            Toast.makeText(getContext(), "Cargando trabajadores, intenta de nuevo.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filtra los que ya están asignados (anti-duplicados por ID)
        List<String> opciones = new ArrayList<>();
        List<TrabajadorDto> disponibles = new ArrayList<>();
        for (TrabajadorDto t : todosTrabajadores) {
            boolean yaAsignado = false;
            for (TrabajadorDto a : trabajadoresAsignados) {
                if (a.getIdTrabajador() != null && a.getIdTrabajador().equals(t.getIdTrabajador())) {
                    yaAsignado = true;
                    break;
                }
            }
            if (!yaAsignado) {
                disponibles.add(t);
                opciones.add(t.getNombreTrabajador()
                        + (t.getEspecialidadTrabajador() != null
                        ? " — " + t.getEspecialidadTrabajador() : ""));
            }
        }

        if (disponibles.isEmpty()) {
            Toast.makeText(getContext(), "No hay trabajadores disponibles.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = opciones.toArray(new String[0]);
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Seleccionar trabajador (" + trabajadoresAsignados.size() + "/" + MAX_TRABAJADORES + ")")
                .setItems(items, (dialog, which) -> {
                    TrabajadorDto seleccionado = disponibles.get(which);
                    trabajadoresAsignados.add(seleccionado);
                    adapterTrabajadoresAsignados.notifyDataSetChanged();
                    actualizarContadores();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ─── QUITAR ASIGNADOS ────────────────────────────────────────────────────

    private void quitarContratista(Contratista c) {
        contratistasAsignados.remove(c);
        adapterContratistasAsignados.notifyDataSetChanged();
        actualizarContadores();
    }

    private void quitarTrabajador(TrabajadorDto t) {
        trabajadoresAsignados.remove(t);
        adapterTrabajadoresAsignados.notifyDataSetChanged();
        actualizarContadores();
    }

    // ─── GUARDAR ASIGNACIÓN ───────────────────────────────────────────────────

    private void guardarAsignacion() {
        Bundle a = getArguments();
        int proyectoId = a != null ? a.getInt(ARG_PROYECTO_ID, -1) : -1;

        if (proyectoId == -1) {
            Toast.makeText(getContext(), "ID de proyecto inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: cuando el backend exponga el endpoint de asignación, llamarlo aquí.
        // Ejemplo:
        //   AsignacionDto dto = new AsignacionDto(proyectoId, contratistasAsignados, trabajadoresAsignados);
        //   RetrofitClient.getApiService().asignarCapitalHumano(proyectoId, dto).enqueue(...)

        // Por ahora confirmamos con un Toast resumiendo la asignación
        String resumen = "Proyecto " + proyectoId + ":\n"
                + contratistasAsignados.size() + " contratista(s)\n"
                + trabajadoresAsignados.size() + " trabajador(es)";

        Toast.makeText(getContext(), "Asignación guardada\n" + resumen, Toast.LENGTH_LONG).show();
        dismiss();
    }

    // ─── CONTADORES ──────────────────────────────────────────────────────────

    private void actualizarContadores() {
        if (tvContadorContratistas != null) {
            tvContadorContratistas.setText(
                    contratistasAsignados.size() + "/" + MAX_CONTRATISTAS);
        }
        if (tvContadorTrabajadores != null) {
            tvContadorTrabajadores.setText(
                    trabajadoresAsignados.size() + "/" + MAX_TRABAJADORES);
        }
    }

    // ─── HELPERS DE NOMBRE ───────────────────────────────────────────────────

    private String getNombreContratista(Contratista c) {
        return c.getNombreContratista() != null ? c.getNombreContratista() : "Sin nombre";
    }

    private String getNombreTrabajador(TrabajadorDto t) {
        String nombre = t.getNombreTrabajador() != null ? t.getNombreTrabajador() : "Sin nombre";
        String esp    = t.getEspecialidadTrabajador();
        return esp != null && !esp.isEmpty() ? nombre + " — " + esp : nombre;
    }

    // ─── ADAPTER GENÉRICO DE ASIGNADOS ───────────────────────────────────────

    /**
     * Adapter genérico que muestra chips/filas con botón "✕" para quitar.
     * Funciona tanto para Contratista como para TrabajadorDto.
     */
    private static class AsignadosAdapter<T>
            extends RecyclerView.Adapter<AsignadosAdapter.VH> {

        interface NombreExtractor<T> { String nombre(T item); }
        interface QuitarCallback<T>  { void quitar(T item); }

        private final List<T>           items;
        private final NombreExtractor<T> nombreFn;
        private final QuitarCallback<T>  quitarFn;

        AsignadosAdapter(List<T> items, NombreExtractor<T> nombreFn, QuitarCallback<T> quitarFn) {
            this.items    = items;
            this.nombreFn = nombreFn;
            this.quitarFn = quitarFn;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Layout inline simple: nombre + botón quitar
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_asignado_chip, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            T item = items.get(position);
            holder.tvNombre.setText(nombreFn.nombre(item));
            holder.btnQuitar.setOnClickListener(v -> quitarFn.quitar(item));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView       tvNombre;
            AppCompatButton btnQuitar;
            VH(View v) {
                super(v);
                tvNombre  = v.findViewById(R.id.tvNombreAsignado);
                btnQuitar = v.findViewById(R.id.btnQuitarAsignado);
            }
        }
    }
}