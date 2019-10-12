package com.project.memowithnfc.categoryselectListView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.project.memowithnfc.R;
import com.project.memowithnfc.vo.Category;

import java.util.ArrayList;

public class CategorySelectAdapter extends  RecyclerView.Adapter<CategorySelectAdapter.CategoryListViewHolder>{
    private Context context;
    private ArrayList<Category> mList;
    private int mSelectedItem = -1;

    public CategorySelectAdapter(Context context, ArrayList<Category> mList) {
        this.context = context;
        this.mList = mList;
    }

    public int getSelectedItem() {
        return mSelectedItem;
    }

    @Override
    public int getItemCount() {
        return (mList != null ? mList.size() : 0);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getId();
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public CategoryListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.category, viewGroup, false);

        CategoryListViewHolder viewHolder = new CategoryListViewHolder(view);

        return viewHolder;
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull final CategoryListViewHolder viewholder, final int position) {
        viewholder.select_category.setChecked(position == mSelectedItem);
        viewholder.select_category.setText(mList.get(position).getName());
    }

    public class CategoryListViewHolder extends RecyclerView.ViewHolder {
        protected RadioButton select_category;

        public CategoryListViewHolder(View view) {
            super(view);
            this.select_category = (RadioButton) view.findViewById(R.id.select_category);

            select_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
