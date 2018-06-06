package com.aguai.stickerview.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.WindowManager;

import com.aguai.stickerview.PointD;

import java.util.List;


public class PointUtils {

    private static Point point = null;

    public static Point getDisplayWidthPixels(Context context) {
        if (point != null) {
            return point;
        }
        WindowManager wm = ((Activity) context).getWindowManager();
        point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point;
    }

    /**
     * 计算两点之间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double lineSpace(double x1, double y1, double x2, double y2) {
        double lineLength = 0;
        double x, y;
        x = x1 - x2;
        y = y1 - y2;
        lineLength = Math.sqrt(x * x + y * y);
        return lineLength;
    }

    /**
     * 获取线段中点坐标
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static PointD getMidpointCoordinate(double x1, double y1, double x2, double y2) {
        PointD midpoint = new PointD();
        midpoint.set((x1 + x2) / 2, (y1 + y2) / 2);
        return midpoint;
    }


    public static double pointToLine(float x1, float y1, float x2, float y2, float x0, float y0) {
        double space = 0;
        double a, b, c;
        a = lineSpace(x1, y1, x2, y2);// 线段的长度
        b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) {
            space = c;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }

    // 计算两点之间的距离
    private static double lineSpace(float x1, float y1, float x2, float y2) {
        double lineLength = 0;
        lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
                * (y1 - y2));
        return lineLength;
    }

    /**
     * 判断点是否在多边形内
     *
     * @param point 检测点
     * @param pts   多边形的顶点
     * @return 点在多边形内返回true, 否则返回false
     */
    public static boolean IsPtInPoly(PointD point, List<PointD> pts) {

        int N = pts.size();
        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;//cross points count of x
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        PointD p1, p2;//neighbour bound vertices
        PointD p = point; //当前点

        p1 = pts.get(0);//left vertex
        for (int i = 1; i <= N; ++i) {//check all rays
            if (p.equals(p1)) {
                return boundOrVertex;//p is an vertex
            }

            p2 = pts.get(i % N);//right vertex
            if (p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)) {//ray is outside of our interests
                p1 = p2;
                continue;//next ray left point
            }

            if (p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)) {//ray is crossing over by the algorithm (common part of)
                if (p.y <= Math.max(p1.y, p2.y)) {//x is before of ray
                    if (p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)) {//overlies on a horizontal ray
                        return boundOrVertex;
                    }

                    if (p1.y == p2.y) {//ray is vertical
                        if (p1.y == p.y) {//overlies on a vertical ray
                            return boundOrVertex;
                        } else {//before ray
                            ++intersectCount;
                        }
                    } else {//cross point on the left side
                        double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;//cross point of y
                        if (Math.abs(p.y - xinters) < precision) {//overlies on a ray
                            return boundOrVertex;
                        }

                        if (p.y < xinters) {//before ray
                            ++intersectCount;
                        }
                    }
                }
            } else {//special case when ray is crossing through the vertex
                if (p.x == p2.x && p.y <= p2.y) {//p crossing over p2
                    PointD p3 = pts.get((i + 1) % N); //next vertex
                    if (p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)) {//p.x lies between p1.x & p3.x
                        ++intersectCount;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;//next ray left point
        }

        return intersectCount % 2 != 0;

    }

    public static float angleComputation(float x1, float y1, float x2, float y2, float x3, float y3) {
        float v1_x = x2 - x1;
        float v1_y = y2 - y1;
        float v2_x = x3 - x1;
        float v2_y = y3 - y1;

        return (float) ((v1_x * v2_x + v1_y * v2_y) / (Math.sqrt(v1_x * v1_x + v1_y * v1_y) * Math.sqrt(v1_x * v1_x + v1_y * v1_y)));
    }

    public static PointF getPoint(float[] pts, int index) {
        if(pts == null) {
            return new PointF(-1, -1);
        }
        return new PointF(pts[index * 2], pts[index * 2 + 1]);
    }

    public static PointF getCenterPoint(PointF pt1, PointF pt2) {
        return new PointF((pt1.x + pt2.x) / 2, (pt1.y + pt2.y) / 2);
    }

    public static PointF getCenterPoint(float[] pts, int index1, int index2) {
        if(pts == null) {
            return new PointF(-1, -1);
        }
        return getCenterPoint(getPoint(pts, index1), getPoint(pts, index2));
    }

    public static float getPointDistanse(PointF pt1, PointF pt2) {
        return (float) Math.sqrt(Math.pow(pt1.x - pt2.x, 2) + Math.pow(pt1.y - pt2.y, 2));
    }

    public static PointF getSymmetricPoint(PointF basePoint, float A, float B, float C) {
        PointF symmetricPoint = new PointF();
        float k = -2 * (A * basePoint.x + B * basePoint.y + C) / (A*A + B*B);
        symmetricPoint.x = basePoint.x + k * A;
        symmetricPoint.y = basePoint.y + k * B;
        return symmetricPoint;
    }

    public static PointF getPedalPoint(PointF basePoint, float A, float B, float C) {
        PointF symmetricPoint = getSymmetricPoint(basePoint, A, B, C);
        // 对称点和原点的中点即垂足
        return getCenterPoint(symmetricPoint, basePoint);
    }
}
