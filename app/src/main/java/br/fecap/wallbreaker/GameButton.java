package br.fecap.wallbreaker;

import android.graphics.Bitmap;
import android.view.MotionEvent;

import br.fecap.wallbreaker.helpers.ITouchable;

public class GameButton extends Sprite implements ITouchable {

    public boolean isPressed;

    public GameButton(Bitmap texture, int width, int height) {
        super(texture, width, height);
    }

    @Override
    public boolean touch(MotionEvent event) {
        if (getBounds().contains(event.getX(), event.getY())) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    this.isPressed = true;

                    return true;
                case MotionEvent.ACTION_UP:
                    this.isPressed = false;

                    return true;
            }
        }

        return false;
    }
}
