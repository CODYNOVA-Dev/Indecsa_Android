package com.example.indecsa_v2.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.indecsa_v2.R;

public class Panel_Inicial_Admin extends AppCompatActivity {

    public static final int TAB_CONTRATISTA    = 0;
    public static final int TAB_PROYECTO       = 1;
    public static final int TAB_TRABAJADOR     = 2;
    public static final int TAB_CAPITAL_HUMANO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_inicial_admin);

        // ✅ Se eliminó el ViewCompat.setOnApplyWindowInsetsListener
        // porque R.id.main no existe en este layout y causaba NullPointerException

        setupCardListeners();
    }

    private void setupCardListeners() {
        View cardContratista   = findViewById(R.id.card_admin_contratista);
        View cardProyecto      = findViewById(R.id.card_admin_proyecto);
        View cardTrabajador    = findViewById(R.id.card_admin_trabajador);
        View cardCapitalHumano = findViewById(R.id.card_admin_capitalhumano);

        if (cardContratista != null)
            cardContratista.setOnClickListener(v -> openPanelAdmin(TAB_CONTRATISTA));
        if (cardProyecto != null)
            cardProyecto.setOnClickListener(v -> openPanelAdmin(TAB_PROYECTO));
        if (cardTrabajador != null)
            cardTrabajador.setOnClickListener(v -> openPanelAdmin(TAB_TRABAJADOR));
        if (cardCapitalHumano != null)
            cardCapitalHumano.setOnClickListener(v -> openPanelAdmin(TAB_CAPITAL_HUMANO));
    }

    private void openPanelAdmin(int tabIndex) {
        Intent intent = new Intent(this, Panel_Admin.class);
        intent.putExtra(Panel_Admin.EXTRA_TAB_INDEX, tabIndex);
        startActivity(intent);
    }
}