/*
 * SonarQube Java Properties Custom Rules Plugin Example
 * Copyright (C) 2016-2016 David RACODON
 * david.racodon@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.jproperties.checks;

import java.io.File;

import org.junit.Test;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

public class ForbiddenKeysCheckTest {

  @Test
  public void test() {
    JavaPropertiesCheckVerifier.issues(new ForbiddenKeysCheck(), new File("src/test/resources/checks/forbiddenKeys.properties"))
      .next().atLine(5).withMessage("Remove the usage of this forbidden \"foo\" key.")
      .next().atLine(8).withMessage("Remove the usage of this forbidden \"bar\" key.")
      .noMore();
  }

}
