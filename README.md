OptionsResolver
===============

###Hint
**Before you checkout the project, run `mvn install` and reference it in your Maven or Gradle project...**  
***→ just copy the one class into your project!***

#####**Version 1.0**

A command line helper for use in e.g. `public static void main(String[] args)` methods. 
Throws `RuntimeExceptions` to fail early.

Tries to read options in the following order from

1. System environment `System.getenv(...)`
2. VM argument `System.getProperty(...)`
3. A `String[] args` passed e.g. from `public static void main(String[] args)`

###Examples

####Setting an option with an integer argument (a string is quite the same):
 
#####System environment
    export PORT=80
    java com.example.Main
 
#####or VM argument
    java -DPORT=80 com.example.Main
 
#####or program named argument
    java com.example.Main --port 80
 
#####or program argument
    java com.example.Main -p 80
 
#####also mixed with a boolean option
    java com.example.Main -tp 80
 
####Setting a boolean option flag
 
#####System environment
    export TEST=true
    java com.example.Main
 
#####or VM option
    java -DTEST=true com.example.Main
 
#####or program named argument
    java com.example.Main --test
 
####or program argument (flag)
    java com.example.Main -t
 
#####also mixed with another option with argument
    java com.example.Main -tp 80
 
**For code examples see `com.github.dittmarsteiner.cli.OptionsResolverTest`**

    ------------------------------------------------------------------------------
    ISC License http://opensource.org/licenses/isc-license.txt
    ------------------------------------------------------------------------------
    Copyright (c) 2014, Dittmar Steiner <dittmar.steiner@googlemail.com>
    
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
