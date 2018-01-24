* 打包: `gradle clean shadowJar`，生成 `build/libs/import-ebag.zip` (忽略)

* MySQL 中创建数据表: 导入 `shitu_paper.sql`

* 执行
  1. 解压 **import-ebag.zip**

  2. 把导出的文件目录复制到服务器，目录如:

     ```
     .
     └── 20171101-111758
         ├── knowledgePoints.json
         ├── paperDirectories.json
         ├── paperKnowledgePointRelation.json
         ├── papers
         │   ├── docs
         │   │   └── 7147825F-91F2-42A8-9CDB-854E1BD9F6C1.doc
         │   │   └── AE0E4D19-45EE-4EE9-AA8A-BC89913C2D52.doc
         │       └── F358C958-BE6A-489E-B9F5-081AA331BB27.doc
         └── papers.json
     ```

  3. 修改配置:
     * 试卷的信息: **config/config.yml**
       * tenantCode: 要导入到的租户的 code
       * paperMetaDirectory: 导出文件的目录路径
     * 数据库信息: **config/mybatis.xml**
       * url: JDBC 连接字符串，注意修改 `数据库名` 和 `字符集` (电子书包里字符集修改为 GB2312)
       * 用户名和密码

  4. 导入试卷: `java -Dfile.encoding=UTF-8 Main`

  5. 把试卷文件目录 papers 复制到指定的目录，以便电子书包里能够访问


