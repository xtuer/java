## 开发环境

* 运行: 执行类 Foo 的 main 函数 `gradle run -DmainClass=Foo`

  > 为什么不在 IDE 中运行呢？因为未打包前配置在 config.groovy 中，直接从 IDEA 中运行 main 函数不会把配置自动替换到 application.properties 文件里，有可能因为找不到配置而运行失败

* 打包: `gradle -Denv=production clean shadowJar`，生成 `build/libs/impex.zip`

## 线上环境

* CSV 知识点转换为 JSON 知识点，构建知识点树
  * 配置
    * `csvKpDir`: 知识点的 CSV 文件存放目录
    * `jsonKpDir`: 知识点的 JSON 文件保存目录

  * 运行 `java -Dfile.encoding=UTF-8 QuestionKnowledgePointFromCsvToJson`

    > -Dfile.encoding=UTF-8 指定运行时的编码为 UTF-8，避免使用系统默认编码(Windows 为 GB2312，Mac 为 UTF-8)
* 导入知识点到数据库
  * 配置
    * `jsonKpDir`: 知识点的 JSON 文件保存目录
  * 运行 `java -Dfile.encoding=UTF-8 ImportQuestionKnowledgePoint`
* 下载题目的 HTML 文件和图片
  * 运行 `java -Dfile.encoding=UTF-8 QuestionDownloader`


