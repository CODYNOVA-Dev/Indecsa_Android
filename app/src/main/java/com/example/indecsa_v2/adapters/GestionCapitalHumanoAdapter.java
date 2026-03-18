package com.example.indecsa_v2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.indecsa_v2.admin.contratista.Tab_Admin_Contratista;
import com.example.indecsa_v2.contratista.Tab_CapitalHumano_Proyecto;
import com.example.indecsa_v2.contratista.Tab_CapitalHumano_Trabajador;

public class GestionCapitalHumanoAdapter extends FragmentStateAdapter {

    public GestionCapitalHumanoAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Tab_CapitalHumano_Proyecto();
            case 1:
                return new Tab_Admin_Contratista();
            case 2:
                return new Tab_CapitalHumano_Trabajador();
            default:
                return new Tab_CapitalHumano_Proyecto();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}