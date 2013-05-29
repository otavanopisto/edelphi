package fi.internetix.edelphi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.utils.SystemUtils;
import fi.internetix.smvc.controllers.RequestControllerMapper;

public class ServletContextListener implements javax.servlet.ServletContextListener {

  public void contextDestroyed(ServletContextEvent ctx) {
  }

  public void contextInitialized(ServletContextEvent servletContextEvent) {
    GenericDAO.setEntityManager(entityManager);
    try {
    
      try {
        ServletContext ctx = servletContextEvent.getServletContext();
        String webappPath = ctx.getRealPath("/");
  
        Properties pageControllers = new Properties();
        Properties jsonControllers = new Properties();
        Properties binaryControllers = new Properties();
  
        loadPropertiesFile(ctx, pageControllers, webappPath + "WEB-INF/classes/pagemappings.properties");
        loadPropertiesFile(ctx, jsonControllers, webappPath + "WEB-INF/classes/jsonmappings.properties");
        loadPropertiesFile(ctx, binaryControllers, webappPath + "WEB-INF/classes/binarymappings.properties");
  
        RequestControllerMapper.mapControllers(pageControllers, ".page");
        RequestControllerMapper.mapControllers(jsonControllers, ".json");
        RequestControllerMapper.mapControllers(binaryControllers, ".binary");
        
        System.getProperties().setProperty("appdirectory", webappPath);
     
        if (!SystemUtils.isProductionEnvironment()) {
          trustSelfSignedCerts();
        }
        
      } catch (Exception e) {
        e.printStackTrace();
        throw new ExceptionInInitializerError(e);
      }
    
    } finally {
      GenericDAO.setEntityManager(null);
    }
  }

  private void loadPropertiesFile(ServletContext servletContext, Properties properties, String filename) throws FileNotFoundException, IOException {
    File settingsFile = new File(filename);
    if (settingsFile.canRead()) {
      properties.load(new FileReader(settingsFile));
    }
  }
  
  private static void trustSelfSignedCerts() {
    try {
      TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return null;
        }
  
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
  
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
      } };
  
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
    }
  }

  
  @PersistenceContext
  private EntityManager entityManager;
}
