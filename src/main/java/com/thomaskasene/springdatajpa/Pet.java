package com.thomaskasene.springdatajpa;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "PET")
public class Pet {

    @Id
    @GeneratedValue(generator = "petIdGen", strategy = SEQUENCE)
    @SequenceGenerator(name = "petIdGen", sequenceName = "PET_SEQ", allocationSize = 1000)
    private Long id;

    @Column(name = "TAGS_JSON")
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonBinaryType")
    private Tags tags;

    @Data
    @Accessors(chain = true)
    public static class Tags {
        private List<String> tags;
    }
}
