package com.example.indecsa_v2.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * Helper común para descarga y apertura de PDFs generados por el backend.
 *
 * Reemplaza tres copias casi idénticas que vivían en Tab_Admin_Reportes,
 * DetalleProyectoDialog (admin) y DetalleProyectoReadonlyDialog (capital
 * humano). Centraliza:
 *   - Streaming a archivo en {@code /Documents}
 *   - Limpieza del archivo parcial si la copia falla
 *   - Apertura del visor PDF del sistema vía FileProvider
 *
 * <h3>Uso</h3>
 * <pre>
 *   PdfHelper.guardarYAbrir(
 *       requireContext().getApplicationContext(),
 *       responseBody,
 *       "reporte_avance_proyecto_7.pdf",
 *       new PdfHelper.Callback() {
 *           &#64;Override public void onSuccess(File file) {
 *               if (!isAdded()) return;
 *               setLoading(false, "PDF guardado ✓");
 *               PdfHelper.abrir(requireContext(), file);
 *           }
 *           &#64;Override public void onError(String msg) {
 *               if (!isAdded()) return;
 *               setLoading(false, msg);
 *           }
 *       });
 * </pre>
 *
 * <h3>Importante</h3>
 * El {@code Context} para {@code guardarYAbrir} tiene que ser
 * {@code applicationContext} — el thread vive más que el Fragment/Activity
 * que lo lanzó. Para {@code abrir} sí usar el context del Fragment porque
 * inicia un Intent y necesita estar attached para que el chooser aparezca.
 */
public final class PdfHelper {

    private static final int BUFFER_SIZE = 4096;

    private PdfHelper() {}

    /**
     * Callback en el main thread tras la descarga.
     */
    public interface Callback {
        void onSuccess(@NonNull File file);
        void onError(@NonNull String msg);
    }

    /**
     * Descarga el body de Retrofit a {@code /Documents/<nombreArchivo>} en
     * un thread aparte. Si falla a media escritura, borra el archivo parcial
     * antes de llamar a {@link Callback#onError}.
     */
    public static void guardarYAbrir(@NonNull Context appContext,
                                     @NonNull ResponseBody body,
                                     @NonNull String nombreArchivo,
                                     @NonNull Callback cb) {
        new Thread(() -> {
            File file = null;
            try {
                File dir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                if (dir == null) throw new IOException("Almacenamiento externo no disponible");
                if (!dir.exists()) dir.mkdirs();
                file = new File(dir, nombreArchivo);

                try (InputStream is = body.byteStream();
                     FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buf = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = is.read(buf)) != -1) fos.write(buf, 0, len);
                }

                final File finalFile = file;
                new Handler(Looper.getMainLooper()).post(() -> cb.onSuccess(finalFile));
            } catch (IOException e) {
                // Archivo a medio escribir → borrar para no dejar basura ni
                // que el usuario lo abra creyendo que es válido.
                if (file != null && file.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                }
                new Handler(Looper.getMainLooper()).post(() ->
                        cb.onError("No se pudo guardar el PDF"));
            }
        }).start();
    }

    /**
     * Abre el PDF con el visor del sistema vía FileProvider. Si no hay
     * visor instalado muestra un Toast informativo (el archivo igual quedó
     * en disco accesible desde la app de archivos).
     */
    public static void abrir(@NonNull Context ctx, @NonNull File file) {
        try {
            Uri uri = FileProvider.getUriForFile(ctx,
                    ctx.getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(Intent.createChooser(intent, "Abrir PDF con…"));
        } catch (Exception e) {
            Toast.makeText(ctx,
                    "PDF guardado. Instala un visor de PDF para abrirlo.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
