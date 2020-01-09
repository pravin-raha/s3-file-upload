package com.example.spring;

import com.amazonaws.services.s3.AmazonS3;
import io.findify.s3mock.S3Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
public class SpringApplicationTests {
  private S3Mock api;

  @Value("${amazonProperties.bucketName}")
  private String bucketName;

  @Autowired
  TestRestTemplate testRestTemplate;


  @Before
  public void before() {
    api = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
    api.start();
    amazonS3.createBucket(bucketName);
  }

  @After
  public void after() {
    api.stop();
  }

  @Autowired
  private AmazonS3 amazonS3;

  @Test
  public void testS3FileUploadEndpoint() {
    LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
    parameters.add("file", new org.springframework.core.io.ClassPathResource("test.txt"));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<LinkedMultiValueMap<String, Object>> entity =
        new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);

    ResponseEntity<String> response =
        testRestTemplate.exchange("/upload", HttpMethod.POST, entity, String.class, "");

    assertThat(response.getStatusCodeValue()).isEqualTo(200);

    assertEquals(amazonS3.getObject(bucketName, "pre-test.txt").getKey(), "pre-test.txt");
  }
}

