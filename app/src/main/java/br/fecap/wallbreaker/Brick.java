package br.fecap.wallbreaker;

import android.graphics.Bitmap;

public class Brick extends Sprite {
    public int points = 100;
    public boolean destroyed = false;
    private DestroyListener onDestroyListener;

    public interface DestroyListener {
        void didDestroy(Brick brick);
    }

    public Brick(Bitmap texture, int width, int height) {
        super(texture, width, height);
    }

    public void setOnDestroyListener(DestroyListener listener) {
        this.onDestroyListener = listener;
    }

    public void destroy() {
         this.destroyed = true;

         if (this.onDestroyListener != null) {
             this.onDestroyListener.didDestroy(this);
         }
    }
}
