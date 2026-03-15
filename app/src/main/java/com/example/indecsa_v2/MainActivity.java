package com.example.indecsa_v2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ProgressBar barraProgreso;
    private TextView textoCarga;
    private CardView cardViewLogo;

    private Handler handler = new Handler(Looper.getMainLooper());
    private int progresoActual = 0;

    private final String[] mensajesCarga = {
            "Cargando",
            "Iniciando módulos",
            "Verificando datos",
            "Preparando interfaz",
            "Listo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        barraProgreso = findViewById(R.id.barraprogreso);
        textoCarga   = findViewById(R.id.carga);
        cardViewLogo = findViewById(R.id.cardViewLogo); // ← asegúrate de agregar este id al XML

        iniciarAnimacionRespiracion();
        iniciarAnimacionCarga();
    }

    // ─── Efecto respiración en el CardView ───────────────────────────────────
    private void iniciarAnimacionRespiracion() {
        // Escala en X
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(cardViewLogo, "scaleX", 1f, 1.08f, 1f);
        // Escala en Y
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(cardViewLogo, "scaleY", 1f, 1.08f, 1f);
        // Elevación (sombra que "sube" y "baja")
        ObjectAnimator elevation = ObjectAnimator.ofFloat(cardViewLogo, "cardElevation", 16f, 32f, 16f);

        AnimatorSet breathSet = new AnimatorSet();
        breathSet.playTogether(scaleX, scaleY, elevation);
        breathSet.setDuration(2000);                                  // 2 s por ciclo
        breathSet.setInterpolator(new AccelerateDecelerateInterpolator());

        // Repetir indefinidamente con un pequeño pausa entre ciclos
        Runnable breathLoop = new Runnable() {
            @Override
            public void run() {
                breathSet.start();
                handler.postDelayed(this, 2400); // 2 s animación + 0,4 s pausa
            }
        };
        handler.post(breathLoop);
    }

    // ─── Barra de progreso + textos ──────────────────────────────────────────
    private void iniciarAnimacionCarga() {
        int[] intervalos = {400, 800, 600, 700, 500};
        int[] saltos     = {20,   40,  60,  80, 100};

        for (int i = 0; i < saltos.length; i++) {
            final int progreso = saltos[i];
            final String mensaje = mensajesCarga[i];
            final boolean esElUltimo = (i == saltos.length - 1); // Detectamos si es el final

            long delay = 0;
            for (int j = 0; j <= i; j++) delay += intervalos[j];

            handler.postDelayed(() -> {
                textoCarga.setText(mensaje);

                // Si es el último salto, pasamos la orden de cambiar de activity
                if (esElUltimo) {
                    animarProgreso(progreso, () -> {
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finish();
                    });
                } else {
                    animarProgreso(progreso, null);
                }
            }, delay);
        }
    }

    // Modificamos ligeramente tu método original:
    private void animarProgreso(int destino, Runnable alTerminar) {
        ObjectAnimator anim = ObjectAnimator.ofInt(barraProgreso, "progress", progresoActual, destino);
        anim.setDuration(400);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());

        if (alTerminar != null) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    alTerminar.run(); // Aquí ejecuta el cambio de Activity
                }
            });
        }

        anim.start();
        progresoActual = destino;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // evitar leaks
    }
}