package com.hogumiwarts.lumos.gesturesensor.service;

import java.io.FileInputStream;
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
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@RequiredArgsConstructor
public class S3DownloadService {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.cloudfront.url}")
	private String cloudfrontUrl;

	private final S3Client s3Client;

	public Path zipGestureDataset() throws IOException {
		String prefix = "gesture_dataset/";
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String fileName = "gesture_dataset_" + time + ".zip";

		Path tempDir = Files.createTempDirectory("s3zip");
		Path zipPath = tempDir.resolveSibling(fileName);

		// 1. 모든 객체 가져오기 (1/, 2/, 3/, 4/ 포함)
		ListObjectsV2Response listRes = s3Client.listObjectsV2(ListObjectsV2Request.builder()
			.bucket(bucket)
			.prefix(prefix)
			.build());

		// 2. 파일 다운로드
		for (S3Object obj : listRes.contents()) {
			String key = obj.key(); // 예: gesture_dataset/1/abc.csv
			if (key.endsWith("/"))
				continue; // 폴더 자체는 skip

			String relativePath = key.substring(prefix.length()); // 예: 1/abc.csv
			Path localPath = tempDir.resolve(relativePath);
			Files.createDirectories(localPath.getParent());

			s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build(), localPath);
		}

		// 3. ZIP 생성
		try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipPath))) {
			Files.walk(tempDir)
				.filter(Files::isRegularFile)
				.forEach(path -> {
					try (InputStream in = new FileInputStream(path.toFile())) {
						String relativePath = tempDir.relativize(path).toString(); // 1/abc.csv
						zipOut.putNextEntry(new ZipEntry(relativePath));
						in.transferTo(zipOut);
						zipOut.closeEntry();
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
		}

		FileSystemUtils.deleteRecursively(tempDir);
		return zipPath;
	}
}

