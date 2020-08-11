package com.boco.sjzyc.commconfig.devvarinfoconfig;

import com.boco.sjzyc.commconfig.XmlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service("devVarCollInfoDataServiceImpl")
public class DevVarCollInfoDataServiceImpl implements DevVarCollInfoDataService {
    private static final Logger logger = LoggerFactory.getLogger(DevVarCollInfoDataServiceImpl.class);

    @Override
    public List<DevVarCollInfo> getListDevVarInfo() throws Exception {
        //XML转为JAVA对象
        DevVarCollInfoList devVarInfoList = null;
        StringBuffer buffer = new StringBuffer();
        try {
            File file = new File("config/devVarListConfig.xml"); // 这里表示从jar同级目录加载
            if (!file.exists()) { // 如果同级目录没有，则去config下面找
                //logger.info("getDevVarInfoList读取路径----------------->!file.exists()");
                file = new File("config/devVarListConfig.xml");
            }

            Resource resource = new FileSystemResource(file);

            if (!resource.exists()) { //config目录下还是找不到，那就直接用classpath下的
                //logger.info("getDevVarInfoList读取路径----------------->从jar包中获取");
                resource = new ClassPathResource("config/devVarListConfig.xml");
                //可放到linux服务器中
//                InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/devCommConfig.xml");
//                BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            }


            //利用输入流获取XML文件内容
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));
            String line = "";
            while ((line = br.readLine()) != null) {// logger.info(line);
                buffer.append(line);
            }
            br.close();
            //XML转为JAVA对象
            devVarInfoList = (DevVarCollInfoList) XmlBuilder.xmlStrToObject(DevVarCollInfoList.class, buffer.toString());
        } catch (Exception ex) {
            logger.info("getlistDevVarInfo读取异常----------------->" + ex.toString());
        }
        return devVarInfoList.getDevVarCollInfoList();
    }

    @Override
    public List<DevVarCollInfo> getCurDevVarCollInfo(String devTypeid) throws Exception {
        List<DevVarCollInfo> result = new ArrayList<>();

        StringBuffer buffer = new StringBuffer();
        try {
            File file = new File("config/devVarListConfig.xml"); // 这里表示从jar同级目录加载
            if (!file.exists()) { // 如果同级目录没有，则去config下面找
                logger.info("getDevVarInfoList读取路径----------------->!file.exists()");
                file = new File("config/devVarListConfig.xml");
            }

            Resource resource = new FileSystemResource(file);

            if (!resource.exists()) { //config目录下还是找不到，那就直接用classpath下的
                logger.info("getDevVarInfoList读取路径----------------->从jar包中获取");
                resource = new ClassPathResource("config/devVarListConfig.xml");
                //可放到linux服务器中
//                InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/devCommConfig.xml");
//                BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            }

            //利用输入流获取XML文件内容
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));

            String line = "";
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            br.close();
            //XML转为对象
            DevVarCollInfoList DevVarCollInfoList = (DevVarCollInfoList) XmlBuilder.xmlStrToObject(DevVarCollInfoList.class, buffer.toString());
            List<DevVarCollInfo> DevVarCollInfos = DevVarCollInfoList.getDevVarCollInfoList();
            for (int k = 0; k < DevVarCollInfos.size(); k++) {
                if (DevVarCollInfos.get(k).getDwdevTypeId().equals(Integer.valueOf(devTypeid))) {
                    result.add(DevVarCollInfos.get(k));
                }
            }
        } catch (Exception ex) {
            logger.info("getlistDevVarInfo读取异常----------------->" + ex.toString());
        }
        return result;
    }

}
