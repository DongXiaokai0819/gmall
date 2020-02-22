package com.atguigu.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.pms.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {

    @Reference
    private SkuService skuService;

    @Autowired
    private JestClient jestClient;

    @Test
    public void contextLoads() throws IOException {
		put();
	}

    public void put() throws IOException {
        List<PmsSkuInfo> skuInfoList = new ArrayList<>();
        List<PmsSearchSkuInfo> searchSkuInfoList = new ArrayList<>();

        skuInfoList = skuService.getAllSkuInfo("61");
        for (PmsSkuInfo pmsSkuInfo : skuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));
            searchSkuInfoList.add(pmsSearchSkuInfo);
        }
        		// 导入es
		for (PmsSearchSkuInfo pmsSearchSkuInfo : searchSkuInfoList) {
			Index put = new Index.Builder(pmsSearchSkuInfo).index("gmall0105").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();
			jestClient.execute(put);
		}
    }

}
