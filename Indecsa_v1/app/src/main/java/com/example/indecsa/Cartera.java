package com.example.indecsa;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Calendar;

public class Cartera extends Fragment {

    private EditText edtProyecto;
    private RatingBar ratingExperiencia;
    private Button btnDisponible, btnFechaInicio, btnFechaFin, btnGuardar;
    private ListView listEquipo;
    private boolean disponible = true;
    private ArrayList<String> equipo;

    private Context mContext;

    public Cartera() {
        // Constructor vacío obligatorio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cartera_contratistas, container, false);

        mContext = getContext();

        edtProyecto = view.findViewById(R.id.edtProyecto);
        ratingExperiencia = view.findViewById(R.id.ratingExperiencia);
        btnDisponible = view.findViewById(R.id.btnDisponible);
        btnFechaInicio = view.findViewById(R.id.btnFechaInicio);
        btnFechaFin = view.findViewById(R.id.btnFechaFin);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        listEquipo = view.findViewById(R.id.listEquipo);

        // Equipo de ejemplo
        equipo = new ArrayList<>();
        equipo.add("Juan Pérez | NSS: 12345 | Albañilería");
        equipo.add("María López | NSS: 67890 | Electricidad");
        equipo.add("Carlos Sánchez | NSS: 54321 | Plomería");
        equipo.add("Ana Torres | NSS: 98765 | Pintura");
        equipo.add("Luis Ramírez | NSS: 11223 | Carpintería");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                mContext,
                android.R.layout.simple_list_item_1,
                equipo
        );
        listEquipo.setAdapter(adapter);

        // Cargar datos previos
        cargarCambios();

        // Botón disponible
        btnDisponible.setOnClickListener(v -> {
            disponible = !disponible;
            actualizarBotonDisponible();
        });

        // Fechas
        btnFechaInicio.setOnClickListener(v -> mostrarCalendario(btnFechaInicio));
        btnFechaFin.setOnClickListener(v -> mostrarCalendario(btnFechaFin));

        // Guardar
        btnGuardar.setOnClickListener(v -> guardarCambios());

        return view;
    }

    private void actualizarBotonDisponible() {
        if (disponible) {
            btnDisponible.setText("Disponible");
            btnDisponible.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            btnDisponible.setText("No disponible");
            btnDisponible.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void mostrarCalendario(Button boton) {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                mContext,
                (view, year, month, dayOfMonth) -> {
                    String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                    boton.setText(fecha);
                },
                año, mes, dia
        );
        datePickerDialog.show();
    }

    private void guardarCambios() {
        SharedPreferences prefs = mContext.getSharedPreferences("CarteraPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("proyecto", edtProyecto.getText().toString());
        editor.putFloat("experiencia", ratingExperiencia.getRating());
        editor.putBoolean("disponible", disponible);
        editor.putString("fechaInicio", btnFechaInicio.getText().toString());
        editor.putString("fechaFin", btnFechaFin.getText().toString());

        StringBuilder sb = new StringBuilder();
        for (String t : equipo) sb.append(t).append(";");
        editor.putString("equipo", sb.toString());

        editor.apply();

        Toast.makeText(mContext, "SE GUARDARON LOS CAMBIOS", Toast.LENGTH_LONG).show();
    }

    private void cargarCambios() {
        SharedPreferences prefs = mContext.getSharedPreferences("CarteraPrefs", Context.MODE_PRIVATE);

        edtProyecto.setText(prefs.getString("proyecto", ""));
        ratingExperiencia.setRating(prefs.getFloat("experiencia", 0f));
        disponible = prefs.getBoolean("disponible", true);
        actualizarBotonDisponible();

        btnFechaInicio.setText(prefs.getString("fechaInicio", "Seleccionar Fecha de Inicio"));
        btnFechaFin.setText(prefs.getString("fechaFin", "Seleccionar Fecha de Fin"));

        String equipoTexto = prefs.getString("equipo", "");
        if (!equipoTexto.isEmpty()) {
            String[] trabajadores = equipoTexto.split(";");
            equipo.clear();
            for (String t : trabajadores) {
                if (!t.trim().isEmpty()) equipo.add(t.trim());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    mContext,
                    android.R.layout.simple_list_item_1,
                    equipo
            );
            listEquipo.setAdapter(adapter);
        }
    }
}
