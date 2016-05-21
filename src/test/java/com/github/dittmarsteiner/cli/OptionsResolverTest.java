/*
 * ------------------------------------------------------------------------------
 * ISC License http://opensource.org/licenses/isc-license.txt
 * ------------------------------------------------------------------------------
 * Copyright (c) 2016, Dittmar Steiner <dittmar.steiner@gmail.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.github.dittmarsteiner.cli;

import static org.junit.Assert.*;
import org.junit.Test;
import com.github.dittmarsteiner.cli.OptionsResolver;

/**
 * @see OptionsResolver
 * @author <a href="mailto:dittmar.steiner@gmail.com">Dittmar Steiner</a>
 */
public class OptionsResolverTest {

	@Test
	public void testGenericResolver() {
	    
	    // flag/single char
        Object o = OptionsResolver.resolve(null, "ARG", 'a', new String[]{"-a", "1000.0"});
        assertNull(o); // defaultValue was null
        
        Integer port = OptionsResolver.resolve(8080, "PORT", 'p', new String[]{"-t"});
        assertNotNull(port);
        assertEquals(8080, port.intValue()); // no matches, so default value
        
        port = OptionsResolver.resolve(8080, null, ' ', new String[]{"-p", "80"});
        assertNotNull(port);
        assertEquals(8080, port.intValue()); // no matches, so default value
        
        port = OptionsResolver.resolve(8080, null, 'p', new String[]{"-p", "80"});
        assertNotNull(port);
        assertEquals(80, port.intValue()); // -p matched
        
        port = OptionsResolver.resolve(8080, "PORT", ' ', new String[]{"-p", "80"});
        assertNotNull(port);
        assertEquals(8080, port.intValue()); // no matches, so default value
        
        port = OptionsResolver.resolve(8080, "PORT", ' ', new String[]{"--port", "80"});
        assertNotNull(port);
        assertEquals(80, port.intValue()); // --port matched
        
        String s = OptionsResolver.resolve("error", "ARG", 'a', new String[]{"-a", "hello"});
        assertNotNull(s);
        assertEquals("hello", s); // String -a matched
        
        Long l = OptionsResolver.resolve(0L, "ARG", 'a', new String[]{"-a", "1000"});
        assertNotNull(l);
        assertEquals(1000L, l.longValue()); // Long -a matched
        
        int i = OptionsResolver.resolve(0, "ARG", 'a', new String[]{"-a", "1000"});
        assertNotNull(i);
        assertEquals(1000, i); // int -a matched
        
        Double d = OptionsResolver.resolve(0d, "ARG", 'a', new String[]{"-a", "1.1"});
        assertNotNull(d);
        assertEquals(1.1d, d.doubleValue(), 0d); // double -a matched
        
        float f = OptionsResolver.resolve(0f, "ARG", 'a', new String[]{"-a", "1.1"});
        assertNotNull(f);
        assertEquals(1.1f, f, 0f); // float -a matched
        
        // Boolean
        Boolean b = OptionsResolver.resolve(false, "ARG", 'a', new String[]{"-a"});
        assertNotNull(b);
        assertTrue(b); // -a was present
        b = OptionsResolver.resolve(false, "ARG", 'a', new String[]{"--arg", "true"});
        assertNotNull(b);
        assertTrue(b); // --arg was present
        b = OptionsResolver.resolve(false, "ff", 'a', new String[]{});
        assertNotNull(b);
        assertFalse(b); // no match, used default false
        
        // Note: no-ff won't work as a system env variable:
        b = OptionsResolver.resolve(false, "no-ff", 'a', new String[]{"--no-ff"});
        assertNotNull(b);
        assertTrue(b); // --no-ff was present
        System.setProperty("no-ff", "true");
        b = OptionsResolver.resolve(false, "no-ff", 'a', new String[]{});
        assertNotNull(b);
        assertTrue(b); // System property no-ff was set to true
        System.setProperty("no-ff", "false");
        b = OptionsResolver.resolve(false, "no-ff", 'n', new String[]{});
        assertNotNull(b);
        assertFalse(b); // System property no-ff was set to false
        
        // $ export FF=true # or xyz or what ever you like...
        if (System.getenv("FF") != null) {
            System.out.println(String.format("    System env found: FF=%s", System.getenv("FF")));
            Boolean inverted = new Boolean(!Boolean.valueOf(System.getenv("FF")));
            b = OptionsResolver.resolve(false, "FF", 
                    'f',
                    new String[]{ "-f", inverted.toString(), "--ff", inverted.toString() });
            assertEquals(Boolean.valueOf(System.getenv("FF")), b); // env matched
        }
        else {
            System.out.println("    No system env found: FF");
        }
        
        // mixed/complex
        b = OptionsResolver.resolve(false, "ARG", 'a', new String[]{"-ab", "bbb", "--xxx", "yyy"});
        assertNotNull(b);
        assertEquals(true, b.booleanValue()); // -a present
        String bbb = OptionsResolver.resolve("error", "BBB", 'b', new String[]{"-ab", "bbb", "--xxx", "yyy"});
        assertNotNull(bbb);
        assertEquals("bbb", bbb); // -a=bbb
        String xxx = OptionsResolver.resolve("error", "xxx", 'x', new String[]{"-ab", "bbb", "--xxx", "xxx"});
        assertNotNull(xxx);
        assertEquals("xxx", xxx); // --xxx=xxx
        
        // program arg --arg
        o = OptionsResolver.resolve(null, "ARG", 'a', new String[]{"--arg", "1000.0"});
        assertNull(o); // null is null
        
        s = OptionsResolver.resolve("error", "ARG", 'a', new String[]{"--arg", "hello"});
        assertNotNull(s);
        assertEquals("hello", s); // String
        
        l = OptionsResolver.resolve(0L, "ARG", 'a', new String[]{"--arg", "1000"});
        assertNotNull(l);
        assertEquals(1000L, l.longValue()); // Long
        
        d = OptionsResolver.resolve(0d, "ARG", 'a', new String[]{"--arg", "1.1"});
        assertNotNull(d);
        assertEquals(1.1d, d.doubleValue(), 1.0d); // Double
        
        b = OptionsResolver.resolve(false, "ARG", 'a', new String[]{"--arg", "true"});
        assertNotNull(b);
        assertEquals(true, b.booleanValue()); // boolean
        
        // system property
        o = OptionsResolver.resolve(null, "ARG", 'a', new String[]{});
        assertNull(o); // null is null
        
        System.setProperty("ARG", "hello");
        o = OptionsResolver.resolve(null, "ARG", 'a', new String[]{});
        assertNull(o); // null is null
        
        System.setProperty("ARG", "hello");
        s = OptionsResolver.resolve("error", "ARG", 'a', new String[]{});
        assertNotNull(s);
        assertEquals("hello", s); // String
        
        System.setProperty("ARG", "1000");
        l = OptionsResolver.resolve(0L, "ARG", 'a', new String[]{});
        assertNotNull(l);
        assertEquals(1000L, l.longValue()); // Long
        
        System.setProperty("ARG", "1.1");
        d = OptionsResolver.resolve(0d, "ARG", 'a', new String[]{});
        assertNotNull(d);
        assertEquals(1.1d, d.doubleValue(), 1.0d); // Double
        
        System.setProperty("ARG", "true");
        b = OptionsResolver.resolve(false, "ARG", 'a', new String[]{});
        assertNotNull(b);
        assertEquals(true, b.booleanValue()); // Boolean
        
        // precedence
        String p = OptionsResolver.resolve("default", "PRECEDENCE", 'p', new String[]{});
        assertEquals("default", p); // no match, so default
        
        p = OptionsResolver.resolve("default", "PRECEDENCE", 'p', new String[]{"-p", "flag", "--notcompeting", "name"});
        assertEquals("flag", p); // -p=flag
        p = OptionsResolver.resolve("default", "PRECEDENCE", 'p', new String[]{"-p", "flag"});
        assertEquals("flag", p); // -p=flag
        
        p = OptionsResolver.resolve("default", "PRECEDENCE", 'p', new String[]{"-p", "flag", "--precedence", "name"});
        assertEquals("name", p); // --precedence=name
        p = OptionsResolver.resolve("default", "PRECEDENCE", 'p', new String[]{"--precedence", "name"});
        assertEquals("name", p); // --precedence=name
        
        System.setProperty("PRECEDENCE", "system.property");
        p = OptionsResolver.resolve("default", "PRECEDENCE", 'p', new String[]{});
        assertEquals("system.property", p); // PRECEDENCE=system.property
        p = OptionsResolver.resolve("default", "PRECEDENCE", 'p', new String[]{"--precedence", "name"});
        assertEquals("system.property", p); // PRECEDENCE=system.property
        p = OptionsResolver.resolve("default", "PRECEDENCE", 'p', new String[]{"-p", "flag", "--precedence", "name"});
        assertEquals("system.property", p); // PRECEDENCE=system.property
        
        // $ export SYSENV=<what-ever-you-like>
        if (System.getenv("SYSENV") != null) {
            System.out.println(String.format("    System env found: SYSENV=%s", System.getenv("SYSENV")));
            p = OptionsResolver.resolve("default", "SYSENV", 'p', new String[]{"-p", "flag", "--precedence", "name"});
            assertEquals(System.getenv("SYSENV"), p);
        }
        else {
            System.out.println(String.format("    No system env found: SYSENV", p));
        }
	}
}
