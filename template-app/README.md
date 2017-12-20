## 开发环境

* 运行类 Foo: `gradle clean appStart -DmainClass=Foo`

  > 为什么不在 IDE 中运行呢？因为未打包前配置在 config.groovy 中，直接从 IDEA 中运行 main 函数不会把配置自动替换到 application.properties 文件里，有可能因为找不到配置而运行失败

* 运行类 Foo: 也可以先 `gradle build` 把生成的配置文件复制到 IDEA 需要的位置，然后从 IDEA 里运行

* 打包: `gradle -Denv=production clean shadowJar`，生成 `build/libs/impex.zip`

## 线上环境

