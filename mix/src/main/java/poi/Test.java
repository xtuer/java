package poi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        List<Student> students = new LinkedList<>();
        students.add(new Student().setId(1).setName("无底").setGender(1).setBirthday(new Date()));
        students.add(new Student().setId(2).setName("黄彪").setGender(1).setBirthday(new Date()));
        students.add(new Student().setId(3).setName("雷雷").setGender(2).setBirthday(new Date()));
        Workbook book = ExcelExportUtil.exportExcel(new ExportParams("计算机一班学生", "学生"), Student.class, students);
        book.write(new FileOutputStream("/Users/Biao/Desktop/x.xls"));
    }
}
