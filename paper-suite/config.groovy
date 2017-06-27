// gradle clean -Denv=production assemble
environments {
    development {
        jdbc {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://localhost:3306/shitu_paper?useUnicode=true&amp;characterEncoding=UTF-8'
            username = 'root'
            password = 'root'
        }

        redis {
            host = 'localhost'
            port = 6379
            password = ''
        }

        paper {
            baseDirectory = '/Users/Biao/Documents/套卷/papers'
            exportDirectory = '/Users/Biao/Documents/套卷/export'
        }

        baseUrl = ''
        logDir = "/temp/shitu.edu-edu.com/logs"
    }

    production {
        jdbc {
            driverClassName = 'com.mysql.jdbc.Driver'
            url = 'jdbc:mysql://127.0.0.1:3306/shitu_paper?useUnicode=true&amp;characterEncoding=UTF-8'
            username = 'root'
            password = 'tiger_sun'
        }

        redis {
            host = '172.16.1.41'
            port = 6379
            password = '123456'
        }

        paper {
            baseDirectory = '/data/shitu.edu-edu.com/papers'
            exportDirectory = '/data/shitu.edu-edu.com/export'
        }

        baseUrl = ''
        logDir = "/temp/shitu.edu-edu.com/logs"
    }
}
