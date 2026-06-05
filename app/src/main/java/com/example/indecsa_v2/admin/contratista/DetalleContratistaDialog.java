package com.example.indecsa_v2.admin.contratista;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.Estado;
import com.example.indecsa_v2.network.RetrofitClient;
import com.example.indecsa_v2.util.ApiErrorMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dialog flotante para ver, editar y eliminar un Contratista.
 *
 * Uso desde Tab_Admin_Contratista:
 *   DetalleContratistaDialog dialog = DetalleContratistaDialog.newInstance(contratista);
 *   dialog.setOnCambioListener(() -> cargarContratistas());
 *   dialog.show(getParentFragmentManager(), "detalle_contratista");
 */
public class DetalleContratistaDialog extends DialogFragment {

    // ─── Args ────────────────────────────────────────────────────────────────
    private static final String ARG_ID           = "id";
    private static final String ARG_NOMBRE       = "nombre";
    private static final String ARG_RFC          = "rfc";
    private static final String ARG_CORREO       = "correo";
    private static final String ARG_TELEFONO     = "telefono";
    private static final String ARG_ESTADO       = "estado";
    private static final String ARG_CALIFICACION = "calificacion";
    private static final String ARG_DESCRIPCION  = "descripcion";
    private static final String ARG_EXPERIENCIA  = "experiencia";
    private static final String ARG_UBICACION    = "ubicacion";

    // ─── Callback ────────────────────────────────────────────────────────────
    public interface OnCambioListener { void onCambio(); }
    private OnCambioListener onCambioListener;
    public void setOnCambioListener(OnCambioListener l) { this.onCambioListener = l; }

    // ─── Estado de edición ───────────────────────────────────────────────────
    private final List<Estado> listaEstados = new ArrayList<>();
    private Contratista contratistaCompleto; // recargado del backend para merge

    // ─── Factory ─────────────────────────────────────────────────────────────
    public static DetalleContratistaDialog newInstance(Contratista c) {
        DetalleContratistaDialog d = new DetalleContratistaDialog();
        Bundle args = new Bundle();
        args.putInt   (ARG_ID,           c.getIdContratista()          != null ? c.getIdContratista()          : -1);
        args.putString(ARG_NOMBRE,       c.getNombreContratista());
        args.putString(ARG_RFC,          c.getRfcContratista());
        args.putString(ARG_CORREO,       c.getCorreoContratista());
        args.putString(ARG_TELEFONO,     c.getTelefonoContratista());
        args.putString(ARG_ESTADO,       c.getEstadoContratista());
        args.putInt   (ARG_CALIFICACION, c.getCalificacionContratista() != null ? c.getCalificacionContratista() : 0);
        args.putString(ARG_DESCRIPCION,  c.getDescripcionContratista());
        args.putString(ARG_EXPERIENCIA,  c.getExperiencia());
        args.putString(ARG_UBICACION,    c.getUbicacionContratista());
        d.setArguments(args);
        return d;
    }

