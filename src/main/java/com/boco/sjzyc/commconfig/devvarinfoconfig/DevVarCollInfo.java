package com.boco.sjzyc.commconfig.devvarinfoconfig;

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
public class DevVarCollInfo {
    @XmlAttribute(name = "dwdevTypeId")   //设备变量类型编码
    private Integer dwdevTypeId;
    @XmlAttribute(name = "dwVariantId")   //设备变量编码
    private Integer dwVariantId;
    @XmlAttribute(name = "szVariantDesc") //设备变量描述
    private String szVariantDesc;
    @XmlAttribute(name = "dwSendCmd") //设备变量通讯发送编码
    private Integer dwSendCmd;
    @XmlAttribute(name = "dwRecvCmd") //设备变量通讯接收编码
    private Integer dwRecvCmd;
    @XmlAttribute(name = "szFuncAbbr") //设备变量通讯功能描述
    private String szFuncAbbr;
}
