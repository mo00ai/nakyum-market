package com.example.auction.domain.image.service;

import static com.example.auction.domain.image.exception.ImageErrorCode.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.image.exception.ImageException;
import com.example.auction.domain.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

	private final ImageRepository imageRepository;
	@Value("${file.upload-dir}")
	private String IMAGE_DIR;

	@Transactional(rollbackFor = IOException.class)
	public Image uploadFile(List<MultipartFile> fileList) {
		List<Image> returnList = new ArrayList<>(); // 업로드한 사진 정보 담아서 리턴

		for (MultipartFile file : fileList) {

			//파일이름 중복 방지
			String originalFilename = file.getOriginalFilename();
			String uploadFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
			Long fileSize = file.getSize();
			String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();

			//파일 저장 경로 설정
			Path filePath = Paths.get(IMAGE_DIR + uploadFilename);

			try {
				Files.write(filePath, file.getBytes());
			} catch (IOException ex) {
				log.info("FAILED TO WRITE FILE PATH: {}", filePath.toString());
				throw new ImageException(FAILED_WRITE_FILE);
			}

			Image image = Image.of(originalFilename, uploadFilename, fileSize, fileExtension);

			imageRepository.save(image);

			returnList.add(image);

		}

		return returnList.get(0);
	}

	@Transactional(readOnly = true)
	public Image getImage(Long imageId) {
		return imageRepository.findById(imageId).orElseThrow(
			() -> new ImageException(IMAGE_NOT_FOUND));
	}

	// 파일 삭제
	@Transactional(rollbackFor = IOException.class)
	public void deleteImage(Long imageId) {
		Image image = imageRepository.findById(imageId).orElseThrow(
			() -> new ImageException(IMAGE_NOT_FOUND));
		Path path = Path.of(IMAGE_DIR + image.getUploadFileName());

		//db삭제
		imageRepository.delete(image);

		//로컬 디렉토리 삭제
		try {
			Files.deleteIfExists(path);
		} catch (IOException ex) {
			throw new ImageException(FAILED_DELETE_FILE);
		}
	}

}
