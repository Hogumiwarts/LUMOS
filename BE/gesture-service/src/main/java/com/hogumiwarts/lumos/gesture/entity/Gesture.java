package com.hogumiwarts.lumos.gesture.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gesture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gesture_id")
    private Long gestureId;

    @Column(name = "gesture_name")
    private String gestureName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
