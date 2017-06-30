import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        用户 新用户 = new 用户();
        新用户.名字 = "道格拉斯·狗";
        新用户.年龄 = 55;

        System.out.println(新用户.名字 + ", " + 新用户.年龄);
    }
}

class 用户 {
    public String 名字;
    public int 年龄;
}
