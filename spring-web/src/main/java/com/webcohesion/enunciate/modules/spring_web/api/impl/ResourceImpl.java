/**
 * Copyright © 2006-2016 Web Cohesion (info@webcohesion.com)
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
package com.webcohesion.enunciate.modules.spring_web.api.impl;

import com.webcohesion.enunciate.api.Styles;
import com.webcohesion.enunciate.api.resources.Method;
import com.webcohesion.enunciate.api.resources.Resource;
import com.webcohesion.enunciate.api.resources.ResourceGroup;
import com.webcohesion.enunciate.javac.decorations.element.ElementUtils;
import com.webcohesion.enunciate.javac.javadoc.JavaDoc;
import com.webcohesion.enunciate.modules.spring_web.model.RequestMapping;

import javax.lang.model.element.AnnotationMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ryan Heaton
 */
public class ResourceImpl implements Resource {

  final RequestMapping requestMapping;
  final ResourceGroup group;

  public ResourceImpl(RequestMapping requestMapping, ResourceGroup group) {
    this.requestMapping = requestMapping;
    this.group = group;
  }

  @Override
  public String getPath() {
    return requestMapping.getFullpath();
  }

  @Override
  public String getRelativePath() {
    String relativePath = getPath();
    while (relativePath.startsWith("/")) {
      relativePath = relativePath.substring(1);
    }
    return relativePath;
  }

  @Override
  public String getSlug() {
    return group.getSlug() + "_" + this.requestMapping.getSimpleName();
  }

  @Override
  public String getDeprecated() {
    return ElementUtils.findDeprecationMessage(this.requestMapping);
  }

  @Override
  public String getSince() {
    JavaDoc.JavaDocTagList tags = this.requestMapping.getJavaDoc().get("since");
    return tags == null ? null : tags.toString();
  }

  @Override
  public String getVersion() {
    JavaDoc.JavaDocTagList tags = this.requestMapping.getJavaDoc().get("version");
    return tags == null ? null : tags.toString();
  }

  @Override
  public List<? extends Method> getMethods() {
    Set<String> httpMethods = this.requestMapping.getHttpMethods();
    List<Method> methodList = new ArrayList<Method>(httpMethods.size());
    for (String httpMethod : httpMethods) {
      methodList.add(new MethodImpl(httpMethod, this.requestMapping, this.group));
    }
    return methodList;
  }

  @Override
  public Map<String, AnnotationMirror> getAnnotations() {
    return this.requestMapping.getAnnotations();
  }

  @Override
  public JavaDoc getJavaDoc() {
    return this.requestMapping.getJavaDoc();
  }

  @Override
  public Set<String> getStyles() {
    return Styles.gatherStyles(this.requestMapping, this.requestMapping.getContext().getContext().getConfiguration().getAnnotationStyles());
  }
}
