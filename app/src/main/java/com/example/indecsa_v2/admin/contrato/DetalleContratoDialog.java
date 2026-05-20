package com.example.indecsa_v2.admin.contrato;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.AsignacionProyectoContratistaDto;
import com.example.indecsa_v2.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleContratoDialog extends DialogFragment {

    public interface OnCambioListener { void onCambio(); }
    private OnCambioListener listener;
    public void setOnCambioListener(OnCambioListener l) { this.listener = l; }

    private static final String ARG_ID         = "idAsignacionPc";
    private static final String ARG_PROYECTO   = "proyectoNombre";
    private static final String ARG_CONTRATIS  = "contratistaNombre";
    private static final String ARG_NUMERO     = "numero";
    private static final String ARG_PERSONAL   = "personal";
    private static final String ARG_FECHA_INI  = "fechaInicio";
    private static final String ARG_FECHA_FIN  = "fechaFin";
    private static final String ARG_ESTATUS    = "estatus";
    private static final String ARG_OBS        = "observaciones";

    private static final String[] ESTATUS_VALUES = {
            "ACTIVO", "VIGENTE", "SUSPENDIDO", "FINALIZADO", "CANCELADO"
    };

    public static DetalleContratoDialog newInstance(AsignacionProyectoContratistaDto c) {
        DetalleContratoDialog d = new DetalleContratoDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, c.getIdAsignacionPc() != null ? c.getIdAsignacionPc() : -1);
        args.putString(ARG_PROYECTO,  c.getProyecto()    != null ? c.getProyecto().getNombreProyecto()    : "—");
        args.putString(ARG_CONTRATIS, c.getContratista() != null ? c.getContratista().getNombreContratista() : "—");
        args.putString(ARG_NUMERO,    c.getNumeroContrato());
        if (c.getPersonalAsignado() != null) args.putInt(ARG_PERSONAL, c.getPersonalAsignado());
        args.putString(ARG_FECHA_INI, c.getFechaInicio());
        args.putString(ARG_FECHA_FIN, c.getFechaFinEstimada());
        args.putString(ARG_ESTATUS,   c.getEstatusContrato());
        args.putString(ARG_OBS,       c.getObservaciones());
        d.setArguments(args);
        return d;
    }

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
        return inflater.inflate(R.layout.dialog_detalle_contrato, container, false);
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

        Bundle a = getArguments();
        if (a == null) { dismiss(); return; }

        int idContrato = a.getInt(ARG_ID, -1);
        if (idContrato == -1) { dismiss(); return; }

        TextView tvTitulo      = view.findViewById(R.id.tvDetalleContratoTitulo);
        TextView tvProyecto    = view.findViewById(R.id.tvDetProyecto);
        TextView tvContratista = view.findViewById(R.id.tvDetContratista);
        EditText edNumero      = view.findViewById(R.id.editDetNumero);
        EditText edPersonal    = view.findViewById(R.id.editDetPersonal);
        EditText edFechaIni    = view.findViewById(R.id.editDetFechaInicio);
        EditText edFechaFin    = view.findViewById(R.id.editDetFechaFin);
        EditText edObs         = view.findViewById(R.id.editDetObservaciones);
        Spinner  spEstatus     = view.findViewById(R.id.spinnerDetEstatus);

        tvTitulo.setText("Contrato #" + idContrato);
        tvProyecto.setText("Proyecto: "    + a.getString(ARG_PROYECTO));
        tvContratista.setText("Contratista: " + a.getString(ARG_CONTRATIS));
        edNumero.setText(safe(a.getString(ARG_NUMERO)));
        if (a.containsKey(ARG_PERSONAL))
            edPersonal.setText(String.valueOf(a.getInt(ARG_PERSONAL)));
        edFechaIni.setText(safe(a.getString(ARG_FECHA_INI)));
        edFechaFin.setText(safe(a.getString(ARG_FECHA_FIN)));
        edObs.setText(safe(a.getString(ARG_OBS)));

        ArrayAdapter<String> adp = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, ESTATUS_VALUES);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstatus.setAdapter(adp);

        String estActual = a.getString(ARG_ESTATUS);
        for (int i = 0; i < ESTATUS_VALUES.length; i++) {
            if (ESTATUS_VALUES[i].equals(estActual)) { spEstatus.setSelection(i); break; }
        }

        AppCompatButton btnGuardar  = view.findViewById(R.id.btnDetGuardar);
        AppCompatButton btnEliminar = view.findViewById(R.id.btnDetEliminar);
        AppCompatButton btnCerrar   = view.findViewById(R.id.btnDetCerrar);

        btnCerrar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            int personalInt;
            try {
                personalInt = Integer.parseInt(edPersonal.getText().toString().trim());
                if (personalInt < 1) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Personal asignado debe ser ≥ 1", Toast.LENGTH_SHORT).show();
                return;
            }

            AsignacionProyectoContratistaDto patch = new AsignacionProyectoContratistaDto();
            patch.setNumeroContrato(edNumero.getText().toString().trim());
            patch.setPersonalAsignado(personalInt);
            String fIni = edFechaIni.getText().toString().trim();
            String fFin = edFechaFin.getText().toString().trim();
            patch.setFechaInicio(fIni.isEmpty() ? null : fIni);
            patch.setFechaFinEstimada(fFin.isEmpty() ? null : fFin);
            patch.setEstatusContrato((String) spEstatus.getSelectedItem());
            patch.setObservaciones(edObs.getText().toString().trim());

            RetrofitClient.getApiService().updateAsignacionPc(idContrato, patch)
                    .enqueue(new Callback<AsignacionProyectoContratistaDto>() {
                        @Override
                        public void onResponse(@NonNull Call<AsignacionProyectoContratistaDto> call,
                                               @NonNull Response<AsignacionProyectoContratistaDto> r) {
                            if (!isAdded()) return;
                            if (r.isSuccessful()) {
                                Toast.makeText(getContext(), "Contrato actualizado", Toast.LENGTH_SHORT).show();
                                if (listener != null) listener.onCambio();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), "Error (" + r.code() + ")", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<AsignacionProyectoContratistaDto> call, @NonNull Throwable t) {
                            if (isAdded()) Toast.makeText(getContext(), "Sin conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnEliminar.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar contrato")
                .setMessage("¿Eliminar el contrato #" + idContrato + "? Esta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dlg, w) -> {
                    RetrofitClient.getApiService().deleteAsignacionPc(idContrato)
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                                    if (!isAdded()) return;
                                    if (r.isSuccessful()) {
                                        Toast.makeText(getContext(), "Contrato eliminado", Toast.LENGTH_SHORT).show();
                                        if (listener != null) listener.onCambio();
                                        dismiss();
                                    } else {
                                        Toast.makeText(getContext(),
                                                "No se pudo eliminar (" + r.code() + ")", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                    if (isAdded()) Toast.makeText(getContext(), "Sin conexión", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show());
    }

    private String safe(String s) { return s != null ? s : ""; }
}
