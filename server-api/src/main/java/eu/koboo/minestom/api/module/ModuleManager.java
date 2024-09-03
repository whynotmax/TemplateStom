package eu.koboo.minestom.api.module;

import eu.koboo.minestom.api.module.annotation.ModuleInfo;
import eu.koboo.minestom.api.module.annotation.dependencies.LoadOption;
import net.minestom.server.event.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Consumer;

public interface ModuleManager {

    /**
     * Load the specified module.
     * @param module    the module
     */
    void enableModule(Module module);

    /**
     * Disable the specified module.
     * @param module    the module
     */
    void disableModule(Module module);

    /**
     * Enable all modules.
     */
    void enableAllModules();

    /**
     * Disable all modules.
     */
    void disableAllModules();

    /**
     * Get the module with the specified name.
     * @param name  the module name
     * @return  the module
     */
    Module getModule(String name);

    /**
     * Get all modules.
     * @return  the modules
     */
    Module[] getModules();

    /**
     * Get the module info for the specified module.
     * @param name  the module name
     * @return  the module info
     */
    ModuleInfo getModuleInfo(String name);

    /**
     * Get all module infos.
     * @return  the module infos
     */
    Map<String, ModuleInfo> getModuleInfos();

    /**
     * Check if the specified module is enabled.
     * @param name  the module name
     * @return  true if the module is enabled, false otherwise
     */
    boolean isModuleEnabled(String name);

    /**
     * Get the load option for the specified module.
     * @param module    the module
     * @return  the load option
     */
    LoadOption getModuleLoadOption(Module module);

    /**
     * Get the load options for all modules.
     * @return  the load options
     */
    Map<String, LoadOption> getModuleLoadOptions();

    /**
     * Register a listener for the specified event class.
     * @param eventClass    the event class
     * @param listener      the listener
     * @param <E>           the event type
     */
    <E extends Event> void registerListener(Class<E> eventClass, Consumer<E> listener);

    /**
     * Load all modules with the specified load option.
     */
    @ApiStatus.Internal
    void loadModulesPostWorld();
}
