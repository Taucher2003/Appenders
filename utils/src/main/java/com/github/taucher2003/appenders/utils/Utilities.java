/*
 *
 *  Copyright 2020 Niklas van Schrick and the Appenders Project
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

public final class Utilities {

    // can't instantiate
    private Utilities() {
    }

    public static String getExceptionStacktrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static String getExceptionStacktrace(Throwable throwable, int maxCharacters) {
        if (throwable == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(throwable + "\n");
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++) {
            final String xMoreConstant = "\t... " + (stackTraceElements.length - i) + " more";
            String toAppend = "\t" + stackTraceElements[i].toString() + "\n";
            if ((builder.length() + toAppend.length() + xMoreConstant.length()) >= maxCharacters) {
                builder.append(xMoreConstant);
                break;
            }
            builder.append(toAppend);
        }

        return builder.toString();
    }

    public static String shortenWithMiddleCut(String message, int max) {
        if(message.length() <= max) {
            return message;
        }

        int length = message.length();
        int firstSubEnd = (max / 2);
        if(max % 2 == 0) {
            firstSubEnd -= 1;
        }
        int secondSubStart = length - (max / 2) + 2;
        return message.substring(0, firstSubEnd) + "..." + message.substring(secondSubStart, length);
    }

    public static String shortenWithEndCut(String message, int max) {
        if(message.length() <= max) {
            return message;
        }

        return message.substring(0, max - 3) + "...";
    }

    @SafeVarargs
    public static <T> T firstNotNull(T first, T... other) {
        if(first != null) {
            return first;
        }
        for(T t : other) {
            if(t != null) {
                return t;
            }
        }
        return null;
    }
}
