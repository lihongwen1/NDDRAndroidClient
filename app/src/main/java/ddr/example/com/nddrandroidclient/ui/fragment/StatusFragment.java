package ddr.example.com.nddrandroidclient.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.CollectingActivity;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.SwitchModeDialog;
import ddr.example.com.nddrandroidclient.widget.view.CircleBarView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.MapImageView;
import ddr.example.com.nddrandroidclient.widget.StatusSwitchButton;

/**
 * time: 2019/10/26
 * desc: 基础状态界面
 */
public final class StatusFragment extends DDRLazyFragment<HomeActivity>implements StatusSwitchButton.OnStatusSwitchListener,Animation.AnimationListener{

    @BindView(R.id.status_switch_bt)
    StatusSwitchButton statusSwitchButton;
    @BindView(R.id.circle)
    CircleBarView circleBarView;
    @BindView(R.id.iv_shrink)
    ImageView ivShrink;      //点击头部伸缩
    @BindView(R.id.shrink_tail_layout)
    RelativeLayout shrinkTailLayout; // 伸缩的尾部布局
    @BindView(R.id.shrink_layout)
    RelativeLayout shrinkLayout;
    @BindView(R.id.tv_ti_map)
    TextView tvTiMap;
    @BindView(R.id.tv_create_map)
    TextView tvCreateMap;
    @BindView(R.id.iv_map)
    MapImageView mapImageView;
    @BindView(R.id.tv_now_task)
    TextView tv_now_task;
    @BindView(R.id.tv_now_device)
    TextView tv_now_device;
    @BindView(R.id.tv_now_map)
    TextView tv_now_map;
    @BindView(R.id.tv_work_statue)
    TextView tv_work_statue;
    @BindView(R.id.tv_task_num)
    TextView tv_task_num;
    @BindView(R.id.tv_task_speed)
    TextView tv_task_speed;
    @BindView(R.id.tv_work_time)
    TextView tv_work_time;
    @BindView(R.id.rel_step_description)
    RelativeLayout rel_step_description;
    @BindView(R.id.recycle_gopoint)
    RecyclerView recycle_gopoint;
    @BindView(R.id.iv_cd_xs)
    ImageView iv_cd_xs;
    @BindView(R.id.iv_task_xl)
    ImageView iv_task_xl;
    @BindView(R.id.tv_set_go)
    TextView tv_set_go;
    @BindView(R.id.tv_restart_point)
    TextView tv_restart_point;

    @BindView(R.id.tv_switch_mode)
    TextView tv_switch_mode;


    private Animation hideAnimation;  //布局隐藏时的动画
    private Animation showAnimation;  // 布局显示时的动画效果

    private NotifyEnvInfo notifyEnvInfo;
    private NotifyBaseStatusEx notifyBaseStatusEx;

