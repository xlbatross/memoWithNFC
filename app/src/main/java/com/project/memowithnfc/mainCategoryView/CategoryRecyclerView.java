package com.project.memowithnfc.mainCategoryView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

//not used
public class CategoryRecyclerView extends RecyclerView {

    public CategoryRecyclerView(Context context) {
        super(context);
        setItemAnimator(new CategoryAnimator());
    }

    public CategoryRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setItemAnimator(new CategoryAnimator());
    }

    public CategoryRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setItemAnimator(new CategoryAnimator());
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        /*
        Log.i(TAG, "setAdapter");

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelperCallback((ItemAdapter)adapter));
        helper.attachToRecyclerView(this);
        */
    }
}
