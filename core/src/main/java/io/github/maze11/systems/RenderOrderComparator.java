package io.github.maze11.systems;

import com.badlogic.ashley.core.Entity;

import java.util.Comparator;

public class RenderOrderComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity e1, Entity e2) {
        throw new RuntimeException("Not implemented");
    }
}
