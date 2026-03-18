package com.example.indecsa_v2.admin.contratista;

// IMPORTANTE: Este archivo vive en el paquete contratista pero es reutilizado
// por proyecto y trabajador. Alternativamente muévelo a un paquete común como:
// com.example.indecsa_v2.admin.common

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indecsa_v2.R;

/**
 * Dialog de confirmación personalizado para eliminar registros.
 * Reutilizable desde cualquier tab de administrador.
 *
 * Uso:
 *   ConfirmEliminarDialog confirm = ConfirmEliminarDialog.newInstance(
 *       "¿Eliminar trabajador?",
 *       "Esta acción no se puede deshacer."
 *   );
 *   confirm.setOnConfirmListener(() -> eliminarTrabajador(id));
 *   confirm.show(getParentFragmentManager(), "confirm_eliminar");
 */
public class ConfirmEliminarDialog extends DialogFragment {

    private static final String ARG_TITULO  = "titulo";
    private static final String ARG_MENSAJE = "mensaje";

    public interface OnConfirmListener { void onConfirm(); }
    private OnConfirmListener onConfirmListener;
    public void setOnConfirmListener(OnConfirmListener l) { this.onConfirmListener = l; }

    public static ConfirmEliminarDialog newInstance(String titulo, String mensaje) {
        ConfirmEliminarDialog d = new ConfirmEliminarDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITULO,  titulo);
        args.putString(ARG_MENSAJE, mensaje);
        d.setArguments(args);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_eliminar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85f);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            getDialog().setCanceledOnTouchOutside(false);
        }

        Bundle a = getArguments();
        if (a == null) return;

        TextView tvTitulo  = view.findViewById(R.id.confirmTvTitulo);
        TextView tvMensaje = view.findViewById(R.id.confirmTvMensaje);
        tvTitulo.setText(a.getString(ARG_TITULO,  "¿Confirmar eliminación?"));
        tvMensaje.setText(a.getString(ARG_MENSAJE, "Esta acción no se puede deshacer."));

        // Botón cancelar — solo cierra este dialog, el detalle sigue abierto
        view.<AppCompatButton>findViewById(R.id.btnConfirmCancelar)
                .setOnClickListener(v -> dismiss());

        // Botón eliminar — ejecuta el callback y cierra
        view.<AppCompatButton>findViewById(R.id.btnConfirmEliminar)
                .setOnClickListener(v -> {
                    if (onConfirmListener != null) onConfirmListener.onConfirm();
                    dismiss();
                });
    }
}
