package top.lvpi.utils;

import top.lvpi.common.BusinessException;
import top.lvpi.common.ErrorCode;
import top.lvpi.model.dto.file.FileUploadResult;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class MinioUtils {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioUtils(MinioClient minioClient, @Value("${minio.bucketName}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    /**
     * 上传文件到MinIO
     * @param file 文件
     * @return 上传结果
     */
    public FileUploadResult uploadFile(File file) {
        try {
            // 计算文件MD5
            String md5;
            try (FileInputStream fis = new FileInputStream(file)) {
                md5 = calculateMD5(fis);
            }
            log.info("文件MD5: {}", md5);

            // 查找是否已存在相同文件
            String existingFileName = findExistingFile(md5);
            if (existingFileName != null) {
                log.info("找到已存在的文件: {}", existingFileName);
                return new FileUploadResult(existingFileName, 0);
            }

            // 如果文件不存在，则上传新文件
            String extension = FileUtils.getFileExtension(file.getName());
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 设置自定义元数据
            Map<String, String> userMetadata = new HashMap<>();
            userMetadata.put("md5", md5);
            
            // 上传文件到MinIO
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(new FileInputStream(file), file.length(), -1)
                .contentType(FileUtils.getContentType(file.getName()))
                .userMetadata(userMetadata)
                .build());
            
            log.info("文件上传成功: {}", fileName);
            return new FileUploadResult(fileName, 0);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 计算文件的MD5值
     */
    private String calculateMD5(InputStream inputStream) throws IOException {
        try {
            return DigestUtils.md5Hex(inputStream);
        } finally {
            inputStream.close();
        }
    }

    /**
     * 根据MD5值查找已存在的文件
     */
    private String findExistingFile(String md5) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix("")
                    .recursive(true)
                    .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                Map<String, String> userMetadata = minioClient.statObject(
                    StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(item.objectName())
                        .build()
                ).userMetadata();
                
                if (userMetadata != null && md5.equals(userMetadata.get("md5"))) {
                    return item.objectName();
                }
            }
        } catch (Exception e) {
            log.error("查找已存在文件失败", e);
        }
        return null;
    }

    /**
     * 获取文件输入流
     */
    public InputStream getFileInputStream(String fileName) throws IOException {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
        } catch (Exception e) {
            throw new IOException("获取文件失败：" + e.getMessage(), e);
        }
    }
} 