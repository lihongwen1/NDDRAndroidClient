package ddr.example.com.nddrandroidclient.entity.point;

import java.io.Serializable;
import java.util.List;

import DDRVLNMapProto.DDRVLNMap;

/**
 * time : 2019/11/9
 * desc : 任务的数据结构
 */
public class TaskMode implements Serializable {
    private String name;
    private List<BaseMode>baseModes;          //这里面可能是路径也可能是目标点
    private int runCounts;                    //运行次数
    /*** 任务的执行时间*/
    private long startHour;            //开始的时间，时
    private long startMin;             //开始的时间，分
    private long endHour;             // ...
    private long endMin;              // ...

    private int taskState;                    //新增 任务状态  1-等待运行； 2-运行中； 3-暂停； 4-运行完了； 5-终止
    private int type;                        //新增 任务类型  0-不在执行队列中  1-临时任务  2-在队列中  3-临时任务+执行队列中


    public TaskMode() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BaseMode> getBaseModes() {
        return baseModes;
    }

    public void setBaseModes(List<BaseMode> baseModes) {
        this.baseModes = baseModes;
    }



    public int getRunCounts() {
        return runCounts;
    }

    public void setRunCounts(int runCounts) {
        this.runCounts = runCounts;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setTaskState(int taskState) {
        this.taskState = taskState;
    }

    public int getTaskState() {
        return taskState;
    }

    public void setEndHour(long endHour) {
        this.endHour = endHour;
    }

    public void setEndMin(long endMin) {
        this.endMin = endMin;
    }

    public void setStartHour(long startHour) {
        this.startHour = startHour;
    }

    public void setStartMin(long startMin) {
        this.startMin = startMin;
    }

    public long getEndHour() {
        return endHour;
    }

    public long getEndMin() {
        return endMin;
    }

    public long getStartHour() {
        return startHour;
    }

    public long getStartMin() {
        return startMin;
    }
}
