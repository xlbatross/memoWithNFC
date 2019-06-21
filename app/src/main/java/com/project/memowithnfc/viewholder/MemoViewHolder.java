package com.project.memowithnfc.viewholder;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project.memowithnfc.R;
import com.project.memowithnfc.WholeMemoActivity;

public class MemoViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public ConstraintLayout cl;
    public TextView content;
    public TextView time;
    public TextView date;
    public TextView category;
    public TextView mid;

    public MemoViewHolder(View view) {
        super(view);
        this.mView = view;
        this.cl = (ConstraintLayout) view.findViewById(R.id.Memo_container);
        this.content = (TextView) view.findViewById(R.id.Memo_content);
        this.time = (TextView) view.findViewById(R.id.Memo_time);
        this.date = (TextView) view.findViewById(R.id.Memo_date);
        this.category = (TextView) view.findViewById(R.id.Memo_category);
        this.mid = (TextView) view.findViewById(R.id.Memo_id);

        this.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), WholeMemoActivity.class);
                intent.putExtra("memo_id", Integer.valueOf(mid.getText().toString()));
                view.getContext().startActivity(intent);
            }
        });
    }
}
