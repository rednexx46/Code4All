package com.josexavier.code4all.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static void validarPermissoes(String[] permissoes, Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> listaPermissoes = new ArrayList<>();

            /*Percorre as Permissões passadas,
            verificando desta forma uma a uma
            * se já tem a permissão aceite*/

            for (String permissao : permissoes) {
                boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;

                if (!temPermissao) listaPermissoes.add(permissao);
            }

            if (listaPermissoes.isEmpty()) return;
            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            // Solicita Permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);

        }
    }

}

