package com.example.walletapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class gestion_categoria extends AppCompatActivity {

    public static class CategoriaItem {
        private String nombre;
        private String detalle;
        private double presupuestoTotal;
        private double gastoAcumulado;

        public CategoriaItem(String nombre, String detalle, double presupuestoTotal, double gastoAcumulado) {
            this.nombre = nombre;
            this.detalle = detalle;
            this.presupuestoTotal = presupuestoTotal;
            this.gastoAcumulado = gastoAcumulado;
        }

        public String getNombre() { return nombre; }
        public String getDetalle() { return detalle; }
        public double getPresupuestoTotal() { return presupuestoTotal; }
        public double getGastoAcumulado() { return gastoAcumulado; }

        public void sumarGasto(double monto) {
            this.gastoAcumulado += monto;
        }

        public double getDineroDisponible() {
            return presupuestoTotal - gastoAcumulado;
        }

        public int getPorcentajeDisponible() {
            if (presupuestoTotal <= 0) return 0;
            double disponible = getDineroDisponible();
            if (disponible <= 0) return 0;
            return (int) Math.min(100, Math.round((disponible / presupuestoTotal) * 100));
        }
    }

    public interface OnGastoSumadoListener {
        void onGastoSumar(CategoriaItem item, int position);
    }

    public static class CategoriaAdapterInterno extends RecyclerView.Adapter<CategoriaAdapterInterno.ViewHolder> {
        private List<CategoriaItem> lista;
        private OnGastoSumadoListener listener;

        public CategoriaAdapterInterno(List<CategoriaItem> lista, OnGastoSumadoListener listener) {
            this.lista = lista;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CategoriaItem item = lista.get(position);
            holder.tvNombre.setText(item.getNombre());
            holder.tvDetalle.setText(item.getDetalle());

            double disponible = item.getDineroDisponible();
            int porcentaje = item.getPorcentajeDisponible();

            if (disponible >= 0) {
                holder.tvDisponible.setText(String.format(Locale.US, "$%,.2f disponibles (%d%%)", disponible, porcentaje));
                try {
                    holder.tvDisponible.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_navy));
                } catch (Exception e) {
                    holder.tvDisponible.setTextColor(Color.parseColor("#050A30"));
                }
            } else {
                holder.tvDisponible.setText(String.format(Locale.US, "-$%,.2f (Excedido)", Math.abs(disponible)));
                try {
                    holder.tvDisponible.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.expense_gold));
                } catch (Exception e) {
                    holder.tvDisponible.setTextColor(Color.parseColor("#B79347"));
                }
            }

            holder.progressBar.setProgress(porcentaje);
            holder.tvGastadoInfo.setText(String.format(Locale.US, "Gastado: $%,.2f de $%,.2f", item.getGastoAcumulado(), item.getPresupuestoTotal()));

            holder.btnAnadirGasto.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onGastoSumar(item, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lista != null ? lista.size() : 0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvDetalle, tvDisponible, tvGastadoInfo;
            ProgressBar progressBar;
            MaterialButton btnAnadirGasto;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tv_nombre_categoria);
                tvDetalle = itemView.findViewById(R.id.tv_tipo_categoria);
                tvDisponible = itemView.findViewById(R.id.tv_presupuesto_categoria);
                tvGastadoInfo = itemView.findViewById(R.id.tv_gastado_info);
                progressBar = itemView.findViewById(R.id.pb_categoria_progreso);
                btnAnadirGasto = itemView.findViewById(R.id.btn_anadir_gasto);
            }
        }
    }

    private RecyclerView rvCategorias;
    private CategoriaAdapterInterno adapter;
    private List<CategoriaItem> listaCategorias;

    private TextView tvTotalGastado;
    private TextView tvPorcentajeGlobal;
    private TextView tvPresupuestoTotal;
    private TextView tvDisponibleGlobal;
    private TextView tvCantidadCategorias;
    private ProgressBar pbProgresoGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion__categoria);

        Toolbar toolbar = findViewById(R.id.toolbar_categorias);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvTotalGastado = findViewById(R.id.tv_total_gastado_categorias);
        tvPorcentajeGlobal = findViewById(R.id.tv_porcentaje_global_categorias);
        tvPresupuestoTotal = findViewById(R.id.tv_presupuesto_total_categorias);
        tvDisponibleGlobal = findViewById(R.id.tv_disponible_global_categorias);
        tvCantidadCategorias = findViewById(R.id.tv_cantidad_categorias);
        pbProgresoGlobal = findViewById(R.id.pb_progreso_global_categorias);

        rvCategorias = findViewById(R.id.rv_categorias);
        rvCategorias.setLayoutManager(new LinearLayoutManager(this));

        listaCategorias = new ArrayList<>();
        listaCategorias.add(new CategoriaItem("Alimentación", "Supermercado, Almuerzos", 200.00, 145.50));
        listaCategorias.add(new CategoriaItem("Transporte", "Gasolina, Uber, Pasajes", 80.00, 42.00));
        listaCategorias.add(new CategoriaItem("Servicios", "Luz, Agua, Recibo de Internet", 100.00, 68.75));
        listaCategorias.add(new CategoriaItem("Ocio y Salidas", "Cine, Juegos, Salidas con amigos", 70.00, 35.00));

        adapter = new CategoriaAdapterInterno(listaCategorias, this::mostrarDialogoSumarGasto);
        rvCategorias.setAdapter(adapter);

        actualizarMetricasCabecera();

        FloatingActionButton fab = findViewById(R.id.fab_agregar_categoria);
        if (fab != null) {
            fab.setOnClickListener(v -> mostrarDialogoNuevaCategoria());
        }
    }

    private void actualizarMetricasCabecera() {
        double gastoTotal = 0.0;
        double presupuestoGlobal = 0.0;

        for (CategoriaItem item : listaCategorias) {
            gastoTotal += item.getGastoAcumulado();
            presupuestoGlobal += item.getPresupuestoTotal();
        }

        double disponibleGlobal = presupuestoGlobal - gastoTotal;
        int porcentajeConsumido = 0;
        if (presupuestoGlobal > 0) {
            porcentajeConsumido = (int) Math.round((gastoTotal / presupuestoGlobal) * 100);
        }

        if (tvTotalGastado != null) {
            tvTotalGastado.setText(String.format(Locale.US, "-$%,.2f", gastoTotal));
        }

        if (tvPresupuestoTotal != null) {
            tvPresupuestoTotal.setText(String.format(Locale.US, "Límite: $%,.2f", presupuestoGlobal));
        }

        if (tvDisponibleGlobal != null) {
            tvDisponibleGlobal.setText(String.format(Locale.US, "Disponible: $%,.2f", Math.max(0, disponibleGlobal)));
        }

        if (tvPorcentajeGlobal != null) {
            tvPorcentajeGlobal.setText(porcentajeConsumido + "% consumido");
        }

        if (pbProgresoGlobal != null) {
            pbProgresoGlobal.setProgress(Math.min(100, porcentajeConsumido));
        }

        if (tvCantidadCategorias != null) {
            tvCantidadCategorias.setText(listaCategorias.size() + " activas");
        }
    }

    private EditText crearCampoEstilizado(String hint, int inputType) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setHintTextColor(Color.parseColor("#8B8383"));
        editText.setTextColor(Color.parseColor("#1C1E21"));
        editText.setTextSize(14);
        editText.setInputType(inputType);

        int paddingH = (int) (14 * getResources().getDisplayMetrics().density);
        int paddingV = (int) (12 * getResources().getDisplayMetrics().density);
        editText.setPadding(paddingH, paddingV, paddingH, paddingV);

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor("#F4F6FC"));
        shape.setCornerRadius(12 * getResources().getDisplayMetrics().density);
        shape.setStroke((int) (1.2 * getResources().getDisplayMetrics().density), Color.parseColor("#CBD5E1"));
        editText.setBackground(shape);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.bottomMargin = (int) (12 * getResources().getDisplayMetrics().density);
        editText.setLayoutParams(lp);

        return editText;
    }

    private void mostrarDialogoSumarGasto(CategoriaItem item, int position) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (14 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (8 * getResources().getDisplayMetrics().density)
        );

        EditText etMonto = crearCampoEstilizado("Monto a sumar ($)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(etMonto);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Sumar Gasto a " + item.getNombre())
                .setMessage("Ingresa el dinero que acabas de gastar:")
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sumar", (d, which) -> {
                    String strMonto = etMonto.getText().toString().trim();
                    if (!TextUtils.isEmpty(strMonto)) {
                        try {
                            double extra = Double.parseDouble(strMonto);
                            item.sumarGasto(extra);
                            adapter.notifyItemChanged(position);
                            actualizarMetricasCabecera();
                            Toast.makeText(this, "Se sumaron $" + extra + " a " + item.getNombre(), Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create();

        dialog.show();
        configurarBotonesDialogo(dialog);
    }

    private void mostrarDialogoNuevaCategoria() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (14 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (8 * getResources().getDisplayMetrics().density)
        );

        EditText etNombre = crearCampoEstilizado("Nombre (ej. Salud, Ropa)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText etDetalle = crearCampoEstilizado("Detalle (ej. Farmacia, Consultas)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText etPresupuesto = crearCampoEstilizado("Presupuesto Asignado ($)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        layout.addView(etNombre);
        layout.addView(etDetalle);
        layout.addView(etPresupuesto);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Nueva Categoría")
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Crear", (d, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String detalle = etDetalle.getText().toString().trim();
                    String presupuestoStr = etPresupuesto.getText().toString().trim();

                    if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(presupuestoStr)) {
                        Toast.makeText(this, "Debes ingresar nombre y presupuesto", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double presupuesto = Double.parseDouble(presupuestoStr);
                        if (TextUtils.isEmpty(detalle)) detalle = "Gastos generales";

                        listaCategorias.add(0, new CategoriaItem(nombre, detalle, presupuesto, 0.0));
                        adapter.notifyItemInserted(0);
                        rvCategorias.scrollToPosition(0);
                        actualizarMetricasCabecera();

                        SweetAlertDialog exito = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        exito.setTitleText("¡Categoría Creada!");
                        exito.setContentText("Categoría lista con $" + presupuesto + " asignados.");
                        exito.setConfirmText("Listo");
                        exito.show();
                        corregirBotonSweetAlert(exito);

                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Valor numérico inválido", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();

        dialog.show();
        configurarBotonesDialogo(dialog);
    }

    private void configurarBotonesDialogo(AlertDialog dialog) {
        Button btnPositivo = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegativo = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (btnPositivo != null) {
            btnPositivo.setTextColor(Color.parseColor("#050A30"));
        }
        if (btnNegativo != null) {
            btnNegativo.setTextColor(Color.parseColor("#8B8383"));
        }
    }

    private void corregirBotonSweetAlert(SweetAlertDialog dialog) {
        Button btn = dialog.findViewById(com.ontbee.legacyforks.cn.pedant.SweetAlert.R.id.confirm_button);
        if (btn != null) {
            btn.setBackgroundColor(Color.parseColor("#050A30"));
            btn.setTextColor(Color.WHITE);
            btn.setMinimumHeight((int) (44 * getResources().getDisplayMetrics().density));
            btn.setPadding(40, 0, 40, 0);
            btn.setGravity(android.view.Gravity.CENTER);
        }
    }
}