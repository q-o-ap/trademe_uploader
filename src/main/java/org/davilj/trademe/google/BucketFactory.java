package org.davilj.trademe.google;/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'daniev' at '4/13/17 8:28 PM' with Gradle 3.2.1
 *
 * @author daniev, @date 4/13/17 8:28 PM
 */
 // Imports the Google Cloud client library
 import com.google.auth.oauth2.ServiceAccountCredentials;
 import com.google.cloud.WriteChannel;
 import com.google.cloud.storage.*;

 import java.io.*;
 import java.nio.ByteBuffer;
 import java.nio.file.Files;

 import static java.nio.charset.StandardCharsets.UTF_8;


public class BucketFactory {
   public static BucketWrapper get(String bucketName) throws Exception {
     // Instantiates a client
       Storage storage = StorageOptions.newBuilder()
               .setProjectId("tradememining")
               .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("trademeMining-key.json")))
               .build()
               .getService();

     return new BucketWrapper(storage, "trademeupload");
   }

   static class BucketWrapper {
       private Storage storage;
       private String bucketName;

       public BucketWrapper(Storage storage, String bucketName) {
           this.storage = storage;
           this.bucketName = bucketName;
       }

       public boolean addFile(File file)  {
           //Doing this in groovy fails??
           BlobId blobId = BlobId.of(this.bucketName, file.getName());
           BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

           try (WriteChannel writer = storage.writer(blobInfo)) {
               byte[] buffer = new byte[1024];
               try (InputStream input = Files.newInputStream(file.toPath())) {
                   int limit;
                   while ((limit = input.read(buffer)) >= 0) {
                       writer.write(ByteBuffer.wrap(buffer, 0, limit));
                   }
               }
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
           return true;

       }
   }
 }
