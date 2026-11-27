package com.salesystem.db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;

/** Keeps the database credential outside the project, protected for the current Windows user. */
public final class WindowsPasswordStore {
    private final Path file;

    public WindowsPasswordStore(Path file) {
        this.file = file;
    }

    public String read() throws IOException, InterruptedException {
        if (!Files.exists(file)) return null;
        String decoded = transform(Files.readString(file), false);
        return new String(Base64.getDecoder().decode(decoded), StandardCharsets.UTF_8);
    }

    public void save(String password) throws IOException, InterruptedException {
        String encoded = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
        String encrypted = transform(encoded, true);
        Files.createDirectories(file.getParent());
        Path temp = Files.createTempFile(file.getParent(), "connection-", ".tmp");
        try {
            Files.writeString(temp, encrypted);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private static String transform(String input, boolean encrypt) throws IOException, InterruptedException {
        String method = encrypt ? "Protect" : "Unprotect";
        String script = "$ErrorActionPreference='Stop'; Add-Type -AssemblyName System.Security;"
                + "$bytes=[Convert]::FromBase64String([Console]::In.ReadToEnd());"
                + "$out=[Security.Cryptography.ProtectedData]::" + method
                + "($bytes,$null,[Security.Cryptography.DataProtectionScope]::CurrentUser);"
                + "[Console]::Write([Convert]::ToBase64String($out))";
        Path shell = Path.of(System.getenv("SystemRoot"), "System32", "WindowsPowerShell", "v1.0", "powershell.exe");
        Process process = new ProcessBuilder(shell.toString(), "-NoProfile", "-NonInteractive", "-Command", script)
                .redirectError(ProcessBuilder.Redirect.DISCARD).start();
        try {
            try (var stream = process.getOutputStream()) {
                stream.write(input.getBytes(StandardCharsets.US_ASCII));
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.US_ASCII);
            if (process.waitFor() != 0 || output.isBlank()) {
                throw new IOException("Windows could not access the saved database credential.");
            }
            return output;
        } finally {
            process.destroy();
        }
    }
}
