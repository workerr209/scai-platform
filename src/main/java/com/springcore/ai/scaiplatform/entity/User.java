package com.springcore.ai.scaiplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "as_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String pin;

    @Column(name = "datefo")
    private LocalDateTime datefo;

    @Column(name = "dateto")
    private LocalDateTime dateto;

    private Long eacentl;

    private String eactype;

    @ManyToOne
    @JoinColumn(name = "emid")
    private Employee emid;

    private LocalDateTime expdate;

    private Integer inactive;

    private String lictype;

    private String name;

    private String username;

    private String valstr;

    private Integer defaultrole;

    private Long patid;

    @Column(name = "atworkId")
    private String atworkId;

    @Column(name = "euniteToken")
    private String euniteToken;

    @Column(name = "euniteId")
    private String euniteId;

    private Long disk;

    private String rem;

    private LocalDateTime ts;

    @Column(name = "mailStatus")
    private Integer mailStatus;

    @Column(name = "tagsValue")
    private String tagsValue;

}