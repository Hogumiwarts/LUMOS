package com.hogumiwarts.lumos.gesturesensor.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;
import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest.SensorValue;
import com.hogumiwarts.lumos.gesturesensor.util.S3Uploader;
import com.hogumiwarts.lumos.gesturesensor.util.S3ZipDownloader;
import com.opencsv.CSVWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

	private final S3Uploader s3Uploader;
	private final S3ZipDownloader s3ZipDownloader;

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

				for (SensorValue d : request.getData()) {
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

			return s3Uploader.upload(tempPath.toFile(), "gesture_dataset/" + request.getGestureId() + "/" + filename);
		} catch (Exception e) {
			log.error("CSV 업로드 실패", e);
			throw new RuntimeException("CSV 생성 또는 업로드 실패");
		}
	}

	public Path zipAndDownloadFolder(int folder) throws IOException {
		return s3ZipDownloader.createZipTempFile(folder);
	}
}
