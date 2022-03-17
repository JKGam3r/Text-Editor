package main.minigame.gameobjects;

import java.awt.*;

public abstract class GameObject {
    private boolean removable;

    private float width, height;

    private float x, y;

    public GameObject(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void removeObject() {
        removable = true;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public abstract void collisions();

    public Rectangle getBounds() {
        return new Rectangle((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
    }

    public abstract void render(Graphics2D graphics2D);

    public abstract void tick();
}
