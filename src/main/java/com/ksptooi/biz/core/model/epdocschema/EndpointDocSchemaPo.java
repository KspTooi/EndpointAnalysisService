package com.ksptooi.biz.core.model.epdocschema;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "ep_document_schema")
@Getter
@Setter
@Comment("端点架构定义")
public class EndpointDocSchemaPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("端点Schema ID")
    private Long id;
    
}
