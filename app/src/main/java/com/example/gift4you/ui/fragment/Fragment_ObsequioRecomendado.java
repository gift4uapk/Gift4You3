package com.example.gift4you.ui.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gift4you.MenuPrincipal;
import com.example.gift4you.R;
import com.example.gift4you.model.Producto;
import com.example.gift4you.model.Solicitud;
import com.example.gift4you.ui.MainActivity;
import com.example.gift4you.ui.adapter.Producto_Adapter;
import com.facebook.login.LoginManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;
import java.util.concurrent.Executor;


public class Fragment_ObsequioRecomendado extends Fragment {

    View vista;

    public static String Uid_producto_cliqueado_Fragment_ObsequioRecomendado;
    public static String suma;
    private FirebaseFirestore db;
    private Producto_Adapter adaptador;
    private ConstraintLayout constraintLayout2;
    private ProgressBar progressBar;


    private String nombre=Fragment_RecomendacionObsequio.nombre_fragment_RecomendacionObsequio;
    private String parentesco=Fragment_RecomendacionObsequio.parentescoSeleccionado_fragment_RecomendacionObsequio;

    FirebaseAuth mAuth;
    CollectionReference productosRef;
    CollectionReference productoEvent;
    Query query;



    public Fragment_ObsequioRecomendado() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment__obsequio__recomendado,container,false);

        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        productosRef =  db.collection("PRODUCTOS_REGISTRADOS")
                .document("Ocasional(cumpleaños, navidad)")
                .collection("Ocasional(cumpleaños, navidad)");



        if( Fragment_RecomendacionObsequio.evento_seleccionado_FRO==null){
            query = productosRef;
            getInformacionSolicitud();
        }
        else if(Fragment_RecomendacionObsequio.evento_seleccionado_FRO.equals("Ocasional(cumpleaños, navidad)")){

            query = productosRef;
            getInformacionSolicitud();
        }else {
            productoEvent=db.collection("PRODUCTOS_REGISTRADOS")
                    .document(Fragment_RecomendacionObsequio.evento_seleccionado_FRO)
                    .collection(Fragment_RecomendacionObsequio.evento_seleccionado_FRO);
            query = productoEvent;
        }

        constraintLayout2=vista.findViewById(R.id.fragment_obsequio_recomendado2);
        progressBar=vista.findViewById(R.id.progress_bar_fragment_obsequio_recomendado);


        FirestoreRecyclerOptions<Producto> options = new FirestoreRecyclerOptions.Builder<Producto>()
                .setQuery(query, Producto.class)
                .build();

        adaptador = new Producto_Adapter(options);

        setUpRecyclerView();


        return vista;
    }



    void getInformacionSolicitud(){

        if(nombre==null){
            nombre=Fragment_Historial.nombreSeleccionado_fragment_historial;
        }
        if (parentesco==null){
            parentesco=Fragment_Historial.parentescoSeleccionado_fragment_historial;
        }

        String uid=mAuth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS")
                .document(parentesco);

        setUpRecyclerView();
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> myMap = (Map<String,Object>)
                    documentSnapshot.get(nombre);

                    suma = myMap.get("Suma_De_Respuestas").toString();

                })
                .addOnFailureListener(command -> {
                    Toast.makeText(getContext(),"Fallo de conexion",Toast.LENGTH_LONG).show();

        });

    }


    private void setUpRecyclerView() {

        RecyclerView recyclerView = vista.findViewById(R.id.recycler_productos_recomendados);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adaptador);



        if(((Activity) getActivity()).findViewById(R.id.textView2)==null){
            return;
        }else{
            ((Activity) getActivity()).findViewById(R.id.textView2).setVisibility(View.GONE);
        }

        //progressBar.setVisibility(View.GONE);

        if(adaptador==null){
            return;
        }
        adaptador.setOnItemClickListener((documentSnapshot, position) ->
        {
            Uid_producto_cliqueado_Fragment_ObsequioRecomendado=documentSnapshot.getId();
            constraintLayout2.setVisibility(View.GONE);
            Fragment_ProductoDetallado  nextFrag= new Fragment_ProductoDetallado();

            this.getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_obsequio_recomendado1, nextFrag, null)
                    .addToBackStack(null)
                    .commit();
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        adaptador.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptador.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                // replace your fragment here
                Fragment_RecomendacionObsequio  nextInicio= new Fragment_RecomendacionObsequio();
                this.getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_RecomendacionObsequio1, nextInicio, null)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        });
    }


}
