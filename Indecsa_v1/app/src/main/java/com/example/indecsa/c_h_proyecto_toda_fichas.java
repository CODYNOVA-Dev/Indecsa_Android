package com.example.indecsa;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.example.indecsa.models.Contratista;
import com.example.indecsa.models.FichaCompletaDto;
import com.example.indecsa.models.FichaDto;
import com.example.indecsa.models.ProyectoDto;
import com.example.indecsa.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class c_h_proyecto_toda_fichas extends Fragment {

    private ProyectoAdapter adapter;
    private String estadoFiltro = "";
    private String especialidadFiltro = "";
    private List<FichaCompletaDto> fichasActuales = new ArrayList<>();
    private static final String TAG = "TodaFichasFragment";
    private List<Contratista> listaContratistas = new ArrayList<>();

    public c_h_proyecto_toda_fichas() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_c_h_proyecto_toda_fichas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            estadoFiltro = getArguments().getString("ESTADO_SELECCIONADO", "");
            especialidadFiltro = getArguments().getString("ESPECIALIDAD_SELECCIONADA", "");
            actualizarTituloConFiltros();
        }

        configurarListView();
        cargarContratistasDesdeAPI();
        cargarProyectosDesdeAPI();
    }

    private void actualizarTituloConFiltros() {
        View view = getView();
        if (view == null) return;

        TextView titulo = view.findViewById(R.id.txtTituloObra);
        if (titulo != null) {
            String texto = "Todos los Proyectos";

            if (!estadoFiltro.isEmpty() && !especialidadFiltro.isEmpty()) {
                texto = "Proyectos - " + estadoFiltro + " - " + especialidadFiltro;
            } else if (!estadoFiltro.isEmpty()) {
                texto = "Proyectos - " + estadoFiltro;
            } else if (!especialidadFiltro.isEmpty()) {
                texto = "Proyectos - " + especialidadFiltro;
            }

            titulo.setText(texto);
        }
    }

    private void configurarListView() {
        View view = getView();
        if (view == null) return;

        Button btnNuevoProyecto = view.findViewById(R.id.btnNuevoProyecto);
        if (btnNuevoProyecto != null) {
            btnNuevoProyecto.setOnClickListener(v -> crearNuevoProyecto());
        }

        adapter = new ProyectoAdapter(requireContext(), new ArrayList<Proyecto>(), new ProyectoAdapter.OnItemClickListener() {
            @Override
            public void onVerDetallesClick(Proyecto proyecto) {
                mostrarDetalleProyectoBasico(proyecto);
            }

            @Override
            public void onEditarClick(Proyecto proyecto) {
                editarProyecto(proyecto);
            }

            @Override
            public void onEliminarClick(Proyecto proyecto) {
                eliminarProyecto(proyecto);
            }
        });

        ListView listView = view.findViewById(R.id.listaTrabajador);
        listView.setAdapter(adapter);
    }

    private void cargarContratistasDesdeAPI() {
        Call<List<Contratista>> call = RetrofitClient.getApiService().obtenerContratistas();

        call.enqueue(new Callback<List<Contratista>>() {
            @Override
            public void onResponse(Call<List<Contratista>> call, Response<List<Contratista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaContratistas = response.body();
                    Log.d(TAG, "✅ Contratistas cargados: " + listaContratistas.size());
                } else {
                    Log.w(TAG, "⚠️ No se pudieron cargar contratistas - Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Contratista>> call, Throwable t) {
                Log.e(TAG, "❌ Error al cargar contratistas", t);
            }
        });
    }

    private void cargarProyectosDesdeAPI() {
        String estadoParam = estadoFiltro.isEmpty() ? null : estadoFiltro;
        String especialidadParam = especialidadFiltro.isEmpty() ? null : especialidadFiltro;

        Log.d(TAG, "Cargando fichas desde API - Estado: " + estadoParam + ", Especialidad: " + especialidadParam);

        Call<List<FichaCompletaDto>> call = RetrofitClient.getApiService()
                .getFichasCompletasFiltradas(estadoParam, especialidadParam);

        call.enqueue(new Callback<List<FichaCompletaDto>>() {
            @Override
            public void onResponse(Call<List<FichaCompletaDto>> call, Response<List<FichaCompletaDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fichasActuales = response.body();
                    List<Proyecto> proyectos = convertirFichasAProyectos(fichasActuales);
                    adapter.actualizarLista(proyectos);

                    Log.d(TAG, "✅ Fichas cargadas: " + fichasActuales.size());
                    Toast.makeText(requireContext(), fichasActuales.size() + " proyectos encontrados", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "No se encontraron proyectos", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "⚠️ Respuesta vacía - Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<FichaCompletaDto>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "❌ Error al cargar fichas", t);
            }
        });
    }

    private List<Proyecto> convertirFichasAProyectos(List<FichaCompletaDto> fichas) {
        List<Proyecto> proyectos = new ArrayList<>();

        for (FichaCompletaDto ficha : fichas) {
            // Ahora sí tenemos el idProyecto
            Proyecto proyecto = new Proyecto(
                    ficha.getIdProyecto(), // <-- ESTE ES EL CAMBIO
                    ficha.getNombreProyecto(),
                    ficha.getDescripcionContratista(),
                    ficha.getNombreContratista(),
                    ficha.getFichaEspecialidad(),
                    ficha.getFichaEstado(),
                    0, // avance
                    ficha.getLugarProyecto(),
                    R.drawable.usuario
            );
            proyectos.add(proyecto);
        }

        return proyectos;
    }

    private void crearNuevoProyecto() {
        DialogCrearEditarProyecto dialog = new DialogCrearEditarProyecto(
                requireContext(),
                new DialogCrearEditarProyecto.OnProyectoGuardadoListener() {
                    @Override
                    public void onProyectoGuardado(Proyecto proyecto) {
                        guardarProyectoEnAPI(proyecto);
                    }

                    @Override
                    public void onProyectoActualizado(Proyecto proyecto) {
                        // No aplica para crear
                    }
                });

        dialog.show();
    }

    private void guardarProyectoEnAPI(Proyecto proyecto) {
        // 1. Crear el Proyecto
        ProyectoDto proyectoDto = new ProyectoDto(
                proyecto.getProyectito(),
                proyecto.getEspecialidad(), // tipoProyecto = especialidad
                proyecto.getDireccion()
        );

        Call<ProyectoDto> call = RetrofitClient.getApiService().crearProyecto(proyectoDto);

        call.enqueue(new Callback<ProyectoDto>() {
            @Override
            public void onResponse(Call<ProyectoDto> call, Response<ProyectoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProyectoDto proyectoCreado = response.body();
                    Toast.makeText(requireContext(), "✅ Proyecto creado", Toast.LENGTH_SHORT).show();

                    // 2. Crear la Ficha asociada
                    crearFichaParaProyecto(proyectoCreado, proyecto);

                } else {
                    Toast.makeText(requireContext(), "❌ Error al crear proyecto", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al crear proyecto - Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProyectoDto> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al crear proyecto", t);
            }
        });
    }

    private void crearFichaParaProyecto(ProyectoDto proyectoDto, Proyecto proyectoOriginal) {
        // Buscar contratista adecuado
        Integer idContratista = buscarContratistaApropiado(
                proyectoOriginal.getEstado(),
                proyectoOriginal.getEspecialidad()
        );

        if (idContratista == null && !listaContratistas.isEmpty()) {
            // Si no encuentra uno apropiado, usar el primero
            idContratista = listaContratistas.get(0).getIdContratista();
            Toast.makeText(requireContext(),
                    "Usando contratista: " + listaContratistas.get(0).getNombreContratista(),
                    Toast.LENGTH_LONG).show();
        }

        if (idContratista == null) {
            Toast.makeText(requireContext(),
                    "⚠️ Crea primero un contratista",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Crear FichaDto
        FichaDto fichaDto = new FichaDto();
        fichaDto.setIdContratista(idContratista);
        fichaDto.setIdProyecto(proyectoDto.getIdProyecto());
        fichaDto.setFichaEstado(proyectoOriginal.getEstado());
        fichaDto.setFichaEspecialidad(proyectoOriginal.getEspecialidad());
        // trabajadoresIds puede ser null o lista vacía

        Call<FichaDto> call = RetrofitClient.getApiService().crearFicha(fichaDto);

        call.enqueue(new Callback<FichaDto>() {
            @Override
            public void onResponse(Call<FichaDto> call, Response<FichaDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "✅ Ficha creada exitosamente", Toast.LENGTH_SHORT).show();
                    cargarProyectosDesdeAPI(); // Recargar lista
                } else {
                    Toast.makeText(requireContext(),
                            "⚠️ Proyecto creado pero error en ficha: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FichaDto> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "⚠️ Proyecto creado pero error en conexión de ficha",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al crear ficha", t);
            }
        });
    }

    private Integer buscarContratistaApropiado(String estado, String especialidad) {
        for (Contratista contratista : listaContratistas) {
            // Primero buscar coincidencia exacta
            boolean estadoCoincide = contratista.getEstadoContratista() != null &&
                    contratista.getEstadoContratista().equalsIgnoreCase(estado);
            boolean especialidadCoincide = contratista.getEspecialidad() != null &&
                    contratista.getEspecialidad().equalsIgnoreCase(especialidad);

            if (estadoCoincide && especialidadCoincide) {
                Log.d(TAG, "✅ Contratista encontrado: " + contratista.getNombreContratista());
                return contratista.getIdContratista();
            }
        }

        // Si no, buscar solo por estado
        for (Contratista contratista : listaContratistas) {
            if (contratista.getEstadoContratista() != null &&
                    contratista.getEstadoContratista().equalsIgnoreCase(estado)) {
                Log.d(TAG, "⚠️ Contratista por estado: " + contratista.getNombreContratista());
                return contratista.getIdContratista();
            }
        }

        // Si no, buscar solo por especialidad
        for (Contratista contratista : listaContratistas) {
            if (contratista.getEspecialidad() != null &&
                    contratista.getEspecialidad().equalsIgnoreCase(especialidad)) {
                Log.d(TAG, "⚠️ Contratista por especialidad: " + contratista.getNombreContratista());
                return contratista.getIdContratista();
            }
        }

        return null;
    }

    private void editarProyecto(Proyecto proyecto) {
        // Para editar necesitamos el idProyecto
        if (proyecto.getIdProyecto() == null) {
            Toast.makeText(requireContext(),
                    "No se puede editar - Falta ID del proyecto",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DialogCrearEditarProyecto dialog = new DialogCrearEditarProyecto(
                requireContext(),
                proyecto,
                new DialogCrearEditarProyecto.OnProyectoGuardadoListener() {
                    @Override
                    public void onProyectoGuardado(Proyecto proyecto) {
                        // No aplica
                    }

                    @Override
                    public void onProyectoActualizado(Proyecto proyectoActualizado) {
                        proyectoActualizado.setIdProyecto(proyecto.getIdProyecto());
                        actualizarProyectoEnAPI(proyectoActualizado);
                    }
                });

        dialog.show();
    }

    private void actualizarProyectoEnAPI(Proyecto proyecto) {
        if (proyecto.getIdProyecto() == null) {
            Toast.makeText(requireContext(), "No se puede actualizar sin ID", Toast.LENGTH_SHORT).show();
            return;
        }

        ProyectoDto proyectoDto = new ProyectoDto(
                proyecto.getProyectito(),
                proyecto.getEspecialidad(),
                proyecto.getDireccion()
        );

        Call<ProyectoDto> call = RetrofitClient.getApiService()
                .actualizarProyecto(proyecto.getIdProyecto(), proyectoDto);

        call.enqueue(new Callback<ProyectoDto>() {
            @Override
            public void onResponse(Call<ProyectoDto> call, Response<ProyectoDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "✅ Proyecto actualizado", Toast.LENGTH_SHORT).show();
                    cargarProyectosDesdeAPI(); // Recargar
                } else {
                    Toast.makeText(requireContext(), "❌ Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProyectoDto> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarProyecto(Proyecto proyecto) {
        if (proyecto.getIdProyecto() == null) {
            Toast.makeText(requireContext(),
                    "No se puede eliminar - Falta ID del proyecto",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Proyecto")
                .setMessage("¿Eliminar \"" + proyecto.getProyectito() + "\"?")
                .setPositiveButton("ELIMINAR", (dialog, which) -> {
                    eliminarProyectoEnAPI(proyecto);
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void eliminarProyectoEnAPI(Proyecto proyecto) {
        Call<Void> call = RetrofitClient.getApiService()
                .eliminarProyecto(proyecto.getIdProyecto());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "✅ Proyecto eliminado", Toast.LENGTH_SHORT).show();
                    cargarProyectosDesdeAPI(); // Recargar
                } else {
                    Toast.makeText(requireContext(), "❌ Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDetalleProyecto(FichaCompletaDto ficha) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.item_trabajador);

        TextView txtNombre = dialog.findViewById(R.id.txtNombreTrabajador);
        TextView txtDescripcion = dialog.findViewById(R.id.DescripcionTrabajador);
        TextView txtEspecialidad = dialog.findViewById(R.id.EspecialidadTrabajador);
        TextView txtEquipo = dialog.findViewById(R.id.EquipoTrabajador);

        if (txtNombre != null) {
            txtNombre.setText(ficha.getNombreProyecto());
        }

        if (txtDescripcion != null) {
            String desc = "Contratista: " + ficha.getNombreContratista() +
                    "\n" + ficha.getDescripcionContratista();
            txtDescripcion.setText(desc);
        }

        if (txtEspecialidad != null) {
            txtEspecialidad.setText("Especialidad: " + ficha.getFichaEspecialidad() +
                    "\nTipo: " + (ficha.getTipoProyecto() != null ? ficha.getTipoProyecto() : "N/A"));
        }

        if (txtEquipo != null) {
            String ubicacion = "Ubicación: " + ficha.getLugarProyecto() +
                    "\nEstado: " + ficha.getFichaEstado();
            if (ficha.getEquipoTrabajo() != null && !ficha.getEquipoTrabajo().isEmpty()) {
                ubicacion += "\n\nEquipo:\n" + ficha.getEquipoTrabajo();
            }
            txtEquipo.setText(ubicacion);
        }

        dialog.show();
    }

    private void mostrarDetalleProyectoBasico(Proyecto proyecto) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.item_trabajador);

        TextView txtNombre = dialog.findViewById(R.id.txtNombreTrabajador);
        TextView txtDescripcion = dialog.findViewById(R.id.DescripcionTrabajador);
        TextView txtEspecialidad = dialog.findViewById(R.id.EspecialidadTrabajador);
        TextView txtEquipo = dialog.findViewById(R.id.EquipoTrabajador);

        if (txtNombre != null) txtNombre.setText(proyecto.getProyectito());
        if (txtDescripcion != null) txtDescripcion.setText("Contratista: " + proyecto.getContratista());
        if (txtEspecialidad != null) txtEspecialidad.setText("Especialidad: " + proyecto.getEspecialidad());
        if (txtEquipo != null) txtEquipo.setText("Lugar: " + proyecto.getDireccion());

        dialog.show();
    }
}