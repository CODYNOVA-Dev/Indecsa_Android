package com.example.indecsa_v2.admin.trabajador;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.Domicilio;
import com.example.indecsa_v2.models.Estado;
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.network.CatalogosCache;
import com.example.indecsa_v2.network.DomicilioHelper;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarTrabajadorDialog extends DialogFragment {

    public interface OnAgregadoListener { void onAgregado(); }
    private OnAgregadoListener listener;
    public void setOnAgregadoListener(OnAgregadoListener l) { this.listener = l; }

    /** Enum del backend: Trabajador.Sexo. */
    private static final String[] SEXOS = { "Masculino", "Femenino", "Otro" };

    private List<Estado> estadosCache;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        if (d.getWindow() != null) d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return d;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_agregar_trabajador, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            getDialog().setCanceledOnTouchOutside(false);
        }

        Spinner spinnerSexo = view.findViewById(R.id.spinnerSexo);
        ArrayAdapter<String> sexoAdp = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, SEXOS);
        sexoAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSexo.setAdapter(sexoAdp);

        Spinner spinnerEstadoGeo = view.findViewById(R.id.spinnerEstadoGeo);
        cargarEstadosEnSpinner(spinnerEstadoGeo);

        AppCompatButton btnGuardar  = view.findViewById(R.id.btnGuardar);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre        = getText(view, R.id.editNombre);
            String curp          = getText(view, R.id.editCurp);
            String rfc           = getText(view, R.id.editRfc);
            String nss           = getText(view, R.id.editNss);
            String nacionalidad  = getText(view, R.id.editNacionalidad);
            String correo        = getText(view, R.id.editCorreo);
            String telefono      = getText(view, R.id.editTelefono);
            String puesto        = getText(view, R.id.editPuesto);
            String descPuesto    = getText(view, R.id.editDescripcion);
            String especialidad  = getText(view, R.id.editEspecialidad);
            String escolaridad   = getText(view, R.id.editEscolaridad);
            String contratacion  = getText(view, R.id.editContratacion);
            String jornada       = getText(view, R.id.editJornada);
            String experiencia   = getText(view, R.id.editExperiencia);
            String fechaIngreso  = getText(view, R.id.editFechaIngreso);
            String calle         = getText(view, R.id.editCalle);
            String municipio     = getText(view, R.id.editMunicipio);
            String sexo          = (String) spinnerSexo.getSelectedItem();

            if (nombre.isEmpty() || curp.isEmpty() || rfc.isEmpty() || nacionalidad.isEmpty()
                    || correo.isEmpty() || telefono.isEmpty() || puesto.isEmpty()
                    || descPuesto.isEmpty() || especialidad.isEmpty() || escolaridad.isEmpty()
                    || contratacion.isEmpty() || jornada.isEmpty() || fechaIngreso.isEmpty()
                    || calle.isEmpty() || municipio.isEmpty()) {
                Toast.makeText(getContext(),
                        "Completa todos los campos marcados con *",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Estado estadoGeo = obtenerEstadoSeleccionado(spinnerEstadoGeo);
            if (estadoGeo == null) {
                Toast.makeText(getContext(),
                        "No hay estados disponibles. Crea uno en /api/estados primero",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // 1) Crear domicilio → 2) crear trabajador con el domicilio + estadoCalidadVida
            DomicilioHelper.crear(calle, null, null, null, municipio, estadoGeo.getIdEstado(),
                    new DomicilioHelper.Callback() {
                        @Override public void onCreated(@NonNull Domicilio dom) {
                            crearTrabajador(nombre, curp, rfc, nss, nacionalidad, sexo,
                                    correo, telefono, puesto, descPuesto, especialidad,
                                    escolaridad, contratacion, jornada, experiencia,
                                    fechaIngreso, dom, estadoGeo);
                        }
                        @Override public void onError(@NonNull String msg) {
                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void crearTrabajador(String nombre, String curp, String rfc, String nss,
                                 String nacionalidad, String sexo, String correo, String telefono,
                                 String puesto, String descPuesto, String especialidad,
                                 String escolaridad, String contratacion, String jornada,
                                 String experiencia, String fechaIngreso,
                                 Domicilio dom, Estado estadoCalidadVida) {
        TrabajadorDto dto = new TrabajadorDto();
        dto.setNombreTrabajador(nombre);
        dto.setCurp(curp);
        dto.setRfc(rfc);
        if (!nss.isEmpty()) dto.setNssTrabajador(nss);
        dto.setNacionalidad(nacionalidad);
        dto.setSexo(sexo);
        dto.setCorreoTrabajador(correo);
        dto.setTelefonoTrabajador(telefono);
        dto.setPuesto(puesto);
        dto.setDescPuesto(descPuesto);
        dto.setEspecialidadTrabajador(especialidad);
        dto.setEscolaridad(escolaridad);
        dto.setContratacion(contratacion);
        dto.setJornada(jornada);
        if (!experiencia.isEmpty()) dto.setExperiencia(experiencia);
        dto.setFechaIngreso(fechaIngreso);
        dto.setDomicilio(dom);
        dto.setEstadoCalidadVida(estadoCalidadVida);
        dto.setEstadoTrabajador("ACTIVO");
        dto.setEvaluacionTrabajador(0);

        RetrofitClient.getApiService().createTrabajador(dto)
                .enqueue(new Callback<TrabajadorDto>() {
                    @Override
                    public void onResponse(@NonNull Call<TrabajadorDto> call, @NonNull Response<TrabajadorDto> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Trabajador agregado", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onAgregado();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al guardar (código " + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<TrabajadorDto> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarEstadosEnSpinner(Spinner spinner) {
        CatalogosCache.getEstados(items -> {
            estadosCache = items;
            String[] labels = new String[items.size()];
            for (int i = 0; i < items.size(); i++) {
                Estado e = items.get(i);
                labels[i] = e.getNombreEst() != null ? e.getNombreEst() : ("Estado #" + e.getIdEstado());
            }
            ArrayAdapter<String> adp = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, labels);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adp);
        });
    }

    @Nullable
    private Estado obtenerEstadoSeleccionado(Spinner spinner) {
        if (estadosCache == null || estadosCache.isEmpty()) return null;
        int pos = spinner.getSelectedItemPosition();
        if (pos < 0 || pos >= estadosCache.size()) return null;
        return estadosCache.get(pos);
    }

    private String getText(View root, int id) {
        EditText et = root.findViewById(id);
        return et != null ? et.getText().toString().trim() : "";
    }
}
