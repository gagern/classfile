package net.von_gagern.martin.classfile;

import java.util.List;

public interface AttributeOwner {

    AttributeOwner getParent();

    ClassFile getClassFile();

    List<Attribute> getAttributes();

}
