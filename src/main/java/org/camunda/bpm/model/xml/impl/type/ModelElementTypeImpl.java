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
package org.camunda.bpm.model.xml.impl.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.ModelException;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.impl.ModelImpl;
import org.camunda.bpm.model.xml.impl.ModelInstanceImpl;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.impl.util.DomUtil;
import org.camunda.bpm.model.xml.impl.util.ModelUtil;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder;
import org.camunda.bpm.model.xml.type.attribute.Attribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Daniel Meyer
 *
 */
public class ModelElementTypeImpl implements ModelElementType {

  private final ModelImpl model;

  private final String typeName;

  private final Class<? extends ModelElementInstance> instanceType;

  private String typeNamespace;

  private ModelElementTypeImpl baseType;

  private final List<ModelElementType> extendingTypes = new ArrayList<ModelElementType>();

  private List<Attribute<?>> attributes = new ArrayList<Attribute<?>>();

  private final List<ModelElementType> childElementTypes = new ArrayList<ModelElementType>();

  private ModelElementTypeBuilder.ModelTypeInstanceProvider<?> instanceProvider;

  private boolean isAbstract;

  public ModelElementTypeImpl(ModelImpl model, String name, Class<? extends ModelElementInstance> instanceType) {
    this.model = model;
    this.typeName = name;
    this.instanceType = instanceType;
  }

  public ModelElementInstance newInstance(ModelInstance modelInstance) {
    ModelInstanceImpl modelInstanceImpl = (ModelInstanceImpl) modelInstance;
    Document document = modelInstanceImpl.getDocument();
    Element domElement = document.createElementNS(typeNamespace, typeName);
    return newInstance(modelInstanceImpl, domElement);
  }

  public ModelElementInstance newInstance(ModelInstanceImpl modelInstance, Element domElement) {
    return createModelElementInstance(new ModelTypeInstanceContext(domElement, modelInstance, this));
  }

  public void registerAttribute(Attribute<?> attribute) {
    attributes.add(attribute);
  }

  public void registerChildElementType(ModelElementType childElementType) {
    childElementTypes.add(childElementType);
  }

  public void registerExtendingType(ModelElementType modelType) {
    extendingTypes.add(modelType);
  }

  ModelElementInstance createModelElementInstance(ModelTypeInstanceContext instanceContext) {
    return instanceProvider.newInstance(instanceContext);
  }

  public final List<Attribute<?>> getAttributes() {
    return attributes;
  }

  final void setAttributes(List<Attribute<?>> attributes) {
    this.attributes = attributes;
  }

  public String getTypeName() {
    return typeName;
  }

  public Class<? extends ModelElementInstance> getInstanceType() {
    return instanceType;
  }

  public void setTypeNamespace(String typeNamespace) {
    this.typeNamespace = typeNamespace;
  }

  public String getTypeNamespace() {
    return typeNamespace;
  }

  public void setBaseType(ModelElementTypeImpl baseType) {
    if (this.baseType == null) {
      this.baseType = baseType;
    }
    else {
      throw new ModelException("Type can not have multiple base types. " + this.getClass() + " already extends type " + this.baseType.getClass()
          + " and can not also extend type " + baseType.getClass());
    }
  }

  public void setInstanceProvider(ModelElementTypeBuilder.ModelTypeInstanceProvider<?> instanceProvider) {
    this.instanceProvider = instanceProvider;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setAbstract() {
    this.isAbstract = true;
  }

  public Collection<ModelElementType> getExtendingTypes() {
    return Collections.unmodifiableCollection(extendingTypes);
  }

  /**
   * Resolve all types recursively which are extending this type
   *
   * @param allExtendingTypes set of calculated extending types
   */
  public void resolveExtendingTypes(Set<ModelElementType> allExtendingTypes) {
    for(ModelElementType modelElementType : extendingTypes) {
      ModelElementTypeImpl modelElementTypeImpl = (ModelElementTypeImpl) modelElementType;
      if (!allExtendingTypes.contains(modelElementTypeImpl)) {
        allExtendingTypes.add(modelElementType);
        modelElementTypeImpl.resolveExtendingTypes(allExtendingTypes);
      }
    }
  }

  /**
   * Resolve all types which are base types of this type
   *
   * @param baseTypes list of calculated base types
   */
  public void resolveBaseTypes(List<ModelElementType> baseTypes) {
    if (baseType != null) {
      baseTypes.add(baseType);
      baseType.resolveBaseTypes(baseTypes);
    }
  }


  public ModelElementType getBaseType() {
    return baseType;
  }

  public Model getModel() {
    return model;
  }

  public List<ModelElementType> getChildElementTypes() {
    List<ModelElementType> allChildElementTypes = new ArrayList<ModelElementType>();
    if (baseType != null) {
      allChildElementTypes.addAll(baseType.getChildElementTypes());
    }
    allChildElementTypes.addAll(childElementTypes);
    return allChildElementTypes;
  }

  public Collection<ModelElementInstance> getInstances(ModelInstance modelInstance) {
    ModelInstanceImpl modelInstanceImpl = (ModelInstanceImpl) modelInstance;
    Document document = modelInstanceImpl.getDocument();
    List<Element> elements = DomUtil.findElementByNameNs(document, typeName, typeNamespace);
    List<ModelElementInstance> resultList = new ArrayList<ModelElementInstance>();
    for (Element element : elements) {
      resultList.add(ModelUtil.getModelElement(element, modelInstanceImpl));
    }
    return resultList;
  }

  /**
   * Test if a element type is a base type of this type. So this type extends the given element type.
   *
   * @param elementType the element type to test
   * @return true if {@code elementType} is a base type of this type, else otherwise
   */
  public boolean isBaseTypeOf(ModelElementType elementType) {
    if (this.equals(elementType)) {
      return true;
    }
    else {
      Collection<ModelElementType> baseTypes = ModelUtil.calculateAllBaseTypes(elementType);
      return baseTypes.contains(this);
    }
  }

  /**
   * Returns a list of all attributes, including the attributes of all base types.
   *
   * @return the list of all attributes
   */
  public Collection<Attribute<?>> getAllAttributes() {
    List<Attribute<?>> allAttributes = new ArrayList<Attribute<?>>();
    allAttributes.addAll(getAttributes());
    Collection<ModelElementType> baseTypes = ModelUtil.calculateAllBaseTypes(this);
    for (ModelElementType baseType : baseTypes) {
      allAttributes.addAll(baseType.getAttributes());
    }
    return allAttributes;
  }

  /**
   * Return the attribute for the attribute name
   *
   * @param attributeName the name of the attribute
   * @return the attribute or null if it not exists
   */
  public Attribute<?> getAttribute(String attributeName) {
    for (Attribute<?> attribute : getAllAttributes()) {
      if (attribute.getAttributeName().equals(attributeName)) {
        return attribute;
      }
    }
    return null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((model == null) ? 0 : model.hashCode());
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
    result = prime * result + ((typeNamespace == null) ? 0 : typeNamespace.hashCode());
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
    ModelElementTypeImpl other = (ModelElementTypeImpl) obj;
    if (model == null) {
      if (other.model != null)
        return false;
    } else if (!model.equals(other.model))
      return false;
    if (typeName == null) {
      if (other.typeName != null)
        return false;
    } else if (!typeName.equals(other.typeName))
      return false;
    if (typeNamespace == null) {
      if (other.typeNamespace != null)
        return false;
    } else if (!typeNamespace.equals(other.typeNamespace))
      return false;
    return true;
  }

}
