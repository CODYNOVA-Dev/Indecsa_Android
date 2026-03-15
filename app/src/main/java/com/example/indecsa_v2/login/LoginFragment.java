package com.example.indecsa_v2.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Google Sign-In
import com.example.indecsa_v2.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginFragment extends Fragment {

    private static final int RC_GOOGLE = 100;
    private GoogleSignInClient googleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // ── Google Sign-In setup ──────────────────────────────────────────────
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // ── Botones ───────────────────────────────────────────────────────────
        view.findViewById(R.id.btnGoogle).setOnClickListener(v -> iniciarGoogle());

        view.findViewById(R.id.btnCorreo).setOnClickListener(v -> irACorreoActivity());

        return view;
    }

    // ── Google ────────────────────────────────────────────────────────────────
    private void iniciarGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // ✅ Login exitoso
                Toast.makeText(getContext(), "Google: " + account.getEmail(), Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Error Google: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ── Correo → nuevo Activity ───────────────────────────────────────────────
    private void irACorreoActivity() {
        Intent intent = new Intent(requireActivity(), CorreoLoginActivity.class);
        startActivity(intent);
    }
}