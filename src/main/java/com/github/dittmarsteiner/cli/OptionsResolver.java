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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A command line helper for use in e.g.
 * <code>public static void main(String[] args)</code>. <br/>
 * Thus it throws <code>RuntimeExceptions</code> to fail early.
 * <p>
 * An <code>OptionsResolver</code> tries to read options in the following order
 * from
 * <ol>
 * <li>System environment <code>System.getenv(...)</code>
 * <li>VM argument <code>System.getProperty(...)</code>
 * <li>A <code>String[]</code> passed from e.g. <code>public static void
 * main(String[] args)</code>
 * </ol>
 * 
 * For more code examples, please see OptionsResolverTest
 * 
 * @version 1.0
 * @author <a href="mailto:dittmar.steiner@gmail.com">Dittmar Steiner</a>
 */
public class OptionsResolver {
    
    /**
     * 
     * @param defaultValue
     *            Defines also the return type. Works fine with {@link Long},
     *            {@link Integer}, {@link Double}, {@link Float},
     *            {@link Boolean} and {@link String}, and any classes with a
     *            constructor <code><i>&lt;Type&gt;</i>(String value)</code>
     * @param key
     *            Case-sensitive for System env or System property, but made
     *            lower case for program argument like <code>args[]</code>
     * @param flag
     *            a single character, case sensitive, Unicode letters and digits
     *            will be accepted (see {@link Character#isLetter(char)} and
     *            {@link Character#isDigit(char)})
     * @param args
     *            a list with 0..n entries or from e.g.
     *            <code>public static void main(<b>String[] args</b>)</code>
     * @return the resolved value if found, else the <code>defaultValue</code>
     */
    public static <T> T resolve(T defaultValue, String key, Character flag,
            String... args) {
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
            String n = String.format("--%s", key.replaceFirst("$\\-{1,2}", "")
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
        if (args != null && flag != null &&
                (Character.isLetter(flag) || Character.isDigit(flag))) {
            String f = new String(new char[] { flag });
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                boolean match = (defaultValue instanceof Boolean) ? arg
                        .contains(f) : arg.endsWith(f);

                if (arg.startsWith("-") && !arg.startsWith("--")
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

        return getValue(args[++index], defaultValue);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValue(String value, T defaultValue) {
        if (defaultValue != null) {
            try {
                Constructor<?> constructor = defaultValue
                        .getClass().getConstructor(String.class);
                return (T) constructor.newInstance(value);
            }
            catch (NoSuchMethodException | SecurityException
                    | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new StringConstructorException(defaultValue, value, e);
            }
        }

        return null;
    }

    public static class OptionsException extends RuntimeException {
        private static final long serialVersionUID = -9131033143652565410L;

        OptionsException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }

    public static class MissingArgumentException extends OptionsException {
        private static final long serialVersionUID = -407287810929352404L;

        public MissingArgumentException(String key, Character flag,
                ArrayIndexOutOfBoundsException cause) {
            super(String.format("Missing argument for --%s resp. -%s", key,
                    flag), cause);
        }
    }

    public static class NotANumberException extends OptionsException {
        private static final long serialVersionUID = 1800421190600255886L;

        public NotANumberException(String key, Character flag,
                NumberFormatException cause) {
            super(String.format("Argument is not a number for --%s resp. -%s",
                    key, flag), cause);
        }
    }

    public static class StringConstructorException extends OptionsException {
        private static final long serialVersionUID = 778304971463439217L;

        public StringConstructorException(Object defaultValue, String value,
                                          Exception cause) {
            super(String.format("Could not use 'new %s(\"%s\")': %s",
                    defaultValue.getClass().getName(), value,
                    cause.getMessage()), cause);
        }
    }
}
