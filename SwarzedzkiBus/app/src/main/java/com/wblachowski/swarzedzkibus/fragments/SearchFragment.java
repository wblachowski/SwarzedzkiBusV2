package com.wblachowski.swarzedzkibus.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.activities.TimeTableActivity;
import com.wblachowski.swarzedzkibus.adapters.IndependentStopsCursorAdapter;
import com.wblachowski.swarzedzkibus.data.MainDataBaseHelper;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class SearchFragment extends Fragment {

    EditText editText;
    Button clearButton;
    ListView listView;
    Thread searchThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        editText = rootView.findViewById(R.id.editText);
        clearButton = rootView.findViewById(R.id.clear_button);
        listView = rootView.findViewById(R.id.search_listview);
        setEditTextListener();
        setClearButtonAction();
        return rootView;
    }


    private void setEditTextListener() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearButton.setVisibility(charSequence.length() > 0 ? View.VISIBLE : View.INVISIBLE);
                killSearchThread();
                if (charSequence.length() >= 2) {
                    searchForStops(charSequence.toString());
                } else {
                    clearListView();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setClearButtonAction() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
    }

    private void searchForStops(final String pattern) {
        searchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Cursor cursor = MainDataBaseHelper.getInstance(getActivity()).getStopsByName(pattern);
                    final IndependentStopsCursorAdapter stopsAdapter = new IndependentStopsCursorAdapter(getActivity(), cursor);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(stopsAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> a, View v, int position,
                                                        long id) {

                                    Intent intent = new Intent(v.getContext(), TimeTableActivity.class);
                                    cursor.moveToPosition(position);
                                    intent.putExtra("nr",cursor.getString(cursor.getColumnIndex("bus_name")));
                                    intent.putExtra("id",cursor.getString(cursor.getColumnIndex("id")));
                                    intent.putExtra("stopName",cursor.getString(cursor.getColumnIndex("STOP")));
                                    intent.putExtra("direction",cursor.getString(cursor.getColumnIndex("FINAL_STOP")));
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    return;
                }
            }
        });
        searchThread.start();
    }

    private void clearListView() {
        listView.setAdapter(null);
    }

    private void killSearchThread(){
        if (searchThread != null && searchThread.isAlive()) {
            searchThread.interrupt();
        }
    }
}
