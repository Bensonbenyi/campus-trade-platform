package com.campus.trade.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String storeImage(MultipartFile file, Long userId);

    Resource loadImage(String yearMonth, Long userId, String filename);
}
