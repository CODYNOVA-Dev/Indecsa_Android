package com.example.indecsa_v2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.indecsa_v2.capitalhumano.avanceobra.Tab_CapitalHumano_AvanceObra;
import com.example.indecsa_v2.capitalhumano.contratista.Tab_CapitalHumano_Contratista;
import com.example.indecsa_v2.capitalhumano.personalobra.Tab_CapitalHumano_PersonalObra;
import com.example.indecsa_v2.capitalhumano.proyecto.Tab_CapitalHumano_Proyecto;
import com.example.indecsa_v2.capitalhumano.registrohoras.Tab_CapitalHumano_RegistroHoras;
import com.example.indecsa_v2.capitalhumano.relacionar.Tab_CapitalHumano_Relacionar;
import com.example.indecsa_v2.capitalhumano.reportes.Tab_CapitalHumano_Reportes;
import com.example.indecsa_v2.capitalhumano.trabajador.Tab_CapitalHumano_Trabajador;

/**
 * Adapter para el ViewPager2 del Panel_CapitalHumano.
 *
 * Tabs (deben coincidir con setupTabLayout() en Panel_CapitalHumano):
 *   0 → Tab_CapitalHumano_Contratista
 *   1 → Tab_CapitalHumano_Proyecto
 *   2 → Tab_CapitalHumano_Trabajador
 *   3 → Tab_CapitalHumano_Relacionar
 *   4 → Tab_CapitalHumano_PersonalObra
 */
public class GestionCapitalHumanoAdapter extends FragmentStateAdapter {

    public GestionCapitalHumanoAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:  return new Tab_CapitalHumano_Contratista();
            case 1:  return new Tab_CapitalHumano_Proyecto();
            case 2:  return new Tab_CapitalHumano_Trabajador();
            case 3:  return new Tab_CapitalHumano_Relacionar();
            case 4:  return new Tab_CapitalHumano_PersonalObra();
            case 5:  return new Tab_CapitalHumano_RegistroHoras();
            case 6:  return new Tab_CapitalHumano_AvanceObra();
            case 7:  return new Tab_CapitalHumano_Reportes();
            default: return new Tab_CapitalHumano_Contratista();
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }
}