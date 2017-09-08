// gradle clean build -Denv=production
environments {
    development {
        database {
            driverClassName = 'com.mysql.jdbc.Driver'
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

        logDir = '/temp/logs'
        staticPath = ''
        thymeleafCacheable = false
    }

    production {
        database {
            driverClassName = 'com.mysql.jdbc.Driver'
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

        logDir = '/temp/logs'
        staticPath = ''
        thymeleafCacheable = true
    }
}
