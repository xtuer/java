// 打包命令: gradle -Denv=production clean build

environments {
    // 开发环境配置
    development {
        // 数据库
        database {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/survey?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'root'
        }

        // 日志目录
        logDir = '/temp/logs'
    }

    // 线上环境配置
    production {
        // 数据库
        database {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/survey?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'huaxia-123'
        }

        // 日志目录
        logDir = '/temp/logs'
    }
}

