import com.xtuer.service.MailService;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:spring-mail.xml"})
public class TestMail {
    @Resource(name="mailService")
    private MailService mailService;

    // 使用 Spring Mail 发送邮件
    @Test
    public void sendSpringMail() {
        mailService.sendMail("biao.mac@icloud.com", "biao.mac@qq.com", "Spring Mail", "This is only for test!");
    }

    // 使用 Commons Mail 发送邮件
    // Help: http://commons.apache.org/proper/commons-email/userguide.html
    @Test
    public void sendCommonsMail() throws EmailException {
        Email email = new SimpleEmail();
        email.setHostName("smtp.mail.me.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("biao.mac@icloud.com", "HuangBiao198266"));
        // email.setSSLOnConnect(true);
        email.setStartTLSEnabled(true);
        email.setFrom("biao.mac@icloud.com");
        email.addTo("biao.mac@qq.com");
        email.setSubject("Commons Mail");
        email.setMsg("This is a test mail ... :-)");
        email.send();
    }
}
