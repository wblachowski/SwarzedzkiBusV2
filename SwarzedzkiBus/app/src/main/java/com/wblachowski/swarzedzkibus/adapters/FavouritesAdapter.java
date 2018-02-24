package com.wblachowski.swarzedzkibus.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codewaves.stickyheadergrid.StickyHeaderGridAdapter;
import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.activities.TimeTableActivity;
import com.wblachowski.swarzedzkibus.data.Stop;

import java.util.ArrayList;

/**
 * Created by wblachowski on 2/24/2018.
 */

public class FavouritesAdapter extends StickyHeaderGridAdapter {

    private ArrayList<Stop> stops;

    public FavouritesAdapter(ArrayList<Stop> stops){
        this.stops=stops;
    }

    @Override
    public int getSectionCount() {
        return 1;
    }

    @Override
    public int getSectionItemCount(int section) {
        return stops.size();
    }


    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        final View view = new View(parent.getContext());
        return new MyHeaderViewHolder(view);
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.independent_stop_item, parent, false);
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int section) {

    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, final int section, final int position) {
        final MyItemViewHolder holder = (MyItemViewHolder)viewHolder;

        holder.nrView.setText(stops.get(position).getBusNr());
        holder.stopView.setText(stops.get(position).getName());
        holder.directionView.setText(stops.get(position).getDirection());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int offset = getItemSectionOffset(section, holder.getAdapterPosition());
                Stop stop=stops.get(offset);

                Intent intent = new Intent(v.getContext(), TimeTableActivity.class);
                intent.putExtra("nr",stop.getBusNr());
                intent.putExtra("id",stop.getId());
                intent.putExtra("stopName",stop.getName());
                intent.putExtra("direction",stop.getDirection());

                v.getContext().startActivity(intent);
            }
        });
    }

    public static class MyItemViewHolder extends ItemViewHolder {
        View itemView;
        TextView nrView;
        TextView stopView;
        TextView directionView;
        MyItemViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            nrView = itemView.findViewById(R.id.independent_bus_nr);
            stopView=itemView.findViewById(R.id.independent_bus_stop);
            directionView=itemView.findViewById(R.id.independent_bus_to);

        }
    }

    public static class MyHeaderViewHolder extends HeaderViewHolder {

        MyHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

}
