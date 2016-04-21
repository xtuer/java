package com.xtuer;

import com.xtuer.solr.repository.PersonSolrRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("solr.xml");

        PersonSolrRepository r = context.getBean(PersonSolrRepository.class);
        System.out.println(r.findOne("person-10003"));
    }
}
