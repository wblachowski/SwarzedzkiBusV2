package com.wblachowski.swarzedzkibus.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codewaves.stickyheadergrid.StickyHeaderGridAdapter;
import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.activities.StopsActivity;
import com.wblachowski.swarzedzkibus.data.Bus;

import java.util.ArrayList;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class AllAdapter  extends StickyHeaderGridAdapter {
    private ArrayList<ArrayList<Bus>> buses;
    private ArrayList<String> headers;

    public AllAdapter(ArrayList<String> headers,ArrayList<ArrayList<Bus>> buses) {
        this.buses=buses;
        this.headers=headers;
    }

    @Override
    public int getSectionCount() {
        return headers.size();
    }

    @Override
    public int getSectionItemCount(int section) {
        return buses.get(section).size();
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_header, parent, false);
        return new MyHeaderViewHolder(view);
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item, parent, false);
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int section) {
        final MyHeaderViewHolder holder = (MyHeaderViewHolder)viewHolder;
        holder.labelView.setText(headers.get(section));
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, final int section, final int position) {
        final MyItemViewHolder holder = (MyItemViewHolder)viewHolder;

        holder.nrView.setText(buses.get(section).get(position).getNr());
        holder.fromView.setText(buses.get(section).get(position).getFrom());
        holder.toView.setText(buses.get(section).get(position).getTo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int section = getAdapterPositionSection(holder.getAdapterPosition());
                final int offset = getItemSectionOffset(section, holder.getAdapterPosition());
                Bus bus=buses.get(section).get(offset);

                Intent intent = new Intent(v.getContext(), StopsActivity.class);
                intent.putExtra("nr",bus.getNr());
                intent.putExtra("routeId",bus.getRouteId());
                v.getContext().startActivity(intent);
            }
        });
    }

    public static class MyHeaderViewHolder extends HeaderViewHolder {
        TextView labelView;

        MyHeaderViewHolder(View itemView) {
            super(itemView);
            labelView = itemView.findViewById(R.id.header_label);
        }
    }

    public static class MyItemViewHolder extends ItemViewHolder {
        View itemView;
        TextView nrView;
        TextView fromView;
        TextView toView;
        MyItemViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            nrView = itemView.findViewById(R.id.bus_nr);
            fromView=itemView.findViewById(R.id.bus_from);
            toView=itemView.findViewById(R.id.bus_to);

        }
    }
}
