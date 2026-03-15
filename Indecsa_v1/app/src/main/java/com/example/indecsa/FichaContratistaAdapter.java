package com.example.indecsa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.indecsa.models.Contratista;

import java.util.List;

public class FichaContratistaAdapter extends BaseAdapter {

    private Context context;
    private List<Contratista> contratistas;

    public FichaContratistaAdapter(Context context, List<Contratista> contratistas) {
        this.context = context;
        this.contratistas = contratistas;
    }

    @Override
    public int getCount() {
        return contratistas.size();
    }

    @Override
    public Object getItem(int position) {
        return contratistas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return contratistas.get(position).getIdContratista();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_contratista, parent, false);
        }

        Contratista c = contratistas.get(position);

        ImageView img = convertView.findViewById(R.id.imgContratista);
        TextView nombre = convertView.findViewById(R.id.txtNombreContratista);
        TextView descripcion = convertView.findViewById(R.id.txtDescripcionContratista);
        EditText editText = convertView.findViewById(R.id.editext);
        Button btnEditar = convertView.findViewById(R.id.btnEditar);

        // Mostrar datos
        nombre.setText(c.getNombreContratista());
        descripcion.setText(c.getDescripcionContratista());
        editText.setText("");

        // Mostrar estrellas
        ImageView[] stars = new ImageView[]{
                convertView.findViewById(R.id.star1),
                convertView.findViewById(R.id.star2),
                convertView.findViewById(R.id.star3),
                convertView.findViewById(R.id.star4),
                convertView.findViewById(R.id.star5)
        };

        int cal = c.getCalificacion() != null ? c.getCalificacion() : 0;

        for (int i = 0; i < stars.length; i++) {
            if (i < cal) {
                stars[i].setImageResource(R.drawable.ic_star);
            } else {
                stars[i].setImageResource(R.drawable.estrellavacia);
            }
        }

        // BOTÓN EDITAR (AQUÍ SÍ NAVEGA)
        btnEditar.setOnClickListener(v -> {

            EditarContratista editarFragment =
                    EditarContratista.newInstance(c.getIdContratista());

            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedorfragmentos, editarFragment)
                    .addToBackStack(null)
                    .commit();
        });


        return convertView;
    }
}
