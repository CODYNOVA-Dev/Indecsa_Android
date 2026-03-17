package com.example.indecsa_v2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.indecsa_v2.admin.Tab_Admin_Contratista;
import com.example.indecsa_v2.admin.Tab_Admin_Proyecto;
import com.example.indecsa_v2.admin.Tab_Admin_Trabajador;

public class GestionAdminAdapter extends FragmentStateAdapter {

    public GestionAdminAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Tab_Admin_Proyecto();
            case 1:
                return new Tab_Admin_Contratista();
            case 2:
                return new Tab_Admin_Trabajador();
            default:
                return new Tab_Admin_Proyecto();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}