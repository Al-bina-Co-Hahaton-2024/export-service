package ru.albina.export.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "export")
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class ExportEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 30)
    @NotNull
    @Column(name = "type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private ExportType type;

    @Size(max = 30)
    @NotNull
    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private ExportStatus status;

    @Size(max = 30)
    @Column(name = "link", length = 30)
    private String link;

    @NotNull
    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "modified_date", nullable = false)
    @LastModifiedDate
    private LocalDateTime modifiedDate;

}