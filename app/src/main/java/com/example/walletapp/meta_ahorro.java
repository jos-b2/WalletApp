package com.example.walletapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class meta_ahorro extends AppCompatActivity {

    private LinearLayout llContenedorMetas;
    private TextView tvTotalAcumulado;
    private TextView tvResumenProgreso;
    private TextView tvObjetivoGlobal;
    private TextView tvRestanteGlobal;
    private TextView tvContadorMetas;
    private ProgressBar pbMetasGlobal;

    private final List<Meta> listaMetas = new ArrayList<>();

    private static class Meta {
        String titulo;
        double objetivo;
        double ahorrado;

        Meta(String titulo, double objetivo, double ahorrado) {
            this.titulo = titulo;
            this.objetivo = objetivo;
            this.ahorrado = ahorrado;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#050A30"));
        setContentView(R.layout.meta_ahorro);

        Toolbar toolbar = findViewById(R.id.toolbar_metas);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        llContenedorMetas = findViewById(R.id.ll_contenedor_metas);
        tvTotalAcumulado = findViewById(R.id.tv_total_acumulado_metas);
        tvResumenProgreso = findViewById(R.id.tv_resumen_metas_progreso);
        tvObjetivoGlobal = findViewById(R.id.tv_objetivo_global_metas);
        tvRestanteGlobal = findViewById(R.id.tv_restante_global_metas);
        tvContadorMetas = findViewById(R.id.tv_contador_metas_activas);
        pbMetasGlobal = findViewById(R.id.pb_metas_global);

        FloatingActionButton fab = findViewById(R.id.fab_agregar_meta);

        Meta metaInicial = new Meta("Nintendo Switch OLED", 350.00, 100.00);
        listaMetas.add(metaInicial);
        agregarMetaVista(metaInicial);
        recalcularMetricasGlobales();

        if (fab != null) {
            fab.setOnClickListener(v -> mostrarDialogoNuevaMeta());
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

    private void mostrarDialogoNuevaMeta() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (14 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (8 * getResources().getDisplayMetrics().density)
        );

        EditText etTitulo = crearCampoEstilizado("Nombre (ej. Nintendo Switch OLED)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText etMeta = crearCampoEstilizado("Monto objetivo ($)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText etAbono = crearCampoEstilizado("Ahorro inicial ($)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        layout.addView(etTitulo);
        layout.addView(etMeta);
        layout.addView(etAbono);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Nueva Meta de Ahorro")
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Crear", (d, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String metaStr = etMeta.getText().toString().trim();
                    String abonoStr = etAbono.getText().toString().trim();

                    if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(metaStr)) {
                        mostrarSweetWarning("Campos obligatorios", "Debes ingresar nombre y monto objetivo.");
                        return;
                    }

                    try {
                        double objetivo = Double.parseDouble(metaStr);
                        double ahorrado = TextUtils.isEmpty(abonoStr) ? 0.0 : Double.parseDouble(abonoStr);

                        if (objetivo <= 0) {
                            mostrarSweetWarning("Monto incorrecto", "El objetivo debe ser mayor a 0.");
                            return;
                        }

                        Meta nuevaMeta = new Meta(titulo, objetivo, ahorrado);
                        listaMetas.add(0, nuevaMeta);
                        agregarMetaVista(nuevaMeta);
                        recalcularMetricasGlobales();

                        SweetAlertDialog exito = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        exito.setTitleText("¡Meta Creada!");
                        exito.setContentText("Iniciaste el ahorro para: " + titulo);
                        exito.setConfirmText("Listo");
                        exito.show();
                        corregirBotonSweetAlert(exito);

                    } catch (NumberFormatException e) {
                        mostrarSweetWarning("Formato Inválido", "Ingresa valores numéricos válidos.");
                    }
                })
                .create();

        dialog.show();
        configurarBotonesDialogo(dialog);
    }

    private void agregarMetaVista(Meta meta) {
        if (llContenedorMetas == null) return;

        View item = LayoutInflater.from(this).inflate(R.layout.item_meta_ahorro, llContenedorMetas, false);

        TextView tvNombre = item.findViewById(R.id.tv_nombre_meta);
        TextView tvPorcentaje = item.findViewById(R.id.tv_porcentaje_meta);
        ProgressBar progressBar = item.findViewById(R.id.pb_meta);
        TextView tvMontos = item.findViewById(R.id.tv_montos_meta);
        Button btnAbonar = item.findViewById(R.id.btn_abonar_meta);

        tvNombre.setText(meta.titulo);
        actualizarDatosItem(meta, tvPorcentaje, progressBar, tvMontos);

        btnAbonar.setOnClickListener(v -> mostrarDialogoAbonar(meta, tvPorcentaje, progressBar, tvMontos));

        llContenedorMetas.addView(item, 0);
    }

    private void mostrarDialogoAbonar(Meta meta, TextView tvPorcentaje, ProgressBar progressBar, TextView tvMontos) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (14 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (8 * getResources().getDisplayMetrics().density)
        );

        EditText etMontoAbonar = crearCampoEstilizado("Monto a abonar ($)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        container.addView(etMontoAbonar);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Abonar a meta")
                .setMessage(meta.titulo)
                .setView(container)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Abonar", (d, which) -> {
                    String abonoStr = etMontoAbonar.getText().toString().trim();
                    if (TextUtils.isEmpty(abonoStr)) {
                        mostrarSweetWarning("Campo vacío", "Debes ingresar una cantidad.");
                        return;
                    }

                    try {
                        double abono = Double.parseDouble(abonoStr);
                        if (abono <= 0) {
                            mostrarSweetWarning("Monto incorrecto", "El abono debe ser mayor a 0.");
                            return;
                        }

                        meta.ahorrado += abono;
                        actualizarDatosItem(meta, tvPorcentaje, progressBar, tvMontos);
                        recalcularMetricasGlobales();

                        SweetAlertDialog exito = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        if (meta.ahorrado >= meta.objetivo) {
                            exito.setTitleText("¡Meta Cumplida!");
                            exito.setContentText("Completaste la meta para: " + meta.titulo);
                        } else {
                            exito.setTitleText("Abono Registrado");
                            exito.setContentText("Abonaste: $" + String.format(Locale.US, "%.2f", abono));
                        }
                        exito.setConfirmText("Listo");
                        exito.show();
                        corregirBotonSweetAlert(exito);

                    } catch (NumberFormatException e) {
                        mostrarSweetWarning("Formato Inválido", "Ingresa un número válido.");
                    }
                })
                .create();

        dialog.show();
        configurarBotonesDialogo(dialog);
    }

    private void actualizarDatosItem(Meta meta, TextView tvPorcentaje, ProgressBar progressBar, TextView tvMontos) {
        int porcentaje = (int) ((meta.ahorrado / meta.objetivo) * 100);
        if (porcentaje > 100) porcentaje = 100;

        if (progressBar != null) progressBar.setProgress(porcentaje);
        if (tvPorcentaje != null) tvPorcentaje.setText(porcentaje + "%");
        if (tvMontos != null) {
            tvMontos.setText(String.format(Locale.US, "$%,.2f / $%,.2f", meta.ahorrado, meta.objetivo));
        }
    }

    private void recalcularMetricasGlobales() {
        double totalAhorrado = 0.0;
        double totalObjetivo = 0.0;

        for (Meta meta : listaMetas) {
            totalAhorrado += meta.ahorrado;
            totalObjetivo += meta.objetivo;
        }

        double faltante = Math.max(0, totalObjetivo - totalAhorrado);
        int porcentajeGlobal = 0;
        if (totalObjetivo > 0) {
            porcentajeGlobal = (int) Math.round((totalAhorrado / totalObjetivo) * 100);
        }

        if (tvTotalAcumulado != null) {
            tvTotalAcumulado.setText(String.format(Locale.US, "$%,.2f", totalAhorrado));
        }

        if (tvObjetivoGlobal != null) {
            tvObjetivoGlobal.setText(String.format(Locale.US, "Meta Total: $%,.2f", totalObjetivo));
        }

        if (tvRestanteGlobal != null) {
            tvRestanteGlobal.setText(String.format(Locale.US, "Faltan: $%,.2f", faltante));
        }

        if (tvResumenProgreso != null) {
            tvResumenProgreso.setText(porcentajeGlobal + "% global");
        }

        if (pbMetasGlobal != null) {
            pbMetasGlobal.setProgress(Math.min(100, porcentajeGlobal));
        }

        if (tvContadorMetas != null) {
            tvContadorMetas.setText(listaMetas.size() + " activas");
        }
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

    private void mostrarSweetWarning(String titulo, String mensaje) {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText(titulo);
        dialog.setContentText(mensaje);
        dialog.setConfirmText("Entendido");
        dialog.show();
        corregirBotonSweetAlert(dialog);
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