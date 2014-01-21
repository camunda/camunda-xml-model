/*
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

import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.impl.instance.ModelElementInstanceImpl;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.testmodel.TestModelConstants;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder;

import static org.camunda.bpm.model.xml.testmodel.TestModelConstants.*;

/**
 * @author Sebastian Menski
 */
public class SpouseRef extends ModelElementInstanceImpl {

  public static void registerType(ModelBuilder modelBuilder) {
    ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(SpouseRef.class, ELEMENT_NAME_SPOUSE_REF)
      .namespaceUri(MODEL_NAMESPACE)
      .instanceProvider(new ModelElementTypeBuilder.ModelTypeInstanceProvider<SpouseRef>() {
        public SpouseRef newInstance(ModelTypeInstanceContext instanceContext) {
          return new SpouseRef(instanceContext);
        }
      });

    typeBuilder.build();
  }

  public SpouseRef(final ModelTypeInstanceContext instanceContext) {
    super(instanceContext);
  }
}
