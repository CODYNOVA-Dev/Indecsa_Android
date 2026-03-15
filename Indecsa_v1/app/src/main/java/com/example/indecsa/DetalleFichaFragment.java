package com.example.indecsa;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.indecsa.network.ApiService;
import com.example.indecsa.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleFichaFragment extends Fragment {

    private static final String TAG = "DetalleFicha";

    // Constantes para los argumentos
    private static final String ARG_ID_FICHA = "idFicha";
    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_DESCRIPCION = "descripcion";
    private static final String ARG_PROYECTO = "proyecto";
    private static final String ARG_EQUIPO = "equipo";
    private static final String ARG_ESTADO = "estado";
    private static final String ARG_ESPECIALIDAD = "especialidad";

    private Integer idFicha;

    public DetalleFichaFragment() {
        // Required empty public constructor
    }

    public static DetalleFichaFragment newInstance(Integer idFicha, String nombre, String descripcion,
                                                   String proyecto, String equipo,
                                                   String estado, String especialidad) {
        DetalleFichaFragment fragment = new DetalleFichaFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_ID_FICHA, idFicha);
        args.putString(ARG_NOMBRE, nombre);
        args.putString(ARG_DESCRIPCION, descripcion);
        args.putString(ARG_PROYECTO, proyecto);
        args.putString(ARG_EQUIPO, equipo);
        args.putString(ARG_ESTADO, estado);
        args.putString(ARG_ESPECIALIDAD, especialidad);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_ficha, container, false);

        Bundle args = getArguments();
        if (args != null) {
            idFicha = args.getInt(ARG_ID_FICHA, 0);
            String nombre = args.getString(ARG_NOMBRE, "");
            String descripcion = args.getString(ARG_DESCRIPCION, "");
            String proyecto = args.getString(ARG_PROYECTO, "");
            String equipo = args.getString(ARG_EQUIPO, "");
            String estado = args.getString(ARG_ESTADO, "");
            String especialidad = args.getString(ARG_ESPECIALIDAD, "");

            // Referencias a los TextView
            TextView txtNombre = view.findViewById(R.id.detalleNombre);
            TextView txtDescripcion = view.findViewById(R.id.detalleDescripcion);
            TextView txtProyecto = view.findViewById(R.id.detalleProyecto);
            TextView txtEquipo = view.findViewById(R.id.detalleEquipo);
            TextView txtEstado = view.findViewById(R.id.detalleEstado);
            TextView txtEspecialidad = view.findViewById(R.id.detalleEspecialidad);
            TextView txtTelefono = view.findViewById(R.id.detalleTelefono);
            TextView txtCorreo = view.findViewById(R.id.detalleCorreo);
            TextView txtFechaRegistro = view.findViewById(R.id.detalleFechaRegistro);

            // Asignar datos
            txtNombre.setText(nombre);
            txtDescripcion.setText(descripcion);
            txtProyecto.setText(proyecto);
            txtEquipo.setText(equipo);
            txtEstado.setText("Estado: " + estado);
            txtEspecialidad.setText("Especialidad: " + especialidad);
            txtTelefono.setText("Teléfono: 55-1234-5678");
            txtCorreo.setText("Correo: " + nombre.toLowerCase().replace(" ", ".") + "@indecsa.com");
            txtFechaRegistro.setText("Fecha de registro: 15/01/2024");

            // BOTÓN MARCAR COMO COMPLETADO
            Button btnCompletado = view.findViewById(R.id.detalleBtnCompletado);
            Button btnRegresar = view.findViewById(R.id.btnRegresar);

            if (btnCompletado != null) {
                btnCompletado.setOnClickListener(v ->
                        mostrarDialogoConfirmacion(nombre)
                );
            }

            if (btnRegresar != null) {
                btnRegresar.setOnClickListener(v ->
                        requireActivity().getSupportFragmentManager().popBackStack()
                );
            }
        }

        return view;
    }

    private void mostrarDialogoConfirmacion(String nombreContratista) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Marcar como completado")
                .setMessage("¿Estás seguro de marcar esta ficha como completada?\n\n" +
                        "Esto eliminará la ficha de " + nombreContratista +
                        " pero conservará el contratista, trabajadores y proyecto.")
                .setPositiveButton("Sí, completar", (dialog, which) ->
                        eliminarFicha()
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarFicha() {
        if (idFicha == null || idFicha == 0) {
            Toast.makeText(getContext(),
                    "Error: ID de ficha no válido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Eliminando ficha con ID: " + idFicha);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = api.deleteFicha(idFicha);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "✓ Ficha marcada como completada",
                            Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "Ficha eliminada exitosamente");

                    // Regresar a la lista
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    String errorMsg = "Error al completar ficha: " + response.code();
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMsg, t);
            }
        });
    }
}