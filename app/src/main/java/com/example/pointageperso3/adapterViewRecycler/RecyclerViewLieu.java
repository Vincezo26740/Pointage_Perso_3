package com.example.pointageperso3.adapterViewRecycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pointageperso3.R;


public class RecyclerViewLieu extends androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerViewLieu.ViewHolder> {


    private final String[][] mDataSet;

    public RecyclerViewLieu(String[][] mDataSet) {
        this.mDataSet = mDataSet;
    }

    static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public TextView itemNom, itemContact;
        public LinearLayout item;
        public RecyclerView_1_ligne_2_String.ListenerDeSelection listenerDeSelection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.RD_choixChantier);
            itemNom = itemView.findViewById(R.id.selection_nom);
            itemContact = itemView.findViewById(R.id.selection_contact);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        // Création de la vue
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ligne_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //modification du contenu de la vue
        holder.itemNom.setText(mDataSet[position][0]);
        holder.itemContact.setText(mDataSet[position][1]);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
