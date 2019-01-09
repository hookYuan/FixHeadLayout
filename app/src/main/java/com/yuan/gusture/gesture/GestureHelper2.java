package com.yuan.gusture.gesture;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 常见手势判断：
 * 1.点击
 * 2.双击
 * 3.长按
 * 4.左滑
 * 5.右滑
 * 6.上滑
 * 7.下滑
 * 8.飞速滑动
 * 9.取消
 * 10.缩放手势
 *
 * @author yuanye
 * @date 2019/1/4
 */
public class GestureHelper2 {

    private final static String TAG = "yuanye";
    /**
     * 设置按下监听
     */
    private OnDownListener onDownListener;

    /**
     * 设置按压监听
     */
    private OnShowPressListener onShowPressListener;

    /**
     * 设置单次点击监听
     */
    private OnSingleTapUpListener onSingleTapUpListener;
    /**
     * 设置左滑监听
     */
    private OnScrollLeftListener onScrollLeftListener;
    /**
     * 设置上滑监听
     */
    private OnScrollTopListener onScrollTopListener;

    /**
     * 设置右滑监听
     */
    private OnScrollRightListener onScrollRightListener;

    /**
     * 设置下滑监听
     */
    private OnScrollBottomListener onScrollBottomListener;

    /**
     * 设置长按监听
     */
    private OnLongPressListener onLongPressListener;

    /**
     * 快速滑动监听
     */
    private OnFlingListener onFlingListener;

    /**
     * 双击之间的最长时间间隔 ，单位毫秒
     */
    private long doubleClickTime = 2000;

    /**
     * 长按时间间隔，单位毫秒
     */
    private long longClickTime = 2000;


    /**
     * 上一次的坐标点
     */
    private List<TouchPoint> lastPoint;


    public GestureHelper2() {
        lastPoint = new ArrayList<>();
    }

