# Javaception

A Java Virtual Machine - running on a Java Virtual Machine - running on a (jk).

# Goals
* JVMS compliant Java Virtual Machine
  - Still unclear which JVMS version to follow.
    We will probably aim for JVMS 8 compliance due to huge complexity increases since.
    Some more modern but simple features can be added (e.g. frozen arrays).
* Somewhat fast
  - We want to be able to virtualise select methods with decent speed however virtualising entire applications is not within our goals - it should be possible but not necessarily fast or practical.
* Java API for applications to virtualise and debug Java code incl hooks, events...
* Multiple VMs in a single process
  - OpenJDK only supports one VM per process.
    For javaception to be effective as an API for virtualising select Java code it should be possible to create multiple VMs in one process, allowing seperate code to use javaception at the same time, or the same code using it on multiple threads.
* Resistant to non verifiable bytecode (to a reasonable extent)

# Non Goals
* Match OpenJDK specific behaviour
* Class file verification
* Alternative specs like JDK, JDWP, JVMTI etc
