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
import com.example.indecsa_v2.admin.Panel_Admin;
import com.example.indecsa_v2.capitalhumano.Panel_CapitalHumano;
import com.example.indecsa_v2.models.LoginRequestDto;
import com.example.indecsa_v2.models.LoginResponseDto;
import com.example.indecsa_v2.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngresarContrasenaFragment extends Fragment {

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

        view.findViewById(R.id.btnIniciarSesion).setOnClickListener(v -> {
            String pass = etContrasena.getText() != null
                    ? etContrasena.getText().toString().trim()
                    : "";

            if (pass.isEmpty()) {
                Toast.makeText(getContext(), "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            // El correo se envía en minúsculas para coincidir con el trigger
            // trg_normalizar_correo_empleado del backend
            iniciarSesion(correo.trim().toLowerCase(), pass);
        });

        return view;
    }

    private void iniciarSesion(String correo, String pass) {
        LoginRequestDto request = new LoginRequestDto(correo, pass);

        RetrofitClient.getApiService().login(request).enqueue(new Callback<LoginResponseDto>() {

            @Override
            public void onResponse(Call<LoginResponseDto> call,
                                   Response<LoginResponseDto> response) {

                if (response.code() == 401 || !response.isSuccessful() || response.body() == null) {
                    // El backend devuelve 401 cuando las credenciales son incorrectas
                    Toast.makeText(getContext(),
                            "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginResponseDto empleado = response.body();
                String rol = empleado.getNombreRol();

                if (rol == null) {
                    Toast.makeText(getContext(),
                            "Error: el servidor no devolvió un rol", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (rol) {
                    case "ADMIN":
                        startActivity(new Intent(requireActivity(), Panel_Admin.class));
                        requireActivity().finish();
                        break;

                    case "CAPITAL_HUMANO":
                        startActivity(new Intent(requireActivity(), Panel_CapitalHumano.class));
                        requireActivity().finish();
                        break;

                    default:
                        Toast.makeText(getContext(),
                                "Rol no reconocido: " + rol, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}