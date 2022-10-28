package com.example.unsmoke;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseHelper {

    private static FirebaseAuth firebaseAuth;
    private static FirebaseFirestore firebaseFirestore;
    private static String UIDUsuario;
    private static FirebaseStorage firebaseStorage;
    private static FirebaseDatabase firebaseDatabase;

    public static FirebaseAuth getFirebaseAuth(){
        if (firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        };
        return firebaseAuth;
    };

    public static FirebaseFirestore getFirebaseFirestore(){
        if (firebaseFirestore == null){
            firebaseFirestore = FirebaseFirestore.getInstance();
        };
        return firebaseFirestore;
    };

    public static String getUIDUsuario() {
        if (UIDUsuario == null){
            UIDUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        };
        return UIDUsuario;
    }

    public static FirebaseStorage getFirebaseStorage(){
        if (firebaseStorage == null){
            firebaseStorage = FirebaseStorage.getInstance();
        };
        return firebaseStorage;
    };

    public static FirebaseDatabase getFirebaseDatabase(){
        if (firebaseDatabase == null){
            firebaseDatabase = FirebaseDatabase.getInstance();
        };
        return firebaseDatabase;
    }
}
