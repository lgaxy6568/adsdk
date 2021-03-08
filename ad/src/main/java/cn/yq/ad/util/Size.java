package cn.yq.ad.util;

/**
 * Created by liguo on 2019/1/28.
 * desc
 */
public class Size {
    private int width;
    private int height;
    private float density;
    public Size() {
    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
