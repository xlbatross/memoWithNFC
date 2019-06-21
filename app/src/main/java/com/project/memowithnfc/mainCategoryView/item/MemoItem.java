package com.project.memowithnfc.mainCategoryView.item;

import com.project.memowithnfc.vo.Memo;

public class MemoItem extends Item {
    public Memo memo;

    public MemoItem(Memo memo) {
        this.viewType = 1;
        this.memo = memo;
    }
}
