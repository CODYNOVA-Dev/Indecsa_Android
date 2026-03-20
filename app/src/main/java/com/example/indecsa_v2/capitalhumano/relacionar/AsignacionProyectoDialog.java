package com.example.indecsa_v2.capitalhumano.relacionar;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dialog para asignar contratista y trabajadores a un proyecto.
 *
 * Reglas:
 *   - Máximo 1 contratista
 *   - Máximo 8 trabajadores
 *   - Sin trabajadores repetidos (validado por ID)
 *
 * Paquete correcto: com.example.indecsa_v2.capitalhumano.relacionar
 * (el archivo original tenía el paquete mal declarado)
 */
public class AsignacionProyectoDialog extends DialogFragment {

    private static final String ARG_ID_PROYECTO     = "idProyecto";
    private static final String ARG_NOMBRE_PROYECTO = "nombreProyecto";

    private static final int MAX_CONTRATISTAS = 1;
    private static final int MAX_TRABAJADORES = 8;

    // ─── Estado en memoria ────────────────────────────────────────────────────
    private Contratista contratistaAsignado = null;
    private final List<TrabajadorDto> trabajadoresAsignados = new ArrayList<>();

    // ─── Listas de la API ─────────────────────────────────────────────────────
    private List<Contratista>   listaContratistas = new ArrayList<>();
    private List<TrabajadorDto> listaTrabajadores = new ArrayList<>();

    // ─── Vistas ───────────────────────────────────────────────────────────────
    private Spinner      spinnerContratista;
    private Spinner      spinnerTrabajador;
    private LinearLayout contenedorTrabajadores;
    private Button       btnEliminarContratista;
    private Button       btnAgregarTrabajador;
    private Button       btnGuardar;
    private Button       btnVolver;

