package dev.brkovic.fridge.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Entity
@Table(name = "fridge_item")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class FridgeItemEntity extends BaseEntity {
    @Column(name = "fridge_id", nullable = false)
    private String fridgeId;

    @Column(name = "item_id", nullable = false)
    private String itemId;

    @Column(name = "stored_at", nullable = false)
    private OffsetDateTime storedAt;

    @Column(name = "best_before_date", nullable = false)
    private OffsetDateTime bestBeforeDate;
}
