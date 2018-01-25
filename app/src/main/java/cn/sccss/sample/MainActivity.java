package cn.sccss.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.sccss.sample.entity.ChildInfo;
import cn.sccss.sample.entity.GroupInfo;
import cn.sccss.view.recycler.expandable.ExpandableRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements ExpandableRecyclerAdapter.OnListItemClickListener {

    private RecyclerView recyclerView;

    private SampleRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        init();
        initData();

    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        List<Pair<GroupInfo, List<ChildInfo>>> dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            GroupInfo data = new GroupInfo();
            data.id = i;
            data.name = String.format(Locale.getDefault(), "第%d条标题数据", i + 1);
            data.desc = "仅是一条简单的列表项";
            data.isExist = i % 3 == 0;
            List<ChildInfo> childList = new ArrayList<>();
            for (int j = 0; j < i + 1; j++) {
                ChildInfo child = new ChildInfo();
                child.id = j;
                child.itemName = String.format(Locale.getDefault(), "==>子项编号：%3d", i + 1);
                child.count = "0";
                childList.add(child);
            }

            dataList.add(Pair.create(data, childList));
        }

        adapter = new SampleRecyclerAdapter(dataList, this);
        recyclerView.setAdapter(adapter);

    }

    private void initView() {
        recyclerView = findViewById(R.id.list);
    }

    @Override
    public void onGroupItemClickListener(int groupPosition) {
        adapter.setExpendState(groupPosition, !adapter.getExpendState(groupPosition));
        //设置该字段是为了模拟业务场景，与功能实现无关
        adapter.getGroup(groupPosition).isExist = adapter.getExpendState(groupPosition);
        Toast.makeText(this, "点击了父条目，下标：" + groupPosition, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChildItemClickListener(View v, int groupPosition, int childPosition) {
        Toast.makeText(this, "点击了子条目，父下标:" + groupPosition + "， 子下标：" + childPosition, Toast.LENGTH_SHORT).show();
    }
}
