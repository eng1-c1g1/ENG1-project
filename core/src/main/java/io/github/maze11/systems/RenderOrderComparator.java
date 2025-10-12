package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;

import java.util.Comparator;

/**
 * Comparator used for sorting entities by rendering order. Sorts by RenderLayer initially,
 * then renders based off y coordinate in case of a tie.
 * Lower y are rendered last to achieve top-down illusion of depth.
 */
public class RenderOrderComparator implements Comparator<Entity> {
    private final ComponentMapper<TransformComponent> transM;
    private final ComponentMapper<SpriteComponent> spriteM;

    public RenderOrderComparator() {
        transM = ComponentMapper.getFor(TransformComponent.class);
        spriteM = ComponentMapper.getFor(SpriteComponent.class);
    }

    @Override
    public int compare(Entity e1, Entity e2) {
        //gets the layers as ints so that they can be compared by magnitude
        int sprite1Layer = spriteM.get(e1).renderLayer.ordinal();
        int sprite2Layer = spriteM.get(e2).renderLayer.ordinal();

        //compares the render layers: anything on lower layers is rendered before higher layers
        if (sprite1Layer < sprite2Layer) {
            return -1;
        }
        if (sprite1Layer > sprite2Layer) {
            return 1;
        }
        else {
            //if the layers are the same, compare by y position
            float y1 = transM.get(e1).position.y;
            float y2 = transM.get(e2).position.y;

            //The expression says (y2, y1) instead of the other way around since objects lower down are rendered last
            return Float.compare(y2, y1);

        }
    }
}
