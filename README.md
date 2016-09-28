# VoodooDI ![Travis](https://travis-ci.org/sknitelius/VoodooDI.svg?branch=master)

VoodooDI is developed as part of an educational blog series, explaining the inner workings of DI frameworks.

* [Part 1: Understanding Dependency Injection - IoC](https://www.knitelius.com/2016/09/21/understanding-dependency-injection-part-1-ioc/) 

## Fork Me 
Fork and/or clone the project.

```cmd
$ git clone https://github.com/sknitelius/VoodooDI.git
```

## Usage as Dependency
Using VoodooDI is simple matter of including the dependency in your pom. 

```xml
  <dependency>
    <groupId>science.raketen.voodoo</groupId>
    <artifactId>VoodooDI</artifactId>
    <version>0.0.2</version>
  </dependency>
```

Starting Voodoo Container:
```java
  public static void main(String[] args) throws IOException {
    Voodoo container = Voodoo.initalize();
    MyApp myApp = container.instance(MyApp.class);
    ...
  }
```

## License

This software is provided under the  Apache 2.0 license, read the `LICENSE` file for details.