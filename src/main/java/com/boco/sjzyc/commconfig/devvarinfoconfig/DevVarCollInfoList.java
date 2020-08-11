package com.boco.sjzyc.commconfig.devvarinfoconfig;

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
@XmlRootElement(name = "devVarList")
@XmlAccessorType(XmlAccessType.FIELD)
public class DevVarCollInfoList {

    @XmlElement(name = "devece")
    private List<DevVarCollInfo> devVarCollInfoList;

}
