package js;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class BaseTest {
    public static void main(String[] args) throws ScriptException, NoSuchMethodException, FileNotFoundException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn"); // JavaScript
        engine.eval(new FileReader("C:/temp/js/Jexl.js"));
        // engine.eval(new FileReader("C:/temp/js/java-1.js"));

        // Invocable invocable = (Invocable) engine;

        // Object result = invocable.invokeFunction("foo", "{\"id\": 1}");
        // System.out.println(result);

        // result = invocable.invokeFunction("Utils.hello", "{\"id\": 1}");
        // System.out.println(result);
    }
}
