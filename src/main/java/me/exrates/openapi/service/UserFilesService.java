package me.exrates.openapi.service;

import me.exrates.service.exception.api.DeleteFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@Service
public class UserFilesService {

    @Value("${upload.userFilesDir}")
    private String userFilesDir;
    @Value("${upload.userFilesLogicalDir}")
    private String userFilesLogicalDir;

    private final UserService userService;
    private final Set<String> contentTypes;

    private static final Logger LOG = LogManager.getLogger(UserFilesService.class);

    @Autowired
    public UserFilesService(final UserService userService) {
        this.userService = userService;
        contentTypes = new HashSet<>();
        contentTypes.addAll(asList("image/jpg", "image/jpeg", "image/png"));
    }

    /**
     * Removes empty files and files with invalid extension from an input array
     *
     * @param files - Uploaded files
     * @return - ArrayList with valid uploaded files
     */
    public List<MultipartFile> reduceInvalidFiles(final MultipartFile[] files) {
        return Stream.of(files)
                .filter(this::checkFileValidity)
                .collect(Collectors.toList());
    }

    public boolean checkFileValidity(final MultipartFile file) {
        return !file.isEmpty() && contentTypes.contains(extractContentType(file));
    }

    /**
     * Moves uploaded files to user dir on the server ({@link UserFilesServiceImpl#userFilesDir} + userId) and persist File names in DB (table USER_DOC)
     * If only one file failed to move - deletes all uploaded files and throws IOException
     *
     * @param userId - UserId who uploads the files
     * @param files  - uploaded files
     * @throws IOException
     */
    public void createUserFiles(final int userId, final List<MultipartFile> files) throws IOException {
        final Path path = Paths.get(userFilesDir + userId);
        final List<Path> logicalPaths = new ArrayList<>();
        final List<Path> realPaths = new ArrayList<>();
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        try {
            for (final MultipartFile file : files) {
                final String name = UUID.randomUUID().toString() + "." + extractFileExtension(file);
                final Path target = Paths.get(path.toString(), name);
                Files.write(target, file.getBytes());
                realPaths.add(target);
                logicalPaths.add(Paths.get(userFilesLogicalDir, String.valueOf(userId), name));
            }
        } catch (final IOException e) {
            if (!realPaths.isEmpty()) {
                final List<IOException> exceptions = new ArrayList<>();
                try {
                    for (final Path toRemove : realPaths) {
                        Files.delete(toRemove);
                    }
                } catch (final IOException ex) {
                    ex.initCause(e);
                    exceptions.add(ex);
                }
                if (!exceptions.isEmpty()) {
                    LOG.error("Exceptions during deleting uploaded files " + exceptions);
                }
            }
            throw e;
        }
        userService.createUserFile(userId, logicalPaths);
    }

    public String saveReceiptScan(final int userId, final int invoiceId, final MultipartFile file) throws IOException {
        final Path path = Paths.get(userFilesDir + userId, "receipts");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        String baseFilename = new StringJoiner("_").add("receipt")
                .add(String.valueOf(invoiceId))
                .add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")))
                .toString();
        Path logicalPath = writeUserFile(path, Paths.get(userFilesLogicalDir, String.valueOf(userId), "receipts"), baseFilename, file);
        return logicalPath.toString();
    }

    public String createUserAvatar(final int userId, final MultipartFile file) throws IOException {
        final Path path = Paths.get(userFilesDir + userId, "avatar");
        LOG.debug(path.toString());
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        List<Path> existingAvatars = Arrays.stream(path.toFile().listFiles())
                .map(avatar -> Paths.get(avatar.getPath())).collect(Collectors.toList());
        Path logicalPath = writeUserFile(path, Paths.get(userFilesLogicalDir, String.valueOf(userId), "avatar"), UUID.randomUUID().toString(), file);
        userService.setUserAvatar(userId, logicalPath);
        existingAvatars.forEach(avatar -> {
            try {
                Files.delete(avatar);
            } catch (final IOException ex) {
                LOG.error("Could not delete files");
                throw new DeleteFileException("Could not delete files");
            }
        });
        return logicalPath.toString();
    }

    private Path writeUserFile(Path initialPath, Path logicalPath, String baseFilename, MultipartFile file) throws IOException {
        Path realPath = null;
        Path logicalFilePath;
        try {
            final String name = baseFilename + "." + extractFileExtension(file);
            realPath = Paths.get(initialPath.toString(), name);
            logicalFilePath = Paths.get(logicalPath.toString(), name);
            Files.write(realPath, file.getBytes());
            return logicalFilePath;
        } catch (final IOException e) {
            if (realPath != null) {
                final List<IOException> exceptions = new ArrayList<>();
                try {
                    Files.delete(realPath);
                } catch (final IOException ex) {
                    ex.initCause(e);
                    exceptions.add(ex);
                }
                if (!exceptions.isEmpty()) {
                    LOG.error("Exceptions during deleting uploaded files " + exceptions);
                }
            }
            throw e;
        }
    }

    public void deleteUserFile(final String filename, final int userId) throws IOException {
        final Path path = Paths.get(userFilesDir, String.valueOf(userId), filename);
        Files.delete(path);
    }

    private String extractContentType(final MultipartFile file) {
        return file.getContentType().toLowerCase();
    }

    /**
     * @param file - Uploaded file
     * @return - Uploaded files extension
     */
    private String extractFileExtension(final MultipartFile file) {
        return extractContentType(file).substring(6); //Index of dash in Content-Type
    }
}
