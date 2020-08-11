package com.boco.sjzyc.commconfig.devcommconfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "devece")
@XmlAccessorType(XmlAccessType.FIELD)
public class DevcommInfo {
    @XmlAttribute(name = "commId")   //设备通讯编码
    private String commId;
    @XmlAttribute(name = "devId")   //设备编码
    private String devId;
    @XmlAttribute(name = "devIp") //设备通讯ip信息
    private String devIp;
    @XmlAttribute(name = "devPort") //设备通讯ip信息
    private String devPort;
    @XmlAttribute(name = "localPort") //设备通讯ip信息
    private String localPort;
    @XmlAttribute(name = "devAddr") //设备地址
    private String devAddr;
}
