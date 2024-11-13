package com.playdata.orderservice.common.configs;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

// AWS에 연결해서 S3에 관련된 서비를 실행하는 전용 객체
@Component
@Slf4j
public class AwsS3Config {

    // S3 Bucket을 제어하는 객체
    private S3Client s3Client;

    @Value("${spring.cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    // S3에 연결해서 인증을 처리하는 로직
    @PostConstruct // 클래스를 기반으로 객체가 생성될때 1번만 실행되는 어노테이션
    private void initializeAmazonS3Client() {

        // accessKey와 secretKey를 이용해서 계정 인증 받기
        AwsBasicCredentials credentials
                = AwsBasicCredentials.create(accessKey, secretKey);

        // 지역 설정 및 인증 정보를 담은 S3Client 객체를 위의 S3Client 변수에 세팅
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * 버킷에 파일을 업로드하고, 업로드한 버킷의 url 정보를 리턴
     *
     * @param uploadFile - 업로드 할 파일의 실제 raw 데이터, 버킷에 쏠때 Byte로 보내야함.
     * @param fileName   - 업로드 할 파일명
     * @return - 버킷에 업로드 된 버킷 경로(url)
     */
    public String uploadToS3Bucket(byte[] uploadFile, String fileName) {
        // 업로드할 파일을 S3 오브젝트로 생성.
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        // 오브젝트를 버킷에 업로드(위에서 선언한 오브젝트, 업로드하고자 하는 파일(바이트 배열))
        s3Client.putObject(request, RequestBody.fromBytes(uploadFile));

        // 업로드 되는 파일의 URL을 리턴.
        return s3Client.utilities()
                .getUrl(b -> b.bucket(bucketName).key(fileName))
                .toString();
    }

    // 버킷에 업로드 된 이미지를 삭제하는 로직
    // 버킷에 오브젝트를 지우기 위해서는 키값을 줘야 하는데,
    // DB에 저장되어 있는 건 키가 아니라 URL 임.
    // 원본: https://ordersvc-img0807.s3.ap-northeast-2.amazonaws.com/82064e07-5c49-4606-b517-0edc43774b1d_add-250x140.jpg
    // 결과: 82064e07-5c49-4606-b517-0edc43774b1d_add-250x140.jpg
    public void deleteFromS3Bucket(String fileName) throws Exception {
        log.info("Deleting file {}", fileName);

        URL url = new URL(fileName);

        // getPath()를 통해 Key 값 앞에 "/"까지 포함해서 제거.
        // 파일명에 한글이 포함되어 있을 경우, UTF-8 decoding 적용
        String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
        String key = decodingKey.substring(1);
        log.info("Deleting key {}", key);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

}
