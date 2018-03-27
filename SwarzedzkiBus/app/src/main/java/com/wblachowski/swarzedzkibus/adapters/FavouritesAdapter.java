package com.wblachowski.swarzedzkibus.adapters;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codewaves.stickyheadergrid.StickyHeaderGridAdapter;
import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.activities.TimeTableActivity;
import com.wblachowski.swarzedzkibus.data.SettingsDataBaseHelper;
import com.wblachowski.swarzedzkibus.data.Stop;
import com.wblachowski.swarzedzkibus.fragments.FavouritesFragment;

import java.util.ArrayList;

/**
 * Created by wblachowski on 2/24/2018.
 */

public class FavouritesAdapter extends StickyHeaderGridAdapter {

    private ArrayList<Stop> stops;
    private FavouritesFragment fragment;

    public FavouritesAdapter(ArrayList<Stop> stops, FavouritesFragment fragment) {
        this.stops = stops;
        this.fragment = fragment;
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
        try {
            final MyItemViewHolder holder = (MyItemViewHolder) viewHolder;
            Stop stop = stops.get(position);
            holder.nrView.setText(stop.getBusNr());
            holder.stopView.setText(stop.getName());
            holder.directionView.setText(stop.getDirection());
            String timeFull = "";
            if (stop.getNextHour() != null && stop.getNextMinute() != null) {
                timeFull = stop.getNextHour() + ":" + (stop.getNextMinute().length() > 1 ? stop.getNextMinute() : "0" + stop.getNextMinute());
            }
            holder.timeView.setText(timeFull);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int offset = getItemSectionOffset(section, holder.getAdapterPosition());
                    final Stop stop = stops.get(offset);

                    Intent intent = new Intent(v.getContext(), TimeTableActivity.class);
                    intent.putExtra("nr", stop.getBusNr());
                    intent.putExtra("id", stop.getId());
                    intent.putExtra("stopName", stop.getName());
                    intent.putExtra("direction", stop.getDirection());

                    v.getContext().startActivity(intent);
                }
            });

            holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(v.getResources().getString(R.string.favourite_remove)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final int offset = holder.getPosition()-1;
                            final String id = stops.get(offset).getId();
                            final String direction = stops.get(offset).getDirection();
                            stops.remove(offset);
                            notifySectionItemRemoved(0, offset);
                            fragment.notifyStopsChanged();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SettingsDataBaseHelper.getInstance(v.getContext()).deleteFromFavourites(id, direction);
                                }
                            }).start();
                            return true;
                        }
                    });
                }
            });
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    public static class MyItemViewHolder extends ItemViewHolder {
        View itemView;
        TextView nrView;
        TextView stopView;
        TextView directionView;
        TextView timeView;

        MyItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nrView = itemView.findViewById(R.id.independent_bus_nr);
            stopView = itemView.findViewById(R.id.independent_bus_stop);
            directionView = itemView.findViewById(R.id.independent_bus_to);
            timeView = itemView.findViewById(R.id.independent_bus_time);
        }
    }

    public static class MyHeaderViewHolder extends HeaderViewHolder {

        MyHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

}
