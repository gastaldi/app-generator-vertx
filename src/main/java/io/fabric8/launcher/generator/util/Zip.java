package io.fabric8.launcher.generator.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class Zip {

    /**
     * Unzip a zip file into a temporary location
     *
     * @param is        the zip file contents to be unzipped
     * @param outputDir the output directory
     * @throws IOException when we could not read the file
     */
    public static void unzip(InputStream is, Path outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                Path entry = outputDir.resolve(zipEntry.getName()).normalize();
                if (!entry.startsWith(outputDir)) {
                    throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
                }
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(entry);
                } else {
                    Files.copy(zis, entry);
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * Zips an entire directory and returns as a byte[]
     *
     * @param root      the root directory to be used
     * @param directory the directory to be zipped
     * @return a byte[] representing the zipped directory
     * @throws IOException if any I/O error happens
     */
    public static byte[] zip(String root, final Path directory) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        zip(root, directory, baos);
        return baos.toByteArray();
    }

    /**
     * Zips an entire directory and stores in the provided {@link OutputStream}
     *
     * @param root      the root directory to be used
     * @param directory the directory to be zipped
     * @param os        the {@link OutputStream} which the zip operation will be written to
     * @throws IOException if any I/O error happens
     */
    public static void zip(String root, final Path directory, OutputStream os) throws IOException {
        String prefix = (root == null || root.isEmpty()) ? "" : root + File.separator;
        try (final ZipOutputStream zos = new ZipOutputStream(os)) {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String entry = prefix + directory.relativize(file).toString();
                    zos.putNextEntry(new ZipEntry(entry));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String entry = prefix + directory.relativize(dir).toString() + File.separator;
                    zos.putNextEntry(new ZipEntry(entry));
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

}
