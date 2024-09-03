package eu.koboo.minestom.module;

import eu.koboo.minestom.api.module.Module;
import eu.koboo.minestom.api.module.ModuleManager;
import eu.koboo.minestom.api.module.annotation.ModuleInfo;
import eu.koboo.minestom.api.module.annotation.dependencies.LoadOption;
import eu.koboo.minestom.api.module.annotation.dependencies.ModuleDependency;
import eu.koboo.minestom.server.ServerImpl;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.JarFile;

@Getter
public class ModuleManagerImpl implements ModuleManager {

    Map<String, Module> modules;
    Map<String, ModuleInfo> moduleInfos;

    Map<Module, LoadOption> moduleLoadOptions;
    List<Module> toLoadPostWorld;

    public ModuleManagerImpl() {
        this.modules = new HashMap<>();
        this.moduleInfos = new HashMap<>();
        this.moduleLoadOptions = new HashMap<>();
        this.toLoadPostWorld = new ArrayList<>();

        Path moduleFolder = Path.of("modules");
        if (!moduleFolder.toFile().exists()) {
            if (ServerImpl.DEBUG) Logger.info("Creating modules folder as it does not exist");
            moduleFolder.toFile().mkdir();
            if (ServerImpl.DEBUG) Logger.info("Modules folder created");
        }
        File moduleFolderAsFile = moduleFolder.toFile();
        if (moduleFolderAsFile.listFiles() == null) {
            if (ServerImpl.DEBUG) Logger.error("No modules found; maybe you should create some? :)");
            return;
        }
        int filesInModuleFolder = moduleFolderAsFile.listFiles().length;
        if (filesInModuleFolder == 0) {
            if (ServerImpl.DEBUG) Logger.error("No modules found; maybe you should create some? :)");
        }
        Logger.info("Found " + filesInModuleFolder + " module(s). Thank you for choosing us!");

    }

    @Override
    public void enableModule(Module module) {
        if (module == null) {
            if (ServerImpl.DEBUG) Logger.error("Module is null");
            return;
        }
        ModuleInfo moduleInfo = module.getClass().getAnnotation(ModuleInfo.class);
        if (moduleInfo == null) {
            if (ServerImpl.DEBUG) Logger.error("ModuleInfo is null");
            return;
        }
        if (moduleInfos.containsKey(moduleInfo.name())) {
            if (ServerImpl.DEBUG) Logger.error("Module with name " + moduleInfo.name() + " is already enabled");
            return;
        }
        moduleLoadOptions.put(module, moduleInfo.loadOption());
        if (moduleInfo.loadOption() == LoadOption.POSTWORLD) {
            toLoadPostWorld.add(module);
            return;
        }
        Logger.info("Enabling module " + moduleInfo.name() + " v" + moduleInfo.version() + " by " + String.join(", ", moduleInfo.authors()));
        for (ModuleDependency moduleDependency : moduleInfo.moduleDependencies()) {
            if (!moduleInfos.containsKey(moduleDependency.name())) {
                if (ServerImpl.DEBUG) Logger.error("Module " + moduleInfo.name() + " requires module " + moduleDependency.name() + " to be enabled. Please enable it first.");
                return;
            }
        }
        modules.put(moduleInfo.name(), module);
        moduleInfos.put(moduleInfo.name(), moduleInfo);
        module.onEnable();
    }

    @Override
    public void disableModule(Module module) {
        if (module == null) {
            if (ServerImpl.DEBUG) Logger.error("Module is null");
            return;
        }
        ModuleInfo moduleInfo = module.getClass().getAnnotation(ModuleInfo.class);
        if (moduleInfo == null) {
            if (ServerImpl.DEBUG) Logger.error("ModuleInfo is null");
            return;
        }
        if (!moduleInfos.containsKey(moduleInfo.name())) {
            if (ServerImpl.DEBUG) Logger.error("Module with name " + moduleInfo.name() + " is not enabled");
            return;
        }
        for (ModuleInfo moduleInfo1 : moduleInfos.values()) {
            for (ModuleDependency moduleDependency : moduleInfo1.moduleDependencies()) {
                if (moduleDependency.name().equals(moduleInfo.name())) {
                    if (ServerImpl.DEBUG) Logger.error("Module " + moduleInfo1.name() + " requires module " + moduleInfo.name() + " to be enabled. Please disable it first.");
                    return;
                }
            }
        }
        Logger.info("Disabling module " + moduleInfo.name() + " v" + moduleInfo.version() + " by " + String.join(", ", moduleInfo.authors()));
        modules.remove(moduleInfo.name());
        moduleInfos.remove(moduleInfo.name());
        moduleLoadOptions.remove(module);
        module.onDisable();
    }

