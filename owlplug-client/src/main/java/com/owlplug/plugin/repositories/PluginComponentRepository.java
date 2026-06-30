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

package com.owlplug.plugin.repositories;

import com.owlplug.plugin.model.PluginComponent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PluginComponentRepository extends JpaRepository<PluginComponent, Long>, JpaSpecificationExecutor<PluginComponent> {

  @Query("SELECT pc.manufacturerName AS label, COUNT(pc) AS cnt "
             + "FROM PluginComponent pc WHERE pc.manufacturerName IS NOT NULL AND pc.manufacturerName <> '' "
             + "GROUP BY pc.manufacturerName")
  List<StringCountEntry> countManufacturerNamesFromComponents();

  @Query("SELECT pc.category AS label, COUNT(pc) AS cnt "
             + "FROM PluginComponent pc WHERE pc.category IS NOT NULL AND pc.category <> '' "
             + "GROUP BY pc.category")
  List<StringCountEntry> countCategoriesFromComponents();

  @Query("SELECT p.format AS label, COUNT(pc) AS cnt FROM PluginComponent pc "
             + "JOIN pc.plugin p WHERE p.format IS NOT NULL GROUP BY p.format")
  List<StringCountEntry> countFormatsFromComponents();
}