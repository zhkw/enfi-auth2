package com.cisdi.cpm.auth.helper.tablehelper;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TableHolder {
    public Map<String, Map<String, String>> parserTable(String fileName) throws ParserConfigurationException, SAXException, IOException {
        String path = this.getClass().getClassLoader().getResource(fileName).getPath();
        Document doc = new XmlParser().getDocument(path);

        NodeList nodeList = doc.getElementsByTagName("table");

        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        if (nodeList != null) {
            String tableName = "";
            //是否被修改过，如果没有则不进行字段映射
            String isModified = "";
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                NamedNodeMap nnm = node.getAttributes();

                //表名
                tableName = nnm.getNamedItem("name").getNodeValue();
                Map<String, String> table = new HashMap<String, String>();
                table.put("tableName", nnm.getNamedItem("newName").getNodeValue());

                //判断该表是否被修改过
                isModified = nnm.getNamedItem("isModified").getNodeValue();
                if ("N".equals(isModified)) {
                    table.put("isModified", "N");
                    result.put(tableName, table);
                    continue;
                }
                table.put("isModified", "Y");

                //字段
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    if ("#text".equals(childNodes.item(j).getNodeName())) {
                        continue;
                    }
                    NamedNodeMap attrs = childNodes.item(j).getAttributes();
                    table.put(attrs.getNamedItem("name").getNodeValue(), attrs.getNamedItem("newName").getNodeValue());
                }
                result.put(tableName, table);
            }
        }

        return result;
    }
}
