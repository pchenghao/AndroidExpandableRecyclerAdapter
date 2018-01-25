package cn.sccss.sample;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import cn.sccss.sample.entity.ChildInfo;
import cn.sccss.sample.entity.GroupInfo;
import cn.sccss.view.recycler.expandable.ExpandableRecyclerAdapter;

/**
 * Created by SCCSS on 2018/1/25 0025.
 * @author SCCSS
 */

public class SampleRecyclerAdapter extends ExpandableRecyclerAdapter<SampleRecyclerAdapter.GroupViewHolder, SampleRecyclerAdapter.ChildViewHolder> {

    private List<Pair<GroupInfo, List<ChildInfo>>> dataList;
    private OnListItemClickListener onListItemClickListener;

    SampleRecyclerAdapter(List<Pair<GroupInfo, List<ChildInfo>>> dataList, OnListItemClickListener onListItemClickListener) {
        this.dataList = dataList;
        this.onListItemClickListener = onListItemClickListener;
        initData();
    }

    private void initData() {
        if (this.dataList == null) return;
        for (int i = 0; i < this.dataList.size(); i++) {
            setExpendStateNoRefresh(i, this.dataList.get(i).first.isExist);
        }
    }

    @Override
    public GroupInfo getGroup(int groupPosition) {
        return dataList.get(groupPosition).first;
    }

    @Override
    public ChildInfo getChild(int groupPosition, int itemPosition) {
        return dataList.get(groupPosition).second.get(itemPosition);
    }

    @Override
    public int getGroupCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        List<?> itemList = dataList.get(groupPosition) == null ? null  : dataList.get(groupPosition).second;
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup parent) {
        return new GroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample_group, parent, false));
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent) {
        return new ChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample_item, parent, false));
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder holder, int groupPosition) {
        GroupInfo data = getGroup(groupPosition);
        holder.tvTitle.setText(data.name);
        holder.tvDesc.setText(data.desc);
        holder.swExist.setOnCheckedChangeListener(null);
        holder.swExist.setChecked(data.isExist);
        holder.itemView.setOnClickListener((v) -> {
            if (this.onListItemClickListener != null)
                onListItemClickListener.onGroupItemClickListener(groupPosition);
        });
        
        holder.swExist.setOnCheckedChangeListener((v, isChecked) -> {
            setExpendState(groupPosition, isChecked);
            data.isExist = isChecked;
        });
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, int groupPosition, int childPosition) {
        ChildInfo data = getChild(groupPosition, childPosition);
        holder.tvTitle.setText(data.itemName);
        holder.etNum.setText(data.count);
        holder.etNum.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                data.count = holder.etNum.getText().toString();
            }
        });
        holder.itemView.setOnClickListener((v) -> {
            if (this.onListItemClickListener != null)
                onListItemClickListener.onChildItemClickListener(v, groupPosition, childPosition);
        });
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvDesc;
        private Switch swExist;

        GroupViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            swExist = itemView.findViewById(R.id.sw_exist);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private EditText etNum;

        ChildViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            etNum = itemView.findViewById(R.id.et_number);
        }
    }
}
