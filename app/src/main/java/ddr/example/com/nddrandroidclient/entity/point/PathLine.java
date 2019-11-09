package ddr.example.com.nddrandroidclient.entity.point;

import java.util.List;

/**
 * time：2019/11/8
 * desc: 路径的数据类型
 */
public class PathLine extends BaseMode {
    private String name;
    private float velocity;    //速度
    private int pathModel;     // 路径模式  64 动态避障； 65 静态避障 ；66 贴边行驶路径（牛棚）
    private String config;    // 配置文件
    private List<PathPoint>pathPoints;

    public PathLine(int type) {
        super(type);
    }
    public PathLine(){
        super();

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public int getPathModel() {
        return pathModel;
    }

    public void setPathModel(int pathModel) {
        this.pathModel = pathModel;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public List<PathPoint> getPathPoints() {
        return pathPoints;
    }

    public void setPathPoints(List<PathPoint> pathPoints) {
        this.pathPoints = pathPoints;
    }

    /**
     * 路径的组成点
     */
    public class PathPoint{
        private float x;
        private float y;
        private int pointType;  // 点的类型 动作属性
        private float rotationAngle;  //旋转角度
        private String config;        //配置文件

        public PathPoint(float x,float y,int pointType) {
            this.x=x;
            this.y=y;
            this.pointType=pointType;
        }

        public PathPoint() {

        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public int getPointType() {
            return pointType;
        }

        public void setPointType(int pointType) {
            this.pointType = pointType;
        }

        public float getRotationAngle() {
            return rotationAngle;
        }

        public void setRotationAngle(float rotationAngle) {
            this.rotationAngle = rotationAngle;
        }

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }
    }
}
