package cn.spider.framework.gateway.oss;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;

@Slf4j
public class OssConfigClient {

    private String endpoint;

    private String keyId;

    private String keySecret;

    private String bucketName;

    private ThreadLocal<OSS> ossThreadLocal;

    public OssConfigClient(String endpoint, String keyId, String keySecret, String bucketName) {
        this.endpoint = endpoint;
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.bucketName = bucketName;
        this.ossThreadLocal = new ThreadLocal<>();
    }

    /**
     * 上传
     * @param uploadFile
     * @throws Exception
     */
    public String upload(UploadFile uploadFile) throws Exception {
        try {
            String path = String.format("%s/%s",uploadFile.getFilePath(),uploadFile.getFileName());
            log.info("[OssConfigClient#upload] fileName：{} path：{}",uploadFile.getFileName(),path);

            OSS oss = createOssConnection();

            // 创建上传文件的元信息，可以通过文件元信息设置HTTP header。
            ObjectMetadata meta = new ObjectMetadata();
            meta.setCacheControl("no-store");

            PutObjectResult putObjectResult = oss.putObject(bucketName, path, uploadFile.getInputStream(),meta);
            log.info("[OssConfigClient#upload] file:{} oss-return：{}",uploadFile.getFileName(), JSON.toJSONString(putObjectResult));

            // 设置签名URL过期时间，单位为毫秒,1个月可见时间
            Date expiration = new Date(new Date().getTime() + 3600 * 1000 * 3600);
            GeneratePresignedUrlRequest signRequest = new GeneratePresignedUrlRequest(bucketName, path, HttpMethod.GET);
            signRequest.setExpiration(expiration);
            URL url = oss.generatePresignedUrl(signRequest);
            return url.toString();
        } finally {
            OSS oss = ossThreadLocal.get();
            if (oss != null) {
                oss.shutdown();
                ossThreadLocal.remove();
            }
        }
    }


    /**
     * 创建OSS连接
     * @param
     */
    private OSS createOssConnection() {
        DefaultCredentialProvider defaultCredentialProvider = CredentialsProviderFactory.newDefaultCredentialProvider(keyId,keySecret);
        OSS ossClient = new OSSClientBuilder().build(endpoint, defaultCredentialProvider);

        ossThreadLocal.set(ossClient);
        return ossClient;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadFile {
        /**
         * 数据字节流
         */
        ByteArrayInputStream inputStream;

        /**
         * 文件名称
         */
        String fileName;

        /**
         * 路劲
         */
        String filePath;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DownloadResourceReq {
        /**
         * 路劲
         */
        String filePath;
    }
}
