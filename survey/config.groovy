environments {
    development {
        database {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/survey?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'root'
        }

        logDir = '/temp/logs'
    }

    production {
        database {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/survey?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'huaxia-123'
        }

        logDir = '/temp/logs'
    }
}

// gradle clean build -Penv=production
