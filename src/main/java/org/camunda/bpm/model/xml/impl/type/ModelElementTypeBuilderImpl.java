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
import java.util.List;

import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.ModelException;
import org.camunda.bpm.model.xml.impl.ModelBuildOperation;
import org.camunda.bpm.model.xml.impl.ModelImpl;
import org.camunda.bpm.model.xml.impl.type.attribute.BooleanAttributeBuilder;
import org.camunda.bpm.model.xml.impl.type.attribute.EnumAttributeBuilder;
import org.camunda.bpm.model.xml.impl.type.attribute.IntegerAttributeBuilder;
import org.camunda.bpm.model.xml.impl.type.attribute.StringAttributeBuilderImpl;
import org.camunda.bpm.model.xml.impl.type.child.SequenceBuilderImpl;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.attribute.AttributeBuilder;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder;
import org.camunda.bpm.model.xml.type.child.SequenceBuilder;
import org.camunda.bpm.model.xml.type.attribute.StringAttributeBuilder;

/**
 * @author Daniel Meyer
 *
 */
public class ModelElementTypeBuilderImpl implements ModelElementTypeBuilder, ModelBuildOperation {

  private final ModelElementTypeImpl modelType;
  private final ModelImpl model;
  private final Class<? extends ModelElementInstance> instanceType;

  private final List<ModelBuildOperation> modelBuildOperations = new ArrayList<ModelBuildOperation>();
  private Class<? extends ModelElementInstance> extendedType;

  public ModelElementTypeBuilderImpl(Class<? extends ModelElementInstance> instanceType, String name, ModelImpl model) {
    this.instanceType = instanceType;
    this.model = model;
    modelType = new ModelElementTypeImpl(model, name, instanceType);
  }

  public ModelElementTypeBuilder extendsType(Class<? extends ModelElementInstance> extendedType) {
    this.extendedType = extendedType;
    return this;
  }

  public <T extends ModelElementInstance> ModelElementTypeBuilder instanceProvider(ModelTypeInstanceProvider<T> instanceProvider) {
    modelType.setInstanceProvider(instanceProvider);
    return this;
  }

  public ModelElementTypeBuilder namespaceUri(String namespaceUri) {
    modelType.setTypeNamespace(namespaceUri);
    return this;
  }

  public AttributeBuilder<Boolean> booleanAttribute(String attributeName) {
    BooleanAttributeBuilder builder = new BooleanAttributeBuilder(attributeName, modelType);
    modelBuildOperations.add(builder);
    return builder;
  }

  public StringAttributeBuilder stringAttribute(String attributeName) {
    StringAttributeBuilderImpl builder = new StringAttributeBuilderImpl(attributeName, modelType);
    modelBuildOperations.add(builder);
    return builder;
  }

  @Override
  public AttributeBuilder<Integer> integerAttribute(String attributeName) {
    IntegerAttributeBuilder builder = new IntegerAttributeBuilder(attributeName, modelType);
    modelBuildOperations.add(builder);
    return builder;
  }

  public <V extends Enum<V>> AttributeBuilder<V> enumAttribute(String attributeName, Class<V> enumType) {
    EnumAttributeBuilder<V> builder = new EnumAttributeBuilder<V>(attributeName, modelType, enumType);
    modelBuildOperations.add(builder);
    return builder;
  }

  public ModelElementType build() {
    model.registerType(modelType, instanceType);
    return modelType;
  }

  public ModelElementTypeBuilder abstractType() {
    modelType.setAbstract();
    return this;
  }

  public SequenceBuilder sequence() {
    SequenceBuilderImpl builder = new SequenceBuilderImpl(modelType);
    modelBuildOperations.add(builder);
    return builder;
  }

  public void performModelBuild(Model model) {

    // build type hierarchy
    if(extendedType != null) {
      ModelElementTypeImpl extendedModelElementType = (ModelElementTypeImpl) model.getType(extendedType);
      if(extendedModelElementType == null) {
        throw new ModelException("Type "+modelType+" is defined to extend "+extendedType+" but no such type is defined.");

      } else {
        modelType.setBaseType(extendedModelElementType);
        extendedModelElementType.registerExtendingType(modelType);
      }
    }

    for (ModelBuildOperation operation : modelBuildOperations) {
      operation.performModelBuild(model);
    }
  }
}
