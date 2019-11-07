package ddr.example.com.nddrandroidclient.ui.adapter;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import DDRVLNMapProto.DDRVLNMap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.TargetPoint;

/**
 * time: 2019/11/7
 * desc: 标记点列表适配器
 */
public class TargetPointAdapter extends BaseAdapter<TargetPoint> {

    public TargetPointAdapter(int layoutResId) {
        super(layoutResId);
    }

    public TargetPointAdapter(int layoutResId, @Nullable List<TargetPoint> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper,TargetPoint item) {
        super.convert(helper, item);
    }

    @Override
    public void setNewData(@Nullable List<TargetPoint> data) {
        super.setNewData(data);
    }
}