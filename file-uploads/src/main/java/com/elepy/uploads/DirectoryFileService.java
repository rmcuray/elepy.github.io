package com.elepy.uploads;

import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.UploadedFile;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DirectoryFileService implements FileService {

    private final String rootFolderLocation;

    private final Tika tika = new Tika();

    public DirectoryFileService(String rootFolderLocation) {
        this.rootFolderLocation = rootFolderLocation;
        ensureDirsMade();
    }


    public void ensureDirsMade() {
        Path path = Paths.get(rootFolderLocation);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new ElepyConfigException("Can't create upload folder");
        }
    }

    @Override
    public synchronized void uploadFile(UploadedFile file) {
        final Path name = Paths.get(rootFolderLocation + File.separator + file.getName());
        try {
            Files.copy(file.getContent(), name);
        } catch (Exception e) {
            throw new ElepyException("Failed to upload file: " + file.getName());
        }
    }

    @Override
    public synchronized UploadedFile readFile(String name) {
        final Path path = Paths.get(rootFolderLocation + File.separator + name);
        try {
            final String[] split = name.split("\\.");
            return UploadedFile.of(tika.detect(path), Files.newInputStream(path), name, split[split.length - 1]);
        } catch (IOException e) {
            throw new ElepyException("Failed at retrieving file: " + name, 500);
        }
    }

    @Override
    public List<String> listFiles() {
        return null;
    }

}