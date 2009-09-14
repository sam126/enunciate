package org.codehaus.enunciate.modules.objc;

import com.sun.mirror.declaration.PackageDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.codehaus.enunciate.contract.jaxb.TypeDefinition;

import java.beans.Introspector;
import java.util.List;
import java.util.Map;

/**
 * Gets a C-style, unambiguous name for a given type definition.
 *
 * @author Ryan Heaton
 */
public class NameForTypeDefinitionMethod implements TemplateMethodModelEx {

  private final String pattern;
  private final String packageIdentifierPattern;
  private final String projectLabel;
  private final Map<String, String> namespaces2ids;

  public NameForTypeDefinitionMethod(String pattern, String packageIdentifierPattern, String projectLabel, Map<String, String> namespaces2ids) {
    this.pattern = pattern;
    this.packageIdentifierPattern = packageIdentifierPattern;
    this.projectLabel = ObjCDeploymentModule.scrubIdentifier(projectLabel);
    this.namespaces2ids = namespaces2ids;
  }

  public Object exec(List list) throws TemplateModelException {
    if (list.size() < 1) {
      throw new TemplateModelException("The nameForTypeDefinition method must have a type definition as a parameter.");
    }

    TemplateModel from = (TemplateModel) list.get(0);
    Object unwrapped = BeansWrapper.getDefaultInstance().unwrap(from);
    if (!(unwrapped instanceof TypeDefinition)) {
      throw new TemplateModelException("The nameForTypeDefinition method must have a type definition as a parameter.");
    }

    return calculateName((TypeDefinition) unwrapped);
  }

  public Object calculateName(TypeDefinition typeDefinition) {
    String name = ObjCDeploymentModule.scrubIdentifier(typeDefinition.getName());
    String simpleName = ObjCDeploymentModule.scrubIdentifier(typeDefinition.getSimpleName());
    String clientName = ObjCDeploymentModule.scrubIdentifier(typeDefinition.getClientSimpleName());
    String simpleNameDecap = ObjCDeploymentModule.scrubIdentifier(Introspector.decapitalize(simpleName));
    String clientNameDecap = ObjCDeploymentModule.scrubIdentifier(Introspector.decapitalize(clientName));
    if (name == null) {
      name = "anonymous_" + clientNameDecap;
    }
    PackageDeclaration pckg = ((TypeDeclaration) typeDefinition).getPackage();
    String packageName = pckg.getQualifiedName();
    String packageIdentifier;
    if (this.packageIdentifierPattern != null) {
      String[] subpackages = packageName.split("\\.", 9);
      packageIdentifier = ObjCDeploymentModule.scrubIdentifier(String.format(this.packageIdentifierPattern, subpackages));
    }
    else {
      packageIdentifier = ObjCDeploymentModule.scrubIdentifier(pckg != null ? packageName : "");
    }
    String nsid = ObjCDeploymentModule.scrubIdentifier(namespaces2ids.get(typeDefinition.getNamespace()));
    return String.format(this.pattern, this.projectLabel, nsid, name, clientName, clientNameDecap, simpleName, simpleNameDecap, packageIdentifier);
  }
}