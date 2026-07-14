package com.campus.trade.file;

import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.result.ResultCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileServiceImplTest {
    Path uploadRoot;

    @BeforeEach
    void createWorkspaceTempDirectory() throws Exception {
        uploadRoot = Path.of("target", "test-uploads", UUID.randomUUID().toString());
        Files.createDirectories(uploadRoot);
    }

    @AfterEach
    void removeWorkspaceTempDirectory() throws Exception {
        FileSystemUtils.deleteRecursively(uploadRoot);
    }

    @Test
    void storesPngByDetectedMagicBytesAndLoadsIt() throws Exception {
        FileServiceImpl service = new FileServiceImpl(uploadRoot.toString());
        byte[] png = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 1, 2, 3};
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", png);

        String url = service.storeImage(file, 7L);
        String[] segments = url.split("/");
        Resource resource = service.loadImage(segments[2], 7L, segments[4]);

        assertTrue(url.matches("/uploads/\\d{6}/7/[0-9a-f-]{36}\\.png"));
        assertTrue(resource.exists());
        assertEquals(png.length, resource.contentLength());
    }

    @Test
    void rejectsFilesWhoseExtensionDoesNotMatchContent() {
        FileServiceImpl service = new FileServiceImpl(uploadRoot.toString());
        byte[] fakeJpeg = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        MockMultipartFile file = new MockMultipartFile("file", "fake.jpg", "image/jpeg", fakeJpeg);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.storeImage(file, 7L));

        assertEquals(ResultCode.FILE_TYPE_NOT_ALLOWED, exception.getResultCode());
    }

    @Test
    void rejectsNonImageMagicBytes() {
        FileServiceImpl service = new FileServiceImpl(uploadRoot.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "payload.jpg", "image/jpeg", "not-an-image".getBytes());

        assertThrows(BusinessException.class, () -> service.storeImage(file, 7L));
    }
}
