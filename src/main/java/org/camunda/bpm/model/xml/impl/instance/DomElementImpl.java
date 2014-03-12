/* Licensed under the Apache License, Version 2.0 (the "License");
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

package org.camunda.bpm.model.xml.impl.instance;

import org.camunda.bpm.model.xml.ModelException;
import org.camunda.bpm.model.xml.impl.ModelInstanceImpl;
import org.camunda.bpm.model.xml.impl.util.DomUtil;
import org.camunda.bpm.model.xml.impl.util.XmlQName;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.w3c.dom.*;

import java.util.List;

import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

/**
 * @author Sebastian Menski
 */
public class DomElementImpl implements DomElement {

  private static final String MODEL_ELEMENT_KEY = "camunda.modelElementRef";

  private final Element element;

  public DomElementImpl(Element element) {
    this.element = element;
  }

  protected Element getElement() {
    return element;
  }

  public String getNamespaceURI() {
    return element.getNamespaceURI();
  }

  public String getLocalName() {
    return element.getLocalName();
  }

  public String getPrefix() {
    return element.getPrefix();
  }

  public DomDocument getDocument() {
    Document ownerDocument = element.getOwnerDocument();
    if (ownerDocument != null) {
      return new DomDocumentImpl(ownerDocument);
    }
    else {
      return null;
    }
  }

  public DomElement getRootElement() {
    DomDocument document = getDocument();
    if (document != null) {
      return document.getRootElement();
    }
    else {
      return null;
    }
  }

  public DomElement getParentElement() {
    Node parentNode = element.getParentNode();
    if (parentNode != null && parentNode instanceof Element) {
      return new DomElementImpl((Element) parentNode);
    }
    else {
      return null;
    }
  }

  public List<DomElement> getChildElements() {
    NodeList childNodes = element.getChildNodes();
    return DomUtil.filterNodeListForElements(childNodes);
  }

  public List<DomElement> getChildElementsByNameNs(String namespaceUri, String elementName) {
    NodeList childNodes = element.getChildNodes();
    return DomUtil.filterNodeListByName(childNodes, namespaceUri, elementName);
  }

  public List<DomElement> getChildElementsByType(ModelInstanceImpl modelInstance, Class<? extends ModelElementInstance> elementType) {
    NodeList childNodes = element.getChildNodes();
    return DomUtil.filterNodeListByType(childNodes, modelInstance, elementType);
  }

  public void replaceChild(DomElement newChildDomElement, DomElement existingChildDomElement) {
    Element newElement = ((DomElementImpl) newChildDomElement).getElement();
    Element existingElement = ((DomElementImpl) existingChildDomElement).getElement();
    try {
      element.replaceChild(newElement, existingElement);
    }
    catch (DOMException e) {
      throw new ModelException("Unable to replace child <" + existingElement + "> of element <" + element + "> with element <" + newElement + ">", e);
    }
  }

  public boolean removeChild(DomElement childDomElement) {
    Element childElement = ((DomElementImpl) childDomElement).getElement();
    try {
      element.removeChild(childElement);
      return true;
    }
    catch (DOMException e) {
      return false;
    }
  }

  public void appendChild(DomElement childDomElement) {
    Element childElement = ((DomElementImpl) childDomElement).getElement();
    element.appendChild(childElement);
  }

  public void insertChildElementAfter(DomElement elementToInsert, DomElement insertAfter) {
    Element newElement = ((DomElementImpl) elementToInsert).getElement();
    // find node to insert before
    Node insertBeforeNode;
    if (insertAfter == null) {
      insertBeforeNode = element.getFirstChild();
    }
    else {
      insertBeforeNode = ((DomElementImpl) insertAfter).getElement().getNextSibling();
    }

    // insert before node or append if no node was found
    if (insertBeforeNode != null) {
      element.insertBefore(newElement, insertBeforeNode);
    }
    else {
      element.appendChild(newElement);
    }
  }

  public boolean hasAttribute(String localName) {
    return hasAttribute(null, localName);
  }

  public boolean hasAttribute(String namespaceUri, String localName) {
    return element.hasAttributeNS(namespaceUri, localName);
  }

