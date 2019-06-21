package com.project.memowithnfc.searchMemoView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.memowithnfc.R;
import com.project.memowithnfc.viewholder.MemoViewHolder;
import com.project.memowithnfc.vo.Memo;

import java.util.ArrayList;

public class SearchMemoAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<Memo> mList;

    public SearchMemoAdapter(Context context, ArrayList<Memo> list) {
        this.context = context;
        this.mList = list;
    }

    @Override
    public int getItemCount() {
        return (mList != null ? mList.size() : 0);
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.memo, viewGroup, false);

        MemoViewHolder viewHolder = new MemoViewHolder(view);

        return viewHolder;
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewholder, final int position) {
        MemoViewHolder vh = (MemoViewHolder) viewholder;
        vh.content.setText(mList.get(position).getContent());
        vh.time.setText(mList.get(position).getTime());
        vh.date.setText("[" + mList.get(position).getDate() + "]");
        vh.category.setText("[" + mList.get(position).getCategory_name() + "]");
        vh.mid.setText(String.valueOf(mList.get(position).getId()));
    }
}

/*
this.mView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN : {
                            cl.setBackgroundColor(Color.rgb(220, 220, 220));
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE:
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP : {
                            cl.setBackgroundColor(Color.rgb(255, 255, 255));
                            return false;
                        }
                        default: {
                            break;
                        }
                    }
                    return false;
                }
            });
 */
