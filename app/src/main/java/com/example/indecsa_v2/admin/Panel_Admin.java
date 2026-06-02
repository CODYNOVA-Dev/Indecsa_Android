package com.example.indecsa_v2.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.admin.avanceobra.Tab_Admin_AvanceObra;
import com.example.indecsa_v2.admin.caphum.Tab_Admin_CapHum;
import com.example.indecsa_v2.admin.contratista.Tab_Admin_Contratista;
import com.example.indecsa_v2.admin.contrato.Tab_Admin_Contratos;
import com.example.indecsa_v2.admin.personalobra.Tab_Admin_PersonalObra;
import com.example.indecsa_v2.admin.proyecto.Tab_Admin_Proyecto;
import com.example.indecsa_v2.admin.registrohoras.Tab_Admin_RegistroHoras;
import com.example.indecsa_v2.admin.reportes.Tab_Admin_Reportes;
import com.example.indecsa_v2.admin.trabajador.Tab_Admin_Trabajador;
import com.google.android.material.navigationrail.NavigationRailView;

public class Panel_Admin extends AppCompatActivity {

    public static final String EXTRA_TAB_INDEX = "extra_tab_index";

    private NavigationRailView navRailAdmin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_admin);

        initViews();
        setupNavigationRail();

        // Seleccionar la categoría indicada por el Intent (default: 0) por su
        // posición en el menú, conservando la semántica del antiguo EXTRA_TAB_INDEX.
        if (savedInstanceState == null) {
            int tabIndex = getIntent().getIntExtra(EXTRA_TAB_INDEX, 0);
            if (tabIndex < 0 || tabIndex >= navRailAdmin.getMenu().size()) {
                tabIndex = 0;
            }
            navRailAdmin.setSelectedItemId(navRailAdmin.getMenu().getItem(tabIndex).getItemId());
        }
    }

    private void initViews() {
        navRailAdmin = findViewById(R.id.navRailAdmin);
    }

    private void setupNavigationRail() {
        navRailAdmin.setOnItemSelectedListener(item -> {
            showFragment(item.getItemId());
            return true;
        });
    }

    /** Muestra el fragmento de la categoría, reutilizándolo si ya fue creado. */
    private void showFragment(int itemId) {
        String tag = "admin_tab_" + itemId;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();

        for (Fragment f : fm.getFragments()) {
            tx.hide(f);
        }

        Fragment target = fm.findFragmentByTag(tag);
        if (target == null) {
            tx.add(R.id.containerAdmin, createFragment(itemId), tag);
        } else {
            tx.show(target);
        }
        tx.commit();
    }

    private Fragment createFragment(int itemId) {
        if (itemId == R.id.nav_admin_contratista)   return new Tab_Admin_Contratista();
        if (itemId == R.id.nav_admin_proyecto)       return new Tab_Admin_Proyecto();
        if (itemId == R.id.nav_admin_contratos)      return new Tab_Admin_Contratos();
        if (itemId == R.id.nav_admin_trabajador)     return new Tab_Admin_Trabajador();
        if (itemId == R.id.nav_admin_caphum)         return new Tab_Admin_CapHum();
        if (itemId == R.id.nav_admin_personalobra)   return new Tab_Admin_PersonalObra();
        if (itemId == R.id.nav_admin_registrohoras)  return new Tab_Admin_RegistroHoras();
        if (itemId == R.id.nav_admin_avanceobra)     return new Tab_Admin_AvanceObra();
        if (itemId == R.id.nav_admin_reportes)       return new Tab_Admin_Reportes();
        return new Tab_Admin_Contratista();
    }
}
