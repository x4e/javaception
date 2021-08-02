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

# Contributing

Javaception has an official git repository hosted at `https://git.sr.ht/~x4e/javaception` and `git@git.sr.ht:~x4e/javaception`.

You can edit files in the repository and test them by running `mvn verify` (make sure you have maven and a jdk installed).

Once you are happy with your changes you can submit them by following the guide here: https://git-send-email.io/.

# Licensing

Javaception: A Java Virtual Machine implementation written in Java.
Copyright (C) 2021  x4e

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
