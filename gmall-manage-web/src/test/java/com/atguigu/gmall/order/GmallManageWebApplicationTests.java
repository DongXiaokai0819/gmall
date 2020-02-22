package com.atguigu.gmall.order;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageWebApplicationTests {

    @Test
    public void textFileUpload() throws IOException, MyException {
        //配置fdfs的全局链接地址
        String file = this.getClass().getResource("/tracker.conf").getFile();
        ClientGlobal.init(file);

        TrackerClient trackerClient=new TrackerClient();
        TrackerServer trackerServer=trackerClient.getConnection();
        StorageClient storageClient=new StorageClient(trackerServer,null);

        String orginalFilename="d://333.jpg";
        String[] upload_file = storageClient.upload_file(orginalFilename, "jpg", null);

        String url = "http://49.232.8.170";

        for (String s : upload_file) {
            url += "/" + s;
        }
        System.out.println(url);
    }


}
