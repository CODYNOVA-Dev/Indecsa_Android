package com.example.indecsa_v2.network;

import android.content.Context;

import com.example.indecsa_v2.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // BASE_URL ahora se inyecta desde BuildConfig:
    //   debug   → http://10.0.2.2:8080/api/        (emulador → backend local)
    //   release → https://springbootindecsa-production-e42a.up.railway.app/api/
    // Cambiar el valor en app/build.gradle.kts, no aquí.
    private static final String BASE_URL = BuildConfig.BASE_URL;

    // Host de producción contra el que se aplica certificate pinning.
    // El pinner SOLO valida hosts declarados; el debug (10.0.2.2/localhost)
    // pasa sin verificación porque no aparece en la lista.
    private static final String PROD_HOST = "springbootindecsa-production-e42a.up.railway.app";

    // Pins SHA-256 del SubjectPublicKeyInfo capturados el 2026-05-26:
    //   leaf         CN=*.up.railway.app           expira 2026-08-02
    //   intermediate CN=E7 (Let's Encrypt ECDSA)   expira 2027-03-12
    //
    // Railway usa wildcard cert para *.up.railway.app — el mismo SPKI cubre
    // todos los subdominios. Por eso este pin sirve aunque se cambie el
    // subdomain del backend.
    //
    // Para regenerar tras una rotación de Let's Encrypt:
    //   openssl s_client -connect springbootindecsa-production-e42a.up.railway.app:443 \
    //       -servername springbootindecsa-production-e42a.up.railway.app -showcerts </dev/null \
    //     | awk '/-----BEGIN/{c++; out="c"c".pem"} {if(out) print > out} /-----END/{out=""}'
    //   for f in c1.pem c2.pem; do
    //     openssl x509 -in $f -pubkey -noout \
    //       | openssl pkey -pubin -outform der \
    //       | openssl dgst -sha256 -binary \
    //       | openssl enc -base64
    //   done
    //
    // Incluimos AMBOS para sobrevivir a la rotación del leaf (cada ~60 días).
    // Si Let's Encrypt rota también el intermediate, la app deja de conectar
    // hasta que se actualicen estos valores y se publique nueva build.
    private static final String PIN_LEAF         = "sha256/sGDbTDZa6e6YT2TE9XG0KNYPBuV/4YoqFrebzjQs1Ss=";
    private static final String PIN_INTERMEDIATE = "sha256/y7xVm0TVJNahMr2sZydE2jQH8SquXV9yLF9seROHHHU=";

    private static Retrofit retrofit;
    private static ApiService apiService;
    private static TokenManager tokenManager;

    public static void init(Context context) {
        tokenManager = new TokenManager(context);

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        // Redact: el JWT no debe aparecer en logcat (pair programming, demos,
        // bug reports con `adb logcat` capturado). Cookie por simetría.
        logger.redactHeader("Authorization");
        logger.redactHeader("Cookie");
        // HEADERS (no BODY) en debug → vemos método/URL/headers pero NO el body,
        // así no se loguea la contraseña en el POST de login.
        // Si necesitás ver bodies para debuggear un endpoint puntual, subilo
        // temporalmente a BODY *en tu máquina* y no commitees el cambio.
        logger.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.HEADERS
                : HttpLoggingInterceptor.Level.NONE);

        CertificatePinner pinner = new CertificatePinner.Builder()
                .add(PROD_HOST, PIN_LEAF, PIN_INTERMEDIATE)
                .build();

        // Orden importante:
        //  1. AuthInterceptor: adjunta Bearer y maneja 401 ANTES de reintentar
        //     (no queremos reintentar pidiendo otra vez con el mismo token muerto).
        //  2. RetryInterceptor: reintenta GETs en 5xx con backoff exponencial.
        //  3. logger: al final para que loguee cada intento por separado.
        OkHttpClient client = new OkHttpClient.Builder()
                .certificatePinner(pinner)
                .addInterceptor(new AuthInterceptor(tokenManager))
                .addInterceptor(new RetryInterceptor())
                .addInterceptor(logger)
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static TokenManager getTokenManager() {
        return tokenManager;
    }

    public static ApiService getApiService() {
        return apiService;
    }
}
