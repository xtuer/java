// gradle clean build -Denv=production
environments {
    development {
        jdbc {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/dragon-ebag?useUnicode=true&amp;characterEncoding=UTF-8'
            username = 'root'
            password = 'root'
        }

        baseUrl = ''
        logsDir = "/dragon-ebag/logs"
    }

    production {
        jdbc {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/dragon-ebag?useUnicode=true&amp;characterEncoding=UTF-8'
            username = 'root'
            password = 'huaxia-123'
        }

        baseUrl = ''
        logsDir = "/dragon-ebag/logs"
    }
}
