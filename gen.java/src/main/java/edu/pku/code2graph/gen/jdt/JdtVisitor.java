package edu.pku.code2graph.gen.jdt;

import edu.pku.code2graph.gen.jdt.model.EdgeType;
import edu.pku.code2graph.gen.jdt.model.NodeType;
import edu.pku.code2graph.model.DeclarationNode;
import edu.pku.code2graph.model.OperationNode;
import edu.pku.code2graph.model.Type;
import edu.pku.code2graph.util.GraphUtil;
import org.eclipse.jdt.core.dom.*;
import org.jgrapht.alg.util.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor to create nodes and collect info to create edges
 *
 * <p>1. visit, store and index all element nodes
 *
 * <p>2. create nodes
 *
 * <p>3. cache bindings for visited nodes
 *
 * <p>4. for all bindings, create or get decl node
 *
 * <p>5. create edges
 */
public class JdtVisitor extends AbstractJdtVisitor {

  @Override
  public void preVisit(ASTNode n) {}

  public boolean visit(TypeDeclaration td) {
    Type type = td.isInterface() ? NodeType.INTERFACE_DECLARATION : NodeType.CLASS_DECLARATION;
    ITypeBinding tdBinding = td.resolveBinding();
    String qname = tdBinding.getQualifiedName();
    DeclarationNode n =
        new DeclarationNode(
            GraphUtil.popNodeID(graph), type, td.toString(), td.getName().toString(), qname);
    graph.addVertex(n);
    defPool.put(qname, n);

    IVariableBinding[] fdBindings = tdBinding.getDeclaredFields();
    IMethodBinding[] mdBindings = tdBinding.getDeclaredMethods();
    for (IVariableBinding b : fdBindings) {
      usePool.add(Triple.of(n, EdgeType.CONTAIN, qname + ":" + b.getName()));
    }
    for (IMethodBinding b : mdBindings) {
      if (b.isDefaultConstructor()) {
        continue;
      }
      usePool.add(Triple.of(n, EdgeType.CONTAIN, getMethodQNameFromBinding(b)));
    }

    return true;
  }

  public boolean visit(FieldDeclaration fd) {
    return false;
  }

  public boolean visit(MethodDeclaration md) {
    // A binding represents a named entity in the Java language
    // for internal, should never be null
    IMethodBinding mdBinding = md.resolveBinding();
    String qname = getMethodQNameFromBinding(mdBinding);
    DeclarationNode n =
        new DeclarationNode(
            GraphUtil.popNodeID(graph),
            NodeType.METHOD_DECLARATION,
            md.toString(),
            md.getName().toString(),
            qname);
    graph.addVertex(n);
    defPool.put(qname, n);

    // return type
    ITypeBinding tpBinding = mdBinding.getReturnType();
    if (tpBinding != null) {
      usePool.add(Triple.of(n, EdgeType.METHOD_RETURN, tpBinding.getQualifiedName()));
    }

    // para types
    ITypeBinding[] paraBindings = mdBinding.getParameterTypes();
    if (paraBindings != null) {
      for (ITypeBinding p : paraBindings) {
        usePool.add(Triple.of(n, EdgeType.METHOD_PARAMETER, p.getQualifiedName()));
      }
    }

    return true;
  }

  public boolean visit(MethodInvocation mi) {
    // get caller
    // get qname of method invocation
    List<Expression> arguments = mi.arguments();

    IMethodBinding mdBinding = mi.resolveMethodBinding();
    // only internal invocation (or consider types, fields and local?)
    if (mdBinding != null) {
      OperationNode n =
          new OperationNode(GraphUtil.popNodeID(graph), NodeType.METHOD_INVOCATION, mi.toString());
      graph.addVertex(n);

      // called method
      usePool.add(Triple.of(n, EdgeType.METHOD_CALLEE, getMethodQNameFromBinding(mdBinding)));
    }

    return false;
  }

  private String getMethodQNameFromBinding(IMethodBinding binding) {
    ITypeBinding[] paraBindings = binding.getParameterTypes();
    List<String> paraTypes = new ArrayList<>();
    for (ITypeBinding b : paraBindings) {
      paraTypes.add(b.getQualifiedName());
    }

    String qname = binding.getName() + "(" + String.join(",", paraTypes).trim() + ")";
    if (binding != null) {
      qname = binding.getDeclaringClass().getQualifiedName() + ":" + qname;
    }
    return qname;
  }
}
