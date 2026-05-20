package com.example.indecsa_v2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.indecsa_v2.admin.avanceobra.Tab_Admin_AvanceObra;
import com.example.indecsa_v2.admin.caphum.Tab_Admin_CapHum;
import com.example.indecsa_v2.admin.contratista.Tab_Admin_Contratista;
import com.example.indecsa_v2.admin.contrato.Tab_Admin_Contratos;
import com.example.indecsa_v2.admin.personalobra.Tab_Admin_PersonalObra;
import com.example.indecsa_v2.admin.proyecto.Tab_Admin_Proyecto;
import com.example.indecsa_v2.admin.registrohoras.Tab_Admin_RegistroHoras;
import com.example.indecsa_v2.admin.reportes.Tab_Admin_Reportes;
import com.example.indecsa_v2.admin.trabajador.Tab_Admin_Trabajador;

/**
 * Adapter para el ViewPager2 del Panel_Admin.
 *
 * Tabs:
 *   0 → Tab_Admin_Contratista
 *   1 → Tab_Admin_Proyecto
 *   2 → Tab_Admin_Contratos    (asignación proyecto-contratista)
 *   3 → Tab_Admin_Trabajador
 *   4 → Tab_Admin_CapitalHumano
 *   5 → Tab_Admin_PersonalObra
 *   6 → Tab_Admin_RegistroHoras
 *   7 → Tab_Admin_AvanceObra
 *   8 → Tab_Admin_Reportes
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
            case 2:  return new Tab_Admin_Contratos();
            case 3:  return new Tab_Admin_Trabajador();
            case 4:  return new Tab_Admin_CapHum();
            case 5:  return new Tab_Admin_PersonalObra();
            case 6:  return new Tab_Admin_RegistroHoras();
            case 7:  return new Tab_Admin_AvanceObra();
            case 8:  return new Tab_Admin_Reportes();
            default: return new Tab_Admin_Contratista();
        }
    }

    @Override
    public int getItemCount() {
        return 9;
    }
}
