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
package org.sonar.core.purge;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.sonar.core.persistence.AbstractDaoTestCase;
import org.sonar.core.persistence.MyBatis;

import java.util.Arrays;

public class PurgeCommandsTest extends AbstractDaoTestCase {

  private PurgeProfiler profiler;

  @Before
  public void prepare() {
    profiler = new PurgeProfiler();
  }

  /**
   * Test that all related data is deleted.
   */
  @Test
  public void shouldDeleteSnapshot() {
    setupData("shouldDeleteSnapshot");

    SqlSession session = getMyBatis().openSession();
    try {
      new PurgeCommands(session, profiler).deleteSnapshots(PurgeSnapshotQuery.create().setId(5L));
    } finally {
      MyBatis.closeQuietly(session);
    }
    checkTables("shouldDeleteSnapshot",
        "snapshots", "project_measures", "measure_data", "rule_failures", "snapshot_sources", "duplications_index", "events", "dependencies", "snapshot_data");
  }

  /**
   * Test that all related data is purged.
   */
  @Test
  public void shouldPurgeSnapshot() {
    setupData("shouldPurgeSnapshot");

    SqlSession session = getMyBatis().openSession();
    try {
      new PurgeCommands(session, profiler).purgeSnapshots(PurgeSnapshotQuery.create().setId(1L));
    } finally {
      MyBatis.closeQuietly(session);
    }
    checkTables("shouldPurgeSnapshot",
        "snapshots", "project_measures", "measure_data", "rule_failures", "snapshot_sources", "duplications_index", "events", "dependencies", "reviews", "snapshot_data");
  }

  @Test
  public void shouldDeleteWastedMeasuresWhenPurgingSnapshot() {
    setupData("shouldDeleteWastedMeasuresWhenPurgingSnapshot");

    SqlSession session = getMyBatis().openSession();
    try {
      new PurgeCommands(session, profiler).purgeSnapshots(PurgeSnapshotQuery.create().setId(1L));
    } finally {
      MyBatis.closeQuietly(session);
    }
    checkTables("shouldDeleteWastedMeasuresWhenPurgingSnapshot", "project_measures");
  }

  @Test
  public void shouldDeleteResource() {
    setupData("shouldDeleteResource");
    SqlSession session = getMyBatis().openSession();
    try {
      new PurgeCommands(session, profiler).deleteResources(Arrays.asList(1L));
    } finally {
      MyBatis.closeQuietly(session);
    }
    assertEmptyTables("projects", "snapshots", "events", "reviews", "review_comments", "issues", "issue_changes", "authors");
  }

}
