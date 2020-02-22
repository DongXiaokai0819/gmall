package com.atguigu.gmall.order.utils;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;


/***
 * 图片工具类
 */
public class ImageUtil {

    /**
     * 将选择的图片文件上传到文件资源服务器
     * @param multipartFile
     * @return
     */
    public static String uplocalImage(MultipartFile multipartFile){
        try {
            //加载tracker.conf,配置fdfs的全局链接地址
            String trackerConf = ImageUtil.class.getResource("/tracker.conf").getFile();
            ClientGlobal.init(trackerConf);

            //获取文件后缀名
            String fileName = multipartFile.getOriginalFilename();
            int index = fileName.lastIndexOf(".");
            String fileSuffix = fileName.substring(index+1);

            //初始化客户端，感觉这里也可以用单例来实现
            TrackerClient trackerClient=new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient=new StorageClient(trackerServer,null);
            //上传文件
            String[] upload_file = storageClient.upload_file(multipartFile.getBytes(), "jpg", null);
            //拼接url
            String url = "http://49.232.8.170";
            for (String s : upload_file) {//这里用stream比较好，
                // 但是忘记怎么用joining了，回头复习复习java8整理整理
                //第二遍用cloud做的时候再把觉得可以改进的地方改进
                //现在首要的任务是先把商城的大致流程走一遍
                url += "/" + s;
            }
            return url;
        } catch (Exception e) {
            System.out.println("上传文件出错！");
            e.printStackTrace();
        }
        return null;
    }
}
