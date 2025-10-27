package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EntitySystem;
import io.github.maze11.components.CoffeeComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.messages.MessageListener;

public class CoffeeSystem extends EntitySystem {
    ComponentMapper<CoffeeComponent> coffeeMapper;
    ComponentMapper<PlayerComponent> playerMapper;
    MessageListener messageListener;
}
