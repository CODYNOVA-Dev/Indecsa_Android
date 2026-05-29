package com.example.indecsa_v2.capitalhumano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.login.CorreoLoginActivity;
import com.example.indecsa_v2.network.RetrofitClient;

public class Panel_Inicial_CapitalHumano extends AppCompatActivity {

    public static final int TAB_CONTRATISTA   = 0;
    public static final int TAB_PROYECTO      = 1;
    public static final int TAB_TRABAJADOR    = 2;
    public static final int TAB_RELACIONAR    = 3;
    public static final int TAB_PERSONAL_OBRA = 4;
    public static final int TAB_REGISTRO_HORAS = 5;
    public static final int TAB_AVANCE_OBRA    = 6;
    public static final int TAB_REPORTES       = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_inicial_capital_humano);

        setupCardListeners();
        setupLogoutButton();
    }

    private void setupLogoutButton() {
        View btnLogout = findViewById(R.id.btnCerrarSesion);
        if (btnLogout == null) return;
        btnLogout.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_msg)
                .setPositiveButton(R.string.logout_confirm_ok, (d, w) -> cerrarSesion())
                .setNegativeButton(R.string.logout_confirm_cancel, null)
                .show());
    }

    private void cerrarSesion() {
        if (RetrofitClient.getTokenManager() != null) {
            RetrofitClient.getTokenManager().clearSession();
        }
        Intent intent = new Intent(this, CorreoLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupCardListeners() {
        View cardContratista  = findViewById(R.id.card_caphum_contratista);
        View cardProyecto     = findViewById(R.id.card_caphum_proyecto);
        View cardTrabajador   = findViewById(R.id.card_caphum_trabajador);
        View cardRelacionar   = findViewById(R.id.card_caphum_relacionar);
        View cardPersonalObra = findViewById(R.id.card_caphum_personalobra);
        View cardRegistroHoras = findViewById(R.id.card_caphum_registro_horas);
        View cardAvanceObra    = findViewById(R.id.card_caphum_avance_obra);
        View cardReportes      = findViewById(R.id.card_caphum_reportes);

        if (cardContratista != null)
            cardContratista.setOnClickListener(v -> openPanelCapitalHumano(TAB_CONTRATISTA));
        if (cardProyecto != null)
            cardProyecto.setOnClickListener(v -> openPanelCapitalHumano(TAB_PROYECTO));
        if (cardTrabajador != null)
            cardTrabajador.setOnClickListener(v -> openPanelCapitalHumano(TAB_TRABAJADOR));
        if (cardRelacionar != null)
            cardRelacionar.setOnClickListener(v -> openPanelCapitalHumano(TAB_RELACIONAR));
        if (cardPersonalObra != null)
            cardPersonalObra.setOnClickListener(v -> openPanelCapitalHumano(TAB_PERSONAL_OBRA));
        if (cardRegistroHoras != null)
            cardRegistroHoras.setOnClickListener(v -> openPanelCapitalHumano(TAB_REGISTRO_HORAS));
        if (cardAvanceObra != null)
            cardAvanceObra.setOnClickListener(v -> openPanelCapitalHumano(TAB_AVANCE_OBRA));
        if (cardReportes != null)
            cardReportes.setOnClickListener(v -> openPanelCapitalHumano(TAB_REPORTES));
    }

    private void openPanelCapitalHumano(int tabIndex) {
        Intent intent = new Intent(this, Panel_CapitalHumano.class);
        intent.putExtra(Panel_CapitalHumano.EXTRA_TAB_INDEX, tabIndex);
        startActivity(intent);
    }
}