package cglibx;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;

public class Main {
    public static void main(String[] args) {
        // 编译出的 class 位置 (默认不用写)
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "/Users/Biao/Temp/cglib");

        // CGLib 使用继承实现代理
        // JDK 使用实现接口实现代理
        Enhancer enhancer1 = new Enhancer();
        enhancer1.setSuperclass(HelloConcrete.class);
        enhancer1.setCallback(new MyMethodInterceptor());
        HelloConcrete hello = (HelloConcrete) enhancer1.create();
        System.out.println(hello.sayHello("I love you!"));
        System.out.println(hello.getClass());

        Enhancer enhancer2 = new Enhancer();
        enhancer2.setSuperclass(BlueBox.class);
        enhancer2.setCallback(new MyMethodInterceptor()); // 多个类使用同一个拦截器
        BlueBox box = (BlueBox) enhancer2.create();
        box.greeting();
    }
}
