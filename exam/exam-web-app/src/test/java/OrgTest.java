import com.exam.bean.Organization;
import com.exam.bean.Result;
import com.exam.bean.User;
import com.exam.mapper.OrganizationMapper;
import com.exam.service.OrganizationService;
import com.exam.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/application.xml"})
public class OrgTest {
    @Autowired
    private OrganizationService orgService;

    @Autowired
    private OrganizationMapper orgMapper;

    /**
     * 使用机构名模糊查询机构
     *
     * gradle clean test --tests OrgTest.testFindOrganizationsLikeName
     */
    @Test
    public void testFindOrganizationsLikeName() {
        Utils.dump(orgMapper.findOrganizationsLikeName("大"));
    }

    /**
     * 创建更新机构
     *
     * gradle clean test --tests OrgTest.testInsertOrUpdateOrg
     */
    @Test
    public void testInsertOrUpdateOrg() {
        Organization org = new Organization();
        org.setId(0);
        org.setName("测试机构");
        org.setContactPerson("李玉山");
        org.setContactMobile("10010");
        org.setHost("org-test.com");
        org.setPort(80);
        org.setLogo("/file/temp/logo.png");
        org.setPortalName("测试机构-继续教育学院");

        User admin = new User();
        admin.setId(0);
        admin.setUsername("Bob");
        admin.setPassword("Passw0rd");
        org.setAdmin(admin);

        Result<String> result = orgService.upsertOrganization(org);
        Utils.dump(result);
    }
}
