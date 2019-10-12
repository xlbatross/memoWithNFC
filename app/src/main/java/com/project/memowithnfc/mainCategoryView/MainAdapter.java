package com.project.memowithnfc.mainCategoryView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.project.memowithnfc.CategoryMemoActivity;
import com.project.memowithnfc.NfcCheckActivity;
import com.project.memowithnfc.R;
import com.project.memowithnfc.db.DBHelper;
import com.project.memowithnfc.mainCategoryView.item.CategoryItem;
import com.project.memowithnfc.mainCategoryView.item.Item;
import com.project.memowithnfc.mainCategoryView.item.MemoItem;
import com.project.memowithnfc.viewholder.CategoryViewHolder;
import com.project.memowithnfc.viewholder.MemoViewHolder;
import com.project.memowithnfc.vo.Category;
import com.project.memowithnfc.vo.Memo;

import java.util.ArrayList;

public class MainAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private DBHelper db;

    private final int CATEGORY_ITEM_VIEW = 0;
    private final int MEMO_ITEM_VIEW = 1;
    private final int MEMO_VIEW_COUNT = 2;
    private ArrayList<Item> visibleItems = new ArrayList<>();

    public MainAdapter(Context context, DBHelper db) {
        this.context = context;
        this.db = db;

        ArrayList<Category> cList = new ArrayList<>(this.db.getAllCategories());
        for(Category cg : cList) {
            visibleItems.add(new CategoryItem(cg));
            CategoryItem cgi = (CategoryItem) visibleItems.get(visibleItems.size() - 1);
            ArrayList<Memo> mms = new ArrayList<>(this.db.getNextMemosByCategory(cg.getId()));
            if(mms.size() != 0) {
                int i = visibleItems.size();
                for(Memo mm : mms) {
                    if(visibleItems.size() - i >= MEMO_VIEW_COUNT )
                        cgi.unvisibleMemoItems.add(new MemoItem(mm));
                    else if(visibleItems.size() - i < MEMO_VIEW_COUNT ) {
                        cgi.visibleMemoSize += 1;
                        visibleItems.add(new MemoItem(mm));
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (visibleItems != null ? visibleItems.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return visibleItems.get(position).viewType;
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch(viewType) {
            case CATEGORY_ITEM_VIEW :
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.category_bar, viewGroup, false);
                viewHolder = new CategoryViewHolder(view);
                break;
            case MEMO_ITEM_VIEW :
                View subview = LayoutInflater.from(context)
                        .inflate(R.layout.memo, viewGroup, false);
                viewHolder = new MemoViewHolder(subview);
                break;
        }

        return viewHolder;
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CategoryViewHolder) {
            final CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            CategoryItem cgi = (CategoryItem) visibleItems.get(position);
            categoryViewHolder.category_name.setText(cgi.category.getName());

            categoryViewHolder.cView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryViewHolder.fold.toggle();
                }
            });

            categoryViewHolder.fold.setTag(holder);
            categoryViewHolder.fold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int holderPosition = ((CategoryViewHolder)buttonView.getTag()).getAdapterPosition();
                    if(isChecked) {
                        expandMemoItems(holderPosition);
                    }
                    else {
                        collapseMemoItems(holderPosition);
                    }
                }
            });

            categoryViewHolder.cView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(context, CategoryMemoActivity.class);
                    intent.putExtra("category_id", cgi.category.getId());
                    context.startActivity(intent);
                    return false;
                }
            });
        }
        else if(holder instanceof MemoViewHolder) {
            final MemoViewHolder memoViewHolder = (MemoViewHolder) holder;
            MemoItem mmi = (MemoItem) visibleItems.get(position);
            memoViewHolder.content.setText(mmi.memo.getContent());
            memoViewHolder.time.setText(mmi.memo.getTime());
            memoViewHolder.date.setText("[" + mmi.memo.getDate() + "]");
            //memoViewHolder.category.setText("[" + mmi.memo.getCategory_name());
            memoViewHolder.mid.setText(String.valueOf(mmi.memo.getId()));
        }
    }

    private void expandMemoItems(int position) {
        CategoryItem cgi = (CategoryItem)visibleItems.get(position);
        int MemoSize = cgi.unvisibleMemoItems.size();

        for(int i = MemoSize - 1; i>=0; i--) {
            visibleItems.add(position + cgi.visibleMemoSize + 1, cgi.unvisibleMemoItems.get(i));
        }

        notifyItemRangeInserted(position + cgi.visibleMemoSize + 1, MemoSize);
    }

    private void collapseMemoItems(int position) {
        CategoryItem cgi = (CategoryItem)visibleItems.get(position);
        int MemoSize = cgi.unvisibleMemoItems.size();

        for(int i = 0; i < MemoSize; i++) {
            visibleItems.remove(position + cgi.visibleMemoSize + 1 );
        }

        notifyItemRangeRemoved(position + cgi.visibleMemoSize + 1 , MemoSize);
    }
}