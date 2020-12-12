package yaml;

import com.alibaba.fastjson.JSON;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class AnimalTest {
    public static void main(String[] args) {
        birdTest();
        ostrichTest();
        ostrichTest2();
    }
    public static void birdTest() {
        Yaml yaml = new Yaml(new Constructor(AnimalHolder.class));
        InputStream in = ClassLoader.getSystemResourceAsStream("animal-bird.yml");
        AnimalHolder holder = yaml.load(in); // 需要 Yaml 构造函数中指定 Constructor

        System.out.println(JSON.toJSONString(holder));
    }

    public static void ostrichTest() {
        Yaml yaml = new Yaml(new Constructor(AnimalHolder.class));
        InputStream in = ClassLoader.getSystemResourceAsStream("animal-ostrich.yml");
        AnimalHolder holder = yaml.load(in);

        System.out.println(JSON.toJSONString(holder));
    }

    public static void ostrichTest2() {
        Yaml yaml = new Yaml();
        InputStream in = ClassLoader.getSystemResourceAsStream("animal-ostrich.yml");
        AnimalHolder holder = yaml.loadAs(in, AnimalHolder.class); // loadAs

        System.out.println(JSON.toJSONString(holder));
    }
}
