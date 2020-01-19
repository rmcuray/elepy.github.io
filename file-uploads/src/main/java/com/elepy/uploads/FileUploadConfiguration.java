package com.elepy.uploads;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Property;

public class FileUploadConfiguration implements Configuration {
    private final DirectoryFileService directoryFileService;

    @ElepyConstructor
    public FileUploadConfiguration(

            @Property(key = "${uploads.location}") String rootFolderLocation) {
        directoryFileService = new DirectoryFileService(rootFolderLocation);
    }

    public static FileUploadConfiguration of(String rootFolder) {
        return new FileUploadConfiguration(rootFolder);
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.withUploads(directoryFileService);
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
