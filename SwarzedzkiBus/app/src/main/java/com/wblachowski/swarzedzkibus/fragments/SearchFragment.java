package com.wblachowski.swarzedzkibus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wblachowski.swarzedzkibus.R;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class SearchFragment extends Fragment {

    EditText editText;
    Button clearButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        editText = rootView.findViewById(R.id.editText);
        clearButton = rootView.findViewById(R.id.clear_button);
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
                clearButton.setVisibility(charSequence.length()>0 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setClearButtonAction(){
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
    }
}
