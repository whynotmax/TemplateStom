# Creating a new module

This project is designed to be modular. This means that you can create new modules to add new features to the project. This document will guide you through the process of creating a new module.

## What we'll be doing

We will be creating a new module that will display a message on the screen. This message will be configurable and will be displayed in the center of the screen.

## Step 1: Create the module

Open up your java IDE of choice and create a new project. Create a new package called `com.example.modules` and create a new class called `MessageModule`. This class should extend `Module` and needs to be annotated with the `@ModuleInfo` annotation. This should look like this:

```java
package com.example.modules;

import eu.koboo.minestom.api.module.Module;
import eu.koboo.minestom.api.module.annotation.ModuleInfo;
import eu.koboo.minestom.api.module.annotation.dependencies.ModuleDependency;

@ModuleInfo(
        name = "MessageModule", 
        version = "1.0.0",
        description = "A module that displays a message on the screen",
        author = "Your Name",
        dependencies = {
                @ModuleDependency(
                        name = "TestModule"
                )
        }
)
public class MessageModule extends Module {

    @Override
    public void onEnable() {
        // Code to run when the module is enabled
    }

    @Override
    public void onDisable() {
        // Code to run when the module is disabled
    }
}
```

Woah! That's a lot of code! Let's break it down:

- `@ModuleInfo`: This annotation is used to provide information about the module. The `name` field is the name of the module, the `version` field is the version of the module, the `description` field is a description of the module, the `author` field is the author of the module, and the `dependencies` field is an array of `@ModuleDependency` annotations that specify the dependencies of the module.
- `@ModuleDependency`: This annotation is used to specify the dependencies of the module. In this case, the `MessageModule` depends on the `TestModule`. **Such dependency annotations are optional**. They should only be included if the module depends on other modules.
- `onEnable()`: This method is called when the module is enabled. This is where you should put the code to run when the module is enabled.
- `onDisable()`: This method is called when the module is disabled. This is where you should put the code to run when the module is disabled.
- `Module`: This is the base class for all modules. It provides methods for enabling and disabling the module.

## Step 2: Create a listener

Now that we have our module, we need to create a listener that will display the message on the screen when a player joins. Create a new class called `MessageListener` in the `com.example.modules.listener` package, that extends the `Consumer` class. This should look like this:

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
        player.sendMessage(Component.text("Welcome to the server!"));
    }
    
}
```

This code listens for the `PlayerSpawnEvent` and sends a message to the player when they spawn. We will register this listener in the `onEnable()` method of the `MessageModule`.

## Step 3: Register the listener

Now that we have our listener, we need to register it in the `onEnable()` method of the `MessageModule`. This should look like this:

```java
    @Override
    public void onEnable() {
        Server.getInstance().getModuleManager().registerListener(new MessageListener());
    }
```

This code registers the `MessageListener` with the server so that it will be called when a player spawns.

## Step 4: Test the module

Now that we have our module set up, we can test it by compiling the project into a jar file and adding it to the `modules` folder of the server. When you start the server, you should see the message "Welcome to the server!" displayed when a player joins.

## Conclusion

Congratulations! You have created a new module for the project. You can now add more features to the module or create new modules to add more functionality to the project. Have fun coding!

## Next Steps

- [Working with packets](working-with-packets.md)
- [Creating a command](creating-a-command.md)
- [Creating a GUI](creating-a-gui.md)
