HistoDiff
=========

HistoDiff is a simple tool for comparing Java heap histograms produced by `jmap`. It's useful as a poor-man's memory leak detector in non-instrumented production environments.


Example
-------

Take two heap histogram snapshots from a running java process at different times:

```
jmap -histo:live <pid> > histo1
jmap -histo:live <pid> > histo2
```

Compare the histograms using HistoDiff:

```
java -jar histodiff.jar histo1 histo2
```

This outputs the difference in object counts and memory between the two snapshots:

```
  #instances       #bytes  class name
-------------------------------------
       +1671       +99896  [C
       +1669       +40056  java.lang.String
       +1298       +51920  java.lang.ref.Finalizer
       +1007       +32224  java.io.FileDescriptor
         ...          ...  ...
         -37         -888  java.util.ArrayList
        -183        -7320  java.util.WeakHashMap$Entry
```


Usage
-----

```
java [-DtrustAllHttpsCerts=true] -jar histodiff.jar file1 file2 [sortBy] [threshold]
```

 * `file`: path to a file or an URL
 * `sortBy`: 0 (default) to sort results by number of instances, 1 to sort by bytes
 * `threshold`: change in the corresponding dimension has to be above this (0 by default, which means types with no changes in instance counts are omitted)
 * `-DtrustAllHttpsCerts=true`: this option explicitly allows histodiff to perform "insecure" HTTPS connections and transfers


Get, Build, Install
-------------------

Get a pre-built executable jar here:

https://github.com/phraktle/histodiff/releases/download/v0.2/histodiff-0.2.jar

Or build from source:

```
git clone https://github.com/phraktle/histodiff.git
cd histodiff
gradlew jar
cp build/libs/histodiff-0.2.jar ~/bin/histodiff.jar
```


License
-------

MIT
