package com.example.indecsa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.indecsa.models.FichaCompletaDto;
import com.example.indecsa.models.FichaDto;
import com.example.indecsa.network.ApiService;
import com.example.indecsa.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FichasCapitalHumano extends Fragment {

    private static final String TAG = "FichasAPI";
    private FichaAdapter adapter;
    private String estadoFiltro = "";
    private String especialidadFiltro = "";

    private ArrayList<Ficha> fichasList = new ArrayList<>();

    public FichasCapitalHumano() {
        // Required empty public constructor
    }

    public static FichasCapitalHumano newInstance(String estado, String especialidad) {
        FichasCapitalHumano fragment = new FichasCapitalHumano();
        Bundle args = new Bundle();
        args.putString("estado", estado);
        args.putString("especialidad", especialidad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fichas_capital_humano, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener parámetros
        if (getArguments() != null) {
            estadoFiltro = getArguments().getString("estado", "");
            especialidadFiltro = getArguments().getString("especialidad", "");

            Log.d(TAG, "Filtros recibidos - Estado: " + estadoFiltro + ", Especialidad: " + especialidadFiltro);
            actualizarTituloConFiltros();
        }

        configurarListView();
        configurarBotonNuevaFicha();
        cargarFichasDeAPI();
    }

    private void actualizarTituloConFiltros() {
        View view = getView();
        if (view == null) return;

        TextView titulo = view.findViewById(R.id.txtTituloFichas);
        if (titulo != null) {
            String texto = "Todas las Fichas";

            if (!estadoFiltro.isEmpty() && !especialidadFiltro.isEmpty()) {
                texto = "Fichas - " + estadoFiltro + " - " + especialidadFiltro;
            } else if (!estadoFiltro.isEmpty()) {
                texto = "Fichas - " + estadoFiltro;
            } else if (!especialidadFiltro.isEmpty()) {
                texto = "Fichas - " + especialidadFiltro;
            }

            titulo.setText(texto);
        }
    }

    private void configurarListView() {
        View view = getView();
        if (view == null) return;

        adapter = new FichaAdapter(requireContext(), fichasList);
        ListView listView = view.findViewById(R.id.listaFichas);
        listView.setAdapter(adapter);

        // Listener para clicks en fichas
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Ficha fichaSeleccionada = fichasList.get(position);
            abrirDetalleFicha(fichaSeleccionada);
        });
    }

    private void configurarBotonNuevaFicha() {
        View view = getView();
        if (view == null) return;

        Button btnNuevaFicha = view.findViewById(R.id.btnNuevaFicha);
        if (btnNuevaFicha != null) {
            btnNuevaFicha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Abrir fragment de agregar ficha pasando los filtros actuales
                    AgregarFichaFragment fragment = AgregarFichaFragment.newInstance(
                            estadoFiltro,
                            especialidadFiltro
                    );

                    FragmentTransaction transaction = requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction();

                    transaction.replace(R.id.contenedorfragmentos, fragment);
                    transaction.addToBackStack("agregar_ficha");
                    transaction.commit();

                    Log.d(TAG, "Abriendo formulario con filtros - Estado: " +
                            estadoFiltro + ", Especialidad: " + especialidadFiltro);
                }
            });
        }
    }

    private void cargarFichasDeAPI() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        Log.d(TAG, "Llamando a API con filtros - Estado: " + estadoFiltro + ", Especialidad: " + especialidadFiltro);

        Call<List<FichaCompletaDto>> call = api.getFichasCompletasFiltradas(estadoFiltro, especialidadFiltro);

        call.enqueue(new Callback<List<FichaCompletaDto>>() {
            @Override
            public void onResponse(Call<List<FichaCompletaDto>> call, Response<List<FichaCompletaDto>> response) {
                Log.d(TAG, "Respuesta recibida. Código: " + response.code());
                Log.d(TAG, "¿Es exitosa?: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Fichas recibidas: " + response.body().size());

                    // Usar el método estático que ya tienes en Ficha.java
                    fichasList.clear();
                    fichasList.addAll(Ficha.fromCompletaDtoList(response.body()));

                    adapter.notifyDataSetChanged();

                    if (fichasList.isEmpty()) {
                        Toast.makeText(getContext(), "No hay fichas para estos filtros",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Lista de fichas vacía");
                    } else {
                        Log.d(TAG, "Fichas cargadas exitosamente: " + fichasList.size());
                        Toast.makeText(getContext(),
                                fichasList.size() + " fichas cargadas",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Error al cargar fichas. Código: " + response.code();
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMsg);

                    // Intentar cargar datos normales
                    cargarFichasNormales();
                }
            }

            @Override
            public void onFailure(Call<List<FichaCompletaDto>> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMsg);
                t.printStackTrace();

                // Cargar datos de ejemplo
                cargarDatosEjemplo();
            }
        });
    }

    private void cargarFichasNormales() {
        // Método alternativo si el endpoint completo falla
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<FichaDto>> call = api.getFichasFiltradas(estadoFiltro, especialidadFiltro);

        call.enqueue(new Callback<List<FichaDto>>() {
            @Override
            public void onResponse(Call<List<FichaDto>> call, Response<List<FichaDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fichasList.clear();

                    // Crear fichas básicas usando el constructor simple
                    for (FichaDto dto : response.body()) {
                        Ficha ficha = new Ficha(
                                "Contratista #" + dto.getIdContratista(),
                                "Descripción del contratista",
                                "Proyecto #" + dto.getIdProyecto(),
                                "Equipo de trabajo"
                        );
                        ficha.setIdFicha(dto.getIdFicha());
                        ficha.setFichaEstado(dto.getFichaEstado());
                        ficha.setFichaEspecialidad(dto.getFichaEspecialidad());
                        fichasList.add(ficha);
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<FichaDto>> call, Throwable t) {
                cargarDatosEjemplo();
            }
        });
    }

    private void cargarDatosEjemplo() {
        Log.d(TAG, "Cargando datos de ejemplo...");

        fichasList.clear();

        // Usar el constructor completo que tienes
        fichasList.add(new Ficha(
                0,
                "⚠️ Sin conexión al servidor",
                "No se pudo conectar con la base de datos. Verifica tu conexión a internet.",
                "000-000-0000",
                "error@ejemplo.com",
                "Error",
                "Proyecto de prueba",
                estadoFiltro.isEmpty() ? "Hidalgo" : estadoFiltro,
                especialidadFiltro.isEmpty() ? "Obra" : especialidadFiltro,
                estadoFiltro.isEmpty() ? "Hidalgo" : estadoFiltro,
                especialidadFiltro.isEmpty() ? "Obra" : especialidadFiltro,
                "Sin equipo asignado"
        ));

        adapter.notifyDataSetChanged();
    }

    private void abrirDetalleFicha(Ficha ficha) {
        try {
            // Crear fragmento de detalle CON ID DE FICHA
            DetalleFichaFragment fragment = DetalleFichaFragment.newInstance(
                    ficha.getIdFicha(),  // ← IMPORTANTE: pasar el ID
                    ficha.getNombreContratista(),
                    ficha.getDescripcionContratista(),
                    ficha.getNombreProyecto(),
                    ficha.getEquipoTrabajo(),
                    ficha.getFichaEstado(),
                    ficha.getFichaEspecialidad()
            );

            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.contenedorfragmentos, fragment);
            transaction.addToBackStack("detalle_ficha");
            transaction.commit();

            Toast.makeText(getContext(),
                    "Detalle de: " + ficha.getNombreContratista(),
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error al abrir detalle: " + e.getMessage());
        }
    }

    // Métodos para CRUD
    private void mostrarDialogoEdicion(Ficha ficha, boolean esNuevo) {
        // Implementar igual que trabajadores
        Toast.makeText(getContext(), "Editar ficha", Toast.LENGTH_SHORT).show();
    }

    private void confirmarEliminacion(Ficha ficha) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Ficha")
                .setMessage("¿Estás seguro de eliminar la ficha de " + ficha.getNombreContratista() + "?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarFicha(ficha);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarFicha(Ficha ficha) {
        if (ficha.getIdFicha() == null) {
            Toast.makeText(getContext(), "No se puede eliminar una ficha sin ID", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = api.deleteFicha(ficha.getIdFicha());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ficha eliminada exitosamente", Toast.LENGTH_SHORT).show();
                    cargarFichasDeAPI();
                } else {
                    Toast.makeText(getContext(),
                            "Error al eliminar ficha: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}