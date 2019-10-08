package com.project.memowithnfc.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.project.memowithnfc.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public View cView;
    public TextView category;
    public ToggleButton nfc;
    public ToggleButton fold;

    public CategoryViewHolder(View view) {
        super(view);
        this.cView = view;
        this.category = (TextView) view.findViewById(R.id.category_name);
        this.nfc = (ToggleButton) view.findViewById(R.id.nfc);
        this.fold = (ToggleButton) view.findViewById(R.id.fold);
    }
}