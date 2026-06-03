package com.example.indecsa_v2.admin;

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
import com.example.indecsa_v2.admin.avanceobra.Tab_Admin_AvanceObra;
import com.example.indecsa_v2.admin.caphum.Tab_Admin_CapHum;
import com.example.indecsa_v2.admin.contratista.Tab_Admin_Contratista;
import com.example.indecsa_v2.admin.contrato.Tab_Admin_Contratos;
import com.example.indecsa_v2.admin.personalobra.Tab_Admin_PersonalObra;
import com.example.indecsa_v2.admin.proyecto.Tab_Admin_Proyecto;
import com.example.indecsa_v2.admin.registrohoras.Tab_Admin_RegistroHoras;
import com.example.indecsa_v2.admin.reportes.Tab_Admin_Reportes;
import com.example.indecsa_v2.admin.trabajador.Tab_Admin_Trabajador;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Panel_Admin extends AppCompatActivity {

    public static final String EXTRA_TAB_INDEX = "extra_tab_index";

    /**
     * Orden estable de categorías (compatibilidad con el antiguo EXTRA_TAB_INDEX
     * usado por Panel_Inicial_Admin). El índice del Intent se traduce a este
     * arreglo de IDs, sin importar si la categoría está en la barra inferior
     * o dentro del menú "Más".
     */
    private static final int[] TAB_ORDER = new int[] {
            R.id.nav_admin_contratista,    // 0
            R.id.nav_admin_proyecto,       // 1
            R.id.nav_admin_contratos,      // 2
            R.id.nav_admin_trabajador,     // 3
            R.id.nav_admin_caphum,         // 4
            R.id.nav_admin_personalobra,   // 5
            R.id.nav_admin_registrohoras,  // 6
            R.id.nav_admin_avanceobra,     // 7
            R.id.nav_admin_reportes        // 8
    };

    private BottomNavigationView bottomNavAdmin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_admin);

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
        bottomNavAdmin = findViewById(R.id.bottomNavAdmin);
    }

    private void setupBottomNav() {
        bottomNavAdmin.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_admin_mas) {
                showMasPopup();
                // No marcamos "Más" como seleccionado: solo abre el popup.
                return false;
            }
            showFragment(item.getItemId());
            return true;
        });
    }

    /** Muestra el popup con las categorías que no caben en la barra inferior. */
    private void showMasPopup() {
        View anchor = bottomNavAdmin.findViewById(R.id.nav_admin_mas);
        if (anchor == null) anchor = bottomNavAdmin;
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_admin_mas, popup.getMenu());
        popup.setOnMenuItemClickListener(it -> {
            selectCategory(it.getItemId());
            return true;
        });
        popup.show();
    }

    /**
     * Selecciona la categoría por ID. Si está en la barra inferior, marca
     * el ítem correspondiente; si está en "Más", solo cambia el fragmento
     * dejando la barra sin selección visual de un ítem fijo.
     */
    private void selectCategory(int itemId) {
        if (bottomNavAdmin.getMenu().findItem(itemId) != null
                && itemId != R.id.nav_admin_mas) {
            bottomNavAdmin.setSelectedItemId(itemId);
        } else {
            showFragment(itemId);
        }
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
