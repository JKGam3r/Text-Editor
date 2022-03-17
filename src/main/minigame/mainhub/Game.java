package main.minigame.mainhub;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * The Main class for the game, in which all parts come together.
 *
 * The constructor should be called to start up the game.  Preferably,
 * this should be placed in the tabbed pane of the text editor.
 */
public class Game extends Canvas implements Runnable {
    private final Handler handler;

    private final Container parentContainer;

    private boolean running;

    private final Thread thread;

    private final int WIDTH, HEIGHT;

    private float zoomX, zoomY;

    public Game(Container parentContainer, int WIDTH, int HEIGHT) {
        this.parentContainer = parentContainer;
        this.WIDTH  = WIDTH;
        this.HEIGHT = HEIGHT;

        handler = new Handler();
        thread  = new Thread(this);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1_000_000_000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                matchGraphicsToScreenSize();
                tick();
                delta--;
            }
            if(running) {
                render();
            }
            frames++;
            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
    }

    public synchronized void start() {
        thread.start();
        running = true;
    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch(InterruptedException i) {
            i.printStackTrace();
        }
    }

    /**
     * Calculates how to properly scale and translate the graphics to fit the screen size
     */
    private void matchGraphicsToScreenSize() {
        zoomX = parentContainer.getWidth() / (float) WIDTH;
        zoomY = parentContainer.getHeight() / (float) HEIGHT;
    }

    private void tick() {
        handler.tick();
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();

        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics2D graphics2D = (Graphics2D) bs.getDrawGraphics();

        graphics2D.setColor(Color.black);
        graphics2D.fillRect(0, 0, WIDTH, HEIGHT);

        graphics2D.setColor(Color.white);
        graphics2D.fillOval(100, 100, 100, 100);

        handler.render(graphics2D);

        graphics2D.scale(zoomX, zoomY);

        bs.show();
        graphics2D.dispose();
    }
}
