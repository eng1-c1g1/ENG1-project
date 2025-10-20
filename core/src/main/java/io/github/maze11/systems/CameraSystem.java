package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.maze11.components.CameraFollowComponent;
import io.github.maze11.components.TransformComponent;

public class CameraSystem extends IteratingSystem {
    OrthographicCamera camera;

    private ComponentMapper<CameraFollowComponent> cameraMapper;
    private ComponentMapper<TransformComponent> transformMapper;

    public CameraSystem(OrthographicCamera camera) {
        super(Family.all(CameraFollowComponent.class, TransformComponent.class).get());
        cameraMapper = ComponentMapper.getFor(CameraFollowComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        setCamera(camera);
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CameraFollowComponent cameraComponent = cameraMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);

    }
}
