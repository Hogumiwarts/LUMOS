package com.hogumiwarts.lumos.gesturesensor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@RequiredArgsConstructor
public class S3ZipDownloader {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.cloudfront.url}")
	private String cloudfrontUrl; // 예: https://cdn.example.com

	private final S3Client s3Client = S3Client.builder().build();

	public Path createZipTempFile(int folder) throws IOException {
		String prefix = "gesture_dataset/" + folder + "/";

		// 1. 날짜+시간 기반 파일명
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String fileName = "gesture_" + folder + "_" + time + ".zip";

		// 2. temp 디렉토리
		Path tempDir = Files.createTempDirectory("s3zip");
		Path zipPath = tempDir.resolveSibling(fileName); // zip은 tempDir의 형제 위치에 생성됨

		// 3. S3 다운로드
		ListObjectsV2Response listRes = s3Client.listObjectsV2(ListObjectsV2Request.builder()
			.bucket(bucket)
			.prefix(prefix)
			.build());

		for (S3Object obj : listRes.contents()) {
			String key = obj.key();
			String fileNameInZip = key.substring(prefix.length());
			Path localPath = tempDir.resolve(fileNameInZip);
			Files.createDirectories(localPath.getParent());
			s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build(), localPath);
		}

		// 4. ZIP 생성
		try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipPath))) {
			Files.walk(tempDir)
				.filter(Files::isRegularFile)
				.forEach(path -> {
					try (InputStream in = new FileInputStream(path.toFile())) {
						String relativePath = tempDir.relativize(path).toString();
						zipOut.putNextEntry(new ZipEntry(relativePath));
						in.transferTo(zipOut);
						zipOut.closeEntry();
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
		}

		// 5. tempDir 삭제
		FileSystemUtils.deleteRecursively(tempDir);

		return zipPath;
	}

	public String zipAndUploadFolder(String prefix, String targetZipKey) throws IOException {
		// 1. temp 디렉토리 생성
		Path tempDir = Files.createTempDirectory("s3zip");

		// 2. S3 객체 목록 가져오기
		ListObjectsV2Request listReq = ListObjectsV2Request.builder()
			.bucket(bucket)
			.prefix(prefix)
			.build();

		ListObjectsV2Response listRes = s3Client.listObjectsV2(listReq);

		// 3. 파일 다운로드
		for (S3Object obj : listRes.contents()) {
			String key = obj.key();
			String fileName = key.substring(prefix.length());
			Path localPath = tempDir.resolve(fileName);
			Files.createDirectories(localPath.getParent());

			s3Client.getObject(
				GetObjectRequest.builder().bucket(bucket).key(key).build(),
				localPath
			);
		}

		// 4. ZIP 생성
		Path zipPath = Files.createTempFile("dataset_", ".zip");
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
			Files.walk(tempDir)
				.filter(Files::isRegularFile)
				.forEach(path -> {
					try (InputStream in = new FileInputStream(path.toFile())) {
						String relativePath = tempDir.relativize(path).toString();
						zipOut.putNextEntry(new ZipEntry(relativePath));
						in.transferTo(zipOut);
						zipOut.closeEntry();
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
		}

		// 5. S3로 업로드
		String uploadKey = targetZipKey; // 예: downloads/dataset_20240507.zip
		s3Client.putObject(
			PutObjectRequest.builder()
				.bucket(bucket)
				.key(uploadKey)
				.contentType("application/zip")
				.build(),
			zipPath
		);

		// 6. CloudFront 경로 리턴
		return cloudfrontUrl + "/" + uploadKey;
	}
}
