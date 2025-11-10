package io.github.maze11.systems.rendering;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.maze11.components.CameraFollowComponent;
import io.github.maze11.components.TransformComponent;

import java.util.ArrayList;
import java.util.List;

public class WorldCameraSystem extends IteratingSystem {
    private OrthographicCamera camera;
    private SpriteBatch batch;

    /**
     * Temporary list containing all the active target positions registered this frame
     */
    private List<Vector2> targetPositions = new ArrayList<Vector2>();

    private final ComponentMapper<CameraFollowComponent> cameraMapper;
    private final ComponentMapper<TransformComponent> transformMapper;

    public WorldCameraSystem(OrthographicCamera camera, SpriteBatch batch) {
        super(Family.all(CameraFollowComponent.class, TransformComponent.class).get());
        cameraMapper = ComponentMapper.getFor(CameraFollowComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        this.batch = batch;
        setCamera(camera);
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        targetPositions.clear();
        super.update(deltaTime);
        calculateCameraPosition();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CameraFollowComponent cameraComponent = cameraMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);

        if (cameraComponent.active) {
            //adds the vectors and appends to targetPositions without modifying either
            targetPositions.add(transform.position.cpy().add(cameraComponent.offset));
        }
    }

    /**
     * Determine the camera position based on the targets it follows
     */
    private void calculateCameraPosition() {
        Vector2 position = new Vector2();
        if (targetPositions.isEmpty()) {
            System.out.println("Warning: No active camera targets");
            return;
        }

        // Sets the position to the average of the target positions
        for (Vector2 targetPosition : targetPositions) {
            position.add(targetPosition);
        }
        position.scl(1f / targetPositions.size());

        // Any additional logic such as screen shake goes here

        camera.position.x = position.x;
        camera.position.y = position.y;
    }
}
