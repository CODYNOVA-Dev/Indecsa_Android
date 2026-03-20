package com.example.indecsa_v2.capitalhumano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCardListeners();
    }

    private void setupCardListeners() {
        View cardContratista = findViewById(R.id.card_caphum_contratista);
        View cardProyecto    = findViewById(R.id.card_caphum_proyecto);
        View cardTrabajador  = findViewById(R.id.card_caphum_trabajador);
        View cardRelacionar  = findViewById(R.id.card_caphum_relacionar);

        cardContratista.setOnClickListener(v -> openPanelCapitalHumano(TAB_CONTRATISTA));
        cardProyecto.setOnClickListener(v -> openPanelCapitalHumano(TAB_PROYECTO));
        cardTrabajador.setOnClickListener(v -> openPanelCapitalHumano(TAB_TRABAJADOR));
        cardRelacionar.setOnClickListener(v -> openPanelCapitalHumano(TAB_RELACIONAR));
    }

    private void openPanelCapitalHumano(int tabIndex) {
        Intent intent = new Intent(this, Panel_CapitalHumano.class);
        intent.putExtra(Panel_CapitalHumano.EXTRA_TAB_INDEX, tabIndex);
        startActivity(intent);
    }
}