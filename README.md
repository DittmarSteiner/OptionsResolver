# CLI OptionsResolver

**Version 1.0**

*Hey—why not just copy* `com.github.dittmarsteiner.cli.OptionsResolver` *into your project?* ;-)

##Description
A command line helper for use in e.g. `public static void main(String[] args)`. 
Thus it throws `RuntimeExceptions` to fail early.  
Reporting or configurable help is not supported, which is not a goal of this tool class.  
It is your responsibility if an option is not found or wrongly formatting like `---option 123`.
This is the domain of the comprehensive [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/) or other libs.

An `OptionsResolver` tries to read options in the following order from

1. System environment `System.getenv(...)`
2. VM argument `System.getProperty(...)`
3. A `String[]` passed from e.g. `public static void main(String[] args)`

The first match wins otherwise the default value will be returned.

##Examples

###Using an option with an `Integer` argument 

*(Using a `String` is quite the same)*

```lang:java
// you could also pass args from main(String[] args) as 4th param ...
Integer port = OptionsResolver.resolve(8080, "PORT", 'p', "-p", "80");
```

*For more code examples, please see `OptionsResolverTest`*
 
####System environment

    $ export PORT=80
    $ java com.example.Main
 
####or VM argument

    $ java -DPORT=80 com.example.Main
 
####or program named argument

    $ java com.example.Main --port 80
 
####or simple program argument

    $ java com.example.Main -p 80
 
####also mixed simple arguments with a `boolean` option

    $ java com.example.Main -tp 80
 
###Using a `boolean` option flag

```lang:java
// you could also pass args from main(String[] args) as 4th param ...
Boolean test = OptionsResolver.resolve(false, "TEST", 't', "-t");
```

*For more code examples, please see `OptionsResolverTest`*

**Note:** Anything else but `true`  will result in `false` or the default if not found.
 
####System environment

    $ export TEST=true
    $ java com.example.Main
 
####or VM option

    $ java -DTEST=true com.example.Main
 
####or program named argument

    $ java com.example.Main --test
 
####or program argument (flag)

    $ java com.example.Main -t
 
####also mixed with another option with argument

    $ java com.example.Main -tp 80
 
**For more code examples, please see** `com.github.dittmarsteiner.cli.OptionsResolverTest`

[LICENSE](LICENSE)
