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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * For use in <code>public static void main(String[] args)<7code> methods. 
 * Throws `RuntimeException`s to fail early.
 * 
 * <p>
 * Reads options with the following precedence
 * <ol>
 * <li>System environment <code><b>System.getenv(...)</b></code></li>
 * <li>VM argument <code><b>System.getProperty(...)</b></code></li>
 * <li><code><b>String[] args</b>, you pass e.g. from </code><code>public static void main(String[] args)</code></li>
 * </ol>
 * 
 * <p>
 * Examples:
 * <p>
 * Setting an option with an integer argument (a string is quite the same):
 * <br/>
 * System environment
 * <pre>
 * $ export PORT=80
 * $ java com.example.Main
 * </pre>
 * 
 * or VM argument
 * <pre>
 * $ java -DPORT=80 com.example.Main
 * </pre>
 * 
 * or program named argument
 * <pre>
 * $ java com.example.Main --port 80
 * </pre>
 * 
 * or program argument
 * <pre>
 * $ java com.example.Main -p 80
 * </pre>
 * 
 * also mixed with a boolean option
 * <pre>
 * $ java com.example.Main -tp 80
 * </pre>
 * 
 * <p>
 * Setting a boolean option flag:
 * <br/>
 * System environment
 * <pre>
 * $ export TEST=true
 * $ java com.example.Main
 * </pre>
 * 
 * or VM option
 * <pre>
 * $ java -DTEST=true com.example.Main
 * </pre>
 * 
 * or program named argument
 * <pre>
 * $ java com.example.Main --test
 * </pre>
 * 
 * or program argument (flag)
 * <pre>
 * $ java com.example.Main -t
 * </pre>
 * 
 * also mixed with another option with argument
 * <pre>
 * $ java com.example.Main -tp 80
 * </pre>
 * 
 * <p>
 * <b>For code examples see {@link com.github.dittmarsteiner.cli.OptionsResolverTest}</b>
 * 
 * @version 1.0
 * @see OptionsResolverTest
 * @author <a href="mailto:dittmar.steiner@gmail.com">Dittmar Steiner</a>
 */
public class OptionsResolver {
    
    /**
     * Constructor not needed, so hide
     */
    private OptionsResolver() {
    }
    
    /**
     * 
     * @param key a {@link String} made lower case for program argument <code>args[]</code>
     * @param flag a single characte, case sensitive
     * @param args e.g. from <code>public static void main(<b>String[] args</b>)</code>
     * @param defaultValue
     * @return
     */
    public static <T> T resolve(String key, char flag, String[] args,
            T defaultValue) {
        // System env, property
        if (key != null) {
            String[] values = { System.getenv(key), System.getProperty(key) };
            for (String value : values) {
                if (value != null) {
                    try {
                        return getValue(value, defaultValue);
                    }
                    catch (NumberFormatException e) {
                        throw new NotANumberException(key, flag, e);
                    }
                }
            }
        }

        // named argument
        if (key != null && args != null) {
            String n = String.format("--%s", key.replaceFirst("$\\-+", "")
                    .toLowerCase());
            for (int i = 0; i < args.length; i++) {
                if (n.equals(args[i])) {
                    try {
                        return getValue(args, i, defaultValue);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        throw new MissingArgumentException(key, flag, e);
                    }
                    catch (NumberFormatException e) {
                        throw new NotANumberException(key, flag, e);
                    }
                }
            }
        }

        // flag (boolean)
        if (args != null && (flag >= 'a' && flag <= 'z')
                || (flag >= 'A' && flag <= 'Z')) {
            String f = new String(new char[] { flag });
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                boolean match = (defaultValue instanceof Boolean) ? arg
                        .contains(f) : arg.endsWith(f);

                if (arg != null && arg.startsWith("-") && !arg.startsWith("--")
                        && match) {
                    try {
                        return getValue(args, i, defaultValue);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        throw new MissingArgumentException(key, flag, e);
                    }
                    catch (NumberFormatException e) {
                        throw new NotANumberException(key, flag, e);
                    }
                }
            }
        }

        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValue(String[] args, int index, T defaultValue) {
        if (defaultValue instanceof Boolean) {
            return (T) Boolean.TRUE;
        }

        return (T) getValue(args[++index], defaultValue);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValue(String value, T defaultValue) {
        if (defaultValue != null) {
            try {
                Constructor<? extends Object> constructor = defaultValue
                        .getClass().getConstructor(String.class);
                return (T) constructor.newInstance(value);
            }
            catch (NoSuchMethodException | SecurityException
                    | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new StringContructorException(defaultValue, value, e);
            }
        }

        return (T) defaultValue;
    }

    public static class OptionsException extends RuntimeException {
        private static final long serialVersionUID = -9131033143652565410L;

        protected OptionsException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }

    public static class MissingArgumentException extends OptionsException {
        private static final long serialVersionUID = -407287810929352404L;

        public MissingArgumentException(String key, char flag,
                ArrayIndexOutOfBoundsException cause) {
            super(String.format("Missing argument for --%s resp. -%s", key,
                    flag), cause);
        }
    }

    public static class NotANumberException extends OptionsException {
        private static final long serialVersionUID = 1800421190600255886L;

        public NotANumberException(String key, char flag,
                NumberFormatException cause) {
            super(String.format("Agrument is not a number for --%s resp. -%s",
                    key, flag), cause);
        }
    }

    public static class StringContructorException extends OptionsException {
        private static final long serialVersionUID = 778304971463439217L;

        public StringContructorException(Object defaultValue, String value,
                Exception cause) {
            super(String.format("Could not use 'new %s(\"%s\")': %s",
                    defaultValue.getClass().getName(), value,
                    cause.getMessage()), cause);
        }
    }
}
