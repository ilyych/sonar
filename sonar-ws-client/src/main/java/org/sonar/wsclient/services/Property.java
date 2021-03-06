/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.wsclient.services;

public class Property extends Model {

  private String key;
  private String value;

  public Property(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public Property() {
  }

  public String getKey() {
    return key;
  }

  public Property setKey(String key) {
    this.key = key;
    return this;
  }

  public String getValue() {
    return value;
  }

  public Property setValue(String value) {
    this.value = value;
    return this;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append('[')
        .append(key)
        .append(':')
        .append(value)
        .append(']')
        .toString();
  }
}
