package xyz.bolitao.cloudmall.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CategoryProductConstant {
    public static String FILE_UPLOAD_DIR;

    @Value("${bolitao.file-upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }
}
