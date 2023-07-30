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

package com.owlplug.core.services;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService extends BaseService {

  @PostConstruct
  private void initialize() {
    // All analytics features has been decommissioned
    
  }
  
  public void pageView(String name) {
    // All analytics features has been decommissioned
    
  }
  
  public void pageView(String name, String... parameters) {
    // All analytics features has been decommissioned
        
  }
  

}
