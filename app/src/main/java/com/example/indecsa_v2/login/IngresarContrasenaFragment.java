package com.example.indecsa_v2.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.admin.Panel_Inicial_Admin;
import com.example.indecsa_v2.capitalhumano.Panel_Inicial_CapitalHumano;
import com.example.indecsa_v2.models.LoginRequestDto;
import com.example.indecsa_v2.models.LoginResponseDto;
import com.example.indecsa_v2.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngresarContrasenaFragment extends Fragment {

    private View btnIniciarSesion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ingresar_contrasena, container, false);

        String correo = getArguments() != null
                ? getArguments().getString("correo", "")
                : "";

        TextView tvCorreoMostrado = view.findViewById(R.id.tvCorreoMostrado);
        tvCorreoMostrado.setText(correo);

        TextInputEditText etContrasena = view.findViewById(R.id.etContrasena);
        btnIniciarSesion = view.findViewById(R.id.btnIniciarSesion);

        btnIniciarSesion.setOnClickListener(v -> {
            String pass = etContrasena.getText() != null
                    ? etContrasena.getText().toString().trim()
                    : "";

            if (pass.isEmpty()) {
                Toast.makeText(getContext(), "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            iniciarSesion(correo.trim().toLowerCase(), pass);
        });

        return view;
    }

    private void mostrarError(String msg) {
        if (!isAdded()) return;
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        if (btnIniciarSesion != null) btnIniciarSesion.setEnabled(true);
    }

    private void iniciarSesion(String correo, String pass) {
        LoginRequestDto request = new LoginRequestDto(correo, pass);
        btnIniciarSesion.setEnabled(false);

        RetrofitClient.getApiService().login(request).enqueue(new Callback<LoginResponseDto>() {

            @Override
            public void onResponse(@NonNull Call<LoginResponseDto> call,
                                   @NonNull Response<LoginResponseDto> response) {
                if (!isAdded()) return;

                if (response.code() == 401) {
                    mostrarError("Correo o contraseña incorrectos");
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    mostrarError("Error en el servidor (" + response.code() + ")");
                    return;
                }

                LoginResponseDto empleado = response.body();
                String rol = empleado.getNombreRol();

                if ("CAPITAL_HUMANO".equals(rol)) {
                    startActivity(new Intent(requireActivity(), Panel_Inicial_CapitalHumano.class));
                    requireActivity().finish();
                } else if ("ADMIN".equals(rol)) {
                    startActivity(new Intent(requireActivity(), Panel_Inicial_Admin.class));
                    requireActivity().finish();
                } else {
                    mostrarError("Rol desconocido: " + rol);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDto> call, @NonNull Throwable t) {
                mostrarError("No hay conexión con el servidor");
            }
        });
    }
}
