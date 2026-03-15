package com.example.indecsa_v2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class IngresarContrasenaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ingresar_contrasena, container, false);

        // Recibe el correo del fragment anterior
        String correo = getArguments() != null ? getArguments().getString("correo", "") : "";

        TextView tvCorreoMostrado = view.findViewById(R.id.tvCorreoMostrado);
        tvCorreoMostrado.setText(correo);

        TextInputEditText etContrasena = view.findViewById(R.id.etContrasena);

        view.findViewById(R.id.btnIniciarSesion).setOnClickListener(v -> {
            String pass = etContrasena.getText() != null ? etContrasena.getText().toString() : "";

            if (pass.isEmpty()) {
                Toast.makeText(getContext(), "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔐 Aquí irá la lógica de autenticación con correo + pass
            Toast.makeText(getContext(), "Autenticando...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}