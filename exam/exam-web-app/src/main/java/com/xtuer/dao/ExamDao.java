package com.xtuer.dao;

import com.xtuer.bean.exam.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 考试 Dao (MongoDB 使用)
 *
 * 提示:
 *     mongoTemplate.save()   : 如果 id 对应的 document 不存在则插入，存在则插入新的属性，替换非 null 属性
 *     mongoTemplate.upsert() : 查询不到复合条件的则插入，存在则替换 (如果 Update 使用 Document 创建则原来的所有属性都会被删除)
 *     mongoTemplate.find()   : 返回 List，查询不到返回空的 List
 *     mongoTemplate.findOne(): 返回查询到的对象，查询不到返回 null
 *
 * 索引:
 *     选项的作答 exam_question_answer : 复合唯一索引 (examRecordId, questionOptionId)
 *     主观题批改 exam_question_correct: 复合唯一索引 (examRecordId, questionOptionId), 普通索引 (examId, questionId)、(examId, questionOptionId)
 */
@Service
@Slf4j
public class ExamDao {
    private static final String QUESTION_ANSWER  = "exam_question_answer";
    private static final String QUESTION_CORRECT = "exam_question_correct";
    private static final String EXAM_RECORD      = "exam_record";
    private static final int ELAPSED_TIME_DELTA  = 5; // 计时的时间差

    @Resource(name = "mongoTemplate")
    private MongoTemplate mongoTemplate;

    /**
     * 统计用户某次考试的考试记录数量
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 返回考试记录数量
     */
    public int countExamRecordsByUserIdAndExamId(long userId, long examId) {
        Criteria criteria = Criteria.where("userId").is(userId).and("examId").is(examId);
        long count = mongoTemplate.count(Query.query(criteria), EXAM_RECORD);
        return (int) count;
    }

    /**
     * 查找用户在某次考试中已经做过的试卷
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 返回试卷 ID 的数组
     */
    public Set<Long> findPaperIdsByUserIdAndExamId(long userId, long examId) {
        Criteria criteria = Criteria.where("userId").is(userId).and("examId").is(examId);
        Query query = Query.query(criteria);
        query.fields().include("paperId");

        List<ExamRecord> records = mongoTemplate.find(query, ExamRecord.class, EXAM_RECORD);
        Set<Long> paperIds = records.stream().map(ExamRecord::getPaperId).filter(id -> id > 0).collect(Collectors.toSet());

        return paperIds;
    }

    /**
     * 插入或者更新题目的作答
     *
     * @param question 题目的作答
     */
    public void upsertQuestionAnswers(QuestionWithAnswer question) {
        // 1. 删除题目选项的回答
        // 2. 插入题目选项的回答

        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, QUESTION_ANSWER);

        // [1] 删除题目选项的回答
        Query condition = Query.query(Criteria
                .where("examRecordId").is(question.getExamRecordId())
                .and("questionId").is(question.getQuestionId())
        );
        bulkOps.remove(condition);

        for (QuestionOptionAnswer answer : question.getAnswers()) {
            Document document = new Document()
                    .append("examId", question.getExamId())
                    .append("examRecordId", question.getExamRecordId())
                    .append("questionId", question.getQuestionId())
                    .append("questionOptionId", answer.getQuestionOptionId())
                    .append("content", answer.getContent())
                    .append("value", answer.getValue());
            // [2] 插入题目选项的回答
            bulkOps.insert(document);
        }

