package com.example.indecsa_v2.capitalhumano;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.capitalhumano.avanceobra.Tab_CapitalHumano_AvanceObra;
import com.example.indecsa_v2.capitalhumano.contratista.Tab_CapitalHumano_Contratista;
import com.example.indecsa_v2.capitalhumano.personalobra.Tab_CapitalHumano_PersonalObra;
import com.example.indecsa_v2.capitalhumano.proyecto.Tab_CapitalHumano_Proyecto;
import com.example.indecsa_v2.capitalhumano.registrohoras.Tab_CapitalHumano_RegistroHoras;
import com.example.indecsa_v2.capitalhumano.relacionar.Tab_CapitalHumano_Relacionar;
import com.example.indecsa_v2.capitalhumano.reportes.Tab_CapitalHumano_Reportes;
import com.example.indecsa_v2.capitalhumano.trabajador.Tab_CapitalHumano_Trabajador;
import com.google.android.material.navigationrail.NavigationRailView;

public class Panel_CapitalHumano extends AppCompatActivity {

    public static final String EXTRA_TAB_INDEX = "extra_tab_index";

    private NavigationRailView navRailCapitalHumano;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_capital_humano);

        initViews();
        setupNavigationRail();

        // Seleccionar la categoría indicada por el Intent (default: 0) por su
        // posición en el menú, conservando la semántica del antiguo EXTRA_TAB_INDEX.
        if (savedInstanceState == null) {
            int tabIndex = getIntent().getIntExtra(EXTRA_TAB_INDEX, 0);
            if (tabIndex < 0 || tabIndex >= navRailCapitalHumano.getMenu().size()) {
                tabIndex = 0;
            }
            navRailCapitalHumano.setSelectedItemId(
                    navRailCapitalHumano.getMenu().getItem(tabIndex).getItemId());
        }
    }

    private void initViews() {
        navRailCapitalHumano = findViewById(R.id.navRailCapitalHumano);
    }

    private void setupNavigationRail() {
        navRailCapitalHumano.setOnItemSelectedListener(item -> {
            showFragment(item.getItemId());
            return true;
        });
    }

    /** Muestra el fragmento de la categoría, reutilizándolo si ya fue creado. */
    private void showFragment(int itemId) {
        String tag = "caphum_tab_" + itemId;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();

        for (Fragment f : fm.getFragments()) {
            tx.hide(f);
        }

        Fragment target = fm.findFragmentByTag(tag);
        if (target == null) {
            tx.add(R.id.containerCapitalHumano, createFragment(itemId), tag);
        } else {
            tx.show(target);
        }
        tx.commit();
    }

    private Fragment createFragment(int itemId) {
        if (itemId == R.id.nav_caphum_contratista)   return new Tab_CapitalHumano_Contratista();
        if (itemId == R.id.nav_caphum_proyecto)       return new Tab_CapitalHumano_Proyecto();
        if (itemId == R.id.nav_caphum_trabajador)     return new Tab_CapitalHumano_Trabajador();
        if (itemId == R.id.nav_caphum_relacionar)     return new Tab_CapitalHumano_Relacionar();
        if (itemId == R.id.nav_caphum_personalobra)   return new Tab_CapitalHumano_PersonalObra();
        if (itemId == R.id.nav_caphum_registrohoras)  return new Tab_CapitalHumano_RegistroHoras();
        if (itemId == R.id.nav_caphum_avanceobra)     return new Tab_CapitalHumano_AvanceObra();
        if (itemId == R.id.nav_caphum_reportes)       return new Tab_CapitalHumano_Reportes();
        return new Tab_CapitalHumano_Contratista();
    }
}
