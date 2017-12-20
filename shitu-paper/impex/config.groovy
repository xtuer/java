// gradle -Denv=production clean shadowJar
environments {
    development {
        database {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'root'
        }
    }

    production {
        database {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://127.0.0.1:3306/shitu_paper?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'tiger_sun'
        }
    }
}
