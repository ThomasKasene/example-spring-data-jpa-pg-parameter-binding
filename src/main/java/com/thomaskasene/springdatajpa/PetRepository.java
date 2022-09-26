package com.thomaskasene.springdatajpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PetRepository extends Repository<Pet, Long> {

    void deleteAll();

    void save(Pet pet);

    @Query(nativeQuery = true, value = """
            select *
            from PET
            where TAGS_JSON -> 'tags' \\?\\? :tag
            """)
    Pet findByTag(String tag);

    @Query(nativeQuery = true, value = """
            select *
            from PET
            where TAGS_JSON -> 'tags' \\?\\? '2222'
            """)
    Pet findByTagEquals2222();
}
