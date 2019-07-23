import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * 知识点解析工具，把二维的 Excel 知识点生成树形结构的知识点。
 * 调用 parse() 进行解析 Excel 生成知识点树，save(dir) 把知识点树保存到文件。
 */
public class KpParser {
    private String excel; // 知识点的 Excel 路径
    private Kp     root;  // 知识点树的根节点

    /**
     * 使用知识点的 Excel 路径创建知识点树的解析器
     *
     * @param excel 知识点的 Excel 路径
     */
    public KpParser(String excel) {
        this.excel = excel;
    }

    /**
     * 解析生成知识点树
     *
     * @throws Exception 操作文件异常
     */
    public void parse() throws Exception {
        // 1. 读取 Excel 数据到二维数组中 (使用 ArrayList<ArrayList<String>>)
        // 2. 逐行构建知识点路径序列
        // 3. 构建知识点树

        // [1] 读取 Excel 数据到二维数组中 (使用 ArrayList<ArrayList<String>>)
        List<List<String>> table = getExcelData(excel, 0);
        table.forEach(System.out::println);
        // 输出:
        // [266, 化学, 10, 常见无机物及其运用, 04, 氮气, 08, 氮气, 01, 氮气, 01, 氮气, 99, 高中必修, 41, 10040801019941, ...]
        // [1, 化学, 10, 化学科学与实验探究, 01, 实验基础, 01, 实验安全, 01, 实验室安全, 01, 实验室安全, 99, 初中, 30, 10010101019930, ...]
        // [2, 化学, 10, 化学科学与实验探究, 01, 实验基础, 01, 实验安全, 01, 化学试剂的保存, 02, 化学试剂的保存, 99, 初中, 30, 10010101029930, ...]

        // [2] 逐行构建知识点路径序列
        List<List<Kp>> rowsKps = buildKpsByRow(table);
        rowsKps.forEach(System.out::println);
        // 输出:
        // [化学-10, 常见无机物及其运用-1004, 氮气-100408, 氮气-10040801, 氮气-1004080101, 氮气-100408010199, 高中必修-10040801019941]
        // [化学-10, 化学科学与实验探究-1001, 实验基础-100101, 实验安全-10010101, 实验室安全-1001010101, 实验室安全-100101010199, 初中-10010101019930]
        // [化学-10, 化学科学与实验探究-1001, 实验基础-100101, 实验安全-10010101, 化学试剂的保存-1001010102, 化学试剂的保存-100101010299, 初中-10010101029930]

        // [3] 构建知识点树
        root = buildKpTree(rowsKps);
    }

    /**
     * 输出知识点树到目录 dir 下，生成 2 个文件: json 格式的文件 kp-tree.json 和普通的树格式 kp-tree.txt
     *
     * @param dir 保存文件的目录
     * @throws IOException
     */
    public void save(String dir) throws IOException {
        // A. 使用 JSON 的方式输出树结构
        FileUtils.writeStringToFile(new File(dir, "kp-tree.json"), JSON.toJSONString(root, true), "UTF-8");

        // B. 使用缩进的方式输出树结构
        StringBuilder sb = new StringBuilder();
        treeWalk(root, 0, sb);
        FileUtils.writeStringToFile(new File(dir, "kp-tree.txt"), sb.toString(), "UTF-8");
    }

