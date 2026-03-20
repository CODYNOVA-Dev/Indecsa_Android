package com.example.indecsa_v2.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.indecsa_v2.R;

public class Panel_Inicial_Admin extends AppCompatActivity {

    // Constantes que mapean cada card con su tab en Panel_Admin
    public static final int TAB_CONTRATISTA    = 0;
    public static final int TAB_PROYECTO       = 1;
    public static final int TAB_TRABAJADOR     = 2;
    public static final int TAB_CAPITAL_HUMANO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_inicial_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCardListeners();
    }

    private void setupCardListeners() {
        // Busca el root view de cada card incluido con <include>
        View cardContratista   = findViewById(R.id.card_admin_contratista);
        View cardProyecto      = findViewById(R.id.card_admin_proyecto);
        View cardTrabajador    = findViewById(R.id.card_admin_trabajador);
        View cardCapitalHumano = findViewById(R.id.card_admin_capitalhumano);

        cardContratista.setOnClickListener(v -> openPanelAdmin(TAB_CONTRATISTA));
        cardProyecto.setOnClickListener(v -> openPanelAdmin(TAB_PROYECTO));
        cardTrabajador.setOnClickListener(v -> openPanelAdmin(TAB_TRABAJADOR));
        cardCapitalHumano.setOnClickListener(v -> openPanelAdmin(TAB_CAPITAL_HUMANO));
    }

    private void openPanelAdmin(int tabIndex) {
        Intent intent = new Intent(this, Panel_Admin.class);
        intent.putExtra(Panel_Admin.EXTRA_TAB_INDEX, tabIndex);
        startActivity(intent);
    }
}