    public boolean onEvent(MotionEvent motionEvent) {
        //手指的下标
        int index = motionEvent.getActionIndex();
        int pointerId = motionEvent.getPointerId(index);

        switch (motionEvent.getActionMasked()) {
            //第一个手指按下
            case MotionEvent.ACTION_DOWN:
                TouchPoint point = new TouchPoint(motionEvent.getX(), motionEvent.getY()
                        , pointerId);
                point.setDownX(motionEvent.getX());
                point.setDownY(motionEvent.getY());
                lastPoint.add(point);
                break;
            //最后一个手指离开
            case MotionEvent.ACTION_UP:
                lastPoint.clear();
                break;
            //非第一个手指按下
            case MotionEvent.ACTION_POINTER_DOWN:
                //添加按下状态
                TouchPoint point2 = new TouchPoint(motionEvent.getX(), motionEvent.getY()
                        , pointerId);
                point2.setDownX(motionEvent.getX());
                point2.setDownY(motionEvent.getY());
                lastPoint.add(point2);
                break;
            //非第一个手指离开
            case MotionEvent.ACTION_POINTER_UP:
                //操作完成后，移除手指坐标
                lastPoint.remove(getTouchPointForPointIndex(lastPoint, pointerId));
                break;
            //移动
            case MotionEvent.ACTION_MOVE:
                moveOrientation(motionEvent);
                //更新坐标点,保证lastPoint是最新的
                for (TouchPoint point1 : lastPoint) {
                    point1.setPointX(motionEvent.getX(motionEvent.findPointerIndex(point1.getPointerId())));
                    point1.setPointY(motionEvent.getY(motionEvent.findPointerIndex(point1.getPointerId())));
                }
                break;
            //取消
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }

    /***
     * 判断移动方向，主要判断上下左右移动，判断依据：
     * 1.多指移动时，每个手指的移动方向必须一致;
     * 2.移动角度误差不操作#deviation
     *
     */
    private void moveOrientation(MotionEvent event) {
        
        float distanceX = 0;
        float distanceY = 0;

        double firstAngle = 0;
        //误差角度，默认15°
        float deviation = 15f;
        /**
         * 偏转因子
         * transformFactor>0,transformFactor越小越灵敏，最小0
         */
        int transformFactor = 80;
        
        for (int i = 0; i < lastPoint.size(); i++) {
            int id = lastPoint.get(i).getPointerId();
            int index = event.findPointerIndex(id);
            float diffX = event.getX(index) - lastPoint.get(i).getPointX();
            float diffY = event.getY(index) - lastPoint.get(i).getPointY();

            if (i == 0) {
                distanceX = diffX;
                distanceY = diffY;

                firstAngle = getAngle(lastPoint.get(i).getPointX(), lastPoint.get(i).getPointY()
                        , event.getX(index), event.getY(index));

            } else {
                //移动方向不一致时返回
                if (distanceX * diffX < 0 || distanceY * diffY < 0) {
                    return;
                }
                double nowAngle = getAngle(lastPoint.get(i).getPointX(), lastPoint.get(i).getPointY()
                        , event.getX(index), event.getY(index));
                //多指角度偏差过大时返回
                if (Math.abs(firstAngle - nowAngle) > deviation) {
                    return;
                }
            }
        }
        //判断上下左右滑动
        if (Math.abs(distanceX) - Math.abs(distanceY) >= transformFactor && distanceX < 0) {
            //向左滑动
            Log.i(TAG,"向左滑动");

        } else if (Math.abs(distanceX) <= Math.abs(distanceY) - transformFactor && distanceY < 0) {
            //向上滑动
            Log.i(TAG,"向上滑动");

        } else if (Math.abs(distanceX) - Math.abs(distanceY) >= transformFactor && distanceX > 0) {
            //向右滑动
            Log.i(TAG,"向右滑动");

        } else if (Math.abs(distanceX) <= Math.abs(distanceY) - transformFactor && distanceY > 0) {
            //向下滑动
            Log.i(TAG,"向下滑动");

        }
    }


    /**
     * 求两点连线的角度
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double getAngle(double x1, double y1, double x2, double y2) {
        double y = Math.sin(y2 - y1) * Math.cos(x2);
        double x = Math.cos(x1) * Math.sin(x2) - Math.sin(x1) * Math.cos(x2) * Math.cos(y2 - y1);
        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        if (brng < 0)
            brng = brng + 360;
        return brng;

    }

    /**
     * 根据手指Index寻找手指
     *
     * @param pointerId
     * @return
     */
    private TouchPoint getTouchPointForPointIndex(List<TouchPoint> list, int pointerId) {
        for (TouchPoint point : list) {
            if (point.getPointerId() == pointerId) {
                return point;
            }
        }
        return null;
    }

    /**
     * @author yuanye
     * @date 2018/12/25
     */
    public interface OnDownListener {
        /**
         * 按下的时候触发，每次该方法都是最先触发
         *
         * @param e 按下点
         * @return 是否向下传递，true继续传递
         */
        boolean onDown(MotionEvent e);
    }

    /**
     * @author yuanye
     * @date 2018/12/25
     */
    public interface OnShowPressListener {
        /**
         * 按压了但是未移动或抬起
         *
         * @param e 当前点
         */
        void onShowPress(MotionEvent e);
    }

    /**
     * @author yuanye
     * @date 2018/12/25
     */
    public interface OnSingleTapUpListener {

        /**
         * 单次点击的时候
         *
         * @param e 当前点
         * @return 是否拦截事件
         */
        boolean onSingleTapUp(MotionEvent e);
    }

    /**
     * @author yuanye
     * @date 2018/12/25
     */
    public interface OnScrollLeftListener {
        /**
         * 向左滑动
         *
         * @param downEvent    按下点
         * @param currentEvent 当前点
         * @param distanceX    x方向多点的平均滑动距离
         * @param distanceY    y方向多点的平均滑动距离
         */
        boolean onScrollLeft(MotionEvent downEvent, MotionEvent currentEvent, float distanceX, float distanceY);

    }

    /**
     * @author yuanye
     * @date 2018/12/25
     */
    public interface OnScrollTopListener {
        /**
         * 向上滑动
         *
         * @param downEvent    按下点
         * @param currentEvent 当前点
         * @param distanceX    x方向多点的平均滑动距离
         * @param distanceY    y方向多点的平均滑动距离
         */
        boolean onScrollTop(MotionEvent downEvent, MotionEvent currentEvent, float distanceX, float distanceY);
    }

    public interface OnScrollRightListener {
        /**
         * 向右滑动
         *
         * @param downEvent    按下点
         * @param currentEvent 当前点
         * @param distanceX    x方向多点的平均滑动距离
         * @param distanceY    y方向多点的平均滑动距离
         */
        boolean onScrollRight(MotionEvent downEvent, MotionEvent currentEvent, float distanceX, float distanceY);
    }

    public interface OnScrollBottomListener {
        /**
         * 向下滑动
         *
         * @param downEvent    按下点
         * @param currentEvent 当前点
         * @param distanceX    x方向多点的平均滑动距离
         * @param distanceY    y方向多点的平均滑动距离
         */
        boolean onScrollBottom(MotionEvent downEvent, MotionEvent currentEvent, float distanceX, float distanceY);
    }

    public interface OnLongPressListener {
        /**
         * 长按事件
         *
         * @param e 当前点
         */
        void onLongPress(MotionEvent e);
    }

    public interface OnFlingListener {
        /**
         * 快速滑动时
         *
         * @param e1        按下点
         * @param e2        当前点
         * @param velocityX x方向速度
         * @param velocityY y方向速度
         */
        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }


    private class TouchPoint {

        private float downX;

        private float downY;

        private float pointX;

        private float pointY;
        /**
         * Id唯一标识
         */
        private int pointerId;

        public TouchPoint(float pointX, float pointY) {
            this.pointX = pointX;
            this.pointY = pointY;
        }

        public TouchPoint(float pointX, float pointY, int pointerId) {
            this.pointX = pointX;
            this.pointY = pointY;
            this.pointerId = pointerId;
        }

        public int getPointerId() {
            return pointerId;
        }

        public void setPointerId(int pointerId) {
            this.pointerId = pointerId;
        }

        public float getPointX() {
            return pointX;
        }

        public void setPointX(float pointX) {
            this.pointX = pointX;
        }

        public float getPointY() {
            return pointY;
        }

        public void setPointY(float pointY) {
            this.pointY = pointY;
        }

        public float getDownX() {
            return downX;
        }

        public void setDownX(float downX) {
            this.downX = downX;
        }

        public float getDownY() {
            return downY;
        }

        public void setDownY(float downY) {
            this.downY = downY;
        }

        @Override
        public String toString() {
            return "TouchPoint{" +
                    "pointX=" + pointX +
                    ", pointY=" + pointY +
                    ", pointerId=" + pointerId +
                    '}';
        }
    }
}
