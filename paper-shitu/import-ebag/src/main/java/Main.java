import com.xtuer.service.PaperImportService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/spring-beans.xml");
        PaperImportService service = context.getBean(PaperImportService.class);

        service.importPapers();
        service.importPaperDirectories();
        service.importKnowledgePoints();
        service.importPaperKnowledgePointRelation();
    }
}
