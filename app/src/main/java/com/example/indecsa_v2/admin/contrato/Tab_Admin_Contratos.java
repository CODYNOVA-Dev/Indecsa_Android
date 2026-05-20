package com.example.indecsa_v2.admin.contrato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indecsa_v2.R;
import com.example.indecsa_v2.models.AsignacionProyectoContratistaDto;
import com.example.indecsa_v2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Lista los AsignacionProyectoContratista (contratos marco entre un proyecto y
 * un contratista). Permite crear nuevos y abrir el detalle para editar/finalizar.
 */
public class Tab_Admin_Contratos extends Fragment {

    private RecyclerView    recycler;
    private TextView        textVacio;
    private EditText        editBuscar;
    private AppCompatButton btnBuscar;
    private AppCompatButton btnAgregar;

    private final List<AsignacionProyectoContratistaDto> lista         = new ArrayList<>();
    private final List<AsignacionProyectoContratistaDto> listaFiltrada = new ArrayList<>();
    private ContratoAdapter adapter;

    public Tab_Admin_Contratos() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab__admin__contratos, container, false);

        recycler   = v.findViewById(R.id.recyclerContratos);
        textVacio  = v.findViewById(R.id.textVacioContratos);
        editBuscar = v.findViewById(R.id.editBuscarContrato);
        btnBuscar  = v.findViewById(R.id.btnBuscarContrato);
        btnAgregar = v.findViewById(R.id.btnAgregarContrato);

        adapter = new ContratoAdapter(listaFiltrada);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        btnBuscar.setOnClickListener(view -> filtrar(editBuscar.getText().toString()));
        btnAgregar.setOnClickListener(view -> {
            AgregarContratoDialog d = new AgregarContratoDialog();
            d.setOnAgregadoListener(this::cargar);
            d.show(getParentFragmentManager(), "agregar_contrato");
        });

        cargar();
        return v;
    }

    private void cargar() {
        textVacio.setVisibility(View.GONE);

        RetrofitClient.getApiService()
                .getAllAsignacionesProyectoContratista(null, null)
                .enqueue(new Callback<List<AsignacionProyectoContratistaDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<AsignacionProyectoContratistaDto>> call,
                                           @NonNull Response<List<AsignacionProyectoContratistaDto>> response) {
                        if (getContext() == null) return;
                        if (response.isSuccessful() && response.body() != null) {
                            lista.clear();
                            lista.addAll(response.body());
                            listaFiltrada.clear();
                            listaFiltrada.addAll(lista);
                            adapter.notifyDataSetChanged();
                            mostrarOcultarVacio();
                        } else {
                            textVacio.setText("Error al cargar (" + response.code() + ")");
                            textVacio.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<AsignacionProyectoContratistaDto>> call,
                                          @NonNull Throwable t) {
                        if (getContext() == null) return;
                        textVacio.setText("Sin conexión: " + t.getMessage());
                        textVacio.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                    }
                });
    }

    private void filtrar(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(lista);
        } else {
            String q = texto.toLowerCase().trim();
            for (AsignacionProyectoContratistaDto a : lista) {
                String proyecto    = a.getProyecto()    != null ? a.getProyecto().getNombreProyecto()    : null;
                String contratista = a.getContratista() != null ? a.getContratista().getNombreContratista() : null;
                String numero      = a.getNumeroContrato();
                boolean match =
                        (proyecto    != null && proyecto.toLowerCase().contains(q)) ||
                        (contratista != null && contratista.toLowerCase().contains(q)) ||
                        (numero      != null && numero.toLowerCase().contains(q));
                if (match) listaFiltrada.add(a);
            }
        }
        adapter.notifyDataSetChanged();
        mostrarOcultarVacio();
    }

    private void mostrarOcultarVacio() {
        boolean vacio = listaFiltrada.isEmpty();
        recycler.setVisibility(vacio ? View.GONE : View.VISIBLE);
        textVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        if (vacio && !lista.isEmpty()) textVacio.setText("Sin resultados");
        else if (vacio) textVacio.setText("Sin contratos registrados");
    }

    private void abrirDetalle(AsignacionProyectoContratistaDto contrato) {
        DetalleContratoDialog d = DetalleContratoDialog.newInstance(contrato);
        d.setOnCambioListener(this::cargar);
        d.show(getParentFragmentManager(), "detalle_contrato");
    }

    // ─── ADAPTER ─────────────────────────────────────────────────────────────

    private class ContratoAdapter extends RecyclerView.Adapter<ContratoAdapter.VH> {

        private final List<AsignacionProyectoContratistaDto> items;

        ContratoAdapter(List<AsignacionProyectoContratistaDto> items) { this.items = items; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_admin_contrato, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(items.get(pos)); }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvTitulo, tvProyecto, tvContratista, tvFechas, tvPersonal, tvNumero, badgeEstatus;
            CardView card;

            VH(View v) {
                super(v);
                tvTitulo      = v.findViewById(R.id.tvContratoTitulo);
                tvProyecto    = v.findViewById(R.id.tvContratoProyecto);
                tvContratista = v.findViewById(R.id.tvContratoContratista);
                tvFechas      = v.findViewById(R.id.tvContratoFechas);
                tvPersonal    = v.findViewById(R.id.tvContratoPersonal);
                tvNumero      = v.findViewById(R.id.tvContratoNumero);
                badgeEstatus  = v.findViewById(R.id.badgeEstatusContrato);
                card          = v.findViewById(R.id.cardContrato);
            }

            void bind(AsignacionProyectoContratistaDto c) {
                tvTitulo.setText("Contrato #" + (c.getIdAsignacionPc() != null ? c.getIdAsignacionPc() : "?"));

                String proyecto    = c.getProyecto()    != null ? c.getProyecto().getNombreProyecto()    : "—";
                String contratista = c.getContratista() != null ? c.getContratista().getNombreContratista() : "—";
                tvProyecto.setText("Proyecto: "    + (proyecto    != null ? proyecto    : "—"));
                tvContratista.setText("Contratista: " + (contratista != null ? contratista : "—"));

                String ini = c.getFechaInicio()      != null ? c.getFechaInicio()      : "—";
                String fin = c.getFechaFinEstimada() != null ? c.getFechaFinEstimada() : "—";
                tvFechas.setText("Vigencia: " + ini + " → " + fin);

                tvPersonal.setText("Personal: " + (c.getPersonalAsignado() != null ? c.getPersonalAsignado() : 0));
                tvNumero.setText(c.getNumeroContrato() != null && !c.getNumeroContrato().isEmpty()
                        ? "Núm: " + c.getNumeroContrato() : "Sin número");

                String est = c.getEstatusContrato();
                badgeEstatus.setText(est != null ? est : "—");
                if ("ACTIVO".equals(est) || "VIGENTE".equals(est)) {
                    badgeEstatus.setBackgroundResource(R.drawable.item_disp_verde);
                } else if ("FINALIZADO".equals(est) || "CANCELADO".equals(est)) {
                    badgeEstatus.setBackgroundResource(R.drawable.item_disp_rojo);
                } else {
                    badgeEstatus.setBackgroundResource(R.drawable.item_disp_amarillo);
                }

                card.setOnClickListener(v -> abrirDetalle(c));
            }
        }
    }
}
