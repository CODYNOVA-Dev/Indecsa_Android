package com.example.indecsa_v2.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.adapters.GestionAdminAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Panel_Admin extends AppCompatActivity {

    private TabLayout tabLayoutAdmin;
    private ViewPager2 viewPagerAdmin;
    private GestionAdminAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_admin);

        // Inicializar vistas
        initViews();

        // Configurar ViewPager2 con el adapter
        setupViewPager();

        // Conectar TabLayout con ViewPager2
        setupTabLayout();
    }

    private void initViews() {
        tabLayoutAdmin = findViewById(R.id.tabLayoutAdmin);
        viewPagerAdmin = findViewById(R.id.viewPagerAdmin);
    }

    private void setupViewPager() {
        adapter = new GestionAdminAdapter(this);
        viewPagerAdmin.setAdapter(adapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayoutAdmin, viewPagerAdmin,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Contratista");
                            break;
                        case 1:
                            tab.setText("Proyecto");
                            break;
                        case 2:
                            tab.setText("Trabajadores");
                            break;
                    }
                }
        ).attach();
    }
}