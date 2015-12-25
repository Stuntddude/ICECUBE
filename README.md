ICECUBE
=======
A continuation of my compo entry to Ludum Dare 34


Building Directions (Eclipse Java 8)
------------------------------------

1. **Clone** into an active workspace
2. Create a **New Java Project** named `ICECUBE`
3. Configure project to use **jdk1.8**
  1. *Project Settings > Java Compiler > Compiler Compliance Level*: `1.8`
  2. *Project Settings > Java Build Path > Libraries* Remove any existing libraries
  3. *Project Settings > Java Build Path > Libraries > Add Library > JRE System* choose jdk1.8
4. **Add required libraries** to the class path:
  1. [`core.jar`](http://stuntddude.github.io/ICECUBE/3rd-party/processing_core-3.0.1.jar) for **Processing 3.0.1**
5. **Run/Debug** with Main class: `net.kopeph.icecube.ICECUBE`
6. **Export** as a *Runnable Jar*.
