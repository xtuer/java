package com.xtuer.solr.repository;

import com.xtuer.bean.Person;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

public interface PersonSolrRepository extends SolrCrudRepository<Person, String> {
    List<Person> findByNameStartingWith(String name);;
}
