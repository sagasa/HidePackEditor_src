//
// このファイルは、JavaTM Architecture for XML Binding(JAXB) Reference Implementation、v2.2.8-b130911.1802によって生成されました 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>を参照してください 
// ソース・スキーマの再コンパイル時にこのファイルの変更は失われます。 
// 生成日: 2020.05.28 時間 12:29:30 PM JST 
//


package org.collada._2005._11.colladaschema;

import java.util.List;


/**
 * <p>anonymous complex typeのJavaクラス。
 * 
 * <p>次のスキーマ・フラグメントは、このクラス内に含まれる予期されるコンテンツを指定します。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}asset" minOccurs="0"/>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}node" maxOccurs="unbounded"/>
 *         &lt;element name="evaluate_scene" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="render" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="layer" type="{http://www.w3.org/2001/XMLSchema}NCName" maxOccurs="unbounded" minOccurs="0"/>
 *                             &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}instance_effect" minOccurs="0"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="camera_node" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
public interface VisualScene {


    /**
     * 
     * 						The visual_scene element may contain an asset element.
     * 						
     * 
     * @return
     *     possible object is
     *     {@link Asset }
     *     
     */
    Asset getAsset();

    /**
     * assetプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Asset }
     *     
     */
    void setAsset(Asset value);

    /**
     * 
     * 						The visual_scene element must have at least one node element.
     * 						Gets the value of the nodes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Node }
     * 
     * 
     */
    List<Node> getNodes();

    /**
     * Gets the value of the evaluateScenes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the evaluateScenes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEvaluateScenes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VisualScene.EvaluateScene }
     * 
     * 
     */
    List<VisualScene.EvaluateScene> getEvaluateScenes();

    /**
     * 
     * 						The extra element may appear any number of times.
     * 						Gets the value of the extras property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extras property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtras().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Extra }
     * 
     * 
     */
    List<Extra> getExtras();

    /**
     * idプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    String getId();

    /**
     * idプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    void setId(String value);

    /**
     * nameプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    String getName();

    /**
     * nameプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    void setName(String value);


    /**
     * <p>anonymous complex typeのJavaクラス。
     * 
     * <p>次のスキーマ・フラグメントは、このクラス内に含まれる予期されるコンテンツを指定します。
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="render" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="layer" type="{http://www.w3.org/2001/XMLSchema}NCName" maxOccurs="unbounded" minOccurs="0"/>
     *                   &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}instance_effect" minOccurs="0"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="camera_node" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    public interface EvaluateScene {


        /**
         * Gets the value of the renders property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the renders property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRenders().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VisualScene.EvaluateScene.Render }
         * 
         * 
         */
        List<VisualScene.EvaluateScene.Render> getRenders();

        /**
         * nameプロパティの値を取得します。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        String getName();

        /**
         * nameプロパティの値を設定します。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        void setName(String value);


        /**
         * <p>anonymous complex typeのJavaクラス。
         * 
         * <p>次のスキーマ・フラグメントは、このクラス内に含まれる予期されるコンテンツを指定します。
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="layer" type="{http://www.w3.org/2001/XMLSchema}NCName" maxOccurs="unbounded" minOccurs="0"/>
         *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}instance_effect" minOccurs="0"/>
         *       &lt;/sequence>
         *       &lt;attribute name="camera_node" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        public interface Render {


            /**
             * Gets the value of the layers property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the layers property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getLayers().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            List<String> getLayers();

            /**
             * 
             * 												The instance_effect element specifies which effect to render in this compositing step 
             * 												while evaluating the scene.
             * 												
             * 
             * @return
             *     possible object is
             *     {@link InstanceEffect }
             *     
             */
            InstanceEffect getInstanceEffect();

            /**
             * instanceEffectプロパティの値を設定します。
             * 
             * @param value
             *     allowed object is
             *     {@link InstanceEffect }
             *     
             */
            void setInstanceEffect(InstanceEffect value);

            /**
             * cameraNodeプロパティの値を取得します。
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            String getCameraNode();

            /**
             * cameraNodeプロパティの値を設定します。
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            void setCameraNode(String value);

        }

    }

}
