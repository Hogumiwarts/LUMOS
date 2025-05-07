package com.hogumiwarts.lumos.gesturesensor.util;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Uploader {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final S3Client s3Client = S3Client.create();

	public String upload(File file, String key) {
		PutObjectRequest putReq = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType("text/csv")
			.build();

		s3Client.putObject(putReq, file.toPath());
		return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(key)).toExternalForm();
	}
}
