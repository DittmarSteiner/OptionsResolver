/*
 * ------------------------------------------------------------------------------
 * ISC License http://opensource.org/licenses/isc-license.txt
 * ------------------------------------------------------------------------------
 * Copyright (c) 2015, Dittmar Steiner <dittmar.steiner@gmail.com>
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
        Object o = OptionsResolver.resolve("ARG", 'a', new String[]{"-a", "1000.0"}, null);
        assertNull(o); // defaultValue was null
        
        Integer port = OptionsResolver.resolve("PORT", 'p', new String[]{"-t"}, 8080);
        assertNotNull(port);
        assertEquals(8080, port.intValue());
        
        port = OptionsResolver.resolve(null, ' ', new String[]{"-p", "80"}, 8080);
        assertNotNull(port);
        assertEquals(8080, port.intValue());
        
        port = OptionsResolver.resolve(null, 'p', new String[]{"-p", "80"}, 8080);
        assertNotNull(port);
        assertEquals(80, port.intValue());
        
        port = OptionsResolver.resolve("PORT", ' ', new String[]{"-p", "80"}, 8080);
        assertNotNull(port);
        assertEquals(8080, port.intValue());
        
        port = OptionsResolver.resolve("PORT", ' ', new String[]{"--port", "80"}, 8080);
        assertNotNull(port);
        assertEquals(80, port.intValue());
        
        String s = OptionsResolver.resolve("ARG", 'a', new String[]{"-a", "hello"}, "error");
        assertNotNull(s);
        assertEquals("hello", s);
        
        Long l = OptionsResolver.resolve("ARG", 'a', new String[]{"-a", "1000"}, 0L);
        assertNotNull(l);
        assertEquals(1000L, l.longValue());
        
        int i = OptionsResolver.resolve("ARG", 'a', new String[]{"-a", "1000"}, 0);
        assertNotNull(i);
        assertEquals(1000, i);
        
        Double d = OptionsResolver.resolve("ARG", 'a', new String[]{"-a", "1.1"}, 0d);
        assertNotNull(d);
        assertEquals(1.1d, d.doubleValue(), 1.0d);
        
        float f = OptionsResolver.resolve("ARG", 'a', new String[]{"-a", "1.1"}, 0f);
        assertNotNull(f);
        assertEquals(1.1f, f, 0f);
        
        Boolean b = OptionsResolver.resolve("ARG", 'a', new String[]{"-a"}, false);
        assertNotNull(b);
        assertEquals(true, b.booleanValue());
        b = OptionsResolver.resolve("ARG", 'a', new String[]{"--arg", "true"}, false);
        assertNotNull(b);
        assertEquals(true, b.booleanValue());
        b = OptionsResolver.resolve("no-ff", 'a', new String[]{"--no-ff"}, false);
        assertNotNull(b);
        // Note: won't work as a system env variable:
        System.setProperty("no-ff", "true");
        assertEquals(true, b.booleanValue());
        b = OptionsResolver.resolve("no-ff", 'a', new String[]{}, false);
        assertNotNull(b);
        assertEquals(true, b.booleanValue());
        
        // mixed/complex
        b = OptionsResolver.resolve("ARG", 'a', new String[]{"-ab", "bbb", "--xxx", "yyy"}, false);
        assertNotNull(b);
        assertEquals(true, b.booleanValue());
        String bbb = OptionsResolver.resolve("BBB", 'b', new String[]{"-ab", "bbb", "--xxx", "yyy"}, "error");
        assertNotNull(bbb);
        assertEquals("bbb", bbb);
        String xxx = OptionsResolver.resolve("xxx", 'x', new String[]{"-ab", "bbb", "--xxx", "xxx"}, "error");
        assertNotNull(xxx);
        assertEquals("xxx", xxx);
        
        // arg
        o = OptionsResolver.resolve("ARG", 'a', new String[]{"--arg", "1000.0"}, null);
        assertNull(o);
        
        s = OptionsResolver.resolve("ARG", 'a', new String[]{"--arg", "hello"}, "error");
        assertNotNull(s);
        assertEquals("hello", s);
        
        l = OptionsResolver.resolve("ARG", 'a', new String[]{"--arg", "1000"}, 0L);
        assertNotNull(l);
        assertEquals(1000L, l.longValue());
        
        d = OptionsResolver.resolve("ARG", 'a', new String[]{"--arg", "1.1"}, 0d);
        assertNotNull(d);
        assertEquals(1.1d, d.doubleValue(), 1.0d);
        
        b = OptionsResolver.resolve("ARG", 'a', new String[]{"--arg", "true"}, false);
        assertNotNull(b);
        assertEquals(true, b.booleanValue());
        
        // system property
        o = OptionsResolver.resolve("ARG", 'a', new String[]{}, null);
        assertNull(o);
        
        System.setProperty("ARG", "hello");
        s = OptionsResolver.resolve("ARG", 'a', new String[]{}, "error");
        assertNotNull(s);
        assertEquals("hello", s);
        
        System.setProperty("ARG", "1000");
        l = OptionsResolver.resolve("ARG", 'a', new String[]{}, 0L);
        assertNotNull(l);
        assertEquals(1000L, l.longValue());
        
        System.setProperty("ARG", "1.1");
        d = OptionsResolver.resolve("ARG", 'a', new String[]{}, 0d);
        assertNotNull(d);
        assertEquals(1.1d, d.doubleValue(), 1.0d);
        
        System.setProperty("ARG", "true");
        b = OptionsResolver.resolve("ARG", 'a', new String[]{}, false);
        assertNotNull(b);
        assertEquals(true, b.booleanValue());
        
        // precedence
        String p = OptionsResolver.resolve("PRECEDENCE", 'p', new String[]{}, "default");
        assertEquals("default", p);
        p = OptionsResolver.resolve("PRECEDENCE", 'p', new String[]{"-p", "flag", "--notcompeting", "name"}, "default");
        assertEquals("flag", p);
        p = OptionsResolver.resolve("PRECEDENCE", 'p', new String[]{"-p", "flag", "--precedence", "name"}, "default");
        assertEquals("name", p);
        
        System.setProperty("PRECEDENCE", "system.property");
        p = OptionsResolver.resolve("PRECEDENCE", 'p', new String[]{}, "default");
        assertEquals("system.property", p);
        
        p = OptionsResolver.resolve("PRECEDENCE", 'p', new String[]{"--precedence", "name"}, "default");
        assertEquals("system.property", p);
        p = OptionsResolver.resolve("PRECEDENCE", 'p', new String[]{"-p", "flag", "--precedence", "name"}, "default");
        assertEquals("system.property", p);
        
        // Important note: System.getenv() omitted if not available, because it cannot be tested from most environments
        if (System.getenv("SYSENV") != null) {
            p = OptionsResolver.resolve("SYSENV", 'p', new String[]{"-p", "flag", "--precedence", "name"}, "default");
            System.out.println(String.format("    System env SYSENV found: %s", p));
        }
        else {
            System.out.println(String.format("    No system env SYSENV found", p));
        }
	}
}
