package com.example.indecsa_v2.capitalhumano;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Panel_CapitalHumano extends AppCompatActivity {

    public static final String EXTRA_TAB_INDEX = "extra_tab_index";

    /**
     * Orden estable de categorías (compatibilidad con el antiguo EXTRA_TAB_INDEX
     * usado por Panel_Inicial_CapitalHumano).
     */
    private static final int[] TAB_ORDER = new int[] {
            R.id.nav_caphum_contratista,    // 0
            R.id.nav_caphum_proyecto,        // 1
            R.id.nav_caphum_trabajador,      // 2
            R.id.nav_caphum_relacionar,      // 3
            R.id.nav_caphum_personalobra,    // 4
            R.id.nav_caphum_registrohoras,   // 5
            R.id.nav_caphum_avanceobra,      // 6
            R.id.nav_caphum_reportes         // 7
    };

    private BottomNavigationView bottomNavCapitalHumano;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_capital_humano);

        initViews();
        setupBottomNav();

        if (savedInstanceState == null) {
            int tabIndex = getIntent().getIntExtra(EXTRA_TAB_INDEX, 0);
            if (tabIndex < 0 || tabIndex >= TAB_ORDER.length) {
                tabIndex = 0;
            }
            selectCategory(TAB_ORDER[tabIndex]);
        }
    }

    private void initViews() {
        bottomNavCapitalHumano = findViewById(R.id.bottomNavCapitalHumano);
    }

    private void setupBottomNav() {
        bottomNavCapitalHumano.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_caphum_mas) {
                showMasPopup();
                return false;
            }
            showFragment(item.getItemId());
            return true;
        });
    }

    private void showMasPopup() {
        View anchor = bottomNavCapitalHumano.findViewById(R.id.nav_caphum_mas);
        if (anchor == null) anchor = bottomNavCapitalHumano;
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_caphum_mas, popup.getMenu());
        popup.setOnMenuItemClickListener(it -> {
            selectCategory(it.getItemId());
            return true;
        });
        popup.show();
    }

    private void selectCategory(int itemId) {
        if (bottomNavCapitalHumano.getMenu().findItem(itemId) != null
                && itemId != R.id.nav_caphum_mas) {
            bottomNavCapitalHumano.setSelectedItemId(itemId);
        } else {
            showFragment(itemId);
        }
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
