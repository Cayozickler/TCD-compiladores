package lps1.common;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtil {
    private FileUtil() {
    }

    public static String readInput(String[] args) throws IOException {
        if (args.length > 0) {
            return new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int bytesRead;
        while ((bytesRead = System.in.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toString(StandardCharsets.UTF_8.name());
    }
}
