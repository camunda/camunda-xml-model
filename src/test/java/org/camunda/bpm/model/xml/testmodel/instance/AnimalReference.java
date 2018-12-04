/*
 * Copyright © 2014-2018 camunda services GmbH and various authors (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package org.camunda.bpm.model.xml.testmodel.instance;

import static org.camunda.bpm.model.xml.testmodel.TestModelConstants.*;

import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.impl.instance.ModelElementInstanceImpl;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.testmodel.TestModelConstants;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder.ModelTypeInstanceProvider;
import org.camunda.bpm.model.xml.type.attribute.Attribute;

public class AnimalReference extends ModelElementInstanceImpl {

  protected static Attribute<String> hrefAttribute;

  public AnimalReference(ModelTypeInstanceContext instanceContext) {
    super(instanceContext);
  }

  public String getHref() {
    return hrefAttribute.getValue(this);
  }

  public void setHref(String href) {
    hrefAttribute.setValue(this, href);
  }

  public static void registerType(ModelBuilder modelBuilder) {
    ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(AnimalReference.class, ELEMENT_NAME_ANIMAL_REFERENCE)
      .namespaceUri(MODEL_NAMESPACE)
      .instanceProvider(new ModelTypeInstanceProvider<AnimalReference>() {
        public AnimalReference newInstance(ModelTypeInstanceContext instanceContext) {
          return new AnimalReference(instanceContext);
        }
      });

    hrefAttribute = typeBuilder.stringAttribute("href")
      .required()
      .build();

    typeBuilder.build();
  }

}
