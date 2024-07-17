package net.spartanb312.everett.launch;

import java.io.*;
import java.net.URL;
import java.util.Arrays;

@Module(name = "Launch Wrapper", version = Main.LAUNCH_WRAPPER_VERSION, description = "Bootstrap launch wrapper", author = "B_312")
public class Main {

    public static final String LAUNCH_WRAPPER_VERSION = "1.0.2";

    public static void main(String[] args) throws Exception {
        LaunchLogger.info("Initializing launch wrapper...");

        // Detect system
        LaunchLogger.info("Running on platform: " + Platform.getPlatform().getPlatformName());

        // Check DevMode
        boolean devMode = Arrays.stream(args).toList().contains("-DevMode");

        // Launch Engine
        if (!devMode) {
            LaunchLogger.info("Loading dependencies...");
            readFiles("libs/", ".jar", true, file -> {
                if (file.getName().startsWith("lwjgl") && file.getName().contains("natives")) {
                    if (file.getName().endsWith("-natives-" + Platform.getPlatform().getClassifier() + ".jar")) {
                        LaunchLogger.info(" - " + file.getName());
                        LaunchClassLoader.loadJarFile(file);
                    }
                } else {
                    LaunchLogger.info(" - " + file.getName());
                    LaunchClassLoader.loadJarFile(file);
                }
            });
            LaunchClassLoader.loadJarFile("engine/boar-main.jar");
            URL manifest = LaunchClassLoader.INSTANCE.findResource("META-INF/MANIFEST.MF");
            LaunchClassLoader.INSTANCE.initKotlinObject(findEntry(manifest));
        } else {
            URL manifest = Main.class.getResource("/META-INF/MANIFEST.MF");
            assert manifest != null;
            ExternalClassLoader.invokeKotlinObjectField(Class.forName(findEntry(manifest)));
        }
    }

    private static String findEntry(URL manifest) {
        var entry = "net.spartanb312.everett.launch.Entry";
        try {
            InputStreamReader ir = new InputStreamReader(manifest.openStream());
            BufferedReader br = new BufferedReader(ir);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Launch-Entry: ")) {
                    entry = line.substring(14);
                }
            }
        } catch (IOException ignored) {
        }
        LaunchLogger.info("Using Entry: " + entry);
        return entry;
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
