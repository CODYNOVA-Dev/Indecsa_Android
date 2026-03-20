package com.example.indecsa_v2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.indecsa_v2.admin.caphum.Tab_Admin_CapitalHumano;
import com.example.indecsa_v2.admin.contratista.Tab_Admin_Contratista;
import com.example.indecsa_v2.admin.proyecto.Tab_Admin_Proyecto;
import com.example.indecsa_v2.admin.trabajador.Tab_Admin_Trabajador;

/**
 * Adapter para el ViewPager2 del Panel_Admin.
 *
 * Tabs:
 *   0 → Tab_Admin_Contratista
 *   1 → Tab_Admin_Proyecto
 *   2 → Tab_Admin_Trabajador
 *   3 → Tab_Admin_CapitalHumano
 */
public class GestionAdminAdapter extends FragmentStateAdapter {

    public GestionAdminAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:  return new Tab_Admin_Contratista();
            case 1:  return new Tab_Admin_Proyecto();
            case 2:  return new Tab_Admin_Trabajador();
            case 3:  return new Tab_Admin_CapitalHumano();
            default: return new Tab_Admin_Contratista();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}