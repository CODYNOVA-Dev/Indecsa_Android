package com.example.indecsa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.indecsa.models.LoginRequestAdmin;
import com.example.indecsa.models.LoginRequestCapHum;
import com.example.indecsa.models.LoginResponse;
import com.example.indecsa.network.ApiService;
import com.example.indecsa.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio_sesion extends Fragment {

    private EditText etCorreo, etPassword;

    public Inicio_sesion() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inicio_sesion, container, false);

        etCorreo = view.findViewById(R.id.etCorreo);
        etPassword = view.findViewById(R.id.etPassword);

        view.findViewById(R.id.btnInicio_sesion)
                .setOnClickListener(v -> intentarLogin());

        view.findViewById(R.id.btnCrear_cuenta)
                .setOnClickListener(v -> {
                    Registrarse r = new Registrarse();
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contenedorfragmentos, r)
                            .addToBackStack(null)
                            .commit();
                });

        return view;
    }

    private void intentarLogin() {

        String correo = etCorreo.getText().toString().trim();
        String contra = etPassword.getText().toString().trim();

        if (correo.isEmpty() || contra.isEmpty()) {
            Toast.makeText(requireContext(), "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // ----- PRIMERO INTENTAMOS LOGIN ADMIN -----
        LoginRequestAdmin adminRequest = new LoginRequestAdmin(correo, contra);

        api.loginAdmin(adminRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                LoginResponse res = response.body();

                if (response.isSuccessful()
                        && res != null
                        && res.isSuccess()
                        && res.getAdmin() != null) {

                    Toast.makeText(requireContext(),
                            "Bienvenido administrador: " + res.getAdmin().getCorreoAdmin(),
                            Toast.LENGTH_SHORT).show();

                    irAlMenuAdministrador();
                    return;
                }

                // Si no es admin → intentamos capital humano
                intentarLoginCapitalHumano(correo, contra);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN_ERROR", "Error ADMIN: ", t);
                intentarLoginCapitalHumano(correo, contra);
            }
        });
    }

    private void intentarLoginCapitalHumano(String correo, String contra) {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        LoginRequestCapHum capHumRequest = new LoginRequestCapHum(correo, contra);

        api.loginCapHum(capHumRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                LoginResponse res = response.body();

                if (response.isSuccessful()
                        && res != null
                        && res.isSuccess()
                        && res.getCapitalHumano() != null) {

                    Toast.makeText(requireContext(),
                            "Bienvenido Capital Humano: " + res.getCapitalHumano().getCorreoCapHum(),
                            Toast.LENGTH_SHORT).show();

                    irAlMenuCapitalHumano();
                    return;
                }

                Toast.makeText(requireContext(),
                        "Correo o contraseña incorrectos",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN_ERROR", "Error CAPHUM: ", t);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---- IR AL MISMO FRAGMENTO PARA AMBOS ROLES ----
    private void irAlMenuAdministrador() {
        Administrador menu = new Administrador();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorfragmentos, menu)
                .commit();
    }

    private void irAlMenuCapitalHumano() {
        CapitalHumano menu = new CapitalHumano();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorfragmentos, menu)
                .commit();
    }
}
