# Contraction Hierachy Visualiser

## Prerequisites

- The project was made using gradle 8.10.2 (though other versions will almost vertainly work as well)
- The currect targeted java version is version 21. If this causes issues look for the "JavaLanguageVersion.of(21)" in [build.gradle](./app/build.gradle) and change it to your java version (or update java).

## Usage

```bash
gradle jar
```

```bash
java -jar app/build/libs/app.jar <width> <height> <path>
```

where \<width\> is the width of the generated image (in pixels), \<height\> is the height of the generated image (in pixels), the image generated will be stored in a file pointed to by \<path\> (e.g ./out.png). The program will then read a cotraction hierachy with the format specified by the assignment from stdin. If you have saved your contraction hierachy to a file, you can do the following:

```bash
java -jar app/build/libs/app.jar <width> <height> <path> < <file>
```

where \<file\> is the path to the file containing your contraction hierachy.

## Example Image

![](./example.png)
