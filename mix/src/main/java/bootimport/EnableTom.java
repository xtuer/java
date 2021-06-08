package bootimport;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Documented 是一个标记注解，没有成员变量。 用 @Documented 注解修饰的注解类会被 JavaDoc 工具提取成文档。
 * 默认情况下，JavaDoc 是不包括注解的，但如果声明注解时指定了 @Documented，就会被 JavaDoc 之类的工具处理，
 * 所以注解类型信息就会被包括在生成的帮助文档中。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomConfiguration.class)
@Documented
public @interface EnableTom {
}
