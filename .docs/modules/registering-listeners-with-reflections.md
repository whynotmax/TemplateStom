# Registering listeners with reflections

In the previous section, we learned how to create a listener and register it with the server. While this approach works well for a small number of listeners, it can become cumbersome when dealing with a large number of listeners. In this section, we will learn how to register listeners using reflections, which allows us to automatically register all listeners in a package.

## Step 1: Create a listener package

The first step is to create a package to store all of our listeners. Create a new package called `com.example.modules.listener` in the `src/main/java` directory of your project. This is where we will store all of our listener classes.

## Step 2: Create a listener

Next, create a new listener class in the `com.example.modules.listener` package. This class should extend the `Consumer` class and implement the `accept` method. This method will be called whenever the event that the listener is listening for is triggered. Here is an example of a simple listener class:

```java
package com.example.modules.listener;

import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.entity.Player;
import net.kyori.adventure.text.Component;
import java.util.function.Consumer;

public class MessageListener implements Consumer<PlayerSpawnEvent> {
    
    @Override
    public void accept(PlayerSpawnEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Component.text("Hello, world!"));
    }
}
```

## Step 3: Register the listener with reflections

Now that we have our listener, we need to register it with the server. To do this, we will use reflections to automatically register all listeners in the `com.example.modules.listener` package. This is quite a tedious task to do manually, especially when you have a large number of listeners.

### Step 3.1: Add the Reflections library to your project
[Click here to learn how to add the Reflections library to your project](https://github.com/ronmamo/reflections?tab=readme-ov-file#usage).

### Step 3.2: Create a ReflectionUtils class

Create a new class called `ReflectionUtils` in the `com.example.modules.util` package. This class will contain a method to get the event type of the listener. Here is an example of the `ReflectionUtils` class:

```java
package com.example.modules.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;

public class ReflectionUtils {
    public static Class<?> getConsumerEventType(Class<? extends Consumer<? extends Event>> consumerClass) {
        Type[] genericInterfaces = consumerClass.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                    return (Class<?>) actualTypeArguments[0];
                }
            }
        }
        return null;
    }
}
```

### Step 3.3: Register the listeners with reflections

Now, we can use the `Reflections` library to scan the `com.example.modules.listener` package for all classes that extend the `Consumer` class. Here is an example of how to register all listeners with reflections:

```java
package com.example.modules;

import com.example.modules.listener.MessageListener;
import eu.koboo.minestom.api.module.Module;
import eu.koboo.minestom.api.module.annotation.ModuleInfo;
import eu.koboo.minestom.api.module.annotation.dependencies.ModuleDependency;
import eu.koboo.minestom.api.server.Server;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

@ModuleInfo(
        name = "MessageModule",
        version = "1.0.0",
        description = "A module that displays a message on the screen",
        authors = {"Your Name"},
        moduleDependencies = {
                //No dependencies, but you can add them here
        }
)
public class MessageModule extends Module {

    @Override
    public void onEnable() {
        Reflections reflections = new Reflections("com.example.modules.listener");
        Set<Class<? extends Consumer<? extends Event>>> listenerClasses = reflections.getSubTypesOf(Consumer.class);
        EventNode eventNode = EventNode.all();
        for (Class<? extends Consumer<? extends Event>> listenerClass : listenerClasses) {
            try {
                Consumer<? extends Event> listener = listenerClass.newInstance();
                Class<?> eventType = ReflectionUtils.getConsumerEventType(listenerClass);
                eventNode.addListener(eventType, listener);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDisable() {
        // Code to run when the module is disabled
    }
}
```

In this code snippet, we use the `Reflections` library to scan the `com.example.modules.listener` package for all classes that extend the `Consumer` class. We then iterate over each listener class, create an instance of the class, and register it with the server using the `EventNode` class.

By using reflections, we can automatically register all listeners in a package without having to manually register each listener individually. This makes it easy to add new listeners to your project without having to modify the registration code.

## Conclusion

In this section, we learned how to register listeners using reflections, which allows us to automatically register all listeners in a package. This approach is useful when dealing with a large number of listeners and makes it easy to add new listeners to your project. By using reflections, you can streamline the listener registration process and focus on developing your server logic.

