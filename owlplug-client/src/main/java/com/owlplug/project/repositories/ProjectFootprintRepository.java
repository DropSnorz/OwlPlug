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

package com.owlplug.project.repositories;

import com.owlplug.project.model.ProjectFootprint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectFootprintRepository extends JpaRepository<ProjectFootprint, Long> {

  ProjectFootprint findByPath(String path);

  List<ProjectFootprint> findByLabelsId(Long labelId);

  @Query("SELECT f FROM ProjectFootprint f WHERE NOT EXISTS (SELECT p FROM DawProject p WHERE p.footprint = f)")
  List<ProjectFootprint> findOrphans();

}