    private static String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";//特殊字符
    private TcpClient tcpClient;
    private int batteryNum;
    private String mapName;//地图名
    private String taskName;//任务名
    public static String robotID;//机器人ID
    private String workStatus; //工作状态
    private int taskNum;//运行次数
    private int workTimes; //工作时间
    private double taskSpeed; //工作速度
    private int lsNum; //临时任务次数
    private List<String> groupList=new ArrayList<>();
    private List<TargetPoint> targetPoints= new ArrayList<>();
    private TargetPointAdapter targetPointAdapter;
    private MapFileStatus mapFileStatus;
    private StringAdapter taskCheckAdapter;
    private CustomPopuWindow customPopWindow;
    //private StringAdapter robotIdAdapter;
    private RecyclerView  recycler_task_check;
    private List<TaskMode> taskModeList =new ArrayList<>();
    private int modeType;

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateBaseStatus:
                initStatusBar();
                break;
            case updateDDRVLNMap:
                Logger.e("------地图名："+mapFileStatus.getMapName()+"当前"+mapName);
                if (mapFileStatus.getMapName().equals(mapName)){
                    Logger.e("group列数"+groupList.size()+"列数1"+mapFileStatus.getTaskModes().size()+" -- "+mapFileStatus.getcTaskModes().size());
                    mapImageView.setMapBitmap(mapName);
                    tvTiMap.setVisibility(View.GONE);
                    tvCreateMap.setVisibility(View.GONE);
                    groupList = new ArrayList<>();
                    targetPoints=new ArrayList<>();
                    for (TaskMode taskMode:mapFileStatus.getcTaskModes()){
                        groupList.add(taskMode.getName());
                    }
                    taskCheckAdapter.setNewData(groupList);
                    targetPoints=mapFileStatus.getcTargetPoints();
                    targetPointAdapter.setNewData(targetPoints);
                    modeType=mapFileStatus.getCurrentMapEx().getBasedata().getAbNaviTypeValue();
                    Logger.e("---------模式:"+modeType);
                    if (modeType==1){
                        tv_switch_mode.setText("自主巡线模式");
                    }else if (modeType==2){
                        tv_switch_mode.setText("自主导航模式");
                    }else{
                        tv_switch_mode.setText("无当前运行地图");
                    }
                }
                break;
        }
    }

    public static StatusFragment newInstance(){
        return new StatusFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_status;
    }

    @Override
    protected void initView() {
        statusSwitchButton.setOnStatusListener(this);
        hideAnimation=AnimationUtils.loadAnimation(getAttachActivity(),R.anim.view_hide);
        showAnimation=AnimationUtils.loadAnimation(getAttachActivity(),R.anim.view_show);
        taskCheckAdapter=new StringAdapter(R.layout.item_recycle_task_check);
        targetPointAdapter=new TargetPointAdapter(R.layout.item_recycle_gopoint);
        @SuppressLint("WrongConstant")
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getAttachActivity(), 4, LinearLayoutManager.VERTICAL, false);
        recycle_gopoint.setLayoutManager(gridLayoutManager);
        recycle_gopoint.setAdapter(targetPointAdapter);
        onItemClick(2);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        taskModeList=mapFileStatus.getcTaskModes();
    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        taskModeList=mapFileStatus.getcTaskModes();
        taskName = notifyBaseStatusEx.getCurrpath();
        if (taskName!=null && !taskName.equals("PathError")){
            String showName=taskName.replaceAll("DDRTask_","");
            showName=showName.replaceAll(".task","");
            tv_now_task.setText(showName);
        }else {
            tv_now_task.setText("无任务");
        }

        for (int i=0;i<mapFileStatus.getcTaskModes().size();i++){
            groupList.add(mapFileStatus.getcTaskModes().get(i).getName());
            Logger.e("group列数"+groupList.size());
        }
        Logger.e("task列表"+groupList.size());
        taskCheckAdapter.setNewData(groupList);
        targetPointAdapter.setNewData(targetPoints);
    }

    /**
     * 获取机器人状态信息
     */
    private void initStatusBar() {
        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat format = new DecimalFormat("0.00");
        int h=60;
        int times=notifyBaseStatusEx.getTaskDuration();
        batteryNum=Integer.parseInt(df.format(notifyEnvInfo.getBatt()));
        mapName = notifyBaseStatusEx.getCurroute();
        taskNum=notifyBaseStatusEx.getTaskCount();
        workTimes=Integer.parseInt(df.format( times/h));
        taskSpeed=Double.parseDouble(format.format(notifyBaseStatusEx.getPosLinespeed()));
        String showName=mapName.replaceAll("OneRoute_","");
        tv_now_map.setText(showName);
//        Logger.e("次数"+taskNum+"时间"+workTimes+"速度"+taskSpeed);
        if (mapName!=null){
            rel_step_description.setVisibility(View.GONE);
            recycle_gopoint.setVisibility(View.VISIBLE);
            tv_set_go.setText("前往目标点");
        }else {
            rel_step_description.setVisibility(View.VISIBLE);
            recycle_gopoint.setVisibility(View.GONE);
            tv_set_go.setText("建立任务步骤：");
        }
        tv_now_device.setText(robotID);
        tv_task_num.setText(String.valueOf(taskNum)+" 次");
        tv_work_time.setText(String.valueOf(workTimes)+" 分");
        tv_task_speed.setText(String.valueOf(taskSpeed)+" m/s");
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                tv_work_statue.setText("自标定中");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        tv_work_statue.setText("待命中");
                        tv_now_task.setClickable(true);
                        tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
                        iv_task_xl.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        tv_work_statue.setText("运动中");
                        tv_now_task.setClickable(false);
                        tv_now_task.setBackgroundResource(0);
                        iv_task_xl.setVisibility(View.GONE);
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 3:
                                tv_work_statue.setText("异常");
                                break;
                            case 15:
                                tv_work_statue.setText("重定位中");
                                break;
                        }
                        break;
                }
                break;
        }


        if(notifyBaseStatusEx.isChargingStatus()) {
            iv_cd_xs.setImageResource(R.mipmap.cd_green);
            circleBarView.setProgress(batteryNum,0,Color.parseColor("#54E361"));
        }else {
            iv_cd_xs.setImageResource(R.mipmap.sd_def);
            circleBarView.setProgress(batteryNum,0,Color.parseColor("#0399FF"));
        }
    }

    /**
     * 将机器id赋值
     */
    public static void setRobotID(String robotid){
        String stringd="DDR";
        if(!Pattern.compile(regEx).matcher(robotid).find()&&!robotid.contains("BLANK")){
            robotID =robotid;
        }else {
            //Looper.prepare();
            //Toast.makeText(context,"机器目前为默认ID，请修改机器ID",Toast.LENGTH_SHORT).show();
            robotID=stringd+"001";
           // Looper.loop();
        }
    }
    /**
     * 路径选择弹窗
     * @param view
     */
    private void showTaskPopupWindow(View view) {
        Logger.e("---------showTaskPopupWindow");
        View contentView = null;
        contentView = getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.recycle_task, null);
        customPopWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                .setView(contentView)
                .enableOutsideTouchableDissmiss(false)
                .setClippingEnable(false)
                .create()
                .showAsDropDown(view, DpOrPxUtils.dip2px(getAttachActivity(), 0), 5);
        recycler_task_check =contentView.findViewById(R.id.recycler_task_check);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getAttachActivity());
        recycler_task_check.setLayoutManager(layoutManager);
        recycler_task_check.setAdapter(taskCheckAdapter);
        onItemClick(1);
        customPopWindow.setOutsideTouchListener(()->{
            Logger.e("点击外部已关闭");
            customPopWindow.dissmiss();
            tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
            iv_task_xl.setImageResource(R.mipmap.xlright_5);
        });
    }


    @OnClick({R.id.iv_shrink,R.id.tv_now_task,R.id.tv_create_map,R.id.tv_restart_point,R.id.tv_switch_mode})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_shrink:
                if (shrinkTailLayout.getVisibility()==View.VISIBLE){
                    shrinkLayout.startAnimation(hideAnimation);
                    hideAnimation.setAnimationListener(this);
                    ivShrink.setImageResource(R.mipmap.iv_shrink);
                }else if (shrinkTailLayout.getVisibility()==View.GONE){
                    shrinkTailLayout.setVisibility(View.VISIBLE);
                    shrinkLayout.startAnimation(showAnimation);
                    ivShrink.setImageResource(R.mipmap.iv_back);
                }
                break;
            case R.id.tv_now_task:
                showTaskPopupWindow(tv_now_task);
                tv_now_task.setBackgroundResource(R.drawable.task_check_bg);
                iv_task_xl.setImageResource(R.mipmap.xl_5);
                break;
            case R.id.tv_create_map:
                new InputDialog.Builder(getAttachActivity())
                        .setTitle("采集地图")
                        .setHint("输入地图名称")
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if (!content.isEmpty()){
                                    content=content.replaceAll(" ","");
                                    String name="OneRoute_"+content;
                                    BaseCmd.reqCmdStartActionMode reqCmdStartActionMode=BaseCmd.reqCmdStartActionMode.newBuilder()
                                            .setMode(BaseCmd.eCmdActionMode.eRec)
                                            .setRouteName(ByteString.copyFromUtf8(name))
                                            .build();
                                    tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eLSMSlamNavigation),reqCmdStartActionMode);
                                    startActivity(CollectingActivity.class);
                                }else {
                                    toast("请输入地图名字");
                                }
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消新建地图");
                            }
                        }).show();
                break;
            case R.id.tv_restart_point:
                float theat= (float) 1.0;
                float x= (float) 1.0;
                float y= (float) 1.0;
                new InputDialog.Builder(getAttachActivity())
                        .setEditVisibility(View.GONE)
                        .setTitle("是否恢复任务")
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                mapImageView.clearDraw();
                                goPointLet(x,y,theat,ByteString.copyFromUtf8("one"),ByteString.copyFromUtf8(mapName),2);
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消恢复任务");
                            }
                        }).show();

                break;
            case R.id.tv_switch_mode:
                new SwitchModeDialog.Builder(getAttachActivity())
                        .setModeType(modeType)
                        .setGravity(Gravity.CENTER)
                        .setListener(new SwitchModeDialog.OnListener() {
                            @Override
                            public void onConfirm(int type) {
                                modeType=type;
                                if (modeType==1){
                                    tv_switch_mode.setText("自主巡线模式");
                                }else if (modeType==2){
                                    tv_switch_mode.setText("自主导航模式");
                                }else{
                                    tv_switch_mode.setText("无当前运行地图");
                                }
                                tcpClient.saveDataToServer(modeType);
                                Logger.e("-----------"+mapFileStatus.getCurrentMapEx().getBasedata().getAbNaviTypeValue());
                            }
                        }).show();
                break;
        }
    }

    /**
     * 机器人暂停/重新运动
     * @param value
     */
    private void pauseOrResume(String value){
        BaseCmd.reqCmdPauseResume reqCmdPauseResume=BaseCmd.reqCmdPauseResume.newBuilder()
                .setError(value)
                .build();
        BaseCmd.CommonHeader commonHeader=BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader,reqCmdPauseResume);
        Logger.e("机器人暂停/重新运动");
    }

    /**
     * 添加或删除临时任务
     * @param routeName
     * @param taskName
     * @param num
     * @param type
     */

    private void addOrDetTemporary(ByteString routeName, ByteString taskName,int num,int type){
        DDRVLNMap.eTaskOperationalType eTaskOperationalType;
        switch (type){
            case 0:
                 eTaskOperationalType=DDRVLNMap.eTaskOperationalType.eTaskOperationalError;
                break;
            case 1:
                 eTaskOperationalType=DDRVLNMap.eTaskOperationalType.eTaskOperationalStopTask;
                break;
            case 2:
                 eTaskOperationalType=DDRVLNMap.eTaskOperationalType.eTaskOperationalAddTemporary;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        DDRVLNMap.reqTaskOperational.OptItem optItem= DDRVLNMap.reqTaskOperational.OptItem.newBuilder()
                .setOnerouteName(routeName)
                .setTaskName(taskName)
                .setRunCount(num)
                .setType(eTaskOperationalType)
                .build();
        DDRVLNMap.reqTaskOperational reqTaskOperational=DDRVLNMap.reqTaskOperational.newBuilder()
                .setOptSet(optItem)
                .build();
        tcpClient.sendData(null,reqTaskOperational);
    }

    /**
     * 导航去目标点或者恢复
     * @param x
     * @param y
     * @param theta
     * @param pname
     * @param routeName
     */

    private void goPointLet(float x,float y,float theta,ByteString pname, ByteString routeName,int type){
        DDRVLNMap.eRunSpecificPointType eRunSpecificPointTyp;
        switch (type){
            case 1:
                eRunSpecificPointTyp=DDRVLNMap.eRunSpecificPointType.eRunSpecificPointTypeAdd;
                break;
            case 2:
                eRunSpecificPointTyp=DDRVLNMap.eRunSpecificPointType.eRunSpecificPointTypeResume;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        DDRVLNMap.space_pointEx space_pointEx=DDRVLNMap.space_pointEx.newBuilder()
                .setX(x)
                .setY(y)
                .setTheta(theta)
                .build();
        DDRVLNMap.targetPtItem targetPtItem=DDRVLNMap.targetPtItem.newBuilder()
                .setPtName(pname)
                .setPtData(space_pointEx)
                .build();
        List<DDRVLNMap.targetPtItem> targetPtItemList=new ArrayList<>();
        targetPtItemList.add(targetPtItem);
        DDRVLNMap.reqRunSpecificPoint reqRunSpecificPoint=DDRVLNMap.reqRunSpecificPoint.newBuilder()
                .setOnerouteName(routeName)
                .addAllTargetPt(targetPtItemList)
                .setBIsDynamicOA(true)
                .setOptType(eRunSpecificPointTyp)
                .build();
        tcpClient.sendData(null,reqRunSpecificPoint);

    }



    public void onItemClick(int type){
        switch (type){
            case 1:
                //任务列表点击事件
                Logger.e("task列表"+groupList.size());
                // Java 8 新特性 Lambda表达式，原来写法即下方注释
                taskCheckAdapter.setOnItemClickListener((adapter, view, position) ->  {
                    new InputDialog.Builder(getAttachActivity())
                            .setTitle("请输入循环次数")
                            .setHint("1")
                            .setConfirm("执行")
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    taskName=groupList.get(position);
                                    String showName=taskName.replaceAll("DDRTask_","");
                                    showName=showName.replaceAll(".task","");
                                    tv_now_task.setText(showName);
                                    mapImageView.setTaskName(taskName);
                                    if (!content.isEmpty() && Integer.parseInt(content)>0 && Integer.parseInt(content)<999 ){
                                        try {
                                            lsNum=Integer.parseInt(content);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }else {
                                        lsNum=1;
                                    }
                                    addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2);
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast("取消添加");
                                }
                            }).show();
                    customPopWindow.dissmiss();
                    tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
                    iv_task_xl.setImageResource(R.mipmap.xlright_5);
                });
                break;
            case 2:
                //标记点列表点击事件
                targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {
                    float x=targetPoints.get(position).getX();
                    float y=targetPoints.get(position).getY();
                    float theta=targetPoints.get(position).getTheta();
                    mapImageView.setTargetPoint(targetPoints.get(position));
                                    Logger.e("当前点的名字"+targetPoints.get(position).getName());
                                    new InputDialog.Builder(getAttachActivity()).setEditVisibility(View.GONE)
                                            .setTitle("是否前往"+targetPoints.get(position).getName())
                                            .setListener(new InputDialog.OnListener() {
                                                @Override
                                                public void onConfirm(BaseDialog dialog, String content) {
                                                    goPointLet(x,y,theta,ByteString.copyFromUtf8(targetPoints.get(position).getName()),ByteString.copyFromUtf8(mapName),1);
                                                    for (int i=0;i<targetPoints.size();i++){
                                                        targetPoints.get(i).setSelected(false);
                                                    }
                                                    targetPoints.get(position).setSelected(true);
                                                    targetPointAdapter.setNewData(targetPoints);
                                                }

                                                @Override
                                                public void onCancel(BaseDialog dialog) {
                                                    toast("取消去目标点");
                                                }
                                            })
                                            .show();
                });
                break;
        }

    }
    @Override
    public void onLeftClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast("请稍等，正在自标定");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
