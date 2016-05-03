# Classfile

This is a library to read (and perhaps one day modify) Java class files,
i.e. bytecode for the Java Virtual Machine (JVM).

It can be used as a library,
but it also contains a collection of tools to be run from the command line.
There might be Ant tasks as well one day.

## Included tools

### Disassembler

There is one tool to disassemble a class file,
much like `javap -v -private` does.
The syntax is slightly different, but not by much.
This tool can be used to ensure that we correctly parsed the things we do parse,
e.g. by simply comparing the output to what `javap` prints.
Don’t rely on the output format to remain fixed.

Usage:

```sh
java net.von_gagern.martin.classfile.tools.Disasm CLASSFILES…
```

### Locale dependency checker

There is another tool to check for invocations of methods or constructors
which make implict use of the current character encoding or current locale.
Such methods may result in portability problems, so it is often better
to avoid their use altogether, and make the use of the default encoding
or locale explicit if it is indeed an intentional use.

Usage:

```sh
java net.von_gagern.martin.classfile.tools.CheckForDefaultCharset CLASSFILES…
```

## Questions and ansers

### How does this differ from ASM?

The [ASM project](http://asm.ow2.org/) is a great toolbox
for inspecting and manipulating JVM bytecode.
But I’m not perfectly comfortable with it for some applications.
In some respects it is too high-level for my taste.
It doesn’t allow convenient inspection of the constant pool,
which is sad since proper inspection of the constant pool
can often help in quickly deciding whether a given class
requires further processing or not.

But probably the main reson I didn’t use ASM for some of my work
was that I wanted to really understand the class file format,
and the best way to do that is by getting your hands dirty
and actually meddle with the internal representation.

### Will there be releases of this?

Perhaps one day.
Feel free to open an issue requesting a release
if you consider the code useful enough
and would like to work with it or build on it.
