package com.nze.nzeframework.widget.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.nze.nzeframework.widget.pulltorefresh.internal.ILoadingLayout;
import com.nze.nzeframework.widget.pulltorefresh.internal.LoadingLayout;
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase;


/**
 * Created by zwy on 2017/5/22.
 * email:16681805@qq.com
 */

public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {

    private RecyclerView mRecyclerView;

    public PullToRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        mRecyclerView = new RecyclerView(context);
        return mRecyclerView;
    }

    /**
     * 设置是否有更多数据的标志
     *
     * @param hasMoreData true表示还有更多的数据，false表示没有更多数据了
     */
    public void setHasMoreData(boolean hasMoreData) {
        if (!hasMoreData) {
            LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
            if (null != footerLoadingLayout) {
                footerLoadingLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
            }
        }
    }

    @Override
    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    @Override
    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    private boolean isFirstItemVisible() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (null == adapter || adapter.getItemCount() == 0) {
            return true;
        }

        int mostTop = (mRecyclerView.getChildCount() > 0) ? mRecyclerView.getChildAt(0).getTop() : 0;
        if (mostTop >= 0) {
            return true;
        }

        return false;

    }

    private boolean isLastItemVisible() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {
            return true;
        }

        final int lastItemPosition = adapter.getItemCount() - 1;
        final int lastVisiblePosition = getLastVisiblePosition();


        /**         * This check should really just be: lastVisiblePosition == lastItemPosition, but ListView         * internally uses a FooterView which messes the positions up. For me we'll just subtract         * one to account for it and rely on the inner condition which checks getBottom().         */
        if (lastVisiblePosition >= lastItemPosition - 1) {
            final int childIndex = lastVisiblePosition - getFirstVisiblePosition();
            final int childCount = mRecyclerView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = mRecyclerView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= mRecyclerView.getBottom();
            }
        }

        return false;
    }

    private int getLastVisiblePosition() {
        int position;
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        } else if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = mRecyclerView.getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    private int getFirstVisiblePosition() {
        int position;
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }
}