//                        sendModel(BaseCmd.eCmdActionMode.eAutoDynamic);
                        //Logger.e("待命模式" + modeView.getText());
                        Logger.e("----mapName:"+mapName+"taskName:"+taskName);
                        if (mapName!=null && taskName!=null && !taskName.equals("PathError")){
                            toast("请稍等，正在进入");
                            addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2);
                        }else {
                            toast("请先建立任务");
                        }
                        break;
                    case 3:
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                toast("正在执行中");
                                break;
                            case 17:
                                toast("开始");
                                pauseOrResume("Resume");
                                break;
                        }
                        break;
                }
                break;
        }


    }

    @Override
    public void onCentreClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast("请稍等，正在自标定");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        toast("正在待命，请先进入执行状态");
                        break;
                    case 3:
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                toast("暂停");
                                pauseOrResume("Pause");
                                break;
                            case 17:
                                toast("暂停状态中");
                                break;
                        }
                        break;
                }
                break;
        }

    }

    @Override
    public void onRightClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast("请稍等，正在自标定");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        toast("正在待命中");
                        break;
                    case 3:
                        toast("请稍等，正在退出");
//                        exitModel();
                        addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,1);
                        break;
                }
                break;
        }

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        shrinkTailLayout.setVisibility(View.GONE);
        Logger.e("----动画效果结束");
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            statusSwitchButton.onDestroy();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    } 




}
