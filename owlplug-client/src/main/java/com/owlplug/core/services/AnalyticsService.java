/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.request.PageViewHit;
import com.owlplug.core.components.ApplicationDefaults;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService extends BaseService {
  
  private GoogleAnalytics ga;
    
  @PostConstruct
  private void initialize() {
    
    ga = GoogleAnalytics.builder()
        .withTrackingId(this.getApplicationDefaults().getAnalyticsTrackingId())
        .build();
    
    // Ignore session UUID for now.
    //sessionUUID = UUID.randomUUID().toString();
    
  }
  
  public void pageView(String name) {
    ga.pageView()
      .documentTitle(name)
      .documentPath(name)
      .applicationName(ApplicationDefaults.APPLICATION_NAME)
      .applicationVersion(this.getApplicationDefaults().getVersion())
      .sendAsync();
    
  }
  
  public void pageView(String name, String... parameters) {
    
    PageViewHit pvh = ga.pageView()
        .documentTitle(name)
        .documentPath(name)
        .applicationName(ApplicationDefaults.APPLICATION_NAME)
        .applicationVersion(this.getApplicationDefaults().getVersion());
    
    int customDimensionId = 1;
    for (String dimension : parameters) {
      pvh.customDimension(customDimensionId, dimension);
      customDimensionId = customDimensionId++;
    }
    pvh.sendAsync();
        
  }
  

}
