package com.cisdi.cpm.auth.listener;

import com.cisdi.cpm.auth.helper.cachehelper.MapCacheOper;
import com.cisdi.cpm.auth.helper.tablehelper.TableHolder;
import org.xml.sax.SAXException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;

public class StartupListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        Map<String, Map<String, String>> tables = null;
        try {
            tables = new TableHolder().parserTable("tables.xml");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MapCacheOper.setKeyValue("tables", tables);
    }

}
