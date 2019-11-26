package br.fecap.wallbreaker.helpers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

public class Box implements ISprite {

    public PointF position;
    public float width;
    public float height;
    public int color;
    public Point direction = new Point(1, 1);

    public Box(PointF position, float width, float height, int color) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();

        paint.setColor(this.color);

        canvas.drawRect(this.getBounds(), paint);
    }

    public RectF getBounds() {
        return new RectF(
                this.position.x,
                this.position.y,
                this.position.x + this.width,
                this.position.y + this.height
                );
    }
}
