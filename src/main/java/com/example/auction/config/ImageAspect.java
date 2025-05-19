package com.example.auction.config;

import java.io.IOException;
import java.util.List;

import org.apache.tika.Tika;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.auction.common.constant.BusinessRuleConstants;
import com.example.auction.common.exception.CustomException;
import com.example.auction.common.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class ImageAspect {

	private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");
	private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png");
	private static final Tika tika = new Tika();

	/**
	 * 이미지 크기, 개수 ,타입 체크 AOP @ImageValid 붙은 컨트롤러에만 적용
	 * 맞는 타입체크하고 검사함
	 */
	@Around("@annotation(com.example.auction.common.annotation.ImageValid)")
	public Object validParma(ProceedingJoinPoint point) throws Throwable {
		for (Object arg : point.getArgs()) {
			// 리스트 x , 리스트 비어있음, 타입이 다르면 for문 탈출
			if (!(arg instanceof List<?> list) || list.isEmpty() || !(list.get(0) instanceof MultipartFile)) {
				continue;
			}
			validateFile((List<MultipartFile>)list); // 이미지 검사
		}
		return point.proceed();
	}

	/**
	 * 실제 이미지 valid 메소드
	 * @param fileList
	 */
	private void validateFile(List<MultipartFile> fileList) {

		// 이미지 개수 검사
		if (fileList.size() > BusinessRuleConstants.MAX_IMAGE_CNT) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		// fileList.stream()
		// 	.map(o -> (MultipartFile)o)
		// 	.forEach(file -> {
		// 		// 이미지 크기 검사
		// 		if (file.getSize() > BusinessRuleConstants.MAX_IMAGE_SIZE) {
		// 			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		// 		}
		// 		// 이미지 타입 검사
		// 		String contentType = file.getContentType();
		// 		if (contentType == null ||
		// 			!(contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
		// 			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		// 		}
		// 	});

		for (MultipartFile file : fileList) {
			// 크기 제한
			if (file.getSize() > BusinessRuleConstants.MAX_IMAGE_SIZE) {
				throw new CustomException(ErrorCode.INVALID_IMAGE_SIZE);
			}

			// 확장자 검사
			String originalFilename = file.getOriginalFilename();
			if (originalFilename == null || !originalFilename.contains(".")) {
				throw new CustomException(ErrorCode.INVALID_FILE_EXTENSION);
			}

			String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
			if (!ALLOWED_EXTENSIONS.contains(extension)) {
				throw new CustomException(ErrorCode.INVALID_FILE_EXTENSION);
			}

			// MIME 검사 - 클라이언트가 header조작하는 상황을 방지
			//tika 사용으로 실제 파일 내용이 이미지가 맞는지 검사
			try {
				//파일 내용(body)가져오기
				//byte 단위로 파일 내부 확인
				String mimeType = tika.detect(file.getInputStream());
				if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
					throw new CustomException(ErrorCode.INVALID_MIME_TYPE);
				}
				//inputstream은 파일은 여는 통로
				//통로가 닫혀있거나
				//파일이 날아갔거나 os 리소스 문제 등으로
				//파일을 못 읽었을 때의 예외를 잡으려고
			} catch (IOException e) {
				throw new CustomException(ErrorCode.FILE_READ_ERROR);
			}
		}

	}
}
