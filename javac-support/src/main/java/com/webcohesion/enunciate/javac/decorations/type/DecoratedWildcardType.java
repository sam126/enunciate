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
package com.webcohesion.enunciate.javac.decorations.type;

import com.webcohesion.enunciate.javac.decorations.TypeMirrorDecorator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;

/**
 * @author Ryan Heaton
 */
public class DecoratedWildcardType extends DecoratedTypeMirror<WildcardType> implements WildcardType {

  public DecoratedWildcardType(WildcardType delegate, ProcessingEnvironment env) {
    super(delegate, env);
  }

  @Override
  public TypeMirror getExtendsBound() {
    return TypeMirrorDecorator.decorate(this.delegate.getExtendsBound(), env);
  }

  @Override
  public TypeMirror getSuperBound() {
    return TypeMirrorDecorator.decorate(this.delegate.getSuperBound(), env);
  }

  public boolean isWildcard() {
    return true;
  }

  @Override
  public <R, P> R accept(TypeVisitor<R, P> v, P p) {
    return v.visitWildcard(this, p);
  }
}
