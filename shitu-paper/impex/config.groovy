// gradle -Denv=production clean shadowJar
environments {
    development {
        database {
            url = 'jdbc:mysql://localhost:3306/shitu_paper?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'root'
        }
    }

    production {
        database {
            url = 'jdbc:mysql://127.0.0.1:3306/shitu_paper?useUnicode=true&characterEncoding=UTF-8'
            username = 'root'
            password = 'tiger_sun'
        }
    }
}
