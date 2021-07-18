package com.xtuer.aspect;

import com.xtuer.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
public class LockAspect {
    private final ExpressionParser parser = new SpelExpressionParser();
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 使用自定义注解配置切入点
     */
    @Pointcut("@annotation(com.xtuer.annotation.Lock)")
    public void pointCut() {}

    /**
     * 使用分布式锁保护函数的调用
     */
    @Around("pointCut() && @annotation(lock)")
    public Object doAround(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        String lockName = buildLockName(pjp, lock);

        try {
            // 1. 获取分布式锁
            log.info("Lock: {}", lockName);

            // 2. 执行函数
            return pjp.proceed();
        } finally {
            // 3. 释放分布式锁
            log.info("Unlock: {}", lockName);
        }
    }

    // 使用 SpEL 表达式创建锁的名字
    private String buildLockName(ProceedingJoinPoint pjp, Lock lock) {
        // 1. 如果 lock.value() 中不包含 #，则说明只是一个普通的字符串，直接返回作为锁的名字
        // 2. 获取被调用的方法以及它的参数名和参数值
        // 3. 把参数名和对应的参数值设置到 context 中
        // 4. 创建执行 SpEL 表达式, 并返回它的结果作为锁的名字

        String spel = lock.value();

        // [1] 如果 lock.value() 中不包含 #，则说明只是一个普通的字符串，直接返回作为锁的名字
        if (!spel.contains("#")) {
            return spel;
        }

        // [2] 获取被调用的方法以及它的参数名和参数值
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod(); // 方法
        String[] params = discoverer.getParameterNames(method);               // 参数名
        Object[] args   = pjp.getArgs();                                      // 参数值
        EvaluationContext context = new StandardEvaluationContext();

        // [3] 把参数名和对应的参数值设置到 context 中
        if (params != null) {
            for (int len = 0; len < params.length; len++) {
                context.setVariable(params[len], args[len]);
            }
        }

        // [4] 创建执行 SpEL 表达式, 并返回它的结果作为锁的名字
        Expression expression = parser.parseExpression(spel);
        return expression.getValue(context, String.class);
    }
}
