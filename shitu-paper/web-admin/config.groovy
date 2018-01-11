// gradle -Denv=production clean assemble
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
            paperDirectory   = '/Users/Biao/Documents/乐学乐教试题/套卷/doc-final'
            exportDirectory  = '/Users/Biao/Documents/乐学乐教试题/套卷/temp/export'
            previewDirectory = '/Users/Biao/Documents/乐学乐教试题/套卷/temp/preview'
        }

        question {
            imageDirectory = '/Users/Biao/Documents/乐教乐学试题/单题/question-img'
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
            paperDirectory   = '/data/shitu.edu-edu.com/data/paper'
            exportDirectory  = '/data/shitu.edu-edu.com/temp/export'
            previewDirectory = '/data/shitu.edu-edu.com/temp/preview'
        }

        question {
            imageDirectory = '/data/shitu.edu-edu.com/data/question-img'
        }

        baseUrl = ''
        logDir = "/data/shitu.edu-edu.com/logs"
    }
}
