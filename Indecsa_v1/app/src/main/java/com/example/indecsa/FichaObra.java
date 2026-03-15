package com.example.indecsa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.indecsa.FichaContratistaAdapter;
import com.example.indecsa.models.Contratista;
import com.example.indecsa.network.ApiService;
import com.example.indecsa.network.RetrofitClient;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FichaObra extends Fragment {

    private String estado;
    private String especialidad;

    private ListView lista;
    private TextView titulo;

    public FichaObra() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ficha_obra, container, false);

        lista = view.findViewById(R.id.listaContratistas);
        titulo = view.findViewById(R.id.txtTitulo);

        // Recibir datos del fragmento anterior
        if (getArguments() != null) {
            estado = getArguments().getString("estado");
            especialidad = getArguments().getString("especialidad");
        }

        titulo.setText("Contratistas de " + estado + " con especialidad en " + especialidad);

        // Llamada a la API para obtener contratistas
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Contratista>> call = api.obtenerContratistas();

        call.enqueue(new Callback<List<Contratista>>() {
            @Override
            public void onResponse(Call<List<Contratista>> call, Response<List<Contratista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Contratista> contratistas = response.body();

                    // Filtrar por estado y especialidad
                    contratistas = contratistas.stream()
                            .filter(c -> estado.equalsIgnoreCase(c.getEstadoContratista())
                                    && especialidad.equalsIgnoreCase(c.getEspecialidad()))
                            .collect(Collectors.toList());

                    FichaContratistaAdapter adapter = new FichaContratistaAdapter(getContext(), contratistas);
                    lista.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No hay contratistas disponibles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Contratista>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