    @Override
    public void enableAllModules() {
        List<JarFile> moduleJars = getModuleJars();
        for (JarFile jarFile : moduleJars) {
            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(jarFile.getName()).toURI().toURL()})) {
                jarFile.stream()
                        .filter(entry -> entry.getName().endsWith(".class"))
                        .forEach(entry -> {
                            String className = entry.getName().replace("/", ".").replace(".class", "");
                            try {
                                Class<?> clazz = classLoader.loadClass(className);
                                if (Module.class.isAssignableFrom(clazz)) {
                                    Module module = (Module) clazz.getDeclaredConstructor().newInstance();
                                    enableModule(module);
                                }
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                                     InvocationTargetException e) {
                                Logger.error("Failed to load module class " + className, e);
                            }
                        });
            } catch (IOException e) {
                Logger.error("Failed to load module jar " + jarFile.getName(), e);
                e.printStackTrace();
            } catch (Exception e) {
                Logger.error("Malformed JAR file: " + jarFile.getName(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void disableAllModules() {
        for (Module module : modules.values()) {
            disableModule(module);
        }
    }

    @Override
    public Module getModule(String name) {
        return modules.getOrDefault(name, null);
    }

    @Override
    public Module[] getModules() {
        return modules.values().toArray(new Module[0]);
    }

    @Override
    public ModuleInfo getModuleInfo(String name) {
        return moduleInfos.getOrDefault(name, null);
    }

    @Override
    public Map<String, ModuleInfo> getModuleInfos() {
        return moduleInfos;
    }

    @Override
    public boolean isModuleEnabled(String name) {
        return modules.containsKey(name) && modules.get(name) != null && modules.get(name).isEnabled();
    }

    @Override
    public LoadOption getModuleLoadOption(Module module) {
        return moduleLoadOptions.getOrDefault(module, LoadOption.PREWORLD);
    }

    @Override
    public Map<String, LoadOption> getModuleLoadOptions() {
        Map<String, LoadOption> moduleLoadOptions = new HashMap<>();
        for (Map.Entry<Module, LoadOption> entry : this.moduleLoadOptions.entrySet()) {
            moduleLoadOptions.put(entry.getKey().getClass().getAnnotation(ModuleInfo.class).name(), entry.getValue());
        }
        return moduleLoadOptions;
    }

    @Override
    public <E extends Event> void registerListener(Class<E> eventClass, Consumer<E> listener) {
        MinecraftServer.getGlobalEventHandler().addListener(eventClass, listener); //Hopefully this works, lmfaooo
    }

    @Override
    public void loadModulesPostWorld() {
        Logger.info("Loading modules that are set to load post-world. (" + toLoadPostWorld.size() + ")");
        for (Module module : toLoadPostWorld) {
            enableModule(module);
        }
    }

    private List<JarFile> getModuleJars() {
        List<JarFile> jarFiles = new ArrayList<>();

        Path moduleFolder = Path.of("modules");
        if (!moduleFolder.toFile().exists()) {
            moduleFolder.toFile().mkdir();
            //no modules to load, yet
            return jarFiles;
        }

        File moduleFolderAsFile = moduleFolder.toFile();

        if (!moduleFolderAsFile.isDirectory()) {
            Logger.error("Module folder is not a directory");
            return jarFiles;
        }

        if (moduleFolderAsFile.listFiles() == null) {
            Logger.error("No modules found; maybe you should create some? :)");
            return jarFiles;
        }

        List<File> filesInModuleFolder = List.of(moduleFolderAsFile.listFiles());

        if (filesInModuleFolder.isEmpty()) {
            Logger.error("No modules found; maybe you should create some? :)");
            return jarFiles;
        }

        for (File file : filesInModuleFolder) {
            if (file.getName().endsWith(".jar")) {
                try {
                    jarFiles.add(new JarFile(file));
                } catch (IOException e) {
                    Logger.error("Failed to load module jar " + file.getName());
                    e.printStackTrace();
                }
            }
        }
        return jarFiles;
    }

    public List<Module> getModulesToLoadPostWorld() {
        return toLoadPostWorld;
    }
}
