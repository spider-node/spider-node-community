package cn.spider.framework.gateway.api.file;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.gateway.common.ResponseData;
import cn.spider.framework.gateway.oss.OssConfigClient;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
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

    public FileHandler(OssConfigClient ossConfigClient, String bpmnPatch, String sdkPacth, Vertx vertx) {
        this.ossConfigClient = ossConfigClient;
        this.bpmnPath = bpmnPatch;
        this.sdkPath = sdkPacth;
        this.vertx = vertx;
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
                    uploadOssClient(upload,response,sdkPath);
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
                    uploadOssClient(upload,response,bpmnPath);
                });
    }

    private void uploadOssClient(FileUpload upload,HttpServerResponse response,String ossPatch) {
        vertx.fileSystem().readFile(upload.uploadedFileName(), result -> {
            if (result.succeeded()) {
                Buffer buffer = result.result();

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.getBytes());
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
