package com.example.indecsa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class c_h_proyecto_ficha extends Fragment {

    // 1. Declaración de las Vistas (Elementos del XML)
    private TextView textViewTitle;
    private ImageView imageViewProject;
    private TextView textViewContractorName;
    private RatingBar ratingBarExperience;
    private TextView textViewLogo;

    // Constantes o Argumentos (Para simular la carga de datos)
    private static final String ARG_PROJECT_NAME = "projectName";
    private String projectName;

    /**
     * Método de fábrica (Factory method) para crear una nueva instancia del Fragment
     * con posibles argumentos.
     */
    public static c_h_proyecto_ficha newInstance(String projectName) {
        c_h_proyecto_ficha fragment = new c_h_proyecto_ficha();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_NAME, projectName);
        fragment.setArguments(args);
        return fragment;
    }

    // Método principal: se llama cuando se crea el Fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Recuperar argumentos (datos) si fueron pasados al Fragment
        if (getArguments() != null) {
            projectName = getArguments().getString(ARG_PROJECT_NAME);
        }
    }

    // Método clave: infla el layout XML
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el layout XML. 'false' significa que no se adjunta directamente al contenedor aún.
        View view = inflater.inflate(R.layout.fragment_c_h_proyecto_ficha, container, false);

        // 3. Inicialización de Vistas
        // Usamos 'view.findViewById' para encontrar los elementos por su ID del XML.
        textViewTitle = view.findViewById(R.id.textViewTitle);
        imageViewProject = view.findViewById(R.id.imageViewProject);
        // NOTA: Asegúrate de que el ID 'textViewContractorName' exista en tu XML.
        textViewContractorName = view.findViewById(R.id.textViewContractorName);
        ratingBarExperience = view.findViewById(R.id.ratingExperiencia);

        // 4. Carga de Datos (Lógica)
        loadProjectData();

        return view;
    }

    /**
     * Método para cargar y mostrar datos en las Vistas.
     */
    private void loadProjectData() {
        // Ejemplo de cómo actualizar el título si se pasó un argumento
        if (projectName != null) {
            textViewTitle.setText("PROYECTOS DE REMODELACION: " + projectName);
        } else {
            textViewTitle.setText("PROYECTOS DE REMODELACION");
        }

        // Ejemplo de carga de datos estáticos o simulados
        // Aquí iría la lógica para obtener datos de una base de datos o API.

        // Simular el nombre del contratista
        if (textViewContractorName != null) {
            textViewContractorName.setText("CONSTRUCTORA X S.A. DE C.V.");
        }

        // Simular el Rating
        ratingBarExperience.setRating(4.5f); // 4.5 estrellas

        // Simular la carga de imagen (de un recurso en 'drawable')
        // Asegúrate de tener una imagen llamada 'mi_foto_proyecto' en la carpeta res/drawable
        imageViewProject.setImageResource(R.drawable.usuario);
    }
}