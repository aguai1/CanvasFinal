package com.aguai.canvaswrap;

import com.aguai.canvaswrap.shape.AbsShape;

import java.util.List;


/**
 *  Created by Aguai on 2016/11/20.
 *  SfDisplayInfoView功能
 */
public interface IDisplay {
    /**
     * 增加一个对象
     */
    void addShape(AbsShape shape);

    /**
     * 批量增加
     */
    void addShapes(List<AbsShape> shapes);


    void removeAllShape();

    /**
     * 移动shape
     */
    void moveShape(AbsShape absShape, int x, int y);

    void refresh();

}
