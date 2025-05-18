package com.example.carpartsshop.ui.parts;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
    public interface Callback {
        void onItemSelected(String item);
    }
    private final Callback cb;

    public SimpleItemSelectedListener(Callback cb) {
        this.cb = cb;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        cb.onItemSelected(parent.getItemAtPosition(pos).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
