/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug.core.migration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.jpa.autoconfigure.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

/**
 * Hooks {@link DatabaseMigrator} phases around the EntityManagerFactory creation,
 * where Hibernate runs its {@code ddl-auto=update} schema update.
 */
@Configuration
public class DatabaseMigrationConfiguration {

  /**
   * Makes the EntityManagerFactory depend on the migrator, so
   * {@link MigrationPhase#BEFORE_SCHEMA_UPDATE} migrations run before Hibernate
   * updates the schema.
   */
  @Bean
  public static EntityManagerFactoryDependsOnPostProcessor entityManagerFactoryDependsOnDatabaseMigrator() {
    return new EntityManagerFactoryDependsOnPostProcessor("databaseMigrator");
  }

  /**
   * Runs {@link MigrationPhase#AFTER_SCHEMA_UPDATE} migrations as soon as the
   * EntityManagerFactory is built, before any other bean can use it.
   */
  @Bean
  public static BeanPostProcessor postSchemaUpdateMigrationTrigger(ObjectProvider<DatabaseMigrator> migrator) {
    return new BeanPostProcessor() {
      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof AbstractEntityManagerFactoryBean) {
          migrator.getObject().runPostSchemaUpdate();
        }
        return bean;
      }
    };
  }

}
