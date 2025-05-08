package com.hogumiwarts.lumos.gesturesensor.service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;
import com.opencsv.CSVWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final S3Client s3Client;

	public String saveCsvAndUpload(SensorDataRequest request) {
		try {
			String filename = System.currentTimeMillis() + ".csv";
			Path tempPath = Files.createTempFile("sensor_", ".csv");

			try (CSVWriter writer = new CSVWriter(
				new FileWriter(tempPath.toFile()),
				CSVWriter.DEFAULT_SEPARATOR,
				CSVWriter.NO_QUOTE_CHARACTER,  // 따옴표 제거
				CSVWriter.DEFAULT_ESCAPE_CHARACTER,
				CSVWriter.DEFAULT_LINE_END
			)) {
				writer.writeNext(new String[]{
					"timestamp", "li_acc_x", "li_acc_y", "li_acc_z",
					"acc_x", "acc_y", "acc_z",
					"gryo_x", "gryo_y", "gryo_z"
				});

				for (SensorDataRequest.SensorValue d : request.getData()) {
					writer.writeNext(new String[]{
						String.valueOf(d.getTimestamp()),
						String.valueOf(d.getLiAccX()),
						String.valueOf(d.getLiAccY()),
						String.valueOf(d.getLiAccZ()),
						String.valueOf(d.getAccX()),
						String.valueOf(d.getAccY()),
						String.valueOf(d.getAccZ()),
						String.valueOf(d.getGryoX()),
						String.valueOf(d.getGryoY()),
						String.valueOf(d.getGryoZ())
					});
				}
			}

			return upload(tempPath.toFile(), "gesture_dataset/" + request.getGestureId() + "/" + filename);
		} catch (Exception e) {
			log.error("CSV 업로드 실패", e);
			throw new RuntimeException("CSV 생성 또는 업로드 실패");
		}
	}

	public String upload(File file, String key) {
		PutObjectRequest putRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType("text/csv")
			.build();

		s3Client.putObject(putRequest, file.toPath());

		// 업로드된 객체의 S3 URL 반환
		return s3Client.utilities()
			.getUrl(b -> b.bucket(bucket).key(key))
			.toExternalForm(); // https://{bucket}.s3.{region}.amazonaws.com/{key}
	}
}