        bulkOps.execute();
    }

    /**
     * 插入或者更新主观题的作答
     *
     * @param questions 题目的作答
     */
    public void upsertSubjectiveQuestionsWithAnswer(List<QuestionWithAnswer> questions) {
        if (questions.size() == 0) { return; }

        // 使用批量操作
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, QUESTION_CORRECT);

        for (QuestionWithAnswer question : questions) {
            Update update = Update.update("examId", question.getExamId())
                    .set("examRecordId", question.getExamRecordId())
                    .set("questionId", question.getQuestionId())
                    .set("teacherId", question.getTeacherId())
                    .set("answers", question.getAnswers())
                    .set("score", question.getScore())
                    .set("scoreStatus", question.getScoreStatus())
                    .set("comment", question.getComment());

            // 更新条件: 指定考试记录的题目
            Query condition = Query.query(Criteria
                    .where("examRecordId").is(question.getExamRecordId())
                    .and("questionId").is(question.getQuestionId())
            );

            bulkOps.upsert(condition, update);
        }

        bulkOps.execute();
    }

    /**
     * 查询考试记录的作答
     *
     * @param examRecordId 考试记录 ID
     * @return 返回作答的数组
     */
    public List<QuestionWithAnswer> findQuestionsWithAnswerByExamRecordId(long examRecordId) {
        // 1. 查询考试记录下的作答
        // 2. 作答按照题目 ID 分组
        // 3. 每个题目的作答创建一个 QuestionWithAnswer

        // [1] 查询考试记录下的作答
        Query query = Query.query(Criteria.where("examRecordId").is(examRecordId));
        query.fields().include("questionId").include("questionOptionId").include("content").include("value");
        List<QuestionOptionAnswer> answers = mongoTemplate.find(query, QuestionOptionAnswer.class, QUESTION_ANSWER);

        // [2] 作答按照题目 ID 分组
        Map<Long, List<QuestionOptionAnswer>> questionAnswers = answers.stream().collect(Collectors.groupingBy(QuestionOptionAnswer::getQuestionId));

        // [3] 每个题目的作答创建一个 QuestionWithAnswer
        List<QuestionWithAnswer> questions = new LinkedList<>();
        questionAnswers.forEach((questionId, ans) -> {
            QuestionWithAnswer question = new QuestionWithAnswer();
            question.setQuestionId(ans.get(0).getQuestionId());
            question.setAnswers(ans);
            questions.add(question);
        });

        return questions;
    }

    /**
     * 查询考试记录的所有主观题的作答
     *
     * @param examRecordId 考试记录 ID
     * @return 返回主观题的作答的数组
     */
    public List<QuestionWithAnswer> findSubjectiveQuestionsWithAnswerByExamRecordId(long examRecordId) {
        Query query = Query.query(Criteria.where("examRecordId").is(examRecordId));
        List<QuestionWithAnswer> answers = mongoTemplate.find(query, QuestionWithAnswer.class, QUESTION_CORRECT);
        answers.forEach(a -> a.setExamRecordId(examRecordId)); // 因为 examRecordId 被标记为 @Transient

        return answers;
    }

    /**
     * 插入或者更新考试记录
     *
     * @param record 考试记录
     */
    public void upsertExamRecord(ExamRecord record) {
        log.info("[开始] 更新考试记录: {}", record);
        mongoTemplate.save(record, EXAM_RECORD);
        log.info("[结束] 更新考试记录: {}", record);
    }

    /**
     * 查询考试记录
     *
     * @param examRecordId 考试记录 ID
     * @return 返回查询得到的考试记录，查询不到返回 null
     */
    public ExamRecord findExamRecordById(long examRecordId) {
        log.info("[开始] 查询考试记录: {}", examRecordId);

        Criteria criteria = Criteria.where("id").is(examRecordId);
        ExamRecord record = mongoTemplate.findOne(Query.query(criteria), ExamRecord.class, EXAM_RECORD);

        log.info("[结束] 查询考试记录: {}", examRecordId);
        return record;
    }

    /**
     * 查找指定考试的所有考试记录
     *
     * @param examId 考试 ID
     * @return 返回查找到的考试记录数组
     */
    public List<ExamRecord> findExamRecordsByExamId(long examId) {
        log.info("[开始] 查询考试的所有记录: 考试 {}", examId);

        Criteria criteria = Criteria.where("examId").is(examId);
        Query query = Query.query(criteria);
        query.fields().exclude("questions"); // 忽略题目的作答
        Sort sort = Sort.by(Sort.Direction.ASC, "userId"); // 按用户排序
        List<ExamRecord> records = mongoTemplate.find(query.with(sort), ExamRecord.class, EXAM_RECORD);

        log.info("[结束] 查询考试的所有记录: 考试 {}", examId);
        return records;
    }

    /**
     * 查询指定用户的多个考试的考试记录
     *
     * @param userId  用户 ID
     * @param examIds 考试 ID 的数组
     * @return 返回考试记录的 Map，key 为考试 ID
     */
    public Map<Long, List<ExamRecord>> findExamRecordsByExamIds(long userId, List<Long> examIds) {
        log.info("[开始] 查询多个考试的所有记录: 用户 {}, 考试 {}", userId, examIds);

        Criteria criteria = Criteria.where("userId").is(userId).and("examId").in(examIds);
        Query query = Query.query(criteria);
        query.fields().exclude("questions"); // 忽略题目的作答
        List<ExamRecord> records = mongoTemplate.find(query, ExamRecord.class, EXAM_RECORD);

        // 按 examId 归类
        Map<Long, List<ExamRecord>> recordsMap = records.stream().collect(Collectors.groupingBy(ExamRecord::getExamId));

        log.info("[结束] 查询多个考试的所有记录: 用户 {}, 考试 {}", userId, examIds);
        return recordsMap;
    }

    /**
     * 查找用户的指定考试的所有考试记录
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 返回查找到的考试记录数组
     */
    public List<ExamRecord> findExamRecordsByUserIdAndExamId(long userId, long examId) {
        log.info("[开始] 查询学员的考试记录: 学员 {}, 考试 {}", userId, examId);

        Criteria criteria = Criteria.where("userId").is(userId).and("examId").is(examId);
        Query query = Query.query(criteria);
        query.fields().exclude("questions"); // 忽略题目的作答
        List<ExamRecord> records = mongoTemplate.find(query, ExamRecord.class, EXAM_RECORD);

        log.info("[结束] 查询学员的考试记录: 学员 {}, 考试 {}", userId, examId);
        return records;
    }

    /**
     * 增加考试记录的使用时间
     *
     * @param examRecordId  考试记录 ID
     * @param timeInSeconds 增加的时间
     */
    public void increaseExamRecordElapsedTime(long examRecordId, int timeInSeconds) {
        // 1. 查询考试记录的 elapsedTime 和 tickAt
        // 2. 如果 tickAt + timeInSeconds <= 当前时间则增加时间

        Criteria criteria = Criteria.where("_id").is(examRecordId); // 奇怪: 使用 id 可以查询到，但是更新不行，使用 _id 的话都可以

        // [1] 查询考试记录的 elapsedTime 和 tickAt
        Query query = Query.query(criteria);
        query.fields().include("tickAt").include("elapsedTime").include("status");
        ExamRecord record = mongoTemplate.findOne(query, ExamRecord.class, EXAM_RECORD);

        // 没有查询到或者已提交则返回
        if (record == null || record.getStatus() >= ExamRecord.STATUS_SUBMITTED) { return; }

        long will = record.getTickAt().getTime() + (timeInSeconds-ELAPSED_TIME_DELTA)*1000L; // 下次最早可计时时间
        long now  = System.currentTimeMillis(); // 当前时间

        // [2] 如果 tickAt + timeInSeconds <= 当前时间则增加时间
        if (will <= now) {
            Update update = new Update().set("elapsedTime", record.getElapsedTime() + timeInSeconds).set("tickAt", new Date());
            mongoTemplate.updateFirst(Query.query(criteria), update, EXAM_RECORD);
        }
    }

    /**
     * 统计某次考试中题目的各选项的作答次数
     *
     * @param examId 考试 ID
     * @return 返回题目各选项作答次数的数组
     */
    public Map<Long, Integer> countExamQuestionOptionAnswers(long examId) {
        // 1. 按题目选项分组，得到每个选项和它的作答次数的对象
        // 2. 转换为 Map

        // [1] 按题目选项分组，得到每个选项和它的作答次数的对象
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("examId").is(examId)),
                Aggregation.project("questionOptionId"),
                Aggregation.group("questionOptionId").count().as("count")
        );

        AggregationResults<QuestionOptionAnswerCount> result = mongoTemplate.aggregate(agg, QUESTION_ANSWER, QuestionOptionAnswerCount.class);
        List<QuestionOptionAnswerCount> resultList = result.getMappedResults();

        // [2] 转换为 Map
        return resultList.stream().collect(Collectors.toMap(QuestionOptionAnswerCount::getQuestionOptionId, QuestionOptionAnswerCount::getCount));
    }

    /**
     * 统计某次考试 (调查问卷) 中星级题的每个星级值的作答次数
     *
     * @param examId 考试 ID
     * @return 返回星级值的作答次数
     */
    public Map<Long, Map<String, Integer>> countExamQuestionOptionStars(long examId) {
        // 1. 查询此考试的所有星级作答
        // 2. 按选项 ID 分组，value 为此选项的星级值的数组
        // 3. 统计每个选项的星级值出现的次数

        // [1] 查询此考试的所有星级作答
        Criteria criteria = Criteria.where("examId").is(examId).and("value").gt(0);
        Query query = Query.query(criteria);
        query.fields().include("questionOptionId").include("value");
        List<QuestionOptionAnswer> starOptionAnswers = mongoTemplate.find(query, QuestionOptionAnswer.class, QUESTION_ANSWER);

        // [2] 按选项 ID 分组，value 为此选项的星级值的数组
        Map<Long, List<Integer>> optionIdWithStarValuesMap = starOptionAnswers.stream().collect(
                Collectors.groupingBy(
                        QuestionOptionAnswer::getQuestionOptionId,
                        Collectors.mapping(QuestionOptionAnswer::getValue, Collectors.toList())
                ));

        // [3] 统计每个选项的星级值出现的次数
        Map<Long, Map<String, Integer>> optionIdWithStarValueAndCountsMap = new HashMap<>();
        optionIdWithStarValuesMap.forEach((questionOptionId, values) -> {
            Map<String, Integer> frequence = values.stream()
                    .map(i -> i+"") // 转为字符串是为了输出 JSON 时 key 为字符串
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e -> 1)));
            optionIdWithStarValueAndCountsMap.put(questionOptionId, frequence);
        });

        return optionIdWithStarValueAndCountsMap;
    }

    /**
     * 统计某次考试的作答记录数，对于调查问卷等只允许作答一次的考试，即作答人数
     *
     * @param examId 考试 ID
     * @return 返回考试记录数
     */
    public int countExamRecords(long examId) {
        Criteria criteria = Criteria.where("examId").is(examId);
        Query query = Query.query(criteria);
        int count = (int) (mongoTemplate.count(query, EXAM_RECORD));

        return count;
    }

    /**
     * 统计考试的考试记录数量
     *
     * @param examIds 考试 ID 的数组
     * @return 返回考试及它的考试记录数量的数组
     */
    public List<ExamRecordCount> countExamRecordsOfExams(List<Long> examIds) {
        // 按考试分组，统计每个考试的考试记录数量
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("examId").in(examIds)),
                Aggregation.project("examId"),
                Aggregation.group("examId").count().as("count")
        );

        AggregationResults<ExamRecordCount> result = mongoTemplate.aggregate(agg, EXAM_RECORD, ExamRecordCount.class);
        List<ExamRecordCount> resultList = result.getMappedResults();
        return resultList;
    }

    /**
     * 查询指定考试的问答题的非空作答内容
     *
     * @param examId     考试 ID
     * @param questionId 题目 ID
     * @return 返回作答内容的数组
     */
    public List<String> findEssayQuestionAnswers(long examId, long questionId) {
        // 作答非空
        Criteria criteria = Criteria.where("examId").is(examId).and("questionId").is(questionId).and("content").ne("");
        Query query = Query.query(criteria);
        query.fields().include("content");
        List<QuestionOptionAnswer> answers = mongoTemplate.find(query, QuestionOptionAnswer.class, QUESTION_ANSWER);

        return answers.stream().map(QuestionOptionAnswer::getContent).collect(Collectors.toList());
    }
}
