package main.minigame.mainhub;

import main.minigame.gameobjects.GameObject;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

public class Handler {
    private final LinkedList<GameObject> gameObjects;

    // Holds the new game objects to add into the game
    private final LinkedList<GameObject> newGameObjects = new LinkedList<>();

    public Handler() {
        gameObjects = new LinkedList<>();
    }

    public void addGameObject(GameObject object) {
        newGameObjects.add(object);
    }

    public Iterator<GameObject> getIterator() {
        return gameObjects.iterator();
    }

    public void removeGameObject(GameObject object) {
        object.removeObject();
    }

    public void render(Graphics2D graphics2D) {
        for(GameObject object : gameObjects) {
            object.render(graphics2D);
        }
    }

    public void tick() {
        // Uses the list's iterator to traverse the game objects' tick methods
        Iterator<GameObject> objIterator = gameObjects.iterator();

        while(objIterator.hasNext()) {

            // The current game object being looked at
            GameObject gameObject = objIterator.next();

            // Removes the game object
            if(gameObject.isRemovable()) {
                objIterator.remove();
            }
            // Calls the game object's tick() method
            else
                gameObject.tick();
        }

        // Uses the list's iterator to add in all of the new game objects
        Iterator<GameObject> newObjIterator = newGameObjects.iterator();

        // Iterates over each of the new game objects to add into the game
        while(newObjIterator.hasNext()) {
            // The current game object being looked at
            GameObject gameObject = newObjIterator.next();

            gameObjects.add(gameObject);
            newObjIterator.remove();
        }
    }
}
