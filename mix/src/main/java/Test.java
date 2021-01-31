import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        Connection connection = null;
        Statement statement;
        ResultSet resultSet;
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");//这个驱动不能是其他的..
            connection = DriverManager.getConnection("jdbc:odbc:ldoa", "root", "root"); //user是data Source ，root是用

            System.out.println("open easy");
            String query = "Select * from user";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            connection.close();
            System.out.println("close easy");

        }
        catch (ClassNotFoundException cnfex) {
            System.err.println(
                    "装载 JDBC/ODBC 驱动程序失败。");
            cnfex.printStackTrace();
            System.exit(1); // terminate program
        }
        catch (SQLException sqlex) {
            System.err.println("无法连接数据库");
            sqlex.printStackTrace();
            System.exit(1); // terminate program
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
