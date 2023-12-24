package net.spartanb312.boar.launch;

import java.io.File;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        LaunchLogger.info("Initializing launch wrapper...");

        // Detect system
        LaunchLogger.info("Running on platform: " + Platform.getPlatform().name);

        // Check DevMode
        boolean devMode = Arrays.stream(args).toList().contains("-DevMode");

        // Launch Engine
        if (!devMode) {
            LaunchLogger.info("Loading dependencies...");
            readFiles("libs/", ".jar", true, file -> {
                if (file.getName().startsWith("lwjgl") && file.getName().contains("natives")) {
                    if (file.getName().endsWith("-natives-" + Platform.getPlatform().classifier + ".jar")) {
                        LaunchLogger.info(" - " + file.getName());
                        LaunchClassLoader.loadJarFile(file);
                    }
                } else {
                    LaunchLogger.info(" - " + file.getName());
                    LaunchClassLoader.loadJarFile(file);
                }
            });
            LaunchClassLoader.loadJarFile("engine/boar-main.jar");
            LaunchClassLoader.INSTANCE.initKotlinObject("net.spartanb312.boar.launch.Entry");
        } else {
            ExternalClassLoader.invokeKotlinObjectField(Class.forName("net.spartanb312.boar.launch.Entry"));
        }
    }

    private interface FileTask {
        void invoke(File file);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void readFiles(String path, String suffix, boolean ignoreCase, FileTask action) {
        File current = new File(path);
        if (!current.exists()) {
            try {
                current.getParentFile().mkdirs();
                current.createNewFile();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return;
        }
        if (current.isDirectory()) {
            String[] list = current.list();
            if (list != null) {
                for (String child : list) {
                    File childFile = new File(path + child);
                    if (childFile.isDirectory()) readFiles(path + child, suffix, ignoreCase, action);
                    else if (endWith(childFile.getName(), suffix, ignoreCase)) try {
                        action.invoke(childFile);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        } else if (endWith(current.getName(), suffix, ignoreCase)) try {
            action.invoke(current);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static boolean endWith(String src, String suffix, boolean ignoreCase) {
        if (ignoreCase) return src.toLowerCase().endsWith(suffix.toLowerCase());
        else return src.endsWith(suffix);
    }

}