  public String getAttribute(String attributeName) {
    return getAttribute(null, attributeName);
  }


  public String getAttribute(String namespaceUri, String localName) {
    XmlQName xmlQName = new XmlQName(this, namespaceUri, localName);
    String value;
    if (xmlQName.hasLocalNamespace()) {
      value = element.getAttributeNS(null, xmlQName.getLocalName());
    }
    else {
      value = element.getAttributeNS(xmlQName.getNamespaceUri(), xmlQName.getLocalName());
    }
    if (value.isEmpty()) {
      return null;
    }
    else {
      return value;
    }
  }

  public void setAttribute(String localName, String value) {
    setAttribute(null, localName, value);
  }

  public void setAttribute(String namespaceUri, String localName, String value) {
    setAttribute(namespaceUri, localName, value, false);
  }

  private void setAttribute(String namespaceUri, String localName, String value, boolean isIdAttribute) {
    XmlQName xmlQName = new XmlQName(this, namespaceUri, localName);
    if (xmlQName.hasLocalNamespace()) {
      element.setAttributeNS(null, xmlQName.getLocalName(), value);
      if (isIdAttribute) {
        element.setIdAttributeNS(null, xmlQName.getLocalName(), true);
      }
    }
    else {
      element.setAttributeNS(xmlQName.getNamespaceUri(), xmlQName.getPrefixedName(), value);
      if (isIdAttribute) {
        element.setIdAttributeNS(xmlQName.getNamespaceUri(), xmlQName.getLocalName(), true);
      }
    }
  }

  public void setIdAttribute(String localName, String value) {
    setIdAttribute(getNamespaceURI(), localName, value);
  }

  public void setIdAttribute(String namespaceUri, String localName, String value) {
    setAttribute(namespaceUri, localName, value, true);
  }

  public void removeAttribute(String localName) {
    removeAttribute(getNamespaceURI(), localName);
  }

  public void removeAttribute(String namespaceUri, String localName) {
    XmlQName xmlQName = new XmlQName(this, namespaceUri, localName);
    if (xmlQName.hasLocalNamespace()) {
      element.removeAttributeNS(null, xmlQName.getLocalName());
    }
    else {
      element.removeAttributeNS(xmlQName.getNamespaceUri(), xmlQName.getLocalName());
    }
  }

  public String getTextContent() {
    return element.getTextContent();
  }

  public void setTextContent(String textContent) {
    element.setTextContent(textContent);
  }

  public ModelElementInstance getModelElementInstance() {
    return (ModelElementInstance) element.getUserData(MODEL_ELEMENT_KEY);
  }

  public void setModelElementInstance(ModelElementInstance modelElementInstance) {
    element.setUserData(MODEL_ELEMENT_KEY, modelElementInstance, null);
  }

  public String registerNamespace(String namespaceUri) {
    String lookupPrefix = lookupPrefix(namespaceUri);
    if (lookupPrefix == null) {
      // check if a prefix is known
      String prefix = XmlQName.KNOWN_PREFIXES.get(namespaceUri);
      // check if prefix is not already used
      if (prefix != null && getRootElement() != null &&
        getRootElement().hasAttribute(XMLNS_ATTRIBUTE_NS_URI, prefix)) {
        prefix = null;
      }
      if (prefix == null) {
        // generate prefix
        prefix = ((DomDocumentImpl) getDocument()).getUnusedGenericNsPrefix();
      }
      registerNamespace(prefix, namespaceUri);
      return prefix;
    }
    else {
      return lookupPrefix;
    }
  }

  public void registerNamespace(String prefix, String namespaceUri) {
    element.setAttributeNS(XMLNS_ATTRIBUTE_NS_URI, XMLNS_ATTRIBUTE + ":" + prefix, namespaceUri);
  }

  public String lookupPrefix(String namespaceUri) {
    return element.lookupPrefix(namespaceUri);
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DomElementImpl that = (DomElementImpl) o;
    return element.equals(that.element);
  }

  public int hashCode() {
    return element.hashCode();
  }

}
