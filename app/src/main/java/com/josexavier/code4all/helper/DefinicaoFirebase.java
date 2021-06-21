package com.josexavier.code4all.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DefinicaoFirebase {

    static FirebaseAuth autenticacao;
    static StorageReference armazenamento;
    static DatabaseReference baseDados;

    public static FirebaseAuth recuperarAutenticacao() {
        if (autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    public static StorageReference recuperarArmazenamento() {
        if (armazenamento == null) {
            armazenamento = FirebaseStorage.getInstance().getReference();
        }
        return armazenamento;
    }

    public static DatabaseReference recuperarBaseDados() {
        if (baseDados == null) {
            baseDados = FirebaseDatabase.getInstance().getReference();
        }
        return baseDados;
    }


}
