import bean.OnlineTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.time.LocalDate;
import java.util.List;

public class AggregationTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("mongodb-security.xml");
        MongoTemplate mongoTemplate = context.getBean("mongoTemplate", MongoTemplate.class);
        byHour(mongoTemplate);
        byDay(mongoTemplate);
    }

    public static void byHour(MongoTemplate mongoTemplate) {
        Aggregation agg = Aggregation.newAggregation(
                // Aggregation.match(criteria),
                Aggregation.project("interval")
                        .andExpression("{ $dateToString: { date: '$time', format: '%Y-%m-%d', timezone: 'Asia/Shanghai' }}").as("date")
                        .andExpression("{ $hour: { date: '$time', timezone: 'Asia/Shanghai' }}").as("hour"),
                Aggregation.group("date", "hour").sum("interval").as("total"),
                Aggregation.sort(Sort.Direction.ASC, "date", "hour")
        );

        // Aggregation agg = Aggregation.newAggregation(
        //         Aggregation.project("interval")
        //                 .andExpression("year(time)").as("year")
        //                 .andExpression("month(time)").as("month")
        //                 .andExpression("dayOfMonth(time)").as("day")
        //                 .andExpression("hour(time)").as("hour"),
        //         Aggregation.group(Aggregation.fields().and("year").and("month").and("day").and("hour"))
        //                 .sum("interval").as("total"),
        //         Aggregation.sort(Sort.Direction.ASC, "year", "month", "day", "hour")
        // );

        AggregationResults<OnlineTime> result = mongoTemplate.aggregate(agg, "online_tick", OnlineTime.class);
        List<OnlineTime> resultList = result.getMappedResults();

        for (OnlineTime time : resultList) {
            System.out.println(time);
        }
    }

    public static void byDay(MongoTemplate mongoTemplate) {
        LocalDate startDate = LocalDate.now().minusDays(6); // 最近 7 天

        // 按照时间聚合
        Aggregation agg = Aggregation.newAggregation(
                // Aggregation.match(Criteria.where("schoolId").is(271909232880648192L).and("time").gte(startDate)),
                Aggregation.project("interval").andExpression("{ $dateToString: { date: '$time', format: '%Y-%m-%d'}}").as("date"),
                Aggregation.group("date").sum("interval").as("total"),
                Aggregation.sort(Sort.Direction.ASC, "_id")
        );

        // 只用一个属性进行聚合时，虽然上面使用了 as("day")，但是结果中的 key 不是 day，而是 _id
        // 使用多个属性进行聚合时，结果中的可以就能和 as 的属性对的上
        AggregationResults<OnlineTime> result = mongoTemplate.aggregate(agg, "online_tick", OnlineTime.class);
        List<OnlineTime> resultList = result.getMappedResults();

        for (OnlineTime time : resultList) {
            System.out.println(time);
        }
    }
}
