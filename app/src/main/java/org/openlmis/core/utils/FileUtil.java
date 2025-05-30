package org.openlmis.core.utils;

import org.openlmis.core.exceptions.LMISException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public final class FileUtil {

    private FileUtil() {
    }

    public static boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }

        if (dir.isDirectory()) {
            for (String directory : dir.list()) {
                boolean success = deleteDir(new File(dir, directory));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public static void copy(File srcFile, File dstFile) throws IOException {
        FileChannel src = new FileInputStream(srcFile).getChannel();
        FileChannel dst = new FileOutputStream(dstFile).getChannel();
        dst.transferFrom(src, 0, src.size());
        src.close();
        dst.close();
    }

    public static void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            new LMISException(e).reportToFabric();
        }
    }
}
