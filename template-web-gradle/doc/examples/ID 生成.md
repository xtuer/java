配置 SnowflakeIdWorker 的 datacenterId 和 workerId，当不同的服务器使用不同的 datacenterId 和 workerId 时，就能保证他们生成的 ID 是**分布式唯一**的。

> datacenterId 的值为 [0, 31]
> workerId 的值为 [0, 31]
>
> 可以保证 1024 台服务器生成的 ID 在 70 年内是唯一的，理论请参考 [分布式 ID 生成算法 Snowflake](http://qtdebug.com/java-snowflake/)
>
> ![](http://qtdebug.com/img/java/snowflake.png)

生成 ID 的步骤分为:

1. 注入 idWorker

   ```java
   @Autowired
   private DemoMapper demoMapper;
   ```

2. 生成 ID

   ```java
   Long id = idWorker.nextId();
   ```

   ​