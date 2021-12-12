package org.jusecase.properties.plugins.hash;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHashPlugin {
    private static final int STREAM_BUFFER_LENGTH = 4096;

    private static final ThreadLocal<MessageDigest> hash = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize message digest.");
        }
    });

    public static byte[] hash(Path file) throws IOException {
        MessageDigest digest = hash.get();

        try (InputStream is = Files.newInputStream(file)) {
            final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
            int read = is.read(buffer, 0, STREAM_BUFFER_LENGTH);

            while (read > -1) {
                digest.update(buffer, 0, read);
                read = is.read(buffer, 0, STREAM_BUFFER_LENGTH);
            }
        }

        return digest.digest();
    }
}
