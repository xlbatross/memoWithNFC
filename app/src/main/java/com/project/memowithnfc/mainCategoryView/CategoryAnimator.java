package com.project.memowithnfc.mainCategoryView;

import android.animation.ObjectAnimator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

//not used
public class CategoryAnimator extends DefaultItemAnimator {
    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        return super.animateAdd(holder);
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        View view = holder.itemView;

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f).setDuration(100);
        alphaAnimator.setInterpolator(new DecelerateInterpolator());
        alphaAnimator.start();

        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        //Log.i(TAG, "animateMove. fromX, fromY, toX, toY : "+fromX+", "+fromY+", "+toX+", "+toY);

        return super.animateMove(holder, fromX, fromY, toX, toY);
    }
}
