package com.example.indecsa_v2.contratista;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.adapters.GestionCapitalHumanoAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Panel_CapitalHumano extends AppCompatActivity {

    private TabLayout tabLayoutCapitalHumano;
    private ViewPager2 viewPagerCapitalHumano;
    private GestionCapitalHumanoAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_capital_humano);

        // Inicializar vistas
        initViews();

        // Configurar ViewPager2 con el adapter
        setupViewPager();

        // Conectar TabLayout con ViewPager2
        setupTabLayout();
    }

    private void initViews() {
        tabLayoutCapitalHumano = findViewById(R.id.tabLayoutCapitalHumano);
        viewPagerCapitalHumano = findViewById(R.id.viewPagerCapitalHumano);
    }

    private void setupViewPager() {
        adapter = new GestionCapitalHumanoAdapter(this);
        viewPagerCapitalHumano.setAdapter(adapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayoutCapitalHumano, viewPagerCapitalHumano,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Bloques");
                            break;
                        case 1:
                            tab.setText("Horarios");
                            break;
                        case 2:
                            tab.setText("Calendarios");
                            break;
                    }
                }
        ).attach();
    }
}