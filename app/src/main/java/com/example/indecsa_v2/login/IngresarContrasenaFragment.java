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
import com.example.indecsa_v2.models.LoginRequestAdmin;
import com.example.indecsa_v2.models.LoginRequestCapHum;
import com.example.indecsa_v2.models.LoginResponse;
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

            // Intenta login como Admin primero
            LoginRequestAdmin reqAdmin = new LoginRequestAdmin(correo, pass);
            RetrofitClient.getApiService().loginAdmin(reqAdmin).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()
                            && response.body().getAdmin() != null) {

                        // ✅ Es Admin → ir a Admin
                        startActivity(new Intent(requireActivity(), Admin.class));
                        requireActivity().finish();

                    } else {
                        // No es Admin → intenta como Capital Humano
                        LoginRequestCapHum reqCapHum = new LoginRequestCapHum(correo, pass);
                        RetrofitClient.getApiService().loginCapHum(reqCapHum).enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                if (response.isSuccessful() && response.body() != null
                                        && response.body().isSuccess()
                                        && response.body().getCapitalHumano() != null) {

                                    // ✅ Es Capital Humano → ir a CapitalHumano
                                    startActivity(new Intent(requireActivity(), CapitalHumano.class));
                                    requireActivity().finish();

                                } else {
                                    Toast.makeText(getContext(),
                                            "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                Toast.makeText(getContext(),
                                        "Error de conexión", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}