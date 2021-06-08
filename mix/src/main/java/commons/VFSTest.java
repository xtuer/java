package commons;

import org.apache.commons.vfs2.*;
import org.apache.curator.shaded.com.google.common.base.Charsets;

import java.io.IOException;

// VFS 指的是虚拟文件系统，其实就是操作各种来源的文件的一套统一的 API
// 依赖: implementation 'org.apache.commons:commons-vfs2:2.8.0'
public class VFSTest {
    public static void main(String[] args) throws IOException {
        FileSystemManager manager = VFS.getManager();

        // 本地文件:
        // A. 需要应该使用 file:// 协议，/ 开头表示绝对路径，Win 下后面再跟上带盘符的绝对路径
        // B. 直接使用绝对路径也可以，如 C:/temp/db-1.yaml
        // FileObject fo = manager.resolveFile("file:///C:/temp/db-1.yaml");

        // Jar: Jar 包内文件的路径需要使用 ! 开头
        FileObject fo = manager.resolveFile("jar:///D:/workspace/Java/ldoa/ldoa-web-boot/build/libs/ldoa.jar!/META-INF/MANIFEST.MF");

        // Zip:
        // FileObject fo = manager.resolveFile("zip:///C:/temp/db-1.zip!/db-1.yaml");

        // SFTP:
        // A. 需要再添加依赖 jsch: 'com.jcraft:jsch:0.1.55'
        // B. 如果密码中包含 @，要替换未 %40
        // C. 文件路径是相对于当前用户目录，例如 /root/x.sh 使用相对路径 x.sh 进行访问
        // FileObject fo = manager.resolveFile("sftp://root:Newdt%40cn@192.168.1.161/x.sh");

        FileContent fc = fo.getContent();

        System.out.println(fc.getString(Charsets.UTF_8));
    }
}
