package com.github.atlantidanetwork.api.lib.plugin;

import com.github.atlantidanetwork.api.lib.command.AtlantidaCommand;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public abstract class AtlantidaPlugin extends JavaPlugin {

    private boolean debug = false;
    @Deprecated
    private String defaultPackage = "com.github.atlantidanetwork.%s";
    private String pluginURI = "api";
    private String pluginPackage = "com.github.atlantidanetwork.api";

    @Deprecated
    public AtlantidaPlugin(String pluginURI) {
        this.pluginURI = pluginURI;
        this.pluginPackage = String.format(getDefaultPackage(), getPluginURI());
    }

    @Deprecated
    public AtlantidaPlugin(String pluginURI, String defaultPackage) {
        this.pluginURI = pluginURI;
        this.defaultPackage = defaultPackage;
        this.pluginPackage = String.format(getDefaultPackage(), getPluginURI());
    }

    public AtlantidaPlugin(Class<?> c) {
        this.pluginPackage = c.getPackage().getName();
        this.pluginURI = pluginPackage.substring(pluginPackage.lastIndexOf('.') + 1);
    }

    protected <T extends AtlantidaPlugin> void listener(Listener listener, T plugin) {
        getServer().getPluginManager().registerEvents(listener, plugin);
    }

    protected void command(String name, CommandExecutor executor) {
        getCommand(name).setExecutor(executor);
    }

    protected void msg(String message) {
        getServer().getConsoleSender().sendMessage(message);
    }

    protected void error(String message) throws Exception {
        throw new Exception(message);
    }

    protected void error(Throwable cause) throws Exception {
        throw new Exception(cause);
    }

    protected void error(String message, Throwable cause) throws Exception {
        throw new Exception(message, cause);
    }

    protected void debug(Level lvl, Object obj) {
        getLogger().log(lvl, obj.toString());
    }

    protected void info(Object object) {
        if (debug) {
            getLogger().info(String.format("[%s] %s", getDescription().getName(), object.toString()));
        }
    }

    protected void info(String prefix, Object object) {
        if (debug) {
            getLogger().info(String.format("[%s] [%s] %s", getDescription().getName(), prefix, object.toString()));
        }
    }

    protected void warn(Object object) {
        if (debug) {
            getLogger().warning(String.format("[%s] %s", getDescription().getName(), object.toString()));
        }
    }

    protected void warn(String prefix, Object object) {
        if (debug) {
            getLogger().warning(String.format("[%s] [%s] %s", getDescription().getName(), prefix, object.toString()));
        }
    }

    protected void severe(Object object) {
        if (debug) {
            getLogger().severe(String.format("[%s] %s", getDescription().getName(), object.toString()));
        }
    }

    protected void severe(String prefix, Object object) {
        if (debug) {
            getLogger().severe(String.format("[%s] [%s] %s", getDescription().getName(), prefix, object.toString()));
        }
    }

    protected <T extends AtlantidaPlugin> void registerCommands(String pkg, T plugin) {
        try {
            Server server = getServer();
            Class<?> serverClass = server.getClass();
            Field field = serverClass.getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap map = (CommandMap) field.get(server);
            getClasses(String.format(getPluginPackage() + ".%s", pkg), plugin.getClass()).stream().forEach(aClass -> {
                try {
                    Constructor<?> cns = aClass.getConstructor(plugin.getClass());
                    cns.setAccessible(true);
                    Object object = cns.newInstance(plugin);
                    if (object instanceof AtlantidaCommand) {
                        AtlantidaCommand cmd = (AtlantidaCommand) object;
                        map.register(this.getName(), cmd);
                    } else {
                        warn(String.format("Couldn't register class '%s' because the class isn't CiberCommand",
                                aClass.getName()));
                    }
                } catch (NoSuchMethodException ex) {
                    warn(String.format("Coudn't find the correct constructor '%s' in '%s'", plugin.getClass().getName(),
                            pkg));
                    try {
                        Constructor<?> cns = aClass.getConstructor();
                        cns.setAccessible(true);
                        Object object = cns.newInstance();
                        if (object instanceof AtlantidaCommand) {
                            AtlantidaCommand cmd = (AtlantidaCommand) object;
                            map.register(plugin.getName(), cmd);
                        } else {
                            warn(String.format("Couldn't register class '%s' because the class isn't CiberCommand",
                                    aClass.getName()));
                        }
                    } catch (Exception ignored) {
                    }
                } catch (SecurityException ex) {
                    getLogger().log(Level.SEVERE, String.format("Coudn't access class constructor of '%s' in '%s'",
                            plugin.getClass().getName(), pkg), ex);
                } catch (Exception ex) {
                    getLogger().log(Level.SEVERE, "An unexpected exception occurred", ex);
                }
            });
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            getLogger().log(Level.SEVERE, "An unexpected exception occurred", ex);
        }
    }

    protected <T extends AtlantidaPlugin> void registerEvents(String pkg, T plugin) {
        getClasses(String.format(getPluginPackage() + ".%s", pkg), plugin.getClass()).stream().forEach(aClass -> {
            try {
                Constructor<?> cns = aClass.getConstructor(plugin.getClass());
                cns.setAccessible(true);
                Object object = cns.newInstance(plugin);
                if (object instanceof Listener) {
                    getServer().getPluginManager().registerEvents((Listener) object, plugin);
                } else {
                    warn(String.format("Couldn't register class '%s' because the class isn't Listener",
                            aClass.getName()));
                }
            } catch (NoSuchMethodException ex) {
                warn(String.format("Coudn't find the correct constructor '%s' in '%s'", plugin.getClass().getName(),
                        pkg));
                try {
                    Constructor<?> cns = aClass.getConstructor();
                    cns.setAccessible(true);
                    Object object = cns.newInstance();
                    if (object instanceof Listener) {
                        getServer().getPluginManager().registerEvents((Listener) object, plugin);
                    } else {
                        warn(String.format("Couldn't register class '%s' because the class isn't Listener",
                                aClass.getName()));
                    }
                } catch (Exception ignored) {
                }
            } catch (SecurityException ex) {
                getLogger().log(Level.SEVERE, String.format("Coudn't access class constructor of '%s' in '%s'",
                        plugin.getClass().getName(), pkg), ex);
            } catch (Exception ex) {
                getLogger().log(Level.SEVERE, "An unexpected exception occurred", ex);
            }
        });
    }

    @SuppressWarnings("resource")
    private List<Class<?>> getClasses(String pkg, Class<?> cls) {
        List<Class<?>> classes = new ArrayList<>();
        CodeSource src = cls.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL resource = src.getLocation();
            String resourcePath = resource.getPath().replace("%20", " ");
            String jarFilePath = resourcePath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
            String realPath = pkg.replace('.', '/');
            info(realPath);
            try {
                Enumeration<JarEntry> entries = new JarFile(new File(jarFilePath)).entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    info(String.format("Getting file %s", name));
                    if ((name.endsWith(".class")) && (name.startsWith(realPath)) && !name.contains("$")) {
                        String className = name.replace('/', '.').replace('\\', '.').replace(".class", "");
                        info(String.format("Getting class %s", className));
                        try {
                            classes.add(Class.forName(className));
                        } catch (ClassNotFoundException exception) {
                            getLogger().log(Level.SEVERE, String.format("Couldn't load %s", className), exception);
                        }
                    }
                }
            } catch (IOException exception) {
                getLogger().log(Level.SEVERE, String.format("Couldn't find %s", jarFilePath), exception);
            }
        }
        return classes;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    public String getPluginURI() {
        return pluginURI;
    }

    public void setPluginURI(String pluginURI) {
        this.pluginURI = pluginURI;
    }

    public String getDefaultPackage() {
        return defaultPackage;
    }

    public void setDefaultPackage(String defaultPackage) {
        this.defaultPackage = defaultPackage;
    }

    public String getPluginPackage() {
        return pluginPackage;
    }

    public void setPluginPackage(String pluginPackage) {
        this.pluginPackage = pluginPackage;
    }
}
