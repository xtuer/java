## 开发环境

* 运行: 执行类 Foo 的 main 函数 `gradle run -DmainClass=Foo`

  > 为什么不在 IDE 中运行呢？因为未打包前配置在 config.groovy 中，直接从 IDEA 中运行 main 函数不会把配置自动替换到 application.properties 文件里，有可能因为找不到配置而运行失败

* 打包: `gradle -Denv=production clean shadowJar`，生成 `build/libs/impex-all.jar`

## 线上环境

* 知识点的 CSV  文件转换为 JSON 文件，构建知识点树
  * 配置
    * `kpCsvDir`: 知识点的 CSV 文件目录
    * `kpJsonDir`: 知识点的 JSON 文件目录

  * 运行 `java -Dfile.encoding=UTF-8 QuestionKnowledgePointFromCsvToJson`

    > -Dfile.encoding=UTF-8 指定运行时的编码为 UTF-8，避免使用系统默认编码(Windows 为 GB2312，Mac 为 UTF-8)

* 导入知识点到数据库
  * 配置
    * `kpJsonDir`: 知识点的 JSON 文件保存目录
  * 运行 `java -Dfile.encoding=UTF-8 ImportQuestionKnowledgePoint`

* 下载题目的 HTML 文件和图片
  * 运行 `java -Dfile.encoding=UTF-8 QuestionDownloader`

* 根据题目的 HTML 文件、知识点的 JSON 文件和 XML 信息文件，解析题目信息保存为完整题目的 JSON 文件

  * 配置
    * `questionHtmlDir`: 题目的 HTML 文件夹，此文件夹下题目按照科目编码存放
    * `questionInfoDir`: 题目信息的 XML 文件夹，保存了题目的答案，难度，分值等数据
    * `questionJsonDir`: 题目的 JSON 文件夹，此文件夹下题目按照科目编码存放
    * `kpJsonDir`: 知识点的 JSON 文件目录
  * 运行 `java -Dfile.encoding=UTF-8 QuestionParser`

* 从完整题目的 JSON 文件解析出题目，然后导入数据库

  * 配置
    * `config/application.properties` 中的数据库访问信息
    * `questionJsonDir`: 题目的 JSON 文件夹，此文件夹下题目按照科目编码存放
  * 运行 `java -Dfile.encoding=UTF-8 ImportQuestion`

