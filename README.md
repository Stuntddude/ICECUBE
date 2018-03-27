ICECUBE
=======
A continuation of my compo entry to Ludum Dare 34

http://ludumdare.com/compo/ludum-dare-34/?action=preview&uid=31623


Building Directions (Eclipse Java 8)
------------------------------------

1. **Clone** into an active workspace
2. Create a **New Java Project** named `ICECUBE`
3. Configure project to use **jdk1.8**
  1. *Project Settings > Java Compiler > Compiler Compliance Level*: `1.8`
  2. *Project Settings > Java Build Path > Libraries* Remove any existing libraries
  3. *Project Settings > Java Build Path > Libraries > Add Library > JRE System* choose jdk1.8
4. **Add all [Processing core libraries](https://stuntddude.github.io/ICECUBE/3rd-party/processing-3.0.1-libs.zip)** to the class path
5. Configure `jogl-all.jar` and `gluegen-rt.jar`
  1. *Project Settings > Java Build Path > Libraries > `jogl-all.jar` > Native library location* point to the folder containing the native jars
  2. *Project Settings > Java Build Path > Libraries > `gluegen-rt.jar` > Native library location* point to the folder containing the native jars
6. **Run/Debug** with Main class: `net.kopeph.icecube.ICECUBE`
7. **Export** as a *Runnable Jar*.
