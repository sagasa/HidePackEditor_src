//
// このファイルは、JavaTM Architecture for XML Binding(JAXB) Reference Implementation、v2.2.8-b130911.1802によって生成されました 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>を参照してください 
// ソース・スキーマの再コンパイル時にこのファイルの変更は失われます。 
// 生成日: 2020.05.28 時間 12:29:30 PM JST 
//


package org.collada._2005._11.colladaschema.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.collada._2005._11.colladaschema.Capsule;
import org.collada._2005._11.colladaschema.Extra;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "height",
    "radius",
    "extras"
})
@XmlRootElement(name = "capsule")
public class CapsuleImpl
    implements Capsule
{

    protected double height;
    @XmlList
    @XmlElement(type = Double.class)
    protected List<Double> radius;
    @XmlElement(name = "extra", type = ExtraImpl.class)
    protected List<Extra> extras;

    public double getHeight() {
        return height;
    }

    public void setHeight(double value) {
        this.height = value;
    }

    public List<Double> getRadius() {
        if (radius == null) {
            radius = new ArrayList<Double>();
        }
        return this.radius;
    }

    public List<Extra> getExtras() {
        if (extras == null) {
            extras = new ArrayList<Extra>();
        }
        return this.extras;
    }

}
