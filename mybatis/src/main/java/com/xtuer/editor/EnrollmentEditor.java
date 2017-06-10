package com.xtuer.editor;

import com.alibaba.fastjson.JSON;
import com.xtuer.bean.Enrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditorSupport;

public class EnrollmentEditor extends PropertyEditorSupport {
    private static Logger logger = LoggerFactory.getLogger(EnrollmentEditor.class);

    /**
     * 把字符串转化为 Enrollment 对象，如果转换失败，则对象为 null，不抛出异常
     *
     * @param text
     * @throws IllegalArgumentException
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String[] tokens = text.split("\\s+");

        if (tokens.length < 18 || text.startsWith("考籍号")) {
            logger.warn("Invalid information: {}", text);
            super.setValue(null);
            return;
        }

        // Other checks

        Enrollment enrollment = new Enrollment(tokens[0], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7]);
        super.setValue(enrollment);
    }

    public static void main(String[] args) throws Exception {
        String[] enrollmentTexts = {
                "63300011 张爱芳 100101198910240012 00208 国际财务管理 101102 北京市通州区 9090 北京大学 31 04 8888 2016-06-22 00:00:00 2016-06-22 18:30:00 是 9090 北京大学",
                "63300012 张爱芳 100101198910240012 00208 国际财务管理 101102 北京市通州区 9090 北京大学 31 04 8888 2016-06-22 00:00:00 2016-06-22 18:30:00 是 9090 北京大学",
                "63300013 张爱芳 100101198910240012 00208 国际财务管理 101102 北京市通州区 9090 北京大学 31 04 8888 2016-06-22 00:00:00 2016-06-22 18:30:00 是 9090 北京大学",
                "63300013 张爱芳 100101198910240012 00208 国际财务管理 101102"
        };

        for (String text : enrollmentTexts) {
            EnrollmentEditor editor = new EnrollmentEditor();
            editor.setAsText(text);

            Enrollment enrollment = (Enrollment) editor.getValue();
            System.out.println(JSON.toJSONString(enrollment));
        }
    }
}
