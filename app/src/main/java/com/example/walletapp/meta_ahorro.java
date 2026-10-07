package com.example.walletapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.Locale;

public class meta_ahorro extends AppCompatActivity {

    private LinearLayout llContenedorMetas;

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
        FloatingActionButton fab = findViewById(R.id.fab_agregar_meta);

        agregarMetaVista(new Meta("Nintendo Switch OLED", 350.00, 100.00));

        if (fab != null) {
            fab.setOnClickListener(v -> mostrarDialogoNuevaMeta());
        }
    }

    private void mostrarDialogoNuevaMeta() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 20, 60, 10);

        EditText etTitulo = new EditText(this);
        etTitulo.setHint("Nombre (ej. Nintendo Switch OLED)");
        layout.addView(etTitulo);

        EditText etMeta = new EditText(this);
        etMeta.setHint("Monto objetivo (ej. 350.00)");
        etMeta.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(etMeta);

        EditText etAbono = new EditText(this);
        etAbono.setHint("Ahorro inicial (ej. 50.00)");
        etAbono.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(etAbono);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Nueva Meta de Ahorro")
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Crear", (dialog, which) -> {
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

                        agregarMetaVista(new Meta(titulo, objetivo, ahorrado));

                        SweetAlertDialog exito = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        exito.setTitleText("¡Meta Creada!");
                        exito.setContentText("Iniciaste el ahorro para: " + titulo);
                        exito.setConfirmText("Aceptar");
                        exito.show();

                    } catch (NumberFormatException e) {
                        mostrarSweetWarning("Formato Inválido", "Ingresa valores numéricos válidos.");
                    }
                })
                .show();
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
        EditText etMontoAbonar = new EditText(this);
        etMontoAbonar.setHint("Ej. 25.00");
        etMontoAbonar.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(60, 20, 60, 10);
        container.addView(etMontoAbonar);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Abonar a meta")
                .setMessage(meta.titulo)
                .setView(container)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Abonar", (dialog, which) -> {
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

                        SweetAlertDialog exito = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        if (meta.ahorrado >= meta.objetivo) {
                            exito.setTitleText("¡Meta Cumplida!");
                            exito.setContentText("Completaste la meta para: " + meta.titulo);
                        } else {
                            exito.setTitleText("Abono Registrado");
                            exito.setContentText("Abonaste: $" + String.format(Locale.US, "%.2f", abono));
                        }
                        exito.setConfirmText("Aceptar");
                        exito.show();

                    } catch (NumberFormatException e) {
                        mostrarSweetWarning("Formato Inválido", "Ingresa un número válido.");
                    }
                })
                .show();
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

    private void mostrarSweetWarning(String titulo, String mensaje) {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText(titulo);
        dialog.setContentText(mensaje);
        dialog.setConfirmText("Entendido");
        dialog.show();
    }
}