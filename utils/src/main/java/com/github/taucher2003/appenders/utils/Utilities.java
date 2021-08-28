/*
 *
 *  Copyright 2021 Niklas van Schrick and the contributors of the Appenders Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.github.taucher2003.appenders.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class which provides some general helper methods used by the appenders
 */
public final class Utilities {

    // can't instantiate
    private Utilities() {
    }

    /**
     * Gets the stack trace of a Throwable as it would be printed by {@link Throwable#printStackTrace()}
     *
     * @param throwable the throwable, which stack trace should be returned
     * @return the stack trace
     */
    public static String getExceptionStacktrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    /**
     * Gets the stack trace of a Throwable as it would be printed by {@link Throwable#printStackTrace()}
     *
     * @param throwable     the throwable, which stack trace should be returned
     * @param maxCharacters the limit of characters at which the stack trace should be cut
     * @return the stack trace
     */
    public static String getExceptionStacktrace(Throwable throwable, int maxCharacters) {
        if (throwable == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(throwable + "\n");

        Throwable currentThrowable = throwable;
        do {
            StackTraceElement[] stackTraceElements = currentThrowable.getStackTrace();
            if (currentThrowable != throwable) {
                builder.append("Caused by: ").append(currentThrowable).append("\n");
            }
            appendException(builder, stackTraceElements, maxCharacters);
        } while ((currentThrowable = currentThrowable.getCause()) != null);


        return builder.toString();
    }

    private static void appendException(StringBuilder builder, StackTraceElement[] stackTraceElements, int maxCharacters) {
        for (int i = 0; i < stackTraceElements.length; i++) {
            final String xMoreConstant = "\t... " + (stackTraceElements.length - i) + " more";
            String toAppend = "\tat " + stackTraceElements[i].toString() + "\n";
            if ((builder.length() + toAppend.length() + xMoreConstant.length()) >= maxCharacters) {
                builder.append(xMoreConstant);
                break;
            }
            builder.append(toAppend);
        }
    }

    /**
     * Cuts a string to the given amount of characters
     * This method shows begin and end.
     * <br>
     * {@code
     * shortenWithMiddleCut("Some Text with a message", 15)
     * => Some Te...ssage
     * }
     *
     * @param message the message to shorten
     * @param max     the amount of maximum characters
     * @return the shortened message
     * @see #shortenWithEndCut(String, int)
     */
    public static String shortenWithMiddleCut(String message, int max) {
        if (message.length() <= max) {
            return message;
        }

        int length = message.length();
        int firstSubEnd = (max / 2);
        if (max % 2 == 0) {
            firstSubEnd -= 1;
        }
        int secondSubStart = length - (max / 2) + 2;
        return message.substring(0, firstSubEnd) + "..." + message.substring(secondSubStart, length);
    }

    /**
     * Cuts a string to the given amount of characters
     * This method shows begin.
     * <br>
     * {@code
     * shortenWithMiddleCut("Some Text with a message", 15)
     * => Some Text wi...
     * }
     *
     * @param message the message to shorten
     * @param max     the amount of maximum characters
     * @return the shortened message
     * @see #shortenWithMiddleCut(String, int)
     */
    public static String shortenWithEndCut(String message, int max) {
        if (message.length() <= max) {
            return message;
        }

        return message.substring(0, max - 3) + "...";
    }

    /**
     * Returns the first given parameter which is not null
     *
     * @param first the first element
     * @param other any other elements
     * @return the first element which is not null
     */
    @SafeVarargs
    public static <T> T firstNotNull(T first, T... other) {
        if (first != null) {
            return first;
        }
        for (T t : other) {
            if (t != null) {
                return t;
            }
        }
        return null;
    }
}
