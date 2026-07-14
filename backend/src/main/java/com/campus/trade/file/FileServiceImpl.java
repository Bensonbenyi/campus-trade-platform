package com.campus.trade.file;

import com.campus.trade.common.constant.Constant;
import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.result.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };
    private final Path uploadRoot;

    public FileServiceImpl(@Value("${app.file.upload-dir:./uploads}") String uploadDirectory) {
        this.uploadRoot = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    @Override
    public String storeImage(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择要上传的图片");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (file.getSize() > Constant.MAX_IMAGE_SIZE) {
            throw new BusinessException(ResultCode.FILE_TOO_LARGE);
        }

        String originalExtension = getOriginalExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(originalExtension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED);
        }

        String detectedExtension = detectImageExtension(file);
        if (!extensionMatches(originalExtension, detectedExtension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED, "文件扩展名与图片内容不一致");
        }

        String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String filename = UUID.randomUUID() + "." + detectedExtension;
        Path userDirectory = resolveInsideRoot(yearMonth, userId.toString());
        Path destination = userDirectory.resolve(filename).normalize();
        if (!destination.startsWith(userDirectory)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "非法文件路径");
        }

        try {
            Files.createDirectories(userDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "图片保存失败");
        }
        return "/uploads/" + yearMonth + "/" + userId + "/" + filename;
    }

    @Override
    public Resource loadImage(String yearMonth, Long userId, String filename) {
        if (yearMonth == null || !yearMonth.matches("\\d{6}")
                || userId == null || userId <= 0
                || filename == null || !filename.matches("[0-9a-fA-F-]{36}\\.(jpg|png|webp)")) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Path path = resolveInsideRoot(yearMonth, userId.toString(), filename);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.isReadable() || !resource.isFile()) {
                throw new BusinessException(ResultCode.NOT_FOUND);
            }
            return resource;
        } catch (MalformedURLException exception) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
    }

    private String detectImageExtension(MultipartFile file) {
        byte[] header = new byte[12];
        int bytesRead;
        try (InputStream inputStream = file.getInputStream()) {
            bytesRead = inputStream.read(header);
        } catch (IOException exception) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无法读取上传文件");
        }
        if (bytesRead >= 3
                && header[0] == (byte) 0xFF
                && header[1] == (byte) 0xD8
                && header[2] == (byte) 0xFF) {
            return "jpg";
        }
        if (bytesRead >= PNG_SIGNATURE.length
                && Arrays.equals(Arrays.copyOf(header, PNG_SIGNATURE.length), PNG_SIGNATURE)) {
            return "png";
        }
        if (bytesRead >= 12
                && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
            return "webp";
        }
        throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED);
    }

    private String getOriginalExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0 || dot == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private boolean extensionMatches(String original, String detected) {
        return original.equals(detected) || (original.equals("jpeg") && detected.equals("jpg"));
    }

    private Path resolveInsideRoot(String... parts) {
        Path resolved = uploadRoot;
        for (String part : parts) {
            resolved = resolved.resolve(part);
        }
        resolved = resolved.normalize();
        if (!resolved.startsWith(uploadRoot)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "非法文件路径");
        }
        return resolved;
    }
}
