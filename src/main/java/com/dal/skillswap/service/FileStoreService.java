package com.dal.skillswap.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStoreService {
    String store(MultipartFile file, String location);

    Resource load(String filename, String location);

    boolean delete(String filename, String location);

}
