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
package org.camunda.bpm.model.xml.impl.type.reference;

import org.camunda.bpm.model.xml.impl.type.child.ChildElementImpl;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class UriElementReferenceBuilderImpl<Target extends ModelElementInstance, Source extends ModelElementInstance> extends ElementReferenceBuilderImpl<Target, Source> {

  public UriElementReferenceBuilderImpl(Class<Source> childElementType, Class<Target> referenceTargetClass, ChildElementImpl<Source> child) {
    super(childElementType, referenceTargetClass, child);
    this.elementReferenceCollectionImpl = new UriElementReferenceImpl<Target, Source>(child);
  }

}
