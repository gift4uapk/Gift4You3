package com.example.gift4you.ui.fragment;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gift4you.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Fragment_ProductoDetallado extends Fragment {

    TextView DetallesTituloTv,DetallesDescripcionTv,DetallesPrecioTv,DetallesSubTituloTv;
    ImageView DetallesImagenIv;

    String titulo,imagen_principal,subtitulo,precio,descripcion;

    FirebaseFirestore db;

    LinearLayout linearLayout_Producto_Detallado;
    ProgressBar progressBar;

    View vista;

    FirebaseAuth mAuth;
    CollectionReference productosRef;

    String suma;

    TextView sTextNumero;

    TextView textViewRegalosRecomendados;
    DocumentReference docRef;


    public Fragment_ProductoDetallado() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        vista = inflater.inflate(R.layout.fragment__producto_detallado,container,false);

        db = FirebaseFirestore.getInstance();

        DetallesTituloTv=vista.findViewById(R.id.titulo_producto_regis_detalle);
        DetallesDescripcionTv=vista.findViewById(R.id.descripcion_producto_regis_detalle);
        DetallesPrecioTv=vista.findViewById(R.id.precio_producto_regis_detalle);
        DetallesSubTituloTv=vista.findViewById(R.id.subtitulo_producto_regis_detalle);
        DetallesImagenIv=vista.findViewById(R.id.imagen_principal_producto_regis_detalle);

        linearLayout_Producto_Detallado=vista.findViewById(R.id.linearLayout_mostrar_detalles);
        progressBar=vista.findViewById(R.id.progress_bar);

        CargarProductoDetallado();

        return vista;
    }


    void CargarProductoDetallado(){

        String uid_producto_clickeado=null;

        if(Fragment_Inicio.Uid_producto_cliqueado!=null){
            uid_producto_clickeado=Fragment_Inicio.Uid_producto_cliqueado;
            docRef = db.collection("PRODUCTOS_REGISTRADOS")
                    .document("Ocasional(cumpleaños, navidad)")
                    .collection("Ocasional(cumpleaños, navidad)")
                    .document(uid_producto_clickeado);
        }

        if(Fragment_ObsequioRecomendado.Uid_producto_cliqueado_Fragment_ObsequioRecomendado!=null){
            uid_producto_clickeado=Fragment_ObsequioRecomendado.Uid_producto_cliqueado_Fragment_ObsequioRecomendado;
            docRef = db.collection("PRODUCTOS_REGISTRADOS")
                    .document(Fragment_RecomendacionObsequio.evento_seleccionado_FRO)
                    .collection(Fragment_RecomendacionObsequio.evento_seleccionado_FRO)
                    .document(uid_producto_clickeado);
        }


        docRef.get().addOnSuccessListener(documentSnapshot -> {

            titulo=documentSnapshot.get("Titulo").toString();
            subtitulo=documentSnapshot.get("SubTitulo").toString();
            descripcion=documentSnapshot.get("Descripcion").toString();
            precio=documentSnapshot.get("Precio").toString();
            imagen_principal=documentSnapshot.get("Imagen_Principal").toString();

            progressBar.setVisibility(View.GONE);
            linearLayout_Producto_Detallado.setVisibility(View.VISIBLE);

            DetallesSubTituloTv.setText(subtitulo);
            DetallesPrecioTv.setText(precio);
            DetallesTituloTv.setText(titulo);
            DetallesDescripcionTv.setText(descripcion);

            Picasso.get().load(imagen_principal).into(DetallesImagenIv);

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                if(Fragment_Inicio.Uid_producto_cliqueado!=null){
                    Fragment_Inicio  nextInicio= new Fragment_Inicio();
                    this.getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_Inicio1, nextInicio, null)
                            .addToBackStack(null)
                            .commit();
                }

                if(Fragment_ObsequioRecomendado.Uid_producto_cliqueado_Fragment_ObsequioRecomendado!=null){
                    Fragment_ObsequioRecomendado  nextInicio= new Fragment_ObsequioRecomendado();

                    ((Activity) getActivity()).findViewById(R.id.tv_regalos_recomendados_fragment_obsequio_recomnedado).setVisibility(View.GONE);

                    this.getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_obsequio_recomendado1, nextInicio, null)
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            }
            return false;
        });
    }


}
