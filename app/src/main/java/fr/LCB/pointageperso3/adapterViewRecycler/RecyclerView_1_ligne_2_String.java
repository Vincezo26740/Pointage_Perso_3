package fr.LCB.pointageperso3.adapterViewRecycler;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.LCB.pointageperso3.R;


public class RecyclerView_1_ligne_2_String extends androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView_1_ligne_2_String.ViewHolder> {

    private final String[][] mDataSet;
    private final ListenerDeSelection listenerSelection;

    public RecyclerView_1_ligne_2_String(String[][] mDataSet, ListenerDeSelection listenerSelection) {
        this.mDataSet=mDataSet;
        this.listenerSelection = listenerSelection;
    }
    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView  itemNom, itemContact;
        public LinearLayout item;
        public ListenerDeSelection listenerDeSelection;

        public ViewHolder(@NonNull View itemView, ListenerDeSelection listenerDeSelection) {
            super(itemView);
//            R.id.RD_choixChantier
            this.listenerDeSelection = listenerDeSelection;
            item = itemView.findViewById(R.id.RD_choixChantier);
            itemNom = itemView.findViewById(R.id.selection_nom);
            itemContact = itemView.findViewById(R.id.selection_contact);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("position",getAbsoluteAdapterPosition());
            listenerDeSelection.ListenerDeSelectionRecylcerView(intent);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Création de la vue
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ligne_selection, parent, false);
        return new ViewHolder(view, listenerSelection);
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

    public interface ListenerDeSelection{
        void ListenerDeSelectionRecylcerView(Intent intent);
    }
}
