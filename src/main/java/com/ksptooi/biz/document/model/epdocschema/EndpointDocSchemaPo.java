package com.ksptooi.biz.document.model.epdocschema;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ep_document_schema", comment = "端点架构定义")
@Getter
@Setter
public class EndpointDocSchemaPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "端点Schema ID")
    private Long id;

}
