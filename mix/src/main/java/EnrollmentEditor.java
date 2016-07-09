import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.util.Scanner;

public class EnrollmentEditor extends PropertyEditorSupport {
    private static Logger logger = LoggerFactory.getLogger(EnrollmentEditor.class);
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String[] tokens = text.split(" ");

        if (tokens.length != 19) {
            logger.info(text);
            super.setValue(null);
        }

        // Other checks

        Enrollment enrollment = new Enrollment(tokens[0], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7]);
        super.setValue(enrollment);
    }

    // 测试
    public static void main(String[] args) throws Exception {
//        String[] enrollmentStrings = {
//                "63300011 张爱芳 100101198910240012 00208 国际财务管理 101102 北京市通州区 9090 北京大学 31 04 8888 2016-06-22 00:00:00 2016-06-22 18:30:00 是 9090 北京大学",
//                "63300012 张爱芳 100101198910240012 00208 国际财务管理 101102 北京市通州区 9090 北京大学 31 04 8888 2016-06-22 00:00:00 2016-06-22 18:30:00 是 9090 北京大学",
//                "63300013 张爱芳 100101198910240012 00208 国际财务管理 101102 北京市通州区 9090 北京大学 31 04 8888 2016-06-22 00:00:00 2016-06-22 18:30:00 是 9090 北京大学",
//                "63300013 张爱芳 100101198910240012 00208 国际财务管理 101102"
//        };
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        for (String enrollmentString : enrollmentStrings) {
//            EnrollmentEditor editor = new EnrollmentEditor();
//            editor.setAsText(enrollmentString);
//
//            Enrollment enrollment = (Enrollment) editor.getValue();
//            System.out.println(mapper.writeValueAsString(enrollment));
//        }
        importEnrollment();
    }

    public static void importEnrollment() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Scanner scanner = new Scanner(new File("/Users/Biao/Desktop/201607-1.txt"));

        while (scanner.hasNextLine()) {
            String enrollmentString = scanner.nextLine().trim();
            EnrollmentEditor editor = new EnrollmentEditor();
            editor.setAsText(enrollmentString);

            Enrollment enrollment = (Enrollment) editor.getValue();
            System.out.println(mapper.writeValueAsString(enrollment));
        }
    }
}