    // ─── onCreateDialog ──────────────────────────────────────────────────────
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    // ─── onCreateView ────────────────────────────────────────────────────────
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detalle_contratista, container, false);
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

        mostrarDetalle(view);
    }

    // ─── MODO DETALLE ────────────────────────────────────────────────────────

    private void mostrarDetalle(View view) {
        view.findViewById(R.id.panelDetalle).setVisibility(View.VISIBLE);
        view.findViewById(R.id.panelEditar).setVisibility(View.GONE);

        Bundle a = getArguments();
        if (a == null) return;

        // Cabecera
        String nombre = a.getString(ARG_NOMBRE, "");
        TextView tvAvatar = view.findViewById(R.id.dialogTvAvatar);
        tvAvatar.setText(!nombre.isEmpty() ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?");
        setTexto(view, R.id.dialogTvNombre, nombre);

        String estado = a.getString(ARG_ESTADO, "");
        TextView tvEstado = view.findViewById(R.id.dialogTvEstado);
        tvEstado.setText("● " + capitalizar(estado));
        tvEstado.setBackgroundResource("ACTIVO".equals(estado)
                ? R.drawable.item_disp_verde : R.drawable.item_disp_rojo);

        // Campos info
        setTexto(view, R.id.dialogTvRfc,         a.getString(ARG_RFC));
        setTexto(view, R.id.dialogTvCorreo,       a.getString(ARG_CORREO));
        setTexto(view, R.id.dialogTvTelefono,     a.getString(ARG_TELEFONO));
        setTexto(view, R.id.dialogTvExperiencia,  a.getString(ARG_EXPERIENCIA));
        setTexto(view, R.id.dialogTvDescripcion,  a.getString(ARG_DESCRIPCION));

        RatingBar ratingBar = view.findViewById(R.id.dialogRatingBar);
        ratingBar.setRating(a.getInt(ARG_CALIFICACION, 0));

        // Botones
        view.<AppCompatButton>findViewById(R.id.btnEditar)
                .setOnClickListener(v -> mostrarEditar(view));
        view.<AppCompatButton>findViewById(R.id.btnEliminar)
                .setOnClickListener(v -> mostrarConfirmEliminar());
        AppCompatButton btnEstado = view.findViewById(R.id.btnEstado);
        if (btnEstado != null) btnEstado.setOnClickListener(v -> mostrarCambiarEstado(view));
    }

    // ─── CAMBIAR ESTADO (suspender / activar) ────────────────────────────────

    private static final String[] ESTADOS_CONTRATISTA = {"ACTIVO", "INACTIVO", "SUSPENDIDO"};

    private void mostrarCambiarEstado(View view) {
        Bundle a = getArguments();
        if (a == null) return;
        final int id = a.getInt(ARG_ID, -1);
        if (id <= 0) {
            Toast.makeText(getContext(), "ID de contratista inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        String actual = a.getString(ARG_ESTADO, "");
        int seleccion = 0;
        for (int i = 0; i < ESTADOS_CONTRATISTA.length; i++) {
            if (ESTADOS_CONTRATISTA[i].equals(actual)) { seleccion = i; break; }
        }
        final int[] elegido = { seleccion };

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cambiar estado del contratista")
                .setSingleChoiceItems(ESTADOS_CONTRATISTA, seleccion, (d, which) -> elegido[0] = which)
                .setPositiveButton("Aplicar", (d, w) ->
                        aplicarEstado(view, id, ESTADOS_CONTRATISTA[elegido[0]]))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void aplicarEstado(View view, int id, String nuevoEstado) {
        Map<String, Object> body = new HashMap<>();
        body.put("estado", nuevoEstado);
        RetrofitClient.getApiService().patchContratistaEstado(id, body)
                .enqueue(new Callback<Contratista>() {
                    @Override
                    public void onResponse(Call<Contratista> call, Response<Contratista> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            Bundle a = getArguments();
                            if (a != null) a.putString(ARG_ESTADO, nuevoEstado);
                            TextView tvEstado = view.findViewById(R.id.dialogTvEstado);
                            if (tvEstado != null) {
                                tvEstado.setText("● " + capitalizar(nuevoEstado));
                                tvEstado.setBackgroundResource("ACTIVO".equals(nuevoEstado)
                                        ? R.drawable.item_disp_verde : R.drawable.item_disp_rojo);
                            }
                            Toast.makeText(getContext(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                        } else {
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forCode(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Contratista> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), ApiErrorMessages.forThrowable(t), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── MODO EDITAR ─────────────────────────────────────────────────────────

    private void mostrarEditar(View view) {
        view.findViewById(R.id.panelDetalle).setVisibility(View.GONE);
        view.findViewById(R.id.panelEditar).setVisibility(View.VISIBLE);

        Bundle a = getArguments();
        if (a == null) return;

        // Relleno rápido con lo que ya tenemos (mejora percepción de velocidad)
        setEditText(view, R.id.editNombre,      a.getString(ARG_NOMBRE));
        setEditText(view, R.id.editRfc,         a.getString(ARG_RFC));
        setEditText(view, R.id.editCorreo,      a.getString(ARG_CORREO));
        setEditText(view, R.id.editTelefono,    a.getString(ARG_TELEFONO));
        setEditText(view, R.id.editExperiencia, a.getString(ARG_EXPERIENCIA));
        setEditText(view, R.id.editDescripcion, a.getString(ARG_DESCRIPCION));
        RatingBar rb = view.findViewById(R.id.editRating);
        if (rb != null) rb.setRating(a.getInt(ARG_CALIFICACION, 0));

        cargarEstadosYContratista(view, a.getInt(ARG_ID, -1));

        view.<AppCompatButton>findViewById(R.id.btnCancelarEdicion)
                .setOnClickListener(v -> mostrarDetalle(view));

        view.<AppCompatButton>findViewById(R.id.btnGuardar)
                .setOnClickListener(v -> guardarContratista(view, a.getInt(ARG_ID, -1)));
    }

    /** Carga el catálogo de estados y el contratista completo (para merge). */
    private void cargarEstadosYContratista(View view, int id) {
        RetrofitClient.getApiService().getAllEstados().enqueue(new Callback<List<Estado>>() {
            @Override
            public void onResponse(Call<List<Estado>> call, Response<List<Estado>> response) {
                if (!isAdded()) return;
                listaEstados.clear();
                if (response.isSuccessful() && response.body() != null) listaEstados.addAll(response.body());
                poblarSpinnerEstados(view);
                if (id > 0) cargarContratistaCompleto(view, id);
            }
            @Override public void onFailure(Call<List<Estado>> call, Throwable t) {
                if (!isAdded()) return;
                poblarSpinnerEstados(view);
                if (id > 0) cargarContratistaCompleto(view, id);
            }
        });
    }

    private void cargarContratistaCompleto(View view, int id) {
        RetrofitClient.getApiService().getContratistaById(id).enqueue(new Callback<Contratista>() {
            @Override
            public void onResponse(Call<Contratista> call, Response<Contratista> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    contratistaCompleto = response.body();
                    setEditText(view, R.id.editNombre,      contratistaCompleto.getNombreContratista());
                    setEditText(view, R.id.editRfc,         contratistaCompleto.getRfcContratista());
                    setEditText(view, R.id.editCurp,        contratistaCompleto.getCurp());
                    setEditText(view, R.id.editCorreo,      contratistaCompleto.getCorreoContratista());
                    setEditText(view, R.id.editTelefono,    contratistaCompleto.getTelefonoContratista());
                    setEditText(view, R.id.editExperiencia, contratistaCompleto.getExperiencia());
                    setEditText(view, R.id.editDescripcion, contratistaCompleto.getDescripcionContratista());
                    setEditText(view, R.id.editFoto,        contratistaCompleto.getFotoPerfilUrl());
                    RatingBar rb = view.findViewById(R.id.editRating);
                    if (rb != null && contratistaCompleto.getCalificacionContratista() != null)
                        rb.setRating(contratistaCompleto.getCalificacionContratista());
                    seleccionarEstado(view, contratistaCompleto.getIdEstadoOperacion());
                }
            }
            @Override public void onFailure(Call<Contratista> call, Throwable t) { }
        });
    }

    private void poblarSpinnerEstados(View view) {
        Spinner sp = view.findViewById(R.id.spinnerEstadoOperacion);
        if (sp == null) return;
        List<String> nombres = new ArrayList<>();
        nombres.add("— Selecciona estado —");
        for (Estado e : listaEstados) nombres.add(e.getNombreEst() != null ? e.getNombreEst() : ("Estado " + e.getIdEstado()));
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(ad);
    }

    private void seleccionarEstado(View view, Integer idEstado) {
        if (idEstado == null) return;
        Spinner sp = view.findViewById(R.id.spinnerEstadoOperacion);
        if (sp == null) return;
        for (int i = 0; i < listaEstados.size(); i++) {
            if (idEstado.equals(listaEstados.get(i).getIdEstado())) { sp.setSelection(i + 1); return; }
        }
    }

    private Integer estadoSeleccionado(View view) {
        Spinner sp = view.findViewById(R.id.spinnerEstadoOperacion);
        if (sp == null) return null;
        int pos = sp.getSelectedItemPosition();
        if (pos <= 0 || pos - 1 >= listaEstados.size()) return null;
        return listaEstados.get(pos - 1).getIdEstado();
    }

    private void guardarContratista(View view, int id) {
        // Merge sobre el objeto completo recargado para no perder campos NOT NULL
        Contratista c = contratistaCompleto != null ? contratistaCompleto : new Contratista();
        c.setNombreContratista     (getEditText(view, R.id.editNombre));
        c.setRfcContratista        (getEditText(view, R.id.editRfc));
        String curp = getEditText(view, R.id.editCurp);
        if (!curp.isEmpty()) c.setCurp(curp.toUpperCase());
        c.setCorreoContratista     (getEditText(view, R.id.editCorreo));
        c.setTelefonoContratista   (getEditText(view, R.id.editTelefono));
        c.setExperiencia           (getEditText(view, R.id.editExperiencia));
        c.setDescripcionContratista(getEditText(view, R.id.editDescripcion));
        String foto = getEditText(view, R.id.editFoto);
        c.setFotoPerfilUrl(foto.isEmpty() ? null : foto);
        RatingBar rb = view.findViewById(R.id.editRating);
        if (rb != null) c.setCalificacionContratista((int) rb.getRating());
        Integer idEstado = estadoSeleccionado(view);
        if (idEstado != null) c.setIdEstadoOperacion(idEstado);
        // Preservar estado (ACTIVO/INACTIVO/SUSPENDIDO) si no vino en el objeto recargado
        if (c.getEstadoContratista() == null) {
            Bundle a = getArguments();
            if (a != null) c.setEstadoContratista(a.getString(ARG_ESTADO));
        }

        AppCompatButton btnGuardar = view.findViewById(R.id.btnGuardar);
        if (btnGuardar != null) btnGuardar.setEnabled(false);

        RetrofitClient.getApiService().updateContratista(id, c)
                .enqueue(new Callback<Contratista>() {
                    @Override
                    public void onResponse(Call<Contratista> call, Response<Contratista> response) {
                        if (!isAdded()) return;
                        if (btnGuardar != null) btnGuardar.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Contratista actualizado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forCode(response.code()),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Contratista> call, Throwable t) {
                        if (!isAdded()) return;
                        if (btnGuardar != null) btnGuardar.setEnabled(true);
                        Toast.makeText(getContext(),
                                ApiErrorMessages.forThrowable(t),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── CONFIRMAR ELIMINAR ──────────────────────────────────────────────────

    private void mostrarConfirmEliminar() {
        Bundle a = getArguments();
        if (a == null) return;

        ConfirmEliminarDialog confirm = ConfirmEliminarDialog.newInstance(
                "¿Eliminar contratista?",
                "Esta acción no se puede deshacer.\n\"" + a.getString(ARG_NOMBRE) + "\" será eliminado permanentemente."
        );
        confirm.setOnConfirmListener(() -> eliminarContratista(a.getInt(ARG_ID, -1)));
        confirm.show(getParentFragmentManager(), "confirm_eliminar_contratista");
    }

    private void eliminarContratista(int id) {
        RetrofitClient.getApiService().deleteContratista(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Contratista eliminado", Toast.LENGTH_SHORT).show();
                            if (onCambioListener != null) onCambioListener.onCambio();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    ApiErrorMessages.forCode(response.code()),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                ApiErrorMessages.forThrowable(t),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── Utilidades ──────────────────────────────────────────────────────────

    private void setTexto(View root, int id, String texto) {
        TextView tv = root.findViewById(id);
        if (tv != null) tv.setText(texto != null ? texto : "—");
    }

    private void setEditText(View root, int id, String texto) {
        EditText et = root.findViewById(id);
        if (et != null) et.setText(texto != null ? texto : "");
    }

    private String getEditText(View root, int id) {
        EditText et = root.findViewById(id);
        return et != null ? et.getText().toString().trim() : "";
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return "—";
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
