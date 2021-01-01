package edu.pku.code2graph.gen.jdt.model;

import edu.pku.code2graph.model.Type;

import static edu.pku.code2graph.model.TypeSet.type;

public class NodeType {
  // OperationNode
  public static final Type ASSIGNMENT_OPERATOR = type("assign");
  public static final Type INFIX_EXPRESSION_OPERATOR = type("infix");
  public static final Type PREFIX_EXPRESSION_OPERATOR = type("prefix");
  public static final Type POSTFIX_EXPRESSION_OPERATOR = type("posfix");

  public static final Type METHOD_INVOCATION = type("method_invocation");
  public static final Type TYPE_INSTANTIATION = type("type_instantiation");

  // DeclarationNode
  public static final Type CLASS_DECLARATION = type("class_declaration");
  public static final Type INTERFACE_DECLARATION = type("interface_declaration");
  public static final Type ENUM_DECLARATION = type("enum_declaration");
  public static final Type FIELD_DECLARATION = type("field_declaration");
  public static final Type METHOD_DECLARATION = type("method_declaration");
  public static final Type VAR_DECLARATION = type("variable_declaration");

  // BlockNode
  public static final Type BLOCK = type("block");

  // ControlNode

}
