package cn.sccss.view.recycler.expandable;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class ExpandableRecyclerAdapter<T extends RecyclerView.ViewHolder, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 视图类型：组
     */
    private static final int TYPE_GROUP = 1;
    /**
     * 视图类型：子
     */
    private static final int TYPE_CHILD = 2;
    /**
     * 组的展示状态集合
     */
    private SparseBooleanArray groupStateArray;

    public ExpandableRecyclerAdapter() {
        groupStateArray = new SparseBooleanArray();
    }

    /**
     * 创建组视图ViewHolder
     * @param parent 同{@link #onCreateViewHolder(ViewGroup, int)} 中的ViewGroup
     * @return 组视图ViewHolder
     */
    public abstract T onCreateGroupViewHolder(ViewGroup parent);

    /**
     * 创建子视图ViewHolder
     * @param parent 同{@link #onCreateViewHolder(ViewGroup, int)} 中的ViewGroup
     * @return 子视图ViewHolder
     */
    public abstract K onCreateChildViewHolder(ViewGroup parent);

    /**
     * 获取组的实体
     * @param groupPosition 组列表下标
     * @return 组列表项位置对应的数据实体
     */
    public abstract Object getGroup(int groupPosition);

    /**
     * 获取子的实体
     * @param groupPosition 组列表下标
     * @param itemPosition 子列表下标
     * @return 组列表项下的子列表项位置对应的数据实体
     */
    public abstract Object getChild(int groupPosition, int itemPosition);

    /**
     * 获取组的数量
     * @return 组列表项总数量
     */
    public abstract int getGroupCount();

    /**
     * 获取指定组中的子视图数量
     * @param groupPosition 组视图下标
     * @return 指定组列表下的子列表数量
     */
    public abstract int getChildCount(int groupPosition);

    /**
     * 绑定数据到组视图
     */
    public abstract void onBindGroupViewHolder(T holder, int groupPosition);

    /**
     * 绑定数据到子视图
     */
    public abstract void onBindChildViewHolder(K holder, int groupPosition, int childPosition);

    /**
     * 设置组的折叠展开状态
     *
     * @param groupPosition 组下标
     * @param isExpend      是否展开
     */
    public void setExpendState(int groupPosition, boolean isExpend) {
        setExpendStateNoRefresh(groupPosition, isExpend);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ExpandableRecyclerAdapter.this.notifyDataSetChanged();
            }
        });
    }

    /**
     * 仅设置折叠状态，但不立即刷新页面显示
     *
     * @param groupPosition 组下标
     * @param isExpend      是否展示
     */
    protected void setExpendStateNoRefresh(int groupPosition, boolean isExpend) {
        groupStateArray.put(groupPosition, isExpend);
    }

    /**
     * 获取组的折叠展示状态
     *
     * @param groupPosition 组下标
     * @return 是否展开
     */
    public boolean getExpendState(int groupPosition) {
        return groupStateArray.get(groupPosition, false);
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_GROUP ? onCreateGroupViewHolder(parent) : onCreateChildViewHolder(parent);
    }

    @Override
    public final int getItemCount() {
        int groupCount = getGroupCount();
        if (groupCount <= 0) return groupCount;
        int count = groupCount;
        for (int i = 0; i <= groupCount; i++) {
            if (getExpendState(i))
                count += getChildCount(i);
        }
        return count;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_GROUP) {
            int groupPosition = getGroupPosition(position);
            if (groupPosition == -1)
                throw new IllegalArgumentException("计算组下标出错了，所在下标：" + position);
            onBindGroupViewHolder((T) holder, groupPosition);
        } else {
            int itemGroupPosition = getItemGroupPosition(position);
            if (itemGroupPosition == -1) {
                throw new IllegalArgumentException("计算条目所有组的下标出错了，所在下标：" + position);
            }
            int itemPosition = getItemPosition(position);
            if (itemPosition == -1) {
                throw new IllegalArgumentException("计算条目所有下标出错了，所在下标：" + position);
            }
            onBindChildViewHolder((K) holder, itemGroupPosition, itemPosition);
        }
    }

    /**
     * 获取条目所有组的下标
     *
     * @param position 在整个RecyclerView中的下标
     */
    private int getItemGroupPosition(int position) {
        if (position <= 0) return 0;
        int groupCount = getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            position -= 1;
            if (getExpendState(i)) {
                int childCount = getChildCount(i);
                if (position < childCount) return i;
                position -= childCount;
            } else {
                if (position == 0) return i + 1;
            }
        }
        return -1;
    }

    /**
     * 获取RecyclerView的下标对应的组下标
     *
     * @param position 在整个RecyclerView中的下标
     */
    private int getGroupPosition(int position) {
        if (position <= 0) return 0;
        int groupCount = getGroupCount();
        int count = 0;
        for (int i = 0; i < groupCount; i++) {
            count += 1;
            if (count - 1 == position) return i;
            if (!getExpendState(i)) continue;
            count += getChildCount(i);
        }
        return -1;
    }

    /**
     * 获取RecyclerView的下标对应的条目的下标
     *
     * @param position 在整个RecyclerView中的下标
     */
    private int getItemPosition(int position) {
        if (position <= 0) return -1;
        int groupCount = getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            position -= 1;
            if (!getExpendState(i)) continue;
            int childCount = getChildCount(i);
            if (position < childCount) return position;
            position -= childCount;
        }
        return -1;
    }

    @Override
    public final int getItemViewType(int position) {
        if (position == 0) return TYPE_GROUP;
        int groupCount = getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            if (position == 0) return TYPE_GROUP;
            position--;
            if (getExpendState(i)) {
                int childCount = getChildCount(i);
                if (position < childCount) return TYPE_CHILD;
                position -= childCount;
            }
        }
        throw new IllegalArgumentException("计算ItemViewType出错了，所在下标：" + position);
    }

    /**
     * 视图点击事件
     */
    public interface OnListItemClickListener {
        /**
         * 父视图点击事件
         * @param groupPosition 当前组所在下标
         */
        void onGroupItemClickListener(int groupPosition);

        /**
         * 子视图点击事件
         * @param v 当前点击的{@link android.view.View}对象
         * @param groupPosition 当前子视图所有组列表的下标
         * @param childPosition 当前子视图所有子列表的下标
         */
        void onChildItemClickListener(View v, int groupPosition, int childPosition);
    }
}