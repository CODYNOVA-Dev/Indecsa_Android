package com.example.indecsa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;  // Cambiar de ArrayList a List

public class FichaAdapter extends ArrayAdapter<Ficha> {

    // Cambiar ArrayList por List
    public FichaAdapter(Context context, List<Ficha> fichas) {
        super(context, 0, fichas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ficha ficha = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_fichas, parent, false);
        }

        ImageView imgContratista = convertView.findViewById(R.id.imgContratista);
        TextView txtNombre = convertView.findViewById(R.id.txtNombreContratista);
        TextView txtDescripcion = convertView.findViewById(R.id.DescripcionTrabajador);
        TextView txtProyecto = convertView.findViewById(R.id.Proyecto);
        TextView txtEquipo = convertView.findViewById(R.id.EquipoTrabajador);

        if (ficha != null) {
            txtNombre.setText(ficha.getNombreContratista());
            txtDescripcion.setText(ficha.getDescripcionContratista());
            txtProyecto.setText("Proyecto: " + ficha.getNombreProyecto());
            txtEquipo.setText("Equipo: " + ficha.getEquipoTrabajo());
        }

        return convertView;
    }
}