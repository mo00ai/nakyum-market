package com.example.auction.domain.image.entity;

import com.example.auction.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "image")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originFileName;

    @Column(nullable = false)
    private String uploadFileName;

    @Column(nullable = false)
    private int fileSize;

    @Column(nullable = false, length = 50)
    private String fileExtension;

    public static Image of(String originFileName, String uploadFileName, int fileSize, String fileExtension) {
        return Image.builder()
                .originFileName(originFileName)
                .uploadFileName(uploadFileName)
                .fileSize(fileSize)
                .fileExtension(fileExtension)
                .build();
    }

}
