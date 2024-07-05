package cn.spider.framework.gateway.api.file;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.gateway.common.ResponseData;
import cn.spider.framework.gateway.enums.FileServerType;
import cn.spider.framework.gateway.minio.MinioManager;
import cn.spider.framework.gateway.oss.OssConfigClient;
import io.minio.MinioClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.gateway.api.file
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-15  13:43
 * @Description: 文件的api交互
 * @Version: 1.0
 */
@Slf4j
public class FileHandler {

    private Router router;

    private OssConfigClient ossConfigClient;

    private Vertx vertx;

    private String bpmnPath;

    private String sdkPath;

    private FileServerType fileServerType;

    private MinioManager minioManager;

    public FileHandler(String bpmnPatch, String sdkPacth, Vertx vertx) {
        this.bpmnPath = bpmnPatch;
        this.sdkPath = sdkPacth;
        this.vertx = vertx;
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        this.fileServerType = FileServerType.valueOf(localMap.get("file_server_type"));
        if (this.fileServerType.equals(FileServerType.MINIO)) {
            String endpoint = localMap.get("minio_url");
            String accessKey = localMap.get("minio_access_key");
            String secretKey = localMap.get("minio_secret_key");
            String bucketName = localMap.get("minio_bucket_name");
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            this.minioManager = new MinioManager(minioClient,bucketName);
        } else if (this.fileServerType.equals(FileServerType.OSS)) {
            String endpoint = localMap.get("oss_endpoint");
            String keyId = localMap.get("oss_keyId");
            String keySecret = localMap.get("oss_keySecret");
            String bucketName = localMap.get("oss_bucketName");
            this.ossConfigClient = new OssConfigClient(endpoint, keyId, keySecret, bucketName);
        }
    }

    public void init(Router router) {
        this.router = router;
        uploadSdk();
        uploadBpmn();
    }

    /**
     * 上传 jar文件到该路径
     */
    public void uploadSdk() {
        router.post("/upload/sdk")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    List<FileUpload> uploads = ctx.fileUploads();
                    if (CollectionUtils.isEmpty(uploads)) {
                        response.end(ResponseData.fail("没有找到上传的文件"));
                        return;
                    }
                    FileUpload upload = uploads.get(0);
                    uploadOssClient(upload, response, sdkPath);
                });
    }

    /**
     * 上传 jar文件到该路径
     */
    public void uploadBpmn() {
        router.post("/upload/bpmn")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    List<FileUpload> uploads = ctx.fileUploads();
                    if (CollectionUtils.isEmpty(uploads)) {
                        response.end(ResponseData.fail("没有找到上传的文件"));
                        return;
                    }
                    FileUpload upload = uploads.get(0);
                    uploadOssClient(upload, response, bpmnPath);
                });
    }

    private void uploadOssClient(FileUpload upload, HttpServerResponse response, String ossPatch) {
        vertx.fileSystem().readFile(upload.uploadedFileName(), result -> {
            if (result.succeeded()) {
                Buffer buffer = result.result();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.getBytes());
                if(fileServerType.equals(FileServerType.MINIO)){
                    String contentType = upload.contentType();
                    String newFileName = System.currentTimeMillis() + upload.fileName();
                    minioManager.uploadFile(byteArrayInputStream, newFileName, contentType);
                    String url = minioManager.getPresignedObjectUrl(newFileName);
                    response.end(ResponseData.suss(new JsonObject().put("patch", url)));
                    return;
                }
                OssConfigClient.UploadFile uploadFile = new OssConfigClient.UploadFile();
                uploadFile.setFileName(System.currentTimeMillis() + upload.fileName());
                uploadFile.setFilePath(ossPatch);
                uploadFile.setInputStream(byteArrayInputStream);
                try {
                    String patch = ossConfigClient.upload(uploadFile);
                    response.end(ResponseData.suss(new JsonObject().put("patch", patch)));
                    // 移除数据
                } catch (Exception e) {
                    response.end(ResponseData.fail(ExceptionMessage.getStackTrace(e)));
                } finally {
                    vertx.fileSystem().delete(upload.uploadedFileName());
                }
            } else {
                response.end(ResponseData.fail(ExceptionMessage.getStackTrace(result.cause())));
            }
        });
    }

}
