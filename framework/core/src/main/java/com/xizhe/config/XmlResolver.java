package com.xizhe.config;

import com.xizhe.IdGenerator;
import com.xizhe.ProtocolConfig;
import com.xizhe.compress.CompressType;
import com.xizhe.discovery.RegistryConfig;
import com.xizhe.loadbalance.LoadBalancer;
import com.xizhe.serialize.SerializerType;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/11 12:31
 */

@Slf4j
public class XmlResolver {

    public void loadFromXMl(Configuration configuration) {
        try {
            // 创建一个document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("rpc.xml");
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            // 获取一个xpath解析器
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            // 解析表达式
            configuration.setPort(resolvePort(doc, xPath));
            configuration.setAppName(resolveAppName(doc,xPath));
            configuration.setCompressType(resolveCompressType(doc,xPath));
            configuration.setSerializeType(resolveSerializeType(doc,xPath));
            configuration.setIdGenerator(resolveIdGenerator(doc,xPath));
            configuration.setRegistryConfig(resolveRegistryConfig(doc,xPath));
            configuration.setLoadBalancer(resolveLoadBalancer(doc,xPath));
            configuration.setProtocolConfig(new ProtocolConfig(SerializerType.getNameByType(configuration.getSerializeType())));
            System.out.println(1);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.error("读取xml配置文件出现异常",e);
            throw new RuntimeException(e);
        }
    }

    private RegistryConfig resolveRegistryConfig(Document doc, XPath xPath) {
        String expression = "configuration/registry";
        String url = parseString(doc, xPath, expression, "url");
        return new RegistryConfig(url);
    }

    private LoadBalancer resolveLoadBalancer(Document doc, XPath xPath) {
        String expression = "configuration/loadBalancer";
        LoadBalancer parse = parse(doc, xPath, expression, null);
        return parse;
    }

    private byte resolveSerializeType(Document doc, XPath xPath) {
        String expression = "configuration/serializeType";
        String compressName = parseString(doc, xPath, expression,"type");
        return SerializerType.getTypeByName(compressName);
    }

    private byte resolveCompressType(Document doc, XPath xPath) {
        String expression = "configuration/compressType";
        String compressName = parseString(doc, xPath, expression,"type");
        return CompressType.getTypeByName(compressName);
    }

    private IdGenerator resolveIdGenerator(Document doc, XPath xPath) {
        String expression = "configuration/idGenerator";
        String classname = parseString(doc, xPath, expression,"class");
        String dataCenterId = parseString(doc, xPath, expression,"dataCenterId");
        String machineId = parseString(doc, xPath, expression,"machineId");
        Class<?> aClass = null;
        try {
            aClass = Class.forName(classname);
            Object instance = aClass.getConstructor(new Class[]{long.class, long.class})
                    .newInstance(Long.parseLong(machineId), Long.parseLong(dataCenterId));
            return (IdGenerator) instance;
        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String resolveAppName(Document doc, XPath xPath) {
        String expression = "configuration/appName";
        return parseString(doc, xPath, expression);
    }

    private int resolvePort(Document doc, XPath xPath) {
        String expression = "configuration/port";
        String portString = parseString(doc, xPath, expression);
        return Integer.parseInt(portString);
    }

    /**
     *  解析一个节点 返回其实例
     * @param doc 文档对象
     * @param xPath xpath解析器
     * @param expr xpath表达式
     * @param parameterType 参数列表
     * @param param 参数
     * @param <T>
     * @return 配置的实例
     */
    private <T> T parse(Document doc, XPath xPath,String expr,Class<?>[] parameterType, Object... param) {
        try {
            XPathExpression expression = xPath.compile(expr);
            Node targetNode =  (Node)expression.evaluate(doc, XPathConstants.NODE);
            String className = targetNode.getAttributes().getNamedItem("class").getNodeValue();
            System.out.println(className);
            Class<?> clazz = Class.forName(className);
            Object instance = null;
            if (parameterType == null) {
                instance = clazz.getConstructor().newInstance();
            }else {
                instance = clazz.getConstructor(parameterType).newInstance(param);
            }
            return (T) instance;
        } catch (ClassNotFoundException | NoSuchMethodException | XPathExpressionException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得一个节点属性的值
     * @param doc 文档对象
     * @param xPath xpath解析器
     * @param expr xpath表达式
     * @param attributeName 节点名称
     * @return 节点的值
     */
    private String parseString(Document doc, XPath xPath,String expr,String attributeName) {
        try {
            XPathExpression expression = xPath.compile(expr);
            Node targetNode =  (Node)expression.evaluate(doc, XPathConstants.NODE);
            return targetNode.getAttributes().getNamedItem(attributeName).getNodeValue();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获得一个节点的文本
     * @param doc 文档对象
     * @param xPath xpath解析器
     * @param expr xpath表达式
     * @return 节点的值
     */
    private String parseString(Document doc, XPath xPath,String expr) {
        try {
            XPathExpression expression = xPath.compile(expr);
            Node targetNode =  (Node)expression.evaluate(doc, XPathConstants.NODE);
            return targetNode.getTextContent();
        } catch (XPathExpressionException  e) {
            e.printStackTrace();
        }
        return null;
    }
}
