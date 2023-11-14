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

package com.owlplug.core.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

  public static final List<Long> times = Arrays.asList(
          TimeUnit.DAYS.toMillis(365),
          TimeUnit.DAYS.toMillis(30),
          TimeUnit.DAYS.toMillis(1),
          TimeUnit.HOURS.toMillis(1),
          TimeUnit.MINUTES.toMillis(1),
          TimeUnit.SECONDS.toMillis(1));
  public static final List<String> timesString = Arrays.asList("year","month","day","hour","minute","second");

  public static String getHumanReadableDurationFrom(Date date) {
    long duration = new Date().getTime() - date.getTime();
    return toDuration(duration);

  }

  private static String toDuration(long duration) {

    StringBuilder res = new StringBuilder();
    for (int i = 0; i < times.size(); i++) {
      Long current = times.get(i);
      long temp = duration / current;
      if (temp > 0) {
        res.append(temp).append(" ").append(timesString.get(i)).append(temp != 1 ? "s" : "").append(" ago");
        break;
      }
    }
    if ("".contentEquals(res)) {
      return "0 seconds ago";
    } else {
      return res.toString();
    }
  }

}
