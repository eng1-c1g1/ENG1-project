package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.maze11.components.CameraComponent;
import io.github.maze11.components.TransformComponent;

public class CameraSystem extends IteratingSystem {

    private ComponentMapper<CameraComponent> cameraMapper;
    private ComponentMapper<TransformComponent> transformMapper;

    public CameraSystem() {
        super(Family.all(CameraComponent.class, TransformComponent.class).get());
        cameraMapper = ComponentMapper.getFor(CameraComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CameraComponent cameraComponent = cameraMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);

        //move the camera to match the transform
        cameraComponent.camera.position.set(transform.position.x, transform.position.y, -1f);
        cameraComponent.camera.update();
    }
}
