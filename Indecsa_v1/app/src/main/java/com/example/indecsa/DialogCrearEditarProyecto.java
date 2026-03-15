package com.example.indecsa;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class DialogCrearEditarProyecto extends Dialog {

    public interface OnProyectoGuardadoListener {
        void onProyectoGuardado(Proyecto proyecto);
        void onProyectoActualizado(Proyecto proyecto);
    }

    private Context context;
    private OnProyectoGuardadoListener listener;
    private Proyecto proyectoEditar;
    private boolean esEdicion = false;

    private TextView txtTituloDialog;
    private EditText etNombreProyecto, etDescripcion, etContratista, etDireccion;
    private Spinner spinnerEspecialidad, spinnerEstado;
    private Button btnGuardar, btnCancelar;

    public DialogCrearEditarProyecto(@NonNull Context context, OnProyectoGuardadoListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    public DialogCrearEditarProyecto(@NonNull Context context, Proyecto proyecto, OnProyectoGuardadoListener listener) {
        super(context);
        this.context = context;
        this.proyectoEditar = proyecto;
        this.listener = listener;
        this.esEdicion = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_dialog_crear_editar_proyecto);

        // Hacer el dialog más ancho
        Window window = getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = (int) (metrics.widthPixels * 0.90); // 90% del ancho
            int height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setLayout(width, height);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        inicializarVistas();
        configurarSpinners();
        cargarDatosSiEdicion();
        configurarListeners();
    }

    private void inicializarVistas() {
        txtTituloDialog = findViewById(R.id.txtTituloDialog);
        etNombreProyecto = findViewById(R.id.etNombreProyecto);

        etContratista = findViewById(R.id.etContratista);
        etDireccion = findViewById(R.id.etDireccion);
        spinnerEspecialidad = findViewById(R.id.spinnerEspecialidad);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        if (esEdicion) {
            txtTituloDialog.setText("EDITAR PROYECTO");
            btnGuardar.setText("ACTUALIZAR");
        }
    }

    private void configurarSpinners() {
        String[] especialidades = {"Obra", "Remodelación", "Venta de Mobiliario", "Instalación de Mobiliario"};
        ArrayAdapter<String> adapterEspecialidad = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, especialidades);
        adapterEspecialidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecialidad.setAdapter(adapterEspecialidad);

        String[] estados = {"CDMX", "Puebla", "Hidalgo"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, estados);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);
    }

    private void cargarDatosSiEdicion() {
        if (esEdicion && proyectoEditar != null) {
            etNombreProyecto.setText(proyectoEditar.getProyectito());
            etContratista.setText(proyectoEditar.getContratista());
            etDireccion.setText(proyectoEditar.getDireccion());

            for (int i = 0; i < spinnerEspecialidad.getCount(); i++) {
                if (spinnerEspecialidad.getItemAtPosition(i).equals(proyectoEditar.getEspecialidad())) {
                    spinnerEspecialidad.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < spinnerEstado.getCount(); i++) {
                if (spinnerEstado.getItemAtPosition(i).equals(proyectoEditar.getEstado())) {
                    spinnerEstado.setSelection(i);
                    break;
                }
            }
        }
    }

    private void configurarListeners() {
        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            if (validarCampos()) {
                guardarProyecto();
            }
        });
    }

    private boolean validarCampos() {
        if (etNombreProyecto.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "El nombre del proyecto es requerido", Toast.LENGTH_SHORT).show();
            return false;
        }



        if (etContratista.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "El contratista es requerido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void guardarProyecto() {
        String nombre = etNombreProyecto.getText().toString().trim();
        String contratista = etContratista.getText().toString().trim();
        String especialidad = spinnerEspecialidad.getSelectedItem().toString();
        String estado = spinnerEstado.getSelectedItem().toString();
        String direccion = etDireccion.getText().toString().trim();

        Proyecto proyecto;

        if (esEdicion && proyectoEditar != null) {
            proyecto = new Proyecto(
                    proyectoEditar.getIdProyecto(),
                    nombre,
                    " ",
                    contratista,
                    especialidad,
                    estado,
                    proyectoEditar.getAvance(),
                    direccion,
                    proyectoEditar.getImagenResId()
            );
            listener.onProyectoActualizado(proyecto);
        } else {
            proyecto = new Proyecto(
                    nombre,
                    " ",
                    contratista,
                    especialidad,
                    estado,
                    0,
                    direccion,
                    R.drawable.usuario
            );
            listener.onProyectoGuardado(proyecto);
        }

        dismiss();
    }
}