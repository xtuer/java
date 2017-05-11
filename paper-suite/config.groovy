// gradle clean build -Penv=production
environments {
    development {
        jdbc {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/paper?useUnicode=true&amp;characterEncoding=UTF-8'
            username = 'root'
            password = 'root'
        }

        baseUrl = ''
        logDir = "/temp/logs"
    }

    production {
        jdbc {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/paper?useUnicode=true&amp;characterEncoding=UTF-8'
            username = 'root'
            password = 'huaxia-123'
        }

        baseUrl = ''
        logDir = "/temp/logs"
    }
}
