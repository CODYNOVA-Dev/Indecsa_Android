package com.example.indecsa_v2.capitalhumano.contratista;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.Contratista;

public class DetalleContratistaReadonlyDialog extends DialogFragment {

    private static final String ARG_NOMBRE       = "nombre";
    private static final String ARG_RFC          = "rfc";
    private static final String ARG_CORREO       = "correo";
    private static final String ARG_TELEFONO     = "telefono";
    private static final String ARG_EXPERIENCIA  = "experiencia";
    private static final String ARG_CALIFICACION = "calificacion";
    private static final String ARG_DESCRIPCION  = "descripcion";
    private static final String ARG_ESTADO       = "estado";

    // ─── Factory ─────────────────────────────────────────────────────────────
    public static DetalleContratistaReadonlyDialog newInstance(Contratista c) {
        DetalleContratistaReadonlyDialog d = new DetalleContratistaReadonlyDialog();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE,       c.getNombreContratista());
        args.putString(ARG_RFC,          c.getRfcContratista());
        args.putString(ARG_CORREO,       c.getCorreoContratista());
        args.putString(ARG_TELEFONO,     c.getTelefonoContratista());
        args.putString(ARG_EXPERIENCIA,  c.getExperiencia());
        args.putInt   (ARG_CALIFICACION, c.getCalificacionContratista() != null ? c.getCalificacionContratista() : 0);
        args.putString(ARG_DESCRIPCION,  c.getDescripcionContratista());
        args.putString(ARG_ESTADO,       c.getEstadoContratista());
        d.setArguments(args);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null)
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detalle_contratista_readonly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            getDialog().setCanceledOnTouchOutside(true);
        }

        Bundle a = getArguments();
        if (a == null) return;

        // Avatar
        String nombre = a.getString(ARG_NOMBRE, "");
        TextView tvAvatar = view.findViewById(R.id.dialogTvAvatar);
        tvAvatar.setText(!nombre.isEmpty()
                ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?");

        // Nombre
        setTexto(view, R.id.dialogTvNombre, nombre);

        // Estado badge
        String estado = a.getString(ARG_ESTADO, "");
        TextView tvEstado = view.findViewById(R.id.dialogTvEstado);
        tvEstado.setText("● " + capitalizar(estado));
        tvEstado.setBackgroundResource("ACTIVO".equals(estado)
                ? R.drawable.item_disp_verde
                : R.drawable.item_disp_rojo);

        // Campos
        setTexto(view, R.id.dialogTvRfc,        a.getString(ARG_RFC));
        setTexto(view, R.id.dialogTvCorreo,      a.getString(ARG_CORREO));
        setTexto(view, R.id.dialogTvTelefono,    a.getString(ARG_TELEFONO));
        setTexto(view, R.id.dialogTvExperiencia, a.getString(ARG_EXPERIENCIA));
        setTexto(view, R.id.dialogTvDescripcion, a.getString(ARG_DESCRIPCION));

        // Rating
        RatingBar ratingBar = view.findViewById(R.id.dialogRatingBar);
        ratingBar.setRating(a.getInt(ARG_CALIFICACION, 0));

        // Botón cerrar
        view.<Button>findViewById(R.id.btnCerrar)
                .setOnClickListener(v -> dismiss());
    }

    private void setTexto(View root, int id, String texto) {
        TextView tv = root.findViewById(id);
        if (tv != null) tv.setText(texto != null ? texto : "—");
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return "—";
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}