/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.model.xml.impl;

import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.impl.util.ModelUtil;
import org.camunda.bpm.model.xml.impl.util.QName;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A model contains all defined types and the relationship between them.
 *
 * @author Daniel Meyer
 *
 */
public class ModelImpl implements Model {

  private final Map<QName, ModelElementType> typesByName = new HashMap<QName, ModelElementType>();
  private final Map<Class<? extends ModelElementInstance>, ModelElementType> typesByClass = new HashMap<Class<? extends ModelElementInstance>, ModelElementType>();
  private final String modelName;

  protected final Map<String, List<String>> alternativeNamesapaces = new HashMap<String, List<String>>();
  protected final Map<String, String> actualNamespaces = new HashMap<String, String>();

  /**
   * Create a new {@link Model} with a model name.
   * @param modelName  the model name to identify the model
   */
  public ModelImpl(String modelName) {
    this.modelName = modelName;
  }

  public void declareAlternativeNamespace(String alternativeNs, String actualNs) {
    List<String> list = alternativeNamesapaces.get(actualNs);
    if(list == null) {
      list = new ArrayList<String>();
      alternativeNamesapaces.put(actualNs, list);
    }
    list.add(alternativeNs);
  }

  public List<String> getAlternativeNamespaces(String actualNs) {
    List<String> list = alternativeNamesapaces.get(actualNs);
    if(list == null) {
      return Collections.emptyList();
    }
    else {
      return list;
    }
  }

  public Collection<ModelElementType> getTypes() {
    return new ArrayList<ModelElementType>(typesByName.values());
  }

  public ModelElementType getType(Class<? extends ModelElementInstance> instanceClass) {
    return typesByClass.get(instanceClass);
  }

  public ModelElementType getTypeForName(String typeName) {
    return getTypeForName(null, typeName);
  }

  public ModelElementType getTypeForName(String namespaceUri, String typeName) {
    return typesByName.get(ModelUtil.getQName(namespaceUri, typeName));
  }

  /**
   * Registers a {@link ModelElementType} in this {@link Model}.
   *
   * @param modelElementType  the element type to register
   * @param instanceType  the instance class of the type to register
   */
  public void registerType(ModelElementType modelElementType, Class<? extends ModelElementInstance> instanceType) {
    QName qName = ModelUtil.getQName(modelElementType.getTypeNamespace(), modelElementType.getTypeName());
    typesByName.put(qName, modelElementType);
    typesByClass.put(instanceType, modelElementType);
  }

  public String getModelName() {
    return modelName;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + ((modelName == null) ? 0 : modelName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ModelImpl other = (ModelImpl) obj;
    if (modelName == null) {
      if (other.modelName != null) {
        return false;
      }
    } else if (!modelName.equals(other.modelName)) {
      return false;
    }
    return true;
  }

  public String getActualNamespace(String namespaceUri) {
    return null;
  }

}
