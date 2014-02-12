/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.model.xml.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sebastian Menski
 */
public final class StringUtil {

  private static final Pattern pattern = Pattern.compile("(\\w[^,]*)|([#$]\\{[^}]*})");

  /**
   * Splits a comma separated list in to single Strings. The list can
   * contain expressions with commas in it.
   *
   * @param text  the comma separated list
   * @return the Strings of the list
   */
  public static List<String> splitCommaSeparatedList(String text) {
    Matcher matcher = pattern.matcher(text);
    List<String> parts = new ArrayList<String>();
    while(matcher.find()) {
      parts.add(matcher.group().trim());
    }
    return parts;
  }

  /**
   * Joins a list of Strings to a comma separated single String.
   *
   * @param list  the list to join
   * @return the resulting comma separated string
   */
  public static String joinCommaSeparatedList(List<String> list) {
    if (list == null) {
      return null;
    }

    int size = list.size();
    if (size == 0) {
      return "";
    }
    else if (size == 1) {
      return list.get(0);
    }
    else {
      StringBuilder builder = new StringBuilder(size * 8);
      builder.append(list.get(0));
      for (Object element : list.subList(1, size)) {
        builder.append(", ");
        builder.append(element);
      }
      return builder.toString();
    }
  }
}
