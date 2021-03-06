package org.infinispan.labs.lab1.service;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.infinispan.cdi.ConfigureCache;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.labs.lab1.transactions.JBoss7TransactionManagerLookup;
import org.infinispan.loaders.jdbc.stringbased.JdbcStringBasedCacheStore;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.CacheMode;

/**
 * Cache definitions
 *
 * @author Kevin Pollet <pollet.kevin@gmail.com> (C) 2011
 */
public class Resources {

   /**
    * <p>This producer defines the ticket allocation cache configuration.</p>
    * 
    */
   @TicketAllocationCache
   @ConfigureCache("ticketAllocationCache")
   @Produces
   public Configuration configureCache() {
      return new ConfigurationBuilder()
            .clustering().cacheMode(CacheMode.DIST_SYNC)
            .transaction().transactionManagerLookup(new JBoss7TransactionManagerLookup())
            .loaders()
               .shared(true)
               .addCacheLoader()
                  .cacheLoader(new JdbcStringBasedCacheStore())
                  .addProperty("connectionFactoryClass", "org.infinispan.loaders.jdbc.connectionfactory.ManagedConnectionFactory")
                  .addProperty("datasourceJndiLocation", "java:jboss/datasources/ExampleDS")
                  .addProperty("idColumnType", "VARCHAR(255)")
                  .addProperty("idColumnName", "ID_COLUMN")
                  .addProperty("dataColumnType", "BINARY")
                  .addProperty("dataColumnName", "DATA_COLUMN")
                  .addProperty("timestampColumnName", "TIMESTAMP_COLUMN")
                  .addProperty("timestampColumnType", "BIGINT")
                  .addProperty("stringsTableNamePrefix", "persistentStore")
                  .addProperty("userName", "sa")
                  .addProperty("password", "sa")
                  .async().threadPoolSize(10)
            .jmxStatistics()
            .build();
   }
   
   @Produces
   @ApplicationScoped
   public EmbeddedCacheManager configureCacheManager() {
      return new DefaultCacheManager(
            GlobalConfigurationBuilder.defaultClusteredBuilder()
                  .transport()
                     .addProperty("configurationFile", "jgroups.xml")
                  .build());
   }
   
   @Produces
   public Logger getLogger(InjectionPoint ip) {
      return Logger.getLogger(ip.getMember().getDeclaringClass().getName());
   }

}