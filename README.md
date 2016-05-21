# OptionsResolver

**Version 1.0**

*Hey—why not just copy* `com.github.dittmarsteiner.cli.OptionsResolver` *into your project?* ;-)

##Description
A command line helper for use in e.g. `public static void main(String[] args)`. 
Thus it throws `RuntimeExceptions` to fail early.

An `OptionsResolver` tries to read options in the following order from

1. System environment `System.getenv(...)`
2. VM argument `System.getProperty(...)`
3. A `String[]` passed from e.g. `public static void main(String[] args)`

##Examples


###Using an option with an `Integer` argument 

*(Using a `String` is quite the same)*

```lang:java
//  new String[]{"-p"} could be any String[] like main(args)
Integer port = OptionsResolver.resolve("PORT", 'p', new String[]{"-p"}, 8080);
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
//  new String[]{"-t"} could be any String[] like main(args)
Boolean test = OptionsResolver.resolve("TEST", 't', new String[]{"-t"}, false);
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
 
**For more code examples, please see `com.github.dittmarsteiner.cli.OptionsResolverTest`**

    ------------------------------------------------------------------------------
    ISC License http://opensource.org/licenses/isc-license.txt
    ------------------------------------------------------------------------------
    Copyright (c) 2016, Dittmar Steiner <dittmar.steiner@gmail.com>
    
    Permission to use, copy, modify, and/or distribute this software for any
    purpose with or without fee is hereby granted, provided that the above
    copyright notice and this permission notice appear in all copies.
    
    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
