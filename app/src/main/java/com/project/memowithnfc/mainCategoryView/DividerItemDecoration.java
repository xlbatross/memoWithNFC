package com.project.memowithnfc.mainCategoryView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.project.memowithnfc.viewholder.CategoryViewHolder;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    public DividerItemDecoration(Context context) {

    }

    // canvas에 적합한 decoration을 draw
    // item view가 그려지기전에 decoration이 먼저 그려지며, view밑에 나타난다.
    @Override
    public void onDraw(Canvas c, RecyclerView recyclerView, RecyclerView.State state) {
        super.onDraw(c, recyclerView, state);
    }


    // outRect를 인자로 보내면, outRect에 값들을 넣어줌. (call by reference)
    // outRect의 각 자리는 각 item의 padding의 정도를 정해줘야함.
    // default는 다 0
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //Log.i(TAG, "getItemOffsets");

        RecyclerView.ViewHolder viewHolder = (parent.getChildViewHolder(view));

        if(viewHolder instanceof CategoryViewHolder){
            outRect.set(4, 30, 8, 0);
        }else{
            outRect.set(4, 0, 8, 0);
        }

    }
}
