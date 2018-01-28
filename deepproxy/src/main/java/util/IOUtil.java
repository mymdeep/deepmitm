package util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wangfei on 2018/1/10.
 */

public class IOUtil {
    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable)input);
    }

    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable)output);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if(closeable != null) {
                closeable.close();
            }
        } catch (IOException var2) {
            ;
        }

    }
}
