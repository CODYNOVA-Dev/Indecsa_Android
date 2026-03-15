package com.example.indecsa;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.indecsa.models.Contratista;
import com.example.indecsa.network.RetrofitClient;
import com.example.indecsa.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarContratista extends Fragment {

    private EditText ediNombre, ediDescripcion, ediCorreo, ediTelefono;
    private Spinner spinnerEstado, spinnerEspecialidad;
    private Button btnEditar;

    private int idContratista;

    // Estados corregidos: solo Hidalgo, CDMX y Puebla
    private final String[] estados = {"Hidalgo", "CDMX", "Puebla"};

    // Especialidades corregidas
    private final String[] especialidades = {
            "Obra",
            "Remodelacion",
            "Venta de mobiliario",
            "Instalacion de mobiliario"
    };

    public static EditarContratista newInstance(int id) {
        EditarContratista fragment = new EditarContratista();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_contratista, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idContratista = getArguments().getInt("id", -1);

        ediNombre = view.findViewById(R.id.edinombrecont);
        ediDescripcion = view.findViewById(R.id.edidatit2);
        ediCorreo = view.findViewById(R.id.edidatit4);
        ediTelefono = view.findViewById(R.id.edidatit3);
        spinnerEspecialidad = view.findViewById(R.id.spinnerEspecialidad);
        spinnerEstado = view.findViewById(R.id.spinnerEstado);
        btnEditar = view.findViewById(R.id.btnEditar);

        configurarSpinners();
        cargarDatos();

        btnEditar.setOnClickListener(v -> actualizarContratista());
    }

    private void configurarSpinners() {
        ArrayAdapter<String> adapterEst = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, estados);
        adapterEst.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEst);

        ArrayAdapter<String> adapterEsp = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, especialidades);
        adapterEsp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecialidad.setAdapter(adapterEsp);
    }

    private void cargarDatos() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Contratista>> call = api.obtenerContratistas();

        call.enqueue(new Callback<List<Contratista>>() {
            @Override
            public void onResponse(Call<List<Contratista>> call, Response<List<Contratista>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "No se pudo cargar", Toast.LENGTH_SHORT).show();
                    return;
                }

                Contratista c = null;
                for (Contratista temp : response.body()) {
                    if (temp.getIdContratista() == idContratista) {
                        c = temp;
                        break;
                    }
                }

                if (c == null) {
                    Toast.makeText(getContext(), "Contratista no encontrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Rellenar campos
                ediNombre.setText(c.getNombreContratista());
                ediDescripcion.setText(c.getDescripcionContratista());
                ediCorreo.setText(c.getCorreo());
                ediTelefono.setText(c.getTelefono());

                spinnerEstado.setSelection(getIndex(spinnerEstado, c.getEstadoContratista()));
                spinnerEspecialidad.setSelection(getIndex(spinnerEspecialidad, c.getEspecialidad()));
            }

            @Override
            public void onFailure(Call<List<Contratista>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e("EditarContratista", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void actualizarContratista() {
        // Validaciones
        if (ediNombre.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ediDescripcion.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "La descripción es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto Contratista
        Contratista c = new Contratista();
        c.setIdContratista(idContratista);
        c.setNombreContratista(ediNombre.getText().toString().trim());
        c.setDescripcionContratista(ediDescripcion.getText().toString().trim());
        c.setCorreo(ediCorreo.getText().toString().trim());
        c.setTelefono(ediTelefono.getText().toString().trim());
        c.setEspecialidad(spinnerEspecialidad.getSelectedItem().toString());
        c.setEstadoContratista(spinnerEstado.getSelectedItem().toString());


        // Logs para debug
        android.util.Log.d("EditarContratista", "=== ACTUALIZANDO CONTRATISTA ===");
        android.util.Log.d("EditarContratista", "ID: " + idContratista);
        android.util.Log.d("EditarContratista", "Nombre: " + c.getNombreContratista());
        android.util.Log.d("EditarContratista", "Estado: " + c.getEstadoContratista());
        android.util.Log.d("EditarContratista", "Especialidad: " + c.getEspecialidad());

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<Contratista> call = api.actualizarContratista(idContratista, c);

        call.enqueue(new Callback<Contratista>() {
            @Override
            public void onResponse(Call<Contratista> call, Response<Contratista> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "✅ Actualizado correctamente", Toast.LENGTH_SHORT).show();
                    android.util.Log.d("EditarContratista", "Actualización exitosa");
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    String errorMsg = "❌ Error " + response.code() + ": ";

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMsg += errorBody;
                            android.util.Log.e("EditarContratista", "Error body: " + errorBody);
                        } else {
                            errorMsg += getHttpErrorMessage(response.code());
                        }
                    } catch (Exception e) {
                        errorMsg += getHttpErrorMessage(response.code());
                        android.util.Log.e("EditarContratista", "Error parseando respuesta", e);
                    }

                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    android.util.Log.e("EditarContratista", "Error al actualizar: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Contratista> call, Throwable t) {
                String errorMsg = "🔴 Error de conexión: " + t.getMessage();
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                android.util.Log.e("EditarContratista", "Error de red", t);
                t.printStackTrace();
            }
        });
    }

    private String getHttpErrorMessage(int code) {
        switch (code) {
            case 400: return "Datos inválidos enviados al servidor";
            case 401: return "No autorizado";
            case 403: return "Acceso prohibido";
            case 404: return "Contratista no encontrado en el servidor";
            case 500: return "Error interno del servidor";
            case 502: return "Bad Gateway - Servidor no disponible";
            case 503: return "Servicio no disponible";
            default: return "Error desconocido";
        }
    }

    private int getIndex(Spinner spinner, String value) {
        if (value == null) return 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
}