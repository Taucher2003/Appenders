package com.github.taucher2003.logback.discord.appender;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Utils {

    // can't instantiate
    private Utils() {
    }

    public static String getExceptionStacktrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static String getExceptionStacktrace(IThrowableProxy throwable, int maxCharacters) {
        if(throwable == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(((ThrowableProxy)throwable).getThrowable().toString()+"\n");
        StackTraceElementProxy[] stackTraceElements = throwable.getStackTraceElementProxyArray();
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
