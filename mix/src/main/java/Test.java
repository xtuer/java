import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class Test {
    public static void main(String[] args) {
        ExpressionParser parser   = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        Expression expression     = parser.parseExpression("'hello-' + #id"); // 字符串的 spel 表达式

        context.setVariable("id", 123);                                       // 设置变量的值
        String result = expression.getValue(context, String.class);           // 结果: hello-123

        System.out.println(result);
    }
}
