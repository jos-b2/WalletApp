package com.example.walletapp;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class BottomSheetRegistro extends BottomSheetDialogFragment {

    public interface OnTransaccionGuardadaListener {
        void onTransaccionGuardada(String monto, String descripcion);
    }

    private OnTransaccionGuardadaListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTransaccionGuardadaListener) {
            listener = (OnTransaccionGuardadaListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_registro, container, false);

        EditText etMonto = view.findViewById(R.id.et_monto);
        EditText etDescripcion = view.findViewById(R.id.et_descripcion);
        Button btnGuardar = view.findViewById(R.id.btn_guardar);

        btnGuardar.setOnClickListener(v -> {
            String monto = etMonto.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (TextUtils.isEmpty(monto)) {
                mostrarAlerta(SweetAlertDialog.WARNING_TYPE, "Campo requerido", "Por favor ingresa el monto.");
                return;
            }

            if (TextUtils.isEmpty(descripcion)) {
                mostrarAlerta(SweetAlertDialog.WARNING_TYPE, "Campo requerido", "Por favor ingresa la descripción.");
                return;
            }

            SweetAlertDialog exitoDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE);
            exitoDialog.setTitleText("Transacción Registrada");
            exitoDialog.setContentText("Monto: $" + monto + "\nDescripción: " + descripcion);
            exitoDialog.setConfirmText("Aceptar");
            exitoDialog.setConfirmClickListener(sDialog -> {
                if (listener != null) {
                    listener.onTransaccionGuardada(monto, descripcion);
                }
                Toast.makeText(getContext(), "¡Guardado temporalmente!", Toast.LENGTH_SHORT).show();
                sDialog.dismissWithAnimation();
                dismiss();
            });

            ajustarBotonAlerta(exitoDialog);
            exitoDialog.show();
        });

        return view;
    }

    private void mostrarAlerta(int tipo, String titulo, String mensaje) {
        SweetAlertDialog dialog = new SweetAlertDialog(requireContext(), tipo);
        dialog.setTitleText(titulo);
        dialog.setContentText(mensaje);
        dialog.setConfirmText("Entendido");
        ajustarBotonAlerta(dialog);
        dialog.show();
    }

    private void ajustarBotonAlerta(SweetAlertDialog dialog) {
        dialog.setOnShowListener(d -> {
            Button confirmBtn = dialog.findViewById(com.ontbee.legacyforks.cn.pedant.SweetAlert.R.id.confirm_button);
            if (confirmBtn != null) {
                ViewGroup.LayoutParams params = confirmBtn.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                confirmBtn.setLayoutParams(params);
                confirmBtn.setPadding(32, 16, 32, 16);
            }
        });
    }
}