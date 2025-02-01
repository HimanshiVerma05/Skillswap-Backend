package com.dal.skillswap.service.impl;

import com.dal.skillswap.service.FileStoreService;
import com.dal.skillswap.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static com.dal.skillswap.constants.PathConstants.PROFILE_IMAGES;

@Service
public class FileStoreServiceImpl implements FileStoreService {

    @Autowired
    UserService userService;

    /**
     * Stores the file
     *
     * @param file     File
     * @param location Location to store the file
     * @return Name of the stored file
     */
    @Override
    public String store(MultipartFile file, String location) {
        Path rootLocation = Paths.get(location);
        String uploadedFileName = "";
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());

            /*generate a random unique name for the image*/
            uploadedFileName = UUID.randomUUID().toString() + "." + extension;
            Files.createDirectories(rootLocation);
            Path destinationFile = rootLocation.resolve(Paths.get(uploadedFileName))
                    .normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
                if (location.equals(PROFILE_IMAGES)) {
                    /*Delete older profile picture*/
                    String oldFile = userService.getProfilePicture();
                    if (oldFile != null && !oldFile.isEmpty()) {
                        delete(oldFile, location);
                    }
                    /*Store new profile picture*/
                    boolean stored = userService.setProfilePicture(uploadedFileName);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
        return uploadedFileName;
    }

    /**
     * Load the file by its name
     *
     * @param filename Name of the file
     * @param location Location to load the file from
     * @return File
     */
    @Override
    public Resource load(String filename, String location) {
        Path rootLocation = Paths.get(location);
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    /**
     * Delete existing file
     *
     * @param filename Name of the file
     * @param location Location of the file
     * @return True if the file was deleted otherwise false
     */
    @Override
    public boolean delete(String filename, String location) {
        Path rootLocation = Paths.get(location);
        try {
            Path file = rootLocation.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
