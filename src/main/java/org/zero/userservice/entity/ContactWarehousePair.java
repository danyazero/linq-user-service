package org.zero.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "contact_warehouse_pair")
public class ContactWarehousePair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_person_id", nullable = false)
    private ContactPerson contactPerson;

    @NotNull
    @Column(name = "warehouse_ref", nullable = false, length = Integer.MAX_VALUE)
    private String warehouseRef;

    @Column(name = "hash", length = Integer.MAX_VALUE)
    private String hash;

    public Integer getIssuerId() {
        return this.getContactPerson().getIssuerUser().getId();
    }

    public Integer getContactPersonId() {
        return this.getContactPerson().getId();
    }
}