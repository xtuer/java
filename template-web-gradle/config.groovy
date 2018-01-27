// 打包: gradle clean build  -Denv=production
// 部署: gradle clean deploy -Denv=production

environments {
    // 开发环境配置
    development {
        deploy {
            hostname = '127.0.0.1'
            username = 'root'
            password = 'root'
        }

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

        idWorker = 0
        thymeleafCacheable = false
    }

    // 线上环境配置
    production {
        deploy {
            hostname = '120.92.26.194'
            username = 'root'
            password = 'xxxx'
        }

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

        idWorker = 0
        thymeleafCacheable = true
    }
}
