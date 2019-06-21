package com.project.memowithnfc.mainCategoryView.item;

import com.project.memowithnfc.vo.Category;

import java.util.ArrayList;

public class CategoryItem extends Item{
    public int visibleMemoSize = 0;
    public ArrayList<MemoItem> unvisibleMemoItems = new ArrayList<>();
    public Category category;

    public CategoryItem(Category cg) {
        this.viewType = 0;
        this.category = cg;
    }
}
