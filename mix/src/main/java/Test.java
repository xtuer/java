import java.io.File;
import java.nio.file.Files;

public class Test {
    public static void main(String[] args) throws Exception {
        File configFile = new File("/");
        final String configYAML = String.join("\n", Files.readAllLines(configFile.toPath()));
        // final Config config = Config.fromKubeconfig(configYAML);
    }
}
