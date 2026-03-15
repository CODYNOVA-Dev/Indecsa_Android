package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.indecsa.models.Contratista;
import com.example.indecsa.network.ApiService;
import com.example.indecsa.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fichaagrega extends Fragment {

    private String estadoRecibido;
    private String especialidadRecibida;

    private float calificacion = 0f;

    // EditTexts del XML
    private EditText ediNombre, ediDescripcion, ediTelefono, ediCorreo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fichaagrega, container, false);

        if (getArguments() != null) {
            estadoRecibido = getArguments().getString("estadoSeleccionado");
            especialidadRecibida = getArguments().getString("especialidadSeleccionada");
        }

        // Obtener referencias
        ediNombre = view.findViewById(R.id.edinombrecont);
        ediDescripcion = view.findViewById(R.id.edidescripcion);
        ediTelefono = view.findViewById(R.id.editelefono);   // ← REUSADO PARA TELEFONO
        ediCorreo = view.findViewById(R.id.edicorreo);     // ← REUSADO PARA CORREO

        configurarSpinners(view);
        configurarRating(view);
        configurarBotonGuardar(view);

        return view;
    }

    private void configurarSpinners(View view) {
        Spinner spinnerEstado = view.findViewById(R.id.spinnerEstado);
        Spinner spinnerEspecialidad = view.findViewById(R.id.spinnerEspecialidad);

        // ESTADO
        String[] estados = new String[]{estadoRecibido};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, estados);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);
        spinnerEstado.setEnabled(false);

        // ESPECIALIDAD
        String[] especialidades = new String[]{especialidadRecibida};
        ArrayAdapter<String> adapterEsp = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, especialidades);
        adapterEsp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecialidad.setAdapter(adapterEsp);
        spinnerEspecialidad.setEnabled(false);
    }

    private void configurarRating(View view) {
        RatingBar ratingBar = view.findViewById(R.id.ratingCalificacion);
        ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) -> {
            calificacion = rating;
        });
    }

    private void configurarBotonGuardar(View view) {
        Button btnGuardar = view.findViewById(R.id.btnAgregar);

        btnGuardar.setOnClickListener(v -> {

            // Validación base
            if (ediNombre.getText().toString().isEmpty() ||
                    ediDescripcion.getText().toString().isEmpty() ||
                    ediTelefono.getText().toString().isEmpty() ||
                    ediCorreo.getText().toString().isEmpty()) {

                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear objeto Contratista EXACTO como tu backend lo pide
            Contratista c = new Contratista();
            c.setNombreContratista(ediNombre.getText().toString());
            c.setDescripcionContratista(ediDescripcion.getText().toString());
            c.setEstadoContratista(estadoRecibido);
            c.setEspecialidad(especialidadRecibida);
            c.setCalificacion((int) calificacion);

            c.setTelefono(ediTelefono.getText().toString());
            c.setCorreo(ediCorreo.getText().toString());

            ApiService api = RetrofitClient.getApiService();
            Call<Contratista> call = api.crearContratista(c);

            call.enqueue(new Callback<Contratista>() {
                @Override
                public void onResponse(Call<Contratista> call, Response<Contratista> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Contratista agregado correctamente", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 400) {
                        Toast.makeText(getContext(), "El contratista ya existe o datos inválidos", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Error " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Contratista> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
