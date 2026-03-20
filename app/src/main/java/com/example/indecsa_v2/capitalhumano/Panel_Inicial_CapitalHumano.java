package com.example.indecsa_v2.capitalhumano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.indecsa_v2.R;

public class Panel_Inicial_CapitalHumano extends AppCompatActivity {

    public static final int TAB_CONTRATISTA = 0;
    public static final int TAB_PROYECTO    = 1;
    public static final int TAB_TRABAJADOR  = 2;
    public static final int TAB_RELACIONAR  = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_inicial_capital_humano);

        // ✅ Se eliminó el ViewCompat.setOnApplyWindowInsetsListener
        // porque R.id.main no existe en este layout y causaba NullPointerException

        setupCardListeners();
    }

    private void setupCardListeners() {
        View cardContratista = findViewById(R.id.card_caphum_contratista);
        View cardProyecto    = findViewById(R.id.card_caphum_proyecto);
        View cardTrabajador  = findViewById(R.id.card_caphum_trabajador);
        View cardRelacionar  = findViewById(R.id.card_caphum_relacionar);

        if (cardContratista != null)
            cardContratista.setOnClickListener(v -> openPanelCapitalHumano(TAB_CONTRATISTA));
        if (cardProyecto != null)
            cardProyecto.setOnClickListener(v -> openPanelCapitalHumano(TAB_PROYECTO));
        if (cardTrabajador != null)
            cardTrabajador.setOnClickListener(v -> openPanelCapitalHumano(TAB_TRABAJADOR));
        if (cardRelacionar != null)
            cardRelacionar.setOnClickListener(v -> openPanelCapitalHumano(TAB_RELACIONAR));
    }

    private void openPanelCapitalHumano(int tabIndex) {
        Intent intent = new Intent(this, Panel_CapitalHumano.class);
        intent.putExtra(Panel_CapitalHumano.EXTRA_TAB_INDEX, tabIndex);
        startActivity(intent);
    }
}