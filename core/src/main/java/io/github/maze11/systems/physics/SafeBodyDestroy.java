package io.github.maze11.systems.physics;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Queue;
import java.util.Set;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;


public final class SafeBodyDestroy{

    /** Queue of bodies waiting for destruction */
    private static final Queue<Body> QUEUE = new ArrayDeque<>();

    /** Prevents double-enqueueing of single instances of a body */
    private static final Set<Body> SET = Collections.newSetFromMap(new IdentityHashMap<>());
    // IdentityHashMap to ensure uniqueness based on reference, not equals()

    private SafeBodyDestroy(){
        // Prevent instantiation
    }

    /**
     * If the body exists, enqueues it for destruction
     * @param body A physics body to destroy
     */
    public static void request(Body body){
        if (body == null) return;
        if (SET.add(body)){
            QUEUE.offer(body); // offer prevents IllegalStateException
        }
    }

    /**
     * Destroy all the entities added for destruction
     * @param defaultWorld In the error state that the body does not know its world, checks defaultWorld instead
     */
    public static void drain(World defaultWorld){
        Body body;
        while ((body = QUEUE.poll()) != null){
            SET.remove(body);
            try {
                // Get the world; fallback to default if null
                World world = body.getWorld() != null ? body.getWorld() : defaultWorld;
                if (world != null){
                    body.setUserData(null); // Clear user data to prevent dangling references
                    world.destroyBody(body);
                }

            } catch (Exception e) {
                // Log the exception without crashing the game
                System.err.println("[SafeBodyDestroy] Failed to destroy body: " + e.getMessage());
            }
        }
    }

    // Debug Methods

    /** Returns the number of bodies queued for destruction (DEBUG)
     * @return Number of bodies queued for destruction */
    public static int size(){
        return QUEUE.size();
    }

    /** Clears the queue and identity set (DEBUG) */
    public static void clear(){
        QUEUE.clear();
        SET.clear();
    }
}
