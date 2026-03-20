package com.example.indecsa_v2.capitalhumano;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.adapters.GestionCapitalHumanoAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Panel_CapitalHumano extends AppCompatActivity {

    public static final String EXTRA_TAB_INDEX = "extra_tab_index";

    private TabLayout tabLayoutCapitalHumano;
    private ViewPager2 viewPagerCapitalHumano;
    private GestionCapitalHumanoAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_capital_humano);

        initViews();
        setupViewPager();
        setupTabLayout();

        // Navegar al tab indicado por el Intent (default: 0)
        int tabIndex = getIntent().getIntExtra(EXTRA_TAB_INDEX, 0);
        viewPagerCapitalHumano.setCurrentItem(tabIndex, false);
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
                        case 0: tab.setText("Contratista"); break;
                        case 1: tab.setText("Proyecto");    break;
                        case 2: tab.setText("Trabajadores"); break;
                        case 3: tab.setText("Relacionar");  break;
                    }
                }
        ).attach();
    }
}