    /**
     * 使用字符串的二维数组存储的知识点按行构建知识点路径序列
     *
     * @param table 字符串的二维数组
     * @return 返回按行构建知识点序列
     */
    private List<List<Kp>> buildKpsByRow(List<List<String>> table) {
        // 1. 第二行起，逐行遍历，每行构建一个知识点的路径序列，从根到叶节点
        // 2. 忽略第一列: 序号
        // 3. 每行知识点的路径序列保存为一个数组 rowKps，从第二列 (include) 到倒数第二列 (exclude):
        //    3.1 每 2 列创建一个知识点对象，前一个对象是后一个对象的 parent
        //    3.2 知识点的完整编码为 parent 的编码 + excel 中给的编码
        // 4. 倒数第二列是给定的知识点编码，但是有可能是错的，因为 3.2 中根据知识点的层级编码组合出了完整的编码
        // 5. 最后一列为知识点的说明，保存到最后一个知识点的 info 属性中
        // 6. 把所有行的知识点序列保存到 rowsKps

        List<List<Kp>> rowsKps = new ArrayList<>(); // 所有行的知识点序列

        // [1] 第二行起，逐行遍历，每行构建一个知识点的路径序列，从根到叶节点
        for (int row = 1; row < table.size(); ++row) {
            List<String> rowData = table.get(row);
            List<Kp> rowKps = new LinkedList<>();
            Kp parent = null;           // 父级知识点
            int cols  = rowData.size(); // 列数

            // [3] 每行知识点的路径序列保存为一个数组 rowKps，从第二列 (include) 到倒数第二列 (exclude):
            for (int col = 1; col < cols-2; ++col) {
                if (col + 1 < cols) {
                    // [3.1] 每 2 列创建一个知识点对象，前一个对象是后一个对象的 parent
                    String name = rowData.get(col);
                    String code = rowData.get(col + 1);
                    Kp kp = new Kp(name, code, parent == null ? "" : parent.code);

                    rowKps.add(kp);

                    parent = kp;
                    col++;
                }
            }

            // [4] 倒数第二列是给定的知识点编码，但是有可能是错的，因为 3.2 中根据知识点的层级编码组合出了完整的编码
            parent.originalCode = rowData.get(cols-2);

            // [5] 最后一列为知识点的说明，保存到最后一个知识点的 info 属性中
            parent.info = rowData.get(cols-1);

            // [6] 把所有行的知识点序列保存到 rowsKps
            rowsKps.add(rowKps);
        }

        return rowsKps;
    }

    /**
     * 逐行的知识点转换为树结构的知识点
     *
     * @param rowsKps 知识点的二维数组
     * @return 返回树的根节点
     */
    private Kp buildKpTree(List<List<Kp>> rowsKps) {
        // 注意: 使用编码来确定一个知识点
        // 1. 遍历所有的知识点
        // 2. 如果编码相同的知识点已经添加到 hierarchyKps 就不重复添加
        // 3. 在 hierarchyKps 中查找父知识点，当前知识点添加到父知识点的 children 中
        // 4. 返回知识点树的根节点 (parentCode 为空)

        Map<String, Kp> hierarchyKps = new TreeMap<>();
        rowsKps.forEach((kps) -> {
            for (Kp kp : kps) {
                // [2] 如果编码相同的知识点已经添加到 hierarchyKps 就不重复添加
                if (hierarchyKps.containsKey(kp.code)) {
                    continue;
                }

                hierarchyKps.put(kp.code, kp);

                // [3] 在 hierarchyKps 中查找父知识点，当前知识点添加到父知识点的 children 中
                Kp parent = hierarchyKps.get(kp.parentCode);
                if (parent != null) {
                    parent.children.add(kp);
                }
            }
        });

        // [4] 返回知识点树的根节点 (parentCode 为空)
        for (Kp kp : hierarchyKps.values()) {
            if (StringUtils.isNotBlank(kp.code)) {
                return kp;
            }
        }

        return null;
    }

    /**
     * 递归、缩进的方式输出知识点树
     *
     * @param kp     知识点
     * @param indent 缩进量
     */
    private void treeWalk(Kp kp, int indent, StringBuilder sb) {
        // 输出缩进，然后输出知识点
        sb.append(StringUtils.repeat("    ", indent)).append(kp.toString()).append("\n");

        // 递归遍历子知识点
        for (Kp child : kp.children) {
            treeWalk(child, indent + 1, sb);
        }
    }

    /**
     * 读取 Excel 数据到二维数组中 (使用 ArrayList<ArrayList<String>>)
     *
     * @param path        Excel 的路径
     * @param sheetNumber Sheet 的序号，从 0 开始
     * @return 返回 Excel 内容的二维数组
     * @throws Exception
     */
    public static List<List<String>> getExcelData(String path, int sheetNumber) throws Exception {
        // 1. 创建 Excel 的 Workbook
        // 2. 获取 sheet 和行数、列数
        // 3. 按行列读取 Excel 的内容到二维数组中
        // 4. 返回内容的二维数组

        // [1] 创建 Excel 的 Workbook
        Workbook wb;
        File file = new File(path);

        if (file.getName().endsWith(".xls")) { // Excel 2003
            wb = new HSSFWorkbook(new POIFSFileSystem(file));
        } else {
            wb = new XSSFWorkbook(file); // Excel 2007
        }

        // [2] 获取 sheet 和行数、列数
        Sheet sheet = wb.getSheetAt(sheetNumber);
        int rows = sheet.getLastRowNum() + 1;                  // 行数
        int cols = sheet.getRow(0).getPhysicalNumberOfCells(); // 列数

        // [3] 按行列读取 Excel 的内容到二维数组中
        Set<String> error = new TreeSet<>();
        List<List<String>> table = new ArrayList<>();

        for (int row = 0; row < rows; ++row) {
            List<String> rowData = new ArrayList<>();
            for (int col = 0; col < cols; ++col) {
                rowData.add(getCellValue(sheet, row, col, String.class, error));
            }
            table.add(rowData);
        }

        // 如果有错误信息则输出
        if (!error.isEmpty()) {
            error.forEach(System.out::println);
        }

        // [4] 返回内容的二维数组
        return table;
    }

