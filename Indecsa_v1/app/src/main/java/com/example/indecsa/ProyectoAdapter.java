package com.example.indecsa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ProyectoAdapter extends BaseAdapter {

    public interface OnItemClickListener {
        void onVerDetallesClick(Proyecto proyecto);
        void onEditarClick(Proyecto proyecto);
        void onEliminarClick(Proyecto proyecto);
    }

    private Context context;
    private List<Proyecto> proyectos;
    private final OnItemClickListener listener;

    public ProyectoAdapter(Context context, List<Proyecto> proyectos, OnItemClickListener listener) {
        this.context = context;
        this.proyectos = proyectos != null ? proyectos : new ArrayList<>();
        this.listener = listener;
    }

    public void actualizarLista(List<Proyecto> nuevaLista) {
        this.proyectos = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Proyecto> getProyectos() {
        return proyectos;
    }

    @Override
    public int getCount() {
        return proyectos.size();
    }

    @Override
    public Object getItem(int position) {
        return proyectos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_proyecto, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Proyecto proyecto = proyectos.get(position);
        holder.bind(proyecto, listener);

        return convertView;
    }

    public static class ViewHolder {
        private ImageView imgProyecto;
        private TextView txtNombreProyecto, txtDescripcionProyecto, txtContratistaProyecto, txtEspecialidadProyecto;
        private Button btnVerDetalles, btnEditar, btnEliminar;

        public ViewHolder(View itemView) {
            imgProyecto = itemView.findViewById(R.id.imgProyecto);
            txtNombreProyecto = itemView.findViewById(R.id.txtNombreProyecto);
            txtDescripcionProyecto = itemView.findViewById(R.id.txtDescripcionProyecto);
            txtContratistaProyecto = itemView.findViewById(R.id.txtContratistaProyecto);
            txtEspecialidadProyecto = itemView.findViewById(R.id.txtEspecialidadProyecto);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public void bind(Proyecto proyecto, OnItemClickListener listener) {
            // Configurar imagen
            imgProyecto.setImageResource(proyecto.getImagenResId());

            // Configurar textos
            txtNombreProyecto.setText(proyecto.getProyectito());
            txtDescripcionProyecto.setText(proyecto.getDescripcion());
            txtContratistaProyecto.setText("Contratista: " + proyecto.getContratista());
            txtEspecialidadProyecto.setText("Especialidad: " + proyecto.getEspecialidad() + " | Estado: " + proyecto.getEstado());

            // Configurar botones
            btnVerDetalles.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerDetallesClick(proyecto);
                }
            });

            btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditarClick(proyecto);
                }
            });

            btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEliminarClick(proyecto);
                }
            });
        }
    }
}