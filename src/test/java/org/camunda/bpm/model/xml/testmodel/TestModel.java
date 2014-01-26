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
package org.camunda.bpm.model.xml.testmodel;

import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.testmodel.instance.*;

/**
 * @author Daniel Meyer
 *
 */
public final class TestModel {

  private static Model model;

  public static Model getTestModel() {
    if(model == null) {
      ModelBuilder modelBuilder = ModelBuilder.createInstance(TestModelConstants.MODEL_NAME);

      Animals.registerType(modelBuilder);
      Animal.registerType(modelBuilder);
      Bird.registerType(modelBuilder);
      ChildRelationshipDefinition.registerType(modelBuilder);
      FlightPartnerRef.registerType(modelBuilder);
      FlyingAnimal.registerType(modelBuilder);
      SpouseRef.registerType(modelBuilder);
      FriendRelationshipDefinition.registerType(modelBuilder);
      RelationshipDefinition.registerType(modelBuilder);
      RelationshipDefinitionRef.registerType(modelBuilder);
      Egg.registerType(modelBuilder);
      FlightInstructor.registerType(modelBuilder);

      model = modelBuilder.build();
    }

    return model;
  }

}
