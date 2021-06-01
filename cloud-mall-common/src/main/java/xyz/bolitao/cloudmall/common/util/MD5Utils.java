package xyz.bolitao.cloudmall.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.MD5;
import xyz.bolitao.cloudmall.common.common.Constant;

import java.security.NoSuchAlgorithmException;

/**
 * @author boli.tao
 */
public class MD5Utils {
    public static String getMD5WithSaltStr(String rawVal) throws NoSuchAlgorithmException {
        return Base64.encode(MD5.create().digest(rawVal + Constant.SALT));
    }


    public static String getMD5Str(String rawVal) throws NoSuchAlgorithmException {
        return Base64.encode(MD5.create().digest(rawVal));
    }

    public static void main(String[] args) {
        try {
            System.out.println(MD5Utils.getMD5Str("123456"));
            System.out.println(MD5Utils.getMD5WithSaltStr("123456"));
            System.out.println(BCrypt.checkpw("12345678", "$2a$10$TEiFEAtFoBXNPx/LZ601l" +
                    ".Ma1UxJPzC8rxyLaS/9o7XSj4rbBlpjS"));
            System.out.println(BCrypt.checkpw("12345678", "$2a$10$TEiFEAtFoBXNPx/LZ601l" +
                    ".Ma1UxJPzC8rxyLaS/9o7XSj4rbBlpjG"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
