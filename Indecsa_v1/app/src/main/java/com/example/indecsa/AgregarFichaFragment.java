package com.example.indecsa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.indecsa.models.Contratista;
import com.example.indecsa.models.FichaCreateDto;
import com.example.indecsa.models.ProyectoDto;
import com.example.indecsa.models.TrabajadorDto;
import com.example.indecsa.network.ApiService;
import com.example.indecsa.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarFichaFragment extends Fragment {

    private static final String TAG = "AgregarFicha";

    // Spinners
    private Spinner spinnerContratista;
    private Spinner spinnerProyecto;
    private Spinner spinnerEstadoFicha;
    private Spinner spinnerEspecialidadFicha;

    // ListView para trabajadores
    private ListView listViewTrabajadores;

    // Botón
    private Button btnGuardarFicha;

    // Listas de datos
    private List<Contratista> listaContratistas = new ArrayList<>();
    private List<ProyectoDto> listaProyectos = new ArrayList<>();
    private List<TrabajadorDto> listaTrabajadores = new ArrayList<>();

    // Adaptadores
    private ArrayAdapter<String> adapterContratistas;
    private ArrayAdapter<String> adapterProyectos;
    private ArrayAdapter<String> adapterTrabajadores;

    // Filtros heredados del fragment anterior
    private String estadoFiltro = "";
    private String especialidadFiltro = "";

    public AgregarFichaFragment() {
        // Required empty public constructor
    }

    public static AgregarFichaFragment newInstance(String estado, String especialidad) {
        AgregarFichaFragment fragment = new AgregarFichaFragment();
        Bundle args = new Bundle();
        args.putString("estado", estado);
        args.putString("especialidad", especialidad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agregar_ficha2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener filtros heredados
        if (getArguments() != null) {
            estadoFiltro = getArguments().getString("estado", "");
            especialidadFiltro = getArguments().getString("especialidad", "");

            Log.d(TAG, "Filtros heredados - Estado: " + estadoFiltro + ", Especialidad: " + especialidadFiltro);
        }

        inicializarVistas();
        configurarSpinners();
        cargarDatosDeAPI();
        configurarBotonGuardar();
    }

    private void inicializarVistas() {
        View view = getView();
        if (view == null) return;

        spinnerContratista = view.findViewById(R.id.spinnerContratista);
        spinnerProyecto = view.findViewById(R.id.spinnerProyecto);
        spinnerEstadoFicha = view.findViewById(R.id.spinnerEstadoFicha);
        spinnerEspecialidadFicha = view.findViewById(R.id.spinnerEspecialidadFicha);
        listViewTrabajadores = view.findViewById(R.id.listViewTrabajadores);
        btnGuardarFicha = view.findViewById(R.id.btnGuardarFicha);
    }

    private void configurarSpinners() {
        // Spinner de Estado - bloqueado con el valor del filtro
        String[] estados = {
                estadoFiltro.isEmpty() ? "Sin filtro de estado" : estadoFiltro
        };
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                estados
        );
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstadoFicha.setAdapter(adapterEstados);
        spinnerEstadoFicha.setEnabled(false); // Deshabilitar para que no se pueda cambiar

        // Spinner de Especialidad - bloqueado con el valor del filtro
        String[] especialidades = {
                especialidadFiltro.isEmpty() ? "Sin filtro de especialidad" : especialidadFiltro
        };
        ArrayAdapter<String> adapterEspecialidades = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                especialidades
        );
        adapterEspecialidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecialidadFicha.setAdapter(adapterEspecialidades);
        spinnerEspecialidadFicha.setEnabled(false); // Deshabilitar para que no se pueda cambiar
    }

    private void cargarDatosDeAPI() {
        cargarContratistas();
        cargarProyectos();
        cargarTrabajadores();
    }

    private void cargarContratistas() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Contratista>> call = api.getContratistas();

        call.enqueue(new Callback<List<Contratista>>() {
            @Override
            public void onResponse(Call<List<Contratista>> call, Response<List<Contratista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaContratistas = response.body();

                    List<String> nombresContratistas = new ArrayList<>();
                    nombresContratistas.add("Seleccionar contratista...");

                    for (Contratista c : listaContratistas) {
                        nombresContratistas.add(c.getNombreContratista() + " - " + c.getEspecialidad());
                    }

                    adapterContratistas = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            nombresContratistas
                    );
                    adapterContratistas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerContratista.setAdapter(adapterContratistas);

                    Log.d(TAG, "Contratistas cargados: " + listaContratistas.size());
                } else {
                    Toast.makeText(getContext(), "Error al cargar contratistas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Contratista>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void cargarProyectos() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<ProyectoDto>> call = api.getProyectos();

        call.enqueue(new Callback<List<ProyectoDto>>() {
            @Override
            public void onResponse(Call<List<ProyectoDto>> call, Response<List<ProyectoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProyectos = response.body();

                    List<String> nombresProyectos = new ArrayList<>();
                    nombresProyectos.add("Seleccionar proyecto...");

                    for (ProyectoDto p : listaProyectos) {
                        nombresProyectos.add(p.getNombreProyecto() + " - " + p.getLugarProyecto());
                    }

                    adapterProyectos = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            nombresProyectos
                    );
                    adapterProyectos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProyecto.setAdapter(adapterProyectos);

                    Log.d(TAG, "Proyectos cargados: " + listaProyectos.size());
                } else {
                    Toast.makeText(getContext(), "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProyectoDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void cargarTrabajadores() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<TrabajadorDto>> call = api.getTrabajadores();

        call.enqueue(new Callback<List<TrabajadorDto>>() {
            @Override
            public void onResponse(Call<List<TrabajadorDto>> call, Response<List<TrabajadorDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTrabajadores = response.body();

                    List<String> nombresTrabajadores = new ArrayList<>();
                    for (TrabajadorDto t : listaTrabajadores) {
                        nombresTrabajadores.add(t.getNombreTrabajador() + " - " + t.getEspecialidadTrabajador());
                    }

                    adapterTrabajadores = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_list_item_multiple_choice,
                            nombresTrabajadores
                    );
                    listViewTrabajadores.setAdapter(adapterTrabajadores);

                    Log.d(TAG, "Trabajadores cargados: " + listaTrabajadores.size());
                } else {
                    Toast.makeText(getContext(), "Error al cargar trabajadores", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TrabajadorDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void configurarBotonGuardar() {
        btnGuardarFicha.setOnClickListener(v -> guardarFicha());
    }

    private void guardarFicha() {
        // Validar selección de contratista
        int posContratista = spinnerContratista.getSelectedItemPosition();
        if (posContratista == 0) {
            Toast.makeText(getContext(), "Selecciona un contratista", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar selección de proyecto
        int posProyecto = spinnerProyecto.getSelectedItemPosition();
        if (posProyecto == 0) {
            Toast.makeText(getContext(), "Selecciona un proyecto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar estado y especialidad heredados
        if (estadoFiltro.isEmpty()) {
            Toast.makeText(getContext(), "No hay estado definido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (especialidadFiltro.isEmpty()) {
            Toast.makeText(getContext(), "No hay especialidad definida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener trabajadores seleccionados
        List<Integer> trabajadoresIds = new ArrayList<>();
        for (int i = 0; i < listViewTrabajadores.getCount(); i++) {
            if (listViewTrabajadores.isItemChecked(i)) {
                trabajadoresIds.add(listaTrabajadores.get(i).getIdTrabajador());
            }
        }

        if (trabajadoresIds.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona al menos un trabajador", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto FichaCreateDto usando los filtros heredados
        FichaCreateDto nuevaFicha = new FichaCreateDto();
        nuevaFicha.setIdContratista(listaContratistas.get(posContratista - 1).getIdContratista());
        nuevaFicha.setIdProyecto(listaProyectos.get(posProyecto - 1).getIdProyecto());
        nuevaFicha.setFichaEstado(estadoFiltro); // Usar el filtro heredado
        nuevaFicha.setFichaEspecialidad(especialidadFiltro); // Usar el filtro heredado
        nuevaFicha.setTrabajadoresIds(trabajadoresIds);

        // Enviar a la API
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = api.createFichaConTrabajadores(nuevaFicha);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ficha creada exitosamente", Toast.LENGTH_SHORT).show();
                    limpiarFormulario();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(),
                            "Error al crear ficha. Código: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void limpiarFormulario() {
        spinnerContratista.setSelection(0);
        spinnerProyecto.setSelection(0);

        for (int i = 0; i < listViewTrabajadores.getCount(); i++) {
            listViewTrabajadores.setItemChecked(i, false);
        }
    }
}