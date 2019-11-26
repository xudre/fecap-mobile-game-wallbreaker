package br.fecap.wallbreaker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import br.fecap.wallbreaker.helpers.ISprite;

public class Sprite implements ISprite {

    public PointF position = new PointF(0, 0);
    public Point direction = new Point(1, 1);
    public float width;
    public float height;
    private Bitmap texture;
    private Paint paint;

    private Rect textureSize;

    public Sprite(Bitmap texture, int width, int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.paint = new Paint();
        this.textureSize = new Rect(0, 0, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.texture, this.textureSize, this.getBounds(), this.paint);
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
