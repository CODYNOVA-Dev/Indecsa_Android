package com.example.indecsa_v2.admin.contrato;

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
import com.example.indecsa_v2.models.AsignacionProyectoContratistaDto;
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarContratoDialog extends DialogFragment {

    public interface OnAgregadoListener { void onAgregado(); }
    private OnAgregadoListener listener;
    public void setOnAgregadoListener(OnAgregadoListener l) { this.listener = l; }

    private final List<ProyectoDto> proyectos    = new ArrayList<>();
    private final List<Contratista> contratistas = new ArrayList<>();

    private Spinner spinnerProyecto;
    private Spinner spinnerContratista;

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
        return inflater.inflate(R.layout.dialog_agregar_contrato, container, false);
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

        spinnerProyecto    = view.findViewById(R.id.spinnerContratoProyecto);
        spinnerContratista = view.findViewById(R.id.spinnerContratoContratista);

        cargarProyectos();
        cargarContratistas();

        AppCompatButton btnGuardar  = view.findViewById(R.id.btnGuardarContrato);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnCancelarContrato);

        btnCancelar.setOnClickListener(v -> dismiss());
        btnGuardar.setOnClickListener(v -> guardar(view));
    }

    private void cargarProyectos() {
        RetrofitClient.getApiService().getAllProyectos()
                .enqueue(new Callback<List<ProyectoDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ProyectoDto>> call, @NonNull Response<List<ProyectoDto>> r) {
                        if (!isAdded()) return;
                        if (r.isSuccessful() && r.body() != null) {
                            proyectos.clear();
                            proyectos.addAll(r.body());
                            String[] labels = new String[proyectos.size()];
                            for (int i = 0; i < proyectos.size(); i++) {
                                labels[i] = proyectos.get(i).getNombreProyecto() != null
                                        ? proyectos.get(i).getNombreProyecto()
                                        : ("Proyecto #" + proyectos.get(i).getIdProyecto());
                            }
                            ArrayAdapter<String> adp = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, labels);
                            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerProyecto.setAdapter(adp);
                        }
                    }
                    @Override public void onFailure(@NonNull Call<List<ProyectoDto>> call, @NonNull Throwable t) {
                        if (isAdded()) Toast.makeText(getContext(), "Error cargando proyectos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarContratistas() {
        RetrofitClient.getApiService().getAllContratistas()
                .enqueue(new Callback<List<Contratista>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Contratista>> call, @NonNull Response<List<Contratista>> r) {
                        if (!isAdded()) return;
                        if (r.isSuccessful() && r.body() != null) {
                            contratistas.clear();
                            contratistas.addAll(r.body());
                            String[] labels = new String[contratistas.size()];
                            for (int i = 0; i < contratistas.size(); i++) {
                                labels[i] = contratistas.get(i).getNombreContratista() != null
                                        ? contratistas.get(i).getNombreContratista()
                                        : ("Contratista #" + contratistas.get(i).getIdContratista());
                            }
                            ArrayAdapter<String> adp = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, labels);
                            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerContratista.setAdapter(adp);
                        }
                    }
                    @Override public void onFailure(@NonNull Call<List<Contratista>> call, @NonNull Throwable t) {
                        if (isAdded()) Toast.makeText(getContext(), "Error cargando contratistas", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardar(View view) {
        if (proyectos.isEmpty() || contratistas.isEmpty()) {
            Toast.makeText(getContext(), "Faltan datos de catálogo", Toast.LENGTH_SHORT).show();
            return;
        }

        int pp = spinnerProyecto.getSelectedItemPosition();
        int pc = spinnerContratista.getSelectedItemPosition();
        if (pp < 0 || pp >= proyectos.size() || pc < 0 || pc >= contratistas.size()) {
            Toast.makeText(getContext(), "Selecciona proyecto y contratista", Toast.LENGTH_SHORT).show();
            return;
        }

        String numero   = text(view, R.id.editContratoNumero);
        String personal = text(view, R.id.editContratoPersonal);
        String fechaIni = text(view, R.id.editContratoFechaInicio);
        String fechaFin = text(view, R.id.editContratoFechaFin);
        String obs      = text(view, R.id.editContratoObservaciones);

        int personalInt;
        try {
            personalInt = Integer.parseInt(personal);
            if (personalInt < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Personal asignado debe ser ≥ 1", Toast.LENGTH_SHORT).show();
            return;
        }

        AsignacionProyectoContratistaDto dto = new AsignacionProyectoContratistaDto();
        dto.setProyecto(new ProyectoDto(proyectos.get(pp).getIdProyecto()));
        dto.setContratista(new Contratista(contratistas.get(pc).getIdContratista()));
        if (!numero.isEmpty())   dto.setNumeroContrato(numero);
        if (!fechaIni.isEmpty()) dto.setFechaInicio(fechaIni);
        if (!fechaFin.isEmpty()) dto.setFechaFinEstimada(fechaFin);
        dto.setPersonalAsignado(personalInt);
        dto.setEstatusContrato("VIGENTE");
        if (!obs.isEmpty()) dto.setObservaciones(obs);

        RetrofitClient.getApiService().createAsignacionPc(dto)
                .enqueue(new Callback<AsignacionProyectoContratistaDto>() {
                    @Override
                    public void onResponse(@NonNull Call<AsignacionProyectoContratistaDto> call,
                                           @NonNull Response<AsignacionProyectoContratistaDto> r) {
                        if (!isAdded()) return;
                        if (r.isSuccessful()) {
                            Toast.makeText(getContext(), "Contrato creado", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onAgregado();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), ApiErrorMessages.forCode(r.code()), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<AsignacionProyectoContratistaDto> call, @NonNull Throwable t) {
                        if (isAdded()) Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String text(View root, int id) {
        EditText et = root.findViewById(id);
        return et != null ? et.getText().toString().trim() : "";
    }
}
