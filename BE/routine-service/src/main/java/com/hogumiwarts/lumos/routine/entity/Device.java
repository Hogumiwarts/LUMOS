package com.hogumiwarts.lumos.routine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_url")
    private String deviceUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "control", columnDefinition = "jsonb")
    private List<Map<String, Object>> control;


}
