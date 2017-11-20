* 打包: `gradle -Denv=production clean shadowJar`，生成 `build/libs/import-admin.zip`
* 执行
  1. 解压 **import-admin.zip**
  2. 把试卷文件和试卷元数据文件复制到服务器
  3. 修改配置:
     * 试卷的信息: **config/config.yml**
     * 数据库信息: **config/mybatis.xml**
  4. 导入试卷: `java PaperImporter`
  5. 导入试卷元数据: `java PaperMetaImporter`


