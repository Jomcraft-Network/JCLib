[![Build Status](https://gitlab.com/jomcraft-sources/JCLib/badges/master/pipeline.svg)](https://gitlab.com/jomcraft-sources/JCLib)

### JCLib

---

JCLib is an opensource library used by the Jomcraft Network development team to allow for easy access to general utility methods regarding mostly database interaction

##### License

This product is licensed under the **GNU Lesser General Public License v2.1** license. We include the [MariaDB Connector/J](https://mariadb.com/kb/en/about-mariadb-connector-j/) in our non-maven builds.

##### Usage

Simply add this to your build.gradle or your dependencies block:

```md
dependencies {
    compile 'org.mariadb.jdbc:mariadb-java-client:2.6.0'
    deobfCompile 'net.jomcraft.jclib:JCLib-1.15.2:1.0.5'
}
```