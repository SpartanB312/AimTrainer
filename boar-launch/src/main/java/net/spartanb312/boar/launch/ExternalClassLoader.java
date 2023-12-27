package net.spartanb312.boar.launch;

import net.spartanb312.boar.launch.transform.ClassTransformerManager;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.lang.Math.max;

public class ExternalClassLoader extends URLClassLoader {

    public ExternalClassLoader(URL[] urls, ClassLoader parent, ClassTransformerManager ctm) {
        super(urls, parent);
        this.ctm = ctm;
    }

    public ExternalClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        ctm = null;
    }

    public ExternalClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
        ctm = null;
    }

    public ExternalClassLoader() {
        super(new URL[0], ExternalClassLoader.class.getClassLoader());
        ctm = null;
    }

    private final ClassTransformerManager ctm;

    private final HashMap<String, byte[]> classesCache = new HashMap<>();
    @SuppressWarnings("CollectionContainsUrl") // It doesn't matter when URL as a key in map.
    private final HashMap<String, URL> resourceCache = new HashMap<>();
    private final Set<String> resourcePaths = new HashSet<>();
    private final String[] systemPaths = System.getProperty("java.library.path").split(";");
    private final String[] dummyPaths = {"C:\\Windows\\System\\", "C:\\Windows\\System32\\"};

    public void addURLs(URL... urls) {
        for (URL url : urls) {
            this.addURL(url);
        }
    }

    public void addPath(String path) {
        resourcePaths.add(path);
    }

    public void removePath(String path) {
        resourcePaths.remove(path);
    }

    @Override
    protected Class<?> findClass(String name) {
        try {
            return super.findClass(name);
        } catch (Exception exception) {
            byte[] bytes = classesCache.getOrDefault(name, null);
            if (bytes != null) {
                byte[] bytes1 = ctm != null ? ctm.transform(name, bytes) : bytes;
                return defineClass(name, bytes1, 0, bytes1.length);
            } else throw new Error("Can't find class " + name);
        }
    }

    @Override
    public URL findResource(String name) {
        // Find in resource caches
        URL resInCache = resourceCache.getOrDefault(name, null);
        if (resInCache != null) return resInCache;

        // Find in this classLoader
        URL resInThis = super.findResource(name);
        if (resInThis != null) return resInThis;

        // Find in parent classLoader
        URL resInParent = getParent().getResource(name);
        if (resInParent != null) return resInParent;

        // Find external resources
        for (String path : resourcePaths) {
            URL res = findInPath(path, name);
            if (res != null) return res;
        }

        // Find in system
        for (String sysPath : systemPaths) {
            URL res = findInPath(sysPath, name);
            if (res != null) return res;
        }

        // Dummy stuff
        for (String dummy : dummyPaths) {
            URL res = findInPath(dummy, name);
            if (res != null) return res;
        }
        return null;
    }

    public URL loadResource(File file) throws IOException {
        URL url = file.toURI().toURL();
        resourceCache.put(file.toString().replace("\\", "/"), url);
        return url;
    }

    public void loadJar(String file) {
        try {
            loadJar(new File(file));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void loadJar(File file) throws IOException {
        ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
        while (true) {
            ZipEntry entry = zip.getNextEntry();
            if (entry == null) break;
            else if (entry.getName().toLowerCase().endsWith(".class")) {
                classesCache.put(removeSuffix(entry.getName().replace("/", "."), ".class"), readBytes(zip));
            } else {
                URL resourceURL = new URL(
                        "jar:file:"
                                + (Platform.getPlatform().getOS() == Platform.OS.Linux ? "" : "/")
                                + file.getAbsolutePath().replace("\\", "/")
                                + "!/" + entry.getName()
                );
                resourceCache.put(entry.getName(), resourceURL);
            }
        }
    }

    private static byte[] readBytes(InputStream input) throws IOException {
        int size = max(8 * 1024, input.available());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(size);
        copyTo(input, buffer, size);
        return buffer.toByteArray();
    }

    private static void copyTo(InputStream in, OutputStream out, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        var bytes = in.read(buffer);
        while (bytes >= 0) {
            out.write(buffer, 0, bytes);
            bytes = in.read(buffer);
        }
    }

    private static String removeSuffix(String value, String suffix) {
        if (value.endsWith(suffix)) {
            return value.substring(0, value.length() - suffix.length());
        } else return value;
    }

    private static URL findInPath(String path, String name) {
        String adjustedPath = removeSuffix(removeSuffix(path, "/"), "\\");
        File file = new File(adjustedPath + "/" + name);
        if (file.exists()) try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
        else return null;
    }

    public void initKotlinObject(String name) throws Exception {
        invokeKotlinObjectField(loadClass(name));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void invokeKotlinObjectField(Class<?> clazz) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().equals("INSTANCE")) {
                field.get(null);
                break;
            }
        }
    }

}
