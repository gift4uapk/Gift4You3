package com.example.gift4you.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gift4you.R;
import com.example.gift4you.model.Producto;
import com.example.gift4you.ui.adapter.Producto_Adapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Fragment_Inicio extends Fragment {

    View vista;
    public static String Uid_producto_cliqueado;
    private FirebaseFirestore db;
    private CollectionReference notebookRef;
    private Producto_Adapter adaptador;
    private ConstraintLayout constraintLayout2;
    private ProgressBar progressBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_inicio,container,false);

        db = FirebaseFirestore.getInstance();
        notebookRef = db.collection("PRODUCTOS_REGISTRADOS")
        .document("Ocasional(cumpleaños, navidad)")
        .collection("Ocasional(cumpleaños, navidad)");

        constraintLayout2=vista.findViewById(R.id.fragment_Inicio2);
        progressBar=vista.findViewById(R.id.progress_bar_fragment_inicio);

        setUpRecyclerView();

        return vista;
    }

    private void setUpRecyclerView() {
        Query query = notebookRef;

        FirestoreRecyclerOptions<Producto> options = new FirestoreRecyclerOptions.Builder<Producto>()
                .setQuery(query, Producto.class)
                .build();

        adaptador = new Producto_Adapter(options);

        RecyclerView recyclerView = vista.findViewById(R.id.recycler_productos_registrados);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adaptador);

        progressBar.setVisibility(View.GONE);

        adaptador.setOnItemClickListener((documentSnapshot, position) ->
        {
            Uid_producto_cliqueado=documentSnapshot.getId();
            constraintLayout2.setVisibility(View.GONE);
            Fragment_ProductoDetallado  nextFrag= new Fragment_ProductoDetallado();

            this.getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_Inicio1, nextFrag, null)
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

}