    // ─── Factory ──────────────────────────────────────────────────────────────
    public static AsignacionProyectoDialog newInstance(ProyectoDto p) {
        AsignacionProyectoDialog d = new AsignacionProyectoDialog();
        Bundle args = new Bundle();
        args.putInt   (ARG_ID_PROYECTO,     p.getIdProyecto() != null ? p.getIdProyecto() : -1);
        args.putString(ARG_NOMBRE_PROYECTO, p.getNombreProyecto());
        d.setArguments(args);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null)
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detalle_relacionar, container, false);
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

        // Título
        Bundle a = getArguments();
        TextView tvTitulo = view.findViewById(R.id.tvTitulo);
        if (tvTitulo != null && a != null)
            tvTitulo.setText("Asignación: " + a.getString(ARG_NOMBRE_PROYECTO, "Proyecto"));

        // Vistas
        spinnerContratista     = view.findViewById(R.id.spinnerContratista);
        spinnerTrabajador      = view.findViewById(R.id.spinnerTrabajador);
        btnEliminarContratista = view.findViewById(R.id.btnEliminarContratista);
        btnAgregarTrabajador   = view.findViewById(R.id.btnAgregarTrabajador);
        btnGuardar             = view.findViewById(R.id.btnGuardar);
        btnVolver              = view.findViewById(R.id.btnVolver);

        // Ocultar filas estáticas de ejemplo del XML
        View fila1 = view.findViewById(R.id.btnEliminarTrabajador1);
        View fila2 = view.findViewById(R.id.btnEliminarTrabajador2);
        if (fila1 != null && fila1.getParent() instanceof LinearLayout)
            ((LinearLayout) fila1.getParent()).setVisibility(View.GONE);
        if (fila2 != null && fila2.getParent() instanceof LinearLayout)
            ((LinearLayout) fila2.getParent()).setVisibility(View.GONE);

        // Contenedor dinámico para las filas de trabajadores
        contenedorTrabajadores = new LinearLayout(getContext());
        contenedorTrabajadores.setOrientation(LinearLayout.VERTICAL);
        ViewGroup botonesInferiores = (ViewGroup) btnGuardar.getParent();
        ViewGroup cardInterna = (ViewGroup) botonesInferiores.getParent();
        int indexBotones = cardInterna.indexOfChild(botonesInferiores);
        cardInterna.addView(contenedorTrabajadores, indexBotones);

        // Estado inicial del botón contratista
        actualizarUIContratista();

        // Cargar datos
        cargarContratistas();
        cargarTrabajadores();

        // Listeners
        btnEliminarContratista.setOnClickListener(v -> toggleContratista());
        btnAgregarTrabajador.setOnClickListener(v -> agregarTrabajador());
        btnGuardar.setOnClickListener(v -> guardar());
        btnVolver.setOnClickListener(v -> dismiss());
    }

    // ─── Contratistas ────────────────────────────────────────────────────────

    private void cargarContratistas() {
        RetrofitClient.getApiService().getAllContratistas().enqueue(new Callback<List<Contratista>>() {
            @Override
            public void onResponse(Call<List<Contratista>> call, Response<List<Contratista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaContratistas.clear();
                    listaContratistas.addAll(response.body());
                    poblarSpinnerContratistas();
                }
            }
            @Override
            public void onFailure(Call<List<Contratista>> call, Throwable t) {
                Toast.makeText(getContext(), "Error cargando contratistas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void poblarSpinnerContratistas() {
        List<String> nombres = new ArrayList<>();
        nombres.add("— Seleccionar contratista —");
        for (Contratista c : listaContratistas)
            nombres.add(c.getNombreContratista());

        ArrayAdapter<String> adp = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContratista.setAdapter(adp);
    }

    /**
     * El botón tiene doble función:
     *  - Si no hay contratista asignado → asigna el seleccionado en el spinner
     *  - Si ya hay uno asignado         → lo elimina
     */
    private void toggleContratista() {
        if (contratistaAsignado != null) {
            contratistaAsignado = null;
            Toast.makeText(getContext(), "Contratista eliminado de la asignación", Toast.LENGTH_SHORT).show();
        } else {
            int pos = spinnerContratista.getSelectedItemPosition();
            if (pos == 0) {
                Toast.makeText(getContext(), "Selecciona un contratista primero", Toast.LENGTH_SHORT).show();
                return;
            }
            contratistaAsignado = listaContratistas.get(pos - 1);
            Toast.makeText(getContext(),
                    "Contratista asignado: " + contratistaAsignado.getNombreContratista(),
                    Toast.LENGTH_SHORT).show();
        }
        actualizarUIContratista();
    }

    private void actualizarUIContratista() {
        if (contratistaAsignado != null) {
            btnEliminarContratista.setText("Eliminar contratista");
            spinnerContratista.setEnabled(false);
        } else {
            btnEliminarContratista.setText("Asignar contratista");
            spinnerContratista.setEnabled(true);
        }
    }

    // ─── Trabajadores ────────────────────────────────────────────────────────

    private void cargarTrabajadores() {
        RetrofitClient.getApiService().getAllTrabajadores().enqueue(new Callback<List<TrabajadorDto>>() {
            @Override
            public void onResponse(Call<List<TrabajadorDto>> call, Response<List<TrabajadorDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTrabajadores.clear();
                    listaTrabajadores.addAll(response.body());
                    poblarSpinnerTrabajadores();
                }
            }
            @Override
            public void onFailure(Call<List<TrabajadorDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error cargando trabajadores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void poblarSpinnerTrabajadores() {
        List<String> nombres = new ArrayList<>();
        nombres.add("— Seleccionar trabajador —");
        for (TrabajadorDto t : listaTrabajadores)
            nombres.add(t.getNombreTrabajador());

        ArrayAdapter<String> adp = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrabajador.setAdapter(adp);
    }

    private void agregarTrabajador() {
        int pos = spinnerTrabajador.getSelectedItemPosition();
        if (pos == 0) {
            Toast.makeText(getContext(), "Selecciona un trabajador", Toast.LENGTH_SHORT).show();
            return;
        }

        // Regla 1: máx. 8
        if (trabajadoresAsignados.size() >= MAX_TRABAJADORES) {
            Toast.makeText(getContext(),
                    "Límite alcanzado: máximo " + MAX_TRABAJADORES + " trabajadores por proyecto",
                    Toast.LENGTH_LONG).show();
            return;
        }

        TrabajadorDto seleccionado = listaTrabajadores.get(pos - 1);

        // Regla 2: sin duplicados
        for (TrabajadorDto t : trabajadoresAsignados) {
            if (t.getIdTrabajador() != null &&
                    t.getIdTrabajador().equals(seleccionado.getIdTrabajador())) {
                Toast.makeText(getContext(),
                        seleccionado.getNombreTrabajador() + " ya está en el equipo",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        trabajadoresAsignados.add(seleccionado);
        agregarFilaTrabajador(seleccionado);
        Toast.makeText(getContext(),
                "Trabajador agregado (" + trabajadoresAsignados.size() + "/" + MAX_TRABAJADORES + ")",
                Toast.LENGTH_SHORT).show();
    }

    private void agregarFilaTrabajador(TrabajadorDto t) {
        LinearLayout fila = new LinearLayout(getContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 8);
        fila.setLayoutParams(params);

        TextView tvNombre = new TextView(getContext());
        tvNombre.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
        tvNombre.setText(t.getNombreTrabajador());
        tvNombre.setTextColor(getResources().getColor(R.color.white, null));
        tvNombre.setTextSize(13);

        TextView tvEsp = new TextView(getContext());
        tvEsp.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
        tvEsp.setText(t.getEspecialidadTrabajador() != null ? t.getEspecialidadTrabajador() : "—");
        tvEsp.setTextColor(getResources().getColor(R.color.white, null));
        tvEsp.setTextSize(13);

        Button btnElim = new Button(getContext());
        btnElim.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPx(36), 1f));
        btnElim.setText("Eliminar");
        btnElim.setTextSize(11);
        btnElim.setAllCaps(false);
        btnElim.setTextColor(getResources().getColor(android.R.color.white, null));
        btnElim.setBackgroundResource(R.drawable.btn_coral);

        btnElim.setOnClickListener(v -> {
            trabajadoresAsignados.remove(t);
            contenedorTrabajadores.removeView(fila);
            Toast.makeText(getContext(),
                    t.getNombreTrabajador() + " eliminado del equipo", Toast.LENGTH_SHORT).show();
        });

        fila.addView(tvNombre);
        fila.addView(tvEsp);
        fila.addView(btnElim);
        contenedorTrabajadores.addView(fila);
    }

    // ─── Guardar ─────────────────────────────────────────────────────────────

    private void guardar() {
        if (contratistaAsignado == null) {
            Toast.makeText(getContext(), "Asigna un contratista antes de guardar",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (trabajadoresAsignados.isEmpty()) {
            Toast.makeText(getContext(), "Agrega al menos un trabajador",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: construir FichaCreateDto y llamar al API cuando el backend lo soporte
        StringBuilder resumen = new StringBuilder();
        resumen.append("Contratista: ").append(contratistaAsignado.getNombreContratista()).append("\n");
        resumen.append("Trabajadores (").append(trabajadoresAsignados.size()).append("):\n");
        for (TrabajadorDto t : trabajadoresAsignados)
            resumen.append("  • ").append(t.getNombreTrabajador()).append("\n");

        Toast.makeText(getContext(), "Asignación guardada:\n" + resumen, Toast.LENGTH_LONG).show();
        dismiss();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}