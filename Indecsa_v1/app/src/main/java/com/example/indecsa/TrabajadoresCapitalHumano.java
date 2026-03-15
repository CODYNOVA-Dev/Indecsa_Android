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

import com.example.indecsa.models.TrabajadorDto;
import com.example.indecsa.network.ApiService;
import com.example.indecsa.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrabajadoresCapitalHumano extends Fragment {

    private static final String TAG = "TrabajadoresAPI";
    private TrabajadorAdapter adapter;
    private String estadoFiltro = "";
    private String especialidadFiltro = "";

    public TrabajadoresCapitalHumano() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trabajadores_capital_humano, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            estadoFiltro = getArguments().getString("ESTADO_SELECCIONADO", "");
            especialidadFiltro = getArguments().getString("ESPECIALIDAD_SELECCIONADA", "");

            Log.d(TAG, "Filtros recibidos - Estado: " + estadoFiltro + ", Especialidad: " + especialidadFiltro);
            actualizarTituloConFiltros();
        }

        configurarListView();
        configurarBotonNuevo();
        cargarTrabajadoresDeAPI();
    }

    private void actualizarTituloConFiltros() {
        View view = getView();
        if (view == null) return;

        TextView titulo = view.findViewById(R.id.txtTituloObra);
        if (titulo != null) {
            String texto = "Todos los Trabajadores";

            if (!estadoFiltro.isEmpty() && !especialidadFiltro.isEmpty()) {
                texto = "Trabajadores - " + estadoFiltro + " - " + especialidadFiltro;
            } else if (!estadoFiltro.isEmpty()) {
                texto = "Trabajadores - " + estadoFiltro;
            } else if (!especialidadFiltro.isEmpty()) {
                texto = "Trabajadores - " + especialidadFiltro;
            }

            titulo.setText(texto);
        }
    }

    private void configurarListView() {
        View view = getView();
        if (view == null) return;

        adapter = new TrabajadorAdapter(requireContext(), new ArrayList<Trabajador>(),
                new TrabajadorAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Trabajador trabajador) {
                        // Muestra Toast con información básica
                        Toast.makeText(getContext(),
                                trabajador.getNombre() + " - " + trabajador.getEspecialidad(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEditarClick(Trabajador trabajador) {
                        // ABRE DIÁLOGO DE EDICIÓN
                        mostrarDialogoEdicion(trabajador, false);
                    }

                    @Override
                    public void onEliminarClick(Trabajador trabajador) {
                        // CONFIRMA ELIMINACIÓN
                        confirmarEliminacion(trabajador);
                    }
                });

        ListView listView = view.findViewById(R.id.listaTrabajador);
        listView.setAdapter(adapter);
    }

    private void configurarBotonNuevo() {
        View view = getView();
        if (view == null) return;

        Button btnNuevo = view.findViewById(R.id.btnNuevoTrabajador);
        if (btnNuevo != null) {
            btnNuevo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crear trabajador vacío para nuevo
                    Trabajador nuevo = new Trabajador(
                            null, // ID
                            "",   // NSS
                            "",   // Nombre
                            "",   // Descripción
                            "Obra", // Especialidad por defecto
                            estadoFiltro.isEmpty() ? "Hidalgo" : estadoFiltro, // Estado por defecto
                            0,    // Experiencia
                            "Disponible", // Disponibilidad
                            R.drawable.usuario // Imagen
                    );

                    mostrarDialogoEdicion(nuevo, true);
                }
            });
        }
    }

    private void cargarTrabajadoresDeAPI() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        String url = "trabajadores/filtros?estado=" + estadoFiltro + "&especialidad=" + especialidadFiltro;
        Log.d(TAG, "Llamando a API: " + url);

        api.getTrabajadoresFiltrados(estadoFiltro, especialidadFiltro)
                .enqueue(new Callback<List<TrabajadorDto>>() {
                    @Override
                    public void onResponse(Call<List<TrabajadorDto>> call, Response<List<TrabajadorDto>> response) {
                        Log.d(TAG, "Respuesta recibida. Código: " + response.code());
                        Log.d(TAG, "¿Es exitosa?: " + response.isSuccessful());

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Trabajadores recibidos: " + response.body().size());

                            List<Trabajador> trabajadores = Trabajador.fromDtoList(response.body());
                            adapter.actualizarLista(trabajadores);

                            if (trabajadores.isEmpty()) {
                                Toast.makeText(getContext(), "No se encontraron trabajadores", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Lista de trabajadores vacía");
                            } else {
                                Log.d(TAG, "Trabajadores cargados exitosamente: " + trabajadores.size());
                            }
                        } else {
                            String errorMsg = "Error al cargar trabajadores. Código: " + response.code();
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, errorMsg);
                            cargarDatosEjemplo();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TrabajadorDto>> call, Throwable t) {
                        String errorMsg = "Error de conexión: " + t.getMessage();
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorMsg);
                        t.printStackTrace();
                        cargarDatosEjemplo();
                    }
                });
    }

    private void cargarDatosEjemplo() {
        Log.d(TAG, "Mostrando estado de conexión...");

        List<Trabajador> trabajadores = new ArrayList<>();

        // Solo un item informativo
        trabajadores.add(new Trabajador(
                0, // ID temporal
                "00000000000", // NSS temporal
                "⚠️ Sin conexión al servidor",
                "No se pudo conectar con la base de datos. Verifica tu conexión a internet.",
                "Error",
                "",
                0,
                "No disponible",
                R.drawable.usuario
        ));

        adapter.actualizarLista(trabajadores);
    }

    // MÉTODO: Mostrar diálogo de edición
    private void mostrarDialogoEdicion(Trabajador trabajador, boolean esNuevo) {
        Log.d(TAG, "===== EDITANDO TRABAJADOR =====");
        Log.d(TAG, "ID: " + trabajador.getId());
        Log.d(TAG, "Nombre: " + trabajador.getNombre());
        Log.d(TAG, "Estado: " + trabajador.getEstado());
        Log.d(TAG, "EsNuevo: " + esNuevo);
        Log.d(TAG, "NSS: " + trabajador.getNss());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_simple_edicion, null);
        builder.setView(dialogView);

        android.widget.EditText etNss = dialogView.findViewById(R.id.etNssSimple);
        android.widget.EditText etNombre = dialogView.findViewById(R.id.etNombreSimple);
        android.widget.EditText etEspecialidad = dialogView.findViewById(R.id.etEspecialidadSimple);
        android.widget.EditText etEstado = dialogView.findViewById(R.id.etEstadoSimple);
        android.widget.EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionSimple);
        android.widget.Button btnCancelar = dialogView.findViewById(R.id.btnCancelarSimple);
        android.widget.Button btnGuardar = dialogView.findViewById(R.id.btnGuardarSimple);

        // Si es edición, cargar datos
        if (!esNuevo) {
            etNss.setText(trabajador.getNss() != null ? trabajador.getNss() : "");
            etNombre.setText(trabajador.getNombre());
            etEspecialidad.setText(trabajador.getEspecialidad());
            etEstado.setText(trabajador.getEstado());
            etDescripcion.setText(trabajador.getDescripcion());
        } else {
            // Si es nuevo, poner valores por defecto
            etNss.setText("");
            etEstado.setText(estadoFiltro.isEmpty() ? "Hidalgo" : estadoFiltro);
            etEspecialidad.setText(especialidadFiltro.isEmpty() ? "Obra" : especialidadFiltro);
        }

        AlertDialog dialog = builder.create();
        dialog.setTitle(esNuevo ? "Nuevo Trabajador" : "Editar Trabajador");

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nss = etNss.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String especialidad = etEspecialidad.getText().toString().trim();
            String estado = etEstado.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            Log.d(TAG, "Guardando trabajador...");
            Log.d(TAG, "NSS: " + nss);
            Log.d(TAG, "Nombre: " + nombre);
            Log.d(TAG, "Especialidad: " + especialidad);
            Log.d(TAG, "Estado: " + estado);
            Log.d(TAG, "Descripción: " + descripcion);
            Log.d(TAG, "ID del trabajador: " + trabajador.getId());

            // Validaciones
            if (nss.isEmpty()) {
                etNss.setError("El NSS es obligatorio (11 dígitos)");
                return;
            }

            if (nss.length() != 11) {
                etNss.setError("El NSS debe tener 11 dígitos");
                return;
            }

            if (nombre.isEmpty()) {
                etNombre.setError("El nombre es requerido");
                return;
            }

            if (especialidad.isEmpty()) {
                etEspecialidad.setError("La especialidad es requerida");
                return;
            }

            if (estado.isEmpty()) {
                etEstado.setError("El estado es requerido");
                return;
            }

            // Crear DTO
            com.example.indecsa.models.TrabajadorDto dto = new com.example.indecsa.models.TrabajadorDto();

            // Si es edición, poner el ID
            if (!esNuevo) {
                if (trabajador.getId() != null) {
                    dto.setIdTrabajador(trabajador.getId());
                    Log.d(TAG, "ID seteado en DTO: " + trabajador.getId());
                } else {
                    Log.e(TAG, "ERROR: Trabajador sin ID pero es edición!");
                    Toast.makeText(getContext(),
                            "Error: Este trabajador no tiene ID válido",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Configurar campos
            dto.setNssTrabajador(nss);
            dto.setNombreTrabajador(nombre);
            dto.setEspecialidadTrabajador(especialidad);
            dto.setEstadoTrabajador(estado);
            dto.setDescripcionTrabajador(descripcion);

            // Llamar API
            com.example.indecsa.network.ApiService api = com.example.indecsa.network.RetrofitClient.getClient().create(com.example.indecsa.network.ApiService.class);

            if (esNuevo) {
                Log.d(TAG, "Creando nuevo trabajador...");
                api.createTrabajador(dto).enqueue(new retrofit2.Callback<com.example.indecsa.models.TrabajadorDto>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.indecsa.models.TrabajadorDto> call,
                                           retrofit2.Response<com.example.indecsa.models.TrabajadorDto> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Trabajador creado exitosamente");
                            Toast.makeText(getContext(), "Trabajador creado exitosamente", Toast.LENGTH_SHORT).show();
                            cargarTrabajadoresDeAPI();
                        } else {
                            String errorBody = "";
                            try {
                                errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e(TAG, "Error al crear: " + response.code() + " - " + errorBody);
                            Toast.makeText(getContext(),
                                    "Error " + response.code() + ": " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.indecsa.models.TrabajadorDto> call, Throwable t) {
                        dialog.dismiss();
                        Log.e(TAG, "Error de conexión al crear: ", t);
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.d(TAG, "Actualizando trabajador existente...");
                api.updateTrabajador(trabajador.getId(), dto).enqueue(new retrofit2.Callback<com.example.indecsa.models.TrabajadorDto>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.indecsa.models.TrabajadorDto> call,
                                           retrofit2.Response<com.example.indecsa.models.TrabajadorDto> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Trabajador actualizado exitosamente");
                            Toast.makeText(getContext(), "Trabajador actualizado exitosamente", Toast.LENGTH_SHORT).show();
                            cargarTrabajadoresDeAPI();
                        } else {
                            String errorBody = "";
                            try {
                                errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e(TAG, "Error al actualizar: " + response.code() + " - " + errorBody);
                            Toast.makeText(getContext(),
                                    "Error " + response.code() + ": " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.indecsa.models.TrabajadorDto> call, Throwable t) {
                        dialog.dismiss();
                        Log.e(TAG, "Error de conexión al actualizar: ", t);
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }

    // MÉTODO: Confirmar eliminación
    private void confirmarEliminacion(Trabajador trabajador) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Trabajador")
                .setMessage("¿Estás seguro de eliminar a " + trabajador.getNombre() + "?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarTrabajador(trabajador);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // MÉTODO: Eliminar trabajador (llamar a API)
    private void eliminarTrabajador(Trabajador trabajador) {
        if (trabajador.getId() == null) {
            Toast.makeText(getContext(), "No se puede eliminar un trabajador sin ID", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.deleteTrabajador(trabajador.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Trabajador eliminado exitosamente", Toast.LENGTH_SHORT).show();
                    // Recargar la lista
                    cargarTrabajadoresDeAPI();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar trabajador: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método antiguo que puedes eliminar si no lo usas
    private void mostrarDetalleTrabajador(Trabajador trabajador) {
        Toast.makeText(getContext(),
                trabajador.getNombre() + "\n" +
                        "Especialidad: " + trabajador.getEspecialidad() + "\n" +
                        "Estado: " + trabajador.getEstado(),
                Toast.LENGTH_LONG).show();
    }
}