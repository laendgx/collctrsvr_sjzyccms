package com.boco.sjzyc.commconfig.devcommconfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "devCommCon")
@XmlAccessorType(XmlAccessType.FIELD)
public class DevcommInfoList {

    @XmlElement(name = "devece")
    private List<DevcommInfo> devcommInfoList;

}
