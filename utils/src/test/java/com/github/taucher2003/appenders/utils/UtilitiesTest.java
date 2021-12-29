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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UtilitiesTest {

    @Test
    void shortenWithMiddleCut() {
        assertEquals("Som...ng", Utilities.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 8));
        assertEquals("SomeT...ong", Utilities.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 11));
        assertEquals("...", Utilities.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 3));
        assertEquals("..", Utilities.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 2));
        assertEquals("S...", Utilities.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 4));
        assertEquals("So...", Utilities.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 5));
        assertEquals("So...g", Utilities.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 6));
        assertEquals("SomeText", Utilities.shortenWithMiddleCut("SomeText", 8));
    }

    @Test
    void shortenWithEndCut() {
        assertEquals("SomeT...", Utilities.shortenWithEndCut("SomeTextWhichIsPrettyLong", 8));
        assertEquals("SomeText...", Utilities.shortenWithEndCut("SomeTextWhichIsPrettyLong", 11));
        assertEquals("...", Utilities.shortenWithEndCut("SomeTextWhichIsPrettyLong", 3));
        assertEquals("..", Utilities.shortenWithEndCut("SomeTextWhichIsPrettyLong", 2));
        assertEquals("SomeText", Utilities.shortenWithEndCut("SomeText", 8));
    }

    @Test
    void firstNotNull() {
        assertEquals("Text", Utilities.firstNotNull(null, null, "Text", null));
        assertEquals("Test", Utilities.firstNotNull(null, "Test", null, "Text", null));
        assertEquals("Text", Utilities.firstNotNull("Text", null, null, "Test"));
        assertEquals("Test", Utilities.firstNotNull(null, "Test", "Text", null));
        assertNull(Utilities.firstNotNull(null));
        assertNull(Utilities.firstNotNull(null, null, null, null));
    }
}