    /**
     * 从单元格中获取值指定类型的值: 整数、浮点数、字符串
     *
     * @param sheet  Excel 表格
     * @param rowNum 行号
     * @param colNum 列号
     * @param clazz  返回类型
     * @param result 验证结果
     * @return 单元格中的数据
     */
    public static <T> T getCellValue(Sheet sheet, int rowNum, int colNum, Class<T> clazz, Set<String> result) throws Exception {
        // 1. 定义指定参数类型的构造函数
        // 2. 判断指定行是否存在，不存在默认返回 0
        // 3. 判断指定单元格是否存在，不存在默认返回 0
        // 4. 判断单元格格式是否正确，不存在默认返回 0
        // 5. 如果是整数去掉 POI 在末尾自动追加的小数，返回对应类型结果

        // [1] 定义指定参数类型的构造函数
        Constructor<T> constructor = clazz.getConstructor(String.class);

        // [2] 判断指定行是否存在，不存在返回默认值 0
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            result.add("第 " + (rowNum + 1) + " 行没有数据但有格式，请在 Excel 中删除再重新导入<br>");
            return constructor.newInstance("0");
        }

        // [3] 判断指定单元格是否存在，不存在默认返回 0
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            result.add("第 " + (rowNum + 1) + " 行" + "第 " + (char) (colNum + 65) + " 列没值<br>");
            return constructor.newInstance("0");
        }

        // [4] 判断单元格格式是否正确，不正确默认返回 0
        String value = cell.toString();
        if ((clazz.equals(Double.class) || clazz.equals(Integer.class) || clazz.equals(Float.class)) && !value.matches("[\\d.]+")) {
            result.add("第 " + (rowNum + 1) + " 行" + "第 " + (char) (colNum + 65) + " 列格式不正确<br>");
            return constructor.newInstance("0");
        }

        // [5] 如果是整数去掉 POI 在末尾自动追加的小数，返回对应类型结果
        if (clazz.equals(Integer.class)) {
            value = value.split("\\.")[0];
        }

        return constructor.newInstance(value);
    }

    // 知识点类
    @Getter
    @Setter
    @JSONType(ignores = {"parent"})
    public static class Kp {
        String name = "";
        String code = "";
        String info = "";
        String parentCode   = "";
        String originalCode = "";
        Set<Kp> children = new TreeSet<>(Comparator.comparing(Kp::getCode)); // 子节点按 code 升序排序

        /**
         * 创建知识点对象，知识点的编码为父知识点的编码+知识点的短编码
         *
         * @param name       知识点
         * @param shortCode  知识点的短编码 (Excel 中给定的)
         * @param parentCode 父知识点的编码
         */
        public Kp(String name, String shortCode, String parentCode) {
            this.name = name;
            this.code = parentCode + shortCode;
            this.parentCode = parentCode;
        }

        // 编码相同则为同一个知识点
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Kp kp = (Kp) o;
            return Objects.equals(code, kp.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code);
        }

        @Override
        public String toString() {
            return name + "-" + code + ("".equals(originalCode) ? "" : "-"+originalCode);
        }
    }

    public static void main(String[] args) throws Exception {
        // 知识点的文件路径和保存文件夹路径
        // java -jar -Dexcel=xxx -DsaveDir=yyy KpParser.jar
        String excel   = System.getProperty("excel", "/Users/Biao/Desktop/code.xlsx");
        String saveDir = System.getProperty("saveDir", "/Users/Biao/Desktop");

        System.out.println("读取知识点: " + excel);
        System.out.println("保存知识点: " + saveDir + "/kp-tree.json");

        KpParser parser = new KpParser(excel); // 知识点解析器
        parser.parse();       // 开始解析
        parser.save(saveDir); // 输出知识点树
    }
}

