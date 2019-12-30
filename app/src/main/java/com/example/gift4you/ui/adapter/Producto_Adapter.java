package com.example.gift4you.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gift4you.R;
import com.example.gift4you.model.Producto;
import com.example.gift4you.ui.fragment.Fragment_ObsequioRecomendado;
import com.example.gift4you.ui.fragment.Fragment_RecomendacionObsequio;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class Producto_Adapter extends FirestoreRecyclerAdapter<Producto, Producto_Adapter.ProductoHolder> {



    private OnItemClickListener listener;

    public Producto_Adapter(@NonNull FirestoreRecyclerOptions<Producto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductoHolder holder, int position, @NonNull Producto producto) {

        holder.TVsuma_Producto_Registrado.setText(producto.getNumero());
        String imagen=producto.getImagen_Principal();
        holder.TVtitulo_Producto_Registrado.setText(producto.getTitulo());
        holder.TVprecio_Producto_Pegistrado.setText(producto.getPrecio());
        holder.TVuid_Producto_Pegistrado.setText(producto.getUid());
        holder.TVdescripcion_Producto_Pegistrado.setText(producto.getDescripcion());
        holder.TVsubTitulo_Producto_Pegistrado.setText(producto.getSubTitulo());
        Picasso.get().load(imagen).into(holder.IVimagen_Principal_Producto_Regis);

    }

    @NonNull
    @Override
    public ProductoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.producto_view,
                parent, false);


        return new ProductoHolder(v);
    }

    class ProductoHolder extends RecyclerView.ViewHolder {
        TextView TVtitulo_Producto_Registrado;
        TextView TVprecio_Producto_Pegistrado;
        TextView TVuid_Producto_Pegistrado;
        TextView TVsubTitulo_Producto_Pegistrado;
        TextView TVdescripcion_Producto_Pegistrado;
        TextView TVsuma_Producto_Registrado;
        ImageView IVimagen_Principal_Producto_Regis;

        public ProductoHolder(View itemView) {


            super(itemView);

            TVsuma_Producto_Registrado=itemView.findViewById(R.id.numero_producto_regis);
            TVtitulo_Producto_Registrado=itemView.findViewById(R.id.titulo_producto_regis);
            TVprecio_Producto_Pegistrado=itemView.findViewById(R.id.precio_producto_regis);
            TVuid_Producto_Pegistrado=itemView.findViewById(R.id.uid_producto_regis);
            TVsubTitulo_Producto_Pegistrado=itemView.findViewById(R.id.subtitulo_producto_regis);
            TVdescripcion_Producto_Pegistrado=itemView.findViewById(R.id.descripcion_producto_regis);
            IVimagen_Principal_Producto_Regis=itemView.findViewById(R.id.imagen_principal_producto_regis);

            itemView.setOnClickListener(v ->
            {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



}
