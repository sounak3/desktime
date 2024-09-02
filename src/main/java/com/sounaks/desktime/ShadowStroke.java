package com.sounaks.desktime;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public class ShadowStroke implements Stroke {
    private final BasicStroke stroke;
    private final double offset;

    public ShadowStroke(float width, double offset) {
        this.stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        this.offset = offset;
    }

    @Override
    public Shape createStrokedShape(Shape shape) {
        Shape strokedShape = stroke.createStrokedShape(shape);
        Area area = new Area();
        AffineTransform at = new AffineTransform();
        at.translate(offset, offset);
        area.add(new Area(at.createTransformedShape(strokedShape)));
        return area;
    }
}
