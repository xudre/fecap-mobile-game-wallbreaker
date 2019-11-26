package br.fecap.wallbreaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import br.fecap.wallbreaker.helpers.Box;

public class MainActivity extends AppCompatActivity {

    protected GameView gameView;
    protected Box barrinha;
    protected Sprite esfera;
    protected GameButton controleEsquerdo;
    protected GameButton controleDireito;
    protected TextView scoreView;

    protected int score = 0;
    protected int lives = 5;

    // Flag para definir elementos de inicio do jogo:
    boolean hasSetup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        this.scoreView = findViewById(R.id.scoreView);

        // Verificando se a barra existe:
        if (this.getSupportActionBar() != null) {
            // Escondendo a barra da aplicação:
            this.getSupportActionBar().hide();
        }

        // Deixando a aplicação em tela cheia:
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Encontrar minha gameview no layout:
        this.gameView = findViewById(R.id.gameView);

        this.barrinha = new Box(new PointF(0, 0), 300, 50, Color.BLUE);
        
        this.esfera = new Sprite(
                BitmapFactory.decodeResource(getResources(), R.drawable.sphere),
                80, 80
        );

        this.controleEsquerdo = new GameButton(
                BitmapFactory.decodeResource(getResources(), R.drawable.control_left),
                200, 200
        );

        this.controleDireito = new GameButton(
                BitmapFactory.decodeResource(getResources(), R.drawable.control_right),
                200, 200
        );

        this.gameView.addSprite(this.esfera);
        this.gameView.addSprite(this.barrinha);
        this.gameView.addSprite(this.controleEsquerdo);
        this.gameView.addSprite(this.controleDireito);

        /* Lógica do jogo. */
        this.gameView.addLogic(new Runnable() {
            @Override
            public void run() {
                if (!hasSetup && gameView.width > 0) {
                    // Centralizar a barra na tela:
                    barrinha.position.x = (gameView.width - barrinha.width) / 2;

                    setupBricks();

                    hasSetup = true; // Afirmo que o setup já ocorreu.
                }

                // Controles:
                float espacamento = 20;
                float posicaoYControle = gameView.height - controleEsquerdo.height - espacamento;

                controleEsquerdo.position.x = espacamento;
                controleEsquerdo.position.y = posicaoYControle;

                controleDireito.position.x = gameView.width - controleDireito.width - espacamento;
                controleDireito.position.y = posicaoYControle;

                // Movimento a barrinha na horizontal:
                if (controleEsquerdo.isPressed) {
                    barrinha.position.x -= 10;
                }

                if (controleDireito.isPressed) {
                    barrinha.position.x += 10;
                }

                barrinha.position.y = gameView.height - barrinha.height;

                // Warp da barrinha:
                if (barrinha.position.x < -barrinha.width) {
                    barrinha.position.x = gameView.width;
                }

                if (barrinha.position.x > gameView.width) {
                    barrinha.position.x = -barrinha.width;
                }

                esfera.position.x += 5 * esfera.direction.x;
                esfera.position.y += 5 * esfera.direction.y;

                // Verificando a colisão com a barrinha:
                if (barrinha.getBounds().intersect(esfera.getBounds())) {
                    // Muda a direção horizontal da esfera dependendo do ponto de colisão na barra:
                    if (esfera.position.x + esfera.width / 2 < barrinha.position.x + barrinha.width / 2) {
                        esfera.direction.x = -1;
                    } else {
                        esfera.direction.x = 1;
                    }

                    esfera.direction.y = -1;
                }

                // Verificar a colisão com os tijolos:
                for (int i = 0; i < gameView.MAX_SPRITES; i++) {
                    // Verifico se o sprite no índice atual é da classe Brick
                    if (gameView.spritesPool[i] instanceof Brick) {
                        Brick tijolo = (Brick) gameView.spritesPool[i];

                        if (esfera.getBounds().intersect(tijolo.getBounds())) {
                            // A esfera colidiu com um tijolo.
                            tijolo.destroy();

                            gameView.spritesPool[i] = null;

                            // Verificar se colidiu com a parte inferior / superior do tijolo:
                            if (esfera.position.y > (tijolo.position.y + tijolo.height / 2)) {
                                // Colidiu na parte inferior do tijolo.
                                esfera.direction.y = 1;
                            } else {
                                // Colidiu na parte superior do tijolo.
                                esfera.direction.y = -1;
                            }

                            // Verificar se colidiu com a parte esquerda / direita do tijolo:
                            if (esfera.position.x > (tijolo.position.x + tijolo.width / 2)) {
                                // Colidiu na parte direita do tijolo.
                                esfera.direction.x = 1;
                            } else {
                                // Colidiu na parte esquerda do tijolo.
                                esfera.direction.x = -1;
                            }

                            break;
                        }
                    }
                }

                // Verificando a colisão com a parte inferior:
                if (esfera.position.y + esfera.height >= gameView.height) {
                    // Centraliza novamente a esfera no centro da tela:
                    esfera.position.x = (gameView.width - esfera.width) / 2;
                    esfera.position.y = (gameView.height - esfera.height) / 2;

                    esfera.direction.x *= -1; // Alterna a direção horizontal
                    esfera.direction.y = -1; // Garante o reinício dela para cima

                    // Perdeu vida bobão!
                    lives--;
                }

                // Verificando a colisão com a lateral direita:
                if (esfera.position.x + esfera.width >= gameView.width) {
                    esfera.direction.x = -1;
                }

                // Verificando a colisão com o topo da tela:
                if (esfera.position.y <= 0) {
                    esfera.direction.y = 1;
                }

                // Verificando a colisão com a lateral esquerda:
                if (esfera.position.x <= 0) {
                    esfera.direction.x = 1;
                }

                // Atualizando o placar na TextView:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scoreView.setText("" + score);
                    }
                });
            }
        });

        this.gameView.start();
    }

    protected void setupBricks() {
        int brickTotal = 45;
        int brickWidth = 120;
        int brickHeight = 60;
        int brickRow = 0;
        int brickCol = 0;

        // Observa quando um tijolo for destruído.
        Brick.DestroyListener destroyListener = new Brick.DestroyListener() {
            @Override
            public void didDestroy(Brick brick) {
                // Adiciona os pontos ao placar total:
                score += brick.points;
            }
        };

        // Obtem a imagem de textura do tijolo:
        Bitmap brickTexture = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.brick
        );

        for (int i = 0; i < brickTotal; i++) {
            Brick tijolo = new Brick(
                    brickTexture, brickWidth, brickHeight
            );

            // Vincula com o observador:
            tijolo.setOnDestroyListener(destroyListener);

            tijolo.position.x = brickWidth * brickCol;
            tijolo.position.y = brickHeight * brickRow;

            // Verifica se o próximo tijolo não passa da margem direita:
            if (tijolo.position.x + brickWidth > this.gameView.width) {
                brickRow = brickRow + 1;
                brickCol = 0;
            } else {
                brickCol = brickCol + 1;
            }

            this.gameView.addSprite(tijolo);
        }
    }
}
