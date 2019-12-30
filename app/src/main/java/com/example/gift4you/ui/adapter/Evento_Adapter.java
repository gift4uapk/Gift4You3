package com.example.gift4you.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gift4you.R;
import com.example.gift4you.model.Evento;
import com.example.gift4you.model.Producto;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class Evento_Adapter extends FirestoreRecyclerAdapter<Evento, Evento_Adapter.EventoHolder> {

    private Evento_Adapter.OnItemClickListener listener;

    public Evento_Adapter(@NonNull FirestoreRecyclerOptions<Evento> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Evento_Adapter.EventoHolder holder, int position, @NonNull Evento evento) {


        holder.TVrecordatorio_Evento_Registrado.setText(evento.getRecordatorio());
        holder.TVtipoEvento_Evento_Pegistrado.setText(evento.getTipo_Evento());
        holder.TVuid_Evento_Pegistrado.setText(evento.getUid());
        holder.TVreferenteA_Evento_Pegistrado.setText(evento.getReferente_A());
        holder.TVparentesco_Evento_Pegistrado.setText(evento.getParentesco());


    }

    @NonNull
    @Override
    public Evento_Adapter.EventoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.evento_view,
                parent, false);


        return new Evento_Adapter.EventoHolder(v);
    }

    class EventoHolder extends RecyclerView.ViewHolder {
        TextView TVrecordatorio_Evento_Registrado;
        TextView TVtipoEvento_Evento_Pegistrado;
        TextView TVuid_Evento_Pegistrado;
        TextView TVreferenteA_Evento_Pegistrado;
        TextView TVparentesco_Evento_Pegistrado;

        public EventoHolder(View itemView) {


            super(itemView);

            TVrecordatorio_Evento_Registrado=itemView.findViewById(R.id.recordatorio_evento_regis);
            TVtipoEvento_Evento_Pegistrado=itemView.findViewById(R.id.tipoEvento_evento_regis);
            TVuid_Evento_Pegistrado=itemView.findViewById(R.id.uid_evento_regis);
            TVreferenteA_Evento_Pegistrado=itemView.findViewById(R.id.referenteA_evento_regis);
            TVparentesco_Evento_Pegistrado=itemView.findViewById(R.id.parentesco_evento_regis);

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

    public void setOnItemClickListener(Evento_Adapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
