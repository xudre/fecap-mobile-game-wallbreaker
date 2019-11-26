package br.fecap.wallbreaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import br.fecap.wallbreaker.helpers.ISprite;
import br.fecap.wallbreaker.helpers.ITouchable;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    final public int MAX_SPRITES = 200;
    final public int MAX_LOGIC = 100;

    public int width;
    public int height;

    private SurfaceHolder surfaceHolder;

    private boolean active;
    private boolean ready;

    private Thread gameLoopThread;
    private Thread renderLoopThread;

    public ISprite[] spritesPool = new ISprite[MAX_SPRITES];
    private Runnable[] logicPool = new Runnable[MAX_LOGIC];

    public GameView(Context context) {
        super(context);

        this.setupView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setupView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setupView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.setupView();
    }

    /*
     * Inicializa os loops de jogo.
     */
    public void start() {
        this.active = true;

        this.setupGameLoop();
        this.setupRenderLoop();
    }

    public int addSprite(ISprite item) {
        int index = -1;

        for (int i = 0; i < MAX_SPRITES; i++) {
            if (this.spritesPool[i] != null) continue;

            this.spritesPool[i] = item;

            index = i;

            break;
        }

        return index;
    }

    public int addLogic(Runnable logic) {
        int index = -1;

        for (int i = 0; i < MAX_LOGIC; i++) {
            if (this.logicPool[i] != null) continue;

            this.logicPool[i] = logic;

            index = i;

            break;
        }

        return index;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.ready = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.ready = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (int i = 0; i < MAX_SPRITES; i++) {
            if (spritesPool[i] == null) continue;

            if (spritesPool[i] instanceof ITouchable == false) continue;

            ITouchable elemento = (ITouchable) spritesPool[i];

            boolean foiToque = elemento.touch(event);

            if (foiToque) {
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    private void setupView() {
        if (this.surfaceHolder == null) {
            this.surfaceHolder = this.getHolder();

            this.surfaceHolder.addCallback(this);
        }

        // Habilita utilizarmos pixels transparents:
        this.surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    private void setupGameLoop() {
        this.gameLoopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    // Game loop:
                    for (int i = 0; i < MAX_LOGIC; i++) {
                        if (logicPool[i] == null) continue;

                        logicPool[i].run();
                    }

                    try {
                        Thread.sleep(13, 600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (active);
            }
        });

        this.gameLoopThread.setName("Logic");

        this.gameLoopThread.start();
    }

    private void setupRenderLoop() {
        this.renderLoopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    if (ready == false) continue;

                    Canvas canvas = surfaceHolder.lockCanvas();

                    canvas.drawColor(Color.WHITE);

                    for (int i = 0; i < MAX_SPRITES; i++) {
                        if (spritesPool[i] == null) continue;

                        spritesPool[i].draw(canvas);
                    }

                    surfaceHolder.unlockCanvasAndPost(canvas);
                } while (active);
            }
        });

        this.renderLoopThread.setName("Render");

        this.renderLoopThread.start();
    }
}
