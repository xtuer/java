package com.xtuer;

import com.xtuer.bean.Company;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:bean.xml");
        Company company = context.getBean("company", Company.class);

        System.out.println(company);
        System.out.println(company.getAdmin());
    }
}
