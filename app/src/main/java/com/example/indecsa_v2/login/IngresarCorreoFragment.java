package com.example.indecsa_v2.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.indecsa_v2.R;
import com.google.android.material.textfield.TextInputEditText;

public class IngresarCorreoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ingresar_correo, container, false);

        TextInputEditText etCorreo = view.findViewById(R.id.etCorreo);

        view.findViewById(R.id.btnSiguiente).setOnClickListener(v -> {
            String correo = etCorreo.getText() != null ? etCorreo.getText().toString().trim() : "";

            if (correo.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(getContext(), "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Le pasa el correo al Activity para que cargue el siguiente fragment
            ((CorreoLoginActivity) requireActivity()).irAContrasena(correo);
        });

        return view;
    }
}