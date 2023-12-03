package com.example.proxytest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"key"})
@Builder
@Entity
@Table(name = "Code", uniqueConstraints = {
        @UniqueConstraint(name = "UK_CODE_ID", columnNames = {"CODE_ID"})
})
public class Code {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODE_KEY")
    private Long key;

    @Column(name = "CODE_ID", nullable = false, unique = true, length = 50)
    private String codeId;

    @Column(name = "LABEL", nullable = false)
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CODE_KEY", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Code parentCode;

    public Code changeParent(Code parentCode) {
        this.parentCode = parentCode;
        return this;
    }
}
