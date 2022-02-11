package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

public class S3DAO {
    private static final String BUCKET_NAME = "my-bucket-340";

    public String upload(String alias, String base64Image) throws IOException {
        URL url = null;

        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();

        String fileName = String.format("%s_profile_image", alias);
        System.out.println(fileName);

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/jpg");

        InputStream stream = new ByteArrayInputStream(imageBytes);

        PutObjectRequest fileRequest = new PutObjectRequest(BUCKET_NAME, fileName,
                stream, metadata).withCannedAcl(CannedAccessControlList.PublicRead);

        stream.close();

        s3.putObject(fileRequest);

        url = s3.getUrl(BUCKET_NAME, fileName);
        if(url == null){
            System.out.println("url in s3dao is null");
        }
        System.out.println(url);

        return url.toString();
    }
}
