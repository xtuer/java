// 打包命令: gradle clean build -Denv=production

environments {
    // 开发环境配置
    development {
        database {
            url = 'jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'root'
        }

        redis {
            host = '127.0.0.1'
            port = 6379
            password = ''
            database = 0
            timeout  = 2000
        }

        idWorker {
            datacenterId = 0
            workerId = 0
        }

        logDir = '/temp/logs'
        thymeleafCacheable = false
    }

    // 线上环境配置
    production {
        database {
            url = 'jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'huaxia-123'
        }

        redis {
            host = '127.0.0.1'
            port = 6379
            password = ''
            database = 0
            timeout  = 2000
        }

        idWorker {
            datacenterId = 0
            workerId = 0
        }

        logDir = '/temp/logs'
        thymeleafCacheable = true
    }
}
