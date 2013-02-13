/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.scan.filesystem;

import org.junit.Test;
import org.sonar.api.CoreProperties;
import org.sonar.api.config.Settings;

import static org.fest.assertions.Assertions.assertThat;

public class ModuleExclusionsTest {
  @Test
  public void ignore_inclusion_of_world() {
    Settings settings = new Settings();
    settings.setProperty(CoreProperties.PROJECT_INCLUSIONS_PROPERTY, "**/*");
    settings.setProperty(CoreProperties.PROJECT_TEST_INCLUSIONS_PROPERTY, "**/*");
    assertThat(new ModuleExclusions(settings).sourceInclusions()).isEmpty();
    assertThat(new ModuleExclusions(settings).testInclusions()).isEmpty();
  }

  @Test
  public void load_inclusions() {
    Settings settings = new Settings();
    settings.setProperty(CoreProperties.PROJECT_INCLUSIONS_PROPERTY, "**/*Foo.java");
    settings.setProperty(CoreProperties.PROJECT_TEST_INCLUSIONS_PROPERTY, "**/*FooTest.java");
    ModuleExclusions moduleExclusions = new ModuleExclusions(settings);

    assertThat(moduleExclusions.sourceInclusions()).containsOnly("**/*Foo.java");
    assertThat(moduleExclusions.testInclusions()).containsOnly("**/*FooTest.java");
  }

  @Test
  public void load_exclusions() {
    Settings settings = new Settings();
    settings.setProperty(CoreProperties.PROJECT_EXCLUSIONS_PROPERTY, "**/*Foo.java");
    settings.setProperty(CoreProperties.PROJECT_TEST_EXCLUSIONS_PROPERTY, "**/*FooTest.java");
    ModuleExclusions moduleExclusions = new ModuleExclusions(settings);

    assertThat(moduleExclusions.sourceInclusions()).isEmpty();
    assertThat(moduleExclusions.sourceExclusions()).containsOnly("**/*Foo.java");
    assertThat(moduleExclusions.testInclusions()).isEmpty();
    assertThat(moduleExclusions.testExclusions()).containsOnly("**/*FooTest.java");
  }
}