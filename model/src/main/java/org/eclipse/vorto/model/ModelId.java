/**
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.vorto.model;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Edelmann - Robert Bosch (SEA) Pte. Ltd.
 */
public class ModelId implements IReferenceType {
  private String name;
  private String namespace;
  private String version;

  private static final List<ModelIdParser> parsers =
      Arrays.asList(new ModelIdParserOld(), new ModelIdParserNew());

  public ModelId() {}

  public ModelId(String name, String namespace, String version) {
    super();
    this.name = name;
    this.namespace = namespace.toLowerCase();
    this.version = version;
  }

  public static ModelId fromReference(String qualifiedName, String version) {
    String name = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    String namespace = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    return new ModelId(name, namespace, version);
  }

  public static ModelId fromPrettyFormat(String prettyFormat) {
    for (ModelIdParser parser : parsers) {
      if (parser.canHandle(prettyFormat)) {
        return parser.parse(prettyFormat);
      }
    }
    throw new IllegalArgumentException(
        "Model ID is invalid. Must follow either pattern <namespace>:<name>:<version> or <namespace>.<name>:<version>");
  }


  public static ModelId newVersion(ModelId id, String newVersion) {
    return ModelId.fromPrettyFormat(id.getNamespace() + ":" + id.getName() + ":" + newVersion);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }


  @Override
  public String toString() {
    return getPrettyFormat();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ModelId other = (ModelId) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (namespace == null) {
      if (other.namespace != null)
        return false;
    } else if (!namespace.equals(other.namespace))
      return false;
    if (version == null) {
      if (other.version != null)
        return false;
    } else if (!version.equals(other.version))
      return false;
    return true;
  }

  public String getPrettyFormat() {
    return namespace + ":" + name + ":" + version;
  }

  private static interface ModelIdParser {
    boolean canHandle(String modelId);

    ModelId parse(String modelId);
  }

  public static class ModelIdParserNew implements ModelIdParser {

    @Override
    public boolean canHandle(String modelId) {
      return modelId.split(":").length == 3;
    }

    @Override
    public ModelId parse(String modelId) {
      String[] tripleParts = modelId.split(":");
      return new ModelId(tripleParts[1], tripleParts[0], tripleParts[2]);
    }
  }

  public static class ModelIdParserOld implements ModelIdParser {

    @Override
    public boolean canHandle(String modelId) {
      return modelId.split(":").length == 2;
    }

    @Override
    public ModelId parse(String modelId) {
      final int versionIndex = modelId.indexOf(":");
      return ModelId.fromReference(modelId.substring(0, versionIndex),
          modelId.substring(versionIndex + 1));
    }
  }
}
