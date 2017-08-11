import com.xiaoleilu.hutool.crypto.SecureUtil;
import com.xiaoleilu.hutool.crypto.symmetric.DES;
import com.xiaoleilu.hutool.crypto.symmetric.SymmetricAlgorithm;

import java.util.Base64;

public class EncryptTest {
    public static void main(String[] args) {
        //随机生成密钥
//        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue()).getEncoded();
//        System.out.println(Base64.getEncoder().encodeToString(key));

        //构建
        DES des = SecureUtil.des(Base64.getDecoder().decode("Ulc7UkyeExU="));

        String encryptHex = des.encryptHex("沙海");
        String decryptStr = des.decryptStr(encryptHex);
        System.out.println(decryptStr);

        System.out.println(des.decryptStr(des.encryptHex("警惕反动分子")));
        System.out.println(des.decryptStr(des.encryptHex("Warning")));
    }
}
