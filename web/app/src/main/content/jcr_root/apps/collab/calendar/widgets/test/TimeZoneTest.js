/*
 * Copyright 1997-2009 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 *
 * Unit tests for widgets/test/TimeZone.js
 * 
 * using jsTestDriver: http://code.google.com/p/js-test-driver/
 * 
 * NOTE: It is important to run these tests with different timezones
 *       set on the computer, because the whole point of the TimeZone.js
 *       code is to work around the limitation that Date objects in
 *       Javascript / in the browser are always based on the local timezone.
 * 
 *       Good test timezones are:
 *       - Europe/Berlin       (used in test case below)
 *       - America/Chicago     (dito)
 *       - GMT / Europe/London (because of its +-0 offset)
 *       - Australia/Darwing   (because of its +9:30 offset)
 *       - Pacific/Samoa etc.  (-12h/-11h/-10h extreme east)
 *       - Pacific/Auckland    (+13 extreme west)
 *       - and some in between
 */

var cal = CQ.collab.cal;
var TimeZone = CQ.collab.cal.TimeZone;
var MILLIS_PER_HOUR = 60 * 60 * 1000;

function utcstr(date) {
    function pad(s, count) {
        return String.leftPad(s, count || 2, '0');
    }
    if (typeof date === "number") {
        date = new Date(date);
    }
    // IS0-8601 format
    return date.getUTCFullYear() + "-" + pad(date.getUTCMonth()+1) + "-" + pad(date.getUTCDate()) + "T" +
        pad(date.getUTCHours()) + ":" + pad(date.getUTCMinutes()) + ":" + pad(date.getUTCSeconds()) + "." +
        pad(date.getUTCMilliseconds(), 4) + "Z" + " " + Date.dayNames[date.getUTCDay()];
}

function assertMillisEqual(expected, actual) {
    var msg = "utc millis are wrong, difference in min " + ((expected-actual)/60000) + ": ";
    msg = msg + "expected " + utcstr(expected) + " but was " + utcstr(actual) + ":"; 
    assertEquals(msg, expected, actual);
    
    assertEquals(expected.valueOf(), actual.valueOf());
}

function assertUTCEquals(expected, actual) {
    assertEquals(expected.getUTCDate(),         actual.getUTCDate());
    assertEquals(expected.getUTCDay(),          actual.getUTCDay());
    assertEquals(expected.getUTCFullYear(),     actual.getUTCFullYear());
    assertEquals(expected.getUTCHours(),        actual.getUTCHours());
    assertEquals(expected.getUTCMilliseconds(), actual.getUTCMilliseconds());
    assertEquals(expected.getUTCMinutes(),      actual.getUTCMinutes());
    assertEquals(expected.getUTCMonth(),        actual.getUTCMonth());
    assertEquals(expected.getUTCSeconds(),      actual.getUTCSeconds());
}

function assertUTCAndLocalEquals(actual) {
    assertEquals(actual.getDate(),         actual.getUTCDate());
    assertEquals(actual.getDay(),          actual.getUTCDay());
    assertEquals(actual.getFullYear(),     actual.getUTCFullYear());
    assertEquals(actual.getHours(),        actual.getUTCHours());
    assertEquals(actual.getMilliseconds(), actual.getUTCMilliseconds());
    assertEquals(actual.getMinutes(),      actual.getUTCMinutes());
    assertEquals(actual.getMonth(),        actual.getUTCMonth());
    assertEquals(actual.getSeconds(),      actual.getUTCSeconds());
}

function assertDate(date, tzOffsetInHours, year, month, day, hours, minutes, seconds, milliseconds, offset) {
    assertEquals("timezone offset", -tzOffsetInHours * 60, date.getTimezoneOffset());
    
    if (typeof offset != "undefined") {
        if (offset > 0) {
            hours -= Math.floor(Math.abs(offset / 60));
            minutes -= Math.abs(offset % 60);
        } else {
            hours += Math.floor(offset / 60);
            minutes += offset % 60;
        }
    }
    
    if (minutes < 0) {
        minutes = minutes + 60;
        hours -= 1;
    } else if (minutes >= 60) {
        minutes = minutes - 60;
        hours += 1;
    }
    
    // simple check for the case when the offset calculation moved the
    // datetime onto a different day, enough for our tests
    if (hours >= 24) {
        day += 1;
        hours -= 24;
    } else if (hours < 0) {
        day -= 1;
        hours += 24;
    }
    
    assertEquals("year",    year,         date.getFullYear());
    assertEquals("month",   month,        date.getMonth());
    assertEquals("day",     day,          date.getDate());
    assertEquals("hours",   hours || 0,   date.getHours());
    assertEquals("minutes", minutes || 0, date.getMinutes());
    assertEquals("seconds", seconds || 0, date.getSeconds());
    assertEquals("ms", milliseconds || 0, date.getMilliseconds());
}

jstestdriver.logStacktrace = function() {
    try {
        i.dont.exist+=0; //doesn't exist- that's the point
    } catch(e) {
        if (e.stack) { //Firefox
            jstestdriver.console.log(e.stack);
        }
    }    
}

// just a dummy to run once per test run only.. and Zxxx to run at the beginning (don't ask)
TestCase("ZLogLocalTimezone", {
    test_log_local_timezone: function() {
        jstestdriver.console.log("local timezone is: " + new Date().format("O (T)") + " on browser: " + navigator.userAgent);        
    }
});

TestCase("TimeZone", {
    setUp: function() {
        TimeZone.load(TestTimeZones);
    },
    
    test_tz_getOffset: function() {
        // once -12, now +12
        var tz = TimeZone.get("Pacific/Kwajalein");
        // test for time before first observance
        assertEquals(40160000, tz.getOffset(Date.UTC(1900, 1, 1)));
        assertEquals(+11 * MILLIS_PER_HOUR, tz.getOffset(Date.UTC(1902, 0, 1)));
        assertEquals(-12 * MILLIS_PER_HOUR, tz.getOffset(Date.UTC(1970, 0, 1)));
        assertEquals(+12 * MILLIS_PER_HOUR, tz.getOffset(Date.UTC(1994, 0, 1)));

        // +1 or +2
        tz = TimeZone.get("Europe/Berlin");
        // test for time before first observance
        assertEquals(3208000, tz.getOffset(Date.UTC(1893, 2, 29)));
        assertEquals(+1 * MILLIS_PER_HOUR, tz.getOffset(Date.UTC(1893, 3, 1)));

        assertEquals(+1 * MILLIS_PER_HOUR, tz.getOffset(Date.UTC(2009, 2, 1)));
        assertEquals("CET", tz.getShortName(Date.UTC(2009, 2, 1)));
        
        assertEquals(+2 * MILLIS_PER_HOUR, tz.getOffset(Date.UTC(2009, 3, 1)));
        assertEquals("CEST", tz.getShortName(Date.UTC(2009, 3, 1)));
        
        // now +14
        tz = TimeZone.get("Pacific/Kiritimati");
        assertEquals(+14 * MILLIS_PER_HOUR, tz.getOffset(Date.UTC(2009, 1, 1)));
    },
    
    test_tz_getOffsetInMinutes: function() {
        var tz = TimeZone.get("Pacific/Kwajalein");
        assertEquals(tz.getOffset(Date.UTC(1994, 1, 1)) / 60000, tz.getOffsetInMinutes(Date.UTC(1994, 1, 1)));
        
        var tz = TimeZone.get("Europe/Berlin");
        assertEquals(tz.getOffset(Date.UTC(1994, 1, 1)) / 60000, tz.getOffsetInMinutes(Date.UTC(1994, 1, 1)));
    },
    
    test_tz_getID: function() {
        assertEquals("Europe/Berlin", TimeZone.get("Europe/Berlin").getID());
        assertEquals("America/Chicago", TimeZone.get("America/Chicago").getID());
        assertEquals("Pacific/Kwajalein", TimeZone.get("Pacific/Kwajalein").getID());
    },
    
    test_tz_getShortName: function() {
        var tz = TimeZone.get("Pacific/Kwajalein");
        assertEquals("MHT", tz.getShortName(Date.UTC(1994, 1, 1)));
        
        var tz = TimeZone.get("Europe/Berlin");
        assertEquals("CET", tz.getShortName(Date.UTC(1994, 1, 1)));
        assertEquals("CEST", tz.getShortName(Date.UTC(1994, 7, 1)));
    },
    
    test_tz_inDaylightTime: function() {
        var tz = TimeZone.get("Europe/Berlin");
        // note: assertFalse tests for === false, but not for undefined or null
        assertTrue( !tz.inDaylightTime(Date.UTC(1994, 1, 1)) );
        assertTrue(tz.inDaylightTime(Date.UTC(1994, 7, 1)));
    }
});
    
TestCase("CQ.collab.cal.Date", {
    setUp: function() {
        TimeZone.load(TestTimeZones);
    },
    
    test_shift: function() {
        // 10.7.2009 10:00 UTC
        var d = new Date(Date.UTC(2009, 6 /* july */ , 10, 10));
        
        // test if millis are equal and the date/time fields match
        var berlin = d.shift("Europe/Berlin");
        assertMillisEqual(d.getTime(), berlin.getTime());
        assertDate(berlin, 2, /* +2 CEST */ 2009, 6, 10, 12);
        
        var chicago = d.shift("America/Chicago");
        assertMillisEqual(d.getTime(), chicago.getTime());
        assertDate(chicago, -5, /* -5 CDT */ 2009, 6, 10, 5);
        
        // some utc-based tests
        // fixed date: UTC 1.7.2009, 10:00 => DST in Berlin, +2
        assertDate(new Date(Date.UTC(2009, 8, 1, 10)).shift("Europe/Berlin"), 2, /* +2 CEST */ 2009, 8, 1, 12);
        // fixed date: UTC 1.2.2009, 10:00 => standard time in Berlin, +1
        assertDate(new Date(Date.UTC(2009, 1, 1, 10)).shift("Europe/Berlin"), 1, /* +1 CET */ 2009, 1, 1, 11);
        
        // fixed date: UTC 1.7.2009, 10:00 => DST in Chicago, -5
        assertDate(new Date(Date.UTC(2009, 8, 1, 10)).shift("America/Chicago"), -5, /* -5 CDT */ 2009, 8, 1, 5);
        // fixed date: UTC 1.2.2009, 10:00 => standard time in Chicago, -6
        assertDate(new Date(Date.UTC(2009, 1, 1, 10)).shift("America/Chicago"), -6, /* -6 CST */ 2009, 1, 1, 4);
        
        // test edge cases
        // UTC -12, need a date before August 21, 1993
        assertDate(new Date(Date.UTC(1990, 1, 1, 14)).shift("Pacific/Kwajalein"), -12, /* UTC-12 */ 1990, 1, 1, 2);
        // UTC +14, need a date before August 21, 1993
        assertDate(new Date(Date.UTC(2009, 1, 1, 0)).shift("Pacific/Kiritimati"), +14, /* UTC+14 */ 2009, 1, 1, 14);
    },
    
    test_shift_multi: function() {
        // 10.7.2009 10:00 (local timezone)
        var d = new Date(Date.UTC(2009, 6 /* july */ , 10, 10));
        var offset = d.getTimezoneOffset(); // important: local offset is dependent on browser
        
        // test millis + date/time fields keep the same after multiple shifts
        var chicago = d.shift("America/Chicago");
        var berlin = chicago.shift("Europe/Berlin");
        
        assertDate(berlin, 2, /* +2 CEST */ 2009, 6, 10, 12);
        assertMillisEqual(d.getTime(), berlin.getTime());
        berlin = berlin.shift("Europe/Berlin");
        assertDate(berlin, 2, /* +2 CEST */ 2009, 6, 10, 12);
        assertMillisEqual(d.getTime(), berlin.getTime());
        
        // test date fields after multiple shifts (using a fixed date)
        // 20.11.2009 10:00 UTC
        d = new Date(Date.UTC(2009, 10, 20, 10));
        chicago = d.shift("America/Chicago");
        assertDate(chicago, -6, /* -6 CST */ 2009, 10, 20, 4);
        berlin = chicago.shift("Europe/Berlin");
        assertDate(berlin, 1, /* +1 CET */ 2009, 10, 20, 11);
        berlin.setMonth(9); // october
        assertDate(berlin, 2, /* +2 CEST */ 2009, 9, 20, 11);
        
    },
    
    test_shift_into_daylight: function() {
        // tests edge case when shifting is done across daylight border
        
        // daylight time ends 25.10.2009 2:00am for Europe/Berlin,
        // but it ends on 1.11.2009 2:00 am for America/Chicago
        // => test a time that which will cross the dst border upon shifting
        
        // 24.10.2009 23:00 UTC => 25.10.2009 01:00 (CEST)
        var d = new Date(Date.UTC(2009, 9 /* october */ , 24, 23));
        
        // first shift
        var berlin = d.shift("Europe/Berlin");
        assertMillisEqual(d.getTime(), berlin.getTime());
        assertDate(berlin, 2, /* +2 CEST */ 2009, 9, 25, 1);
        // second shift (test multi-shift for the edge case as well)
        var chicago = berlin.shift("America/Chicago");
        assertMillisEqual(d.getTime(), chicago.getTime());
        assertDate(chicago, -5, /* -5 CDT */ 2009, 9, 24, 18); // => 24.9.2009 18:00 -5 central daylight time
        
        // first shift
        chicago = d.shift("America/Chicago");
        assertMillisEqual(d.getTime(), chicago.getTime());
        // second shift
        berlin = chicago.shift("Europe/Berlin");
        assertMillisEqual(d.getTime(), berlin.getTime());
        
        // 20.11.2009 10:00 UTC
        d = new Date(Date.UTC(2009, 10, 20, 10));
        chicago = d.shift("America/Chicago");
        assertDate(chicago, -6, /* -6 CST */ 2009, 10, 20, 4);
        chicago.setMonth(9);
        assertDate(chicago, -5, /* -5 CDT */ 2009, 9, 20, 4);
    },
    
    test_setter: function() {
        // test the tz-enabled-date object is still working after
        // Date.setMonth(), setDate(), setHour(), etc. has been called

        // 20.11.2009 10:00 UTC
        var d = new Date(Date.UTC(2009, 10, 20, 10));
        var chicago = d.shift("America/Chicago");
        assertDate(chicago, -6, /* -6 CST */ 2009, 10, 20, 4);
        chicago.setMonth(9);
        assertDate(chicago, -5, /* -5 CDT */ 2009, 9, 20, 4);
        
        // 25.10.2009 01:00 (+2 CEST) => 24.10.2009 23:00 UTC
        d = new Date(Date.UTC(2009, 9 /* october */ , 24, 23));
        
        // first shift
        chicago = d.shift("America/Chicago");
        assertMillisEqual(d.getTime(), chicago.getTime());
        // second shift
        var berlin = chicago.shift("Europe/Berlin");
        assertMillisEqual(d.getTime(), berlin.getTime());
        
        // test correct getTimezoneOffset() after setDate etc.
        // move from October (+2) to December (+1)
        assertEquals(-120, berlin.getTimezoneOffset());
        berlin.setMonth(11); // december
        assertEquals(-60, berlin.getTimezoneOffset());
        
        berlin.setMonth(8); // september
        assertEquals(-120, berlin.getTimezoneOffset());
        
        /*
        // TODO: breaks when using Europe/London / GMT +0000 as timezone

        // right at the daylight border for Berlin
        d = new Date(Date.UTC(2009, 9 , 24, 23)); // october
        berlin = d.shift("Europe/Berlin");
        jstestdriver.console.log("berlin: " + berlin.toUTCString());
        berlin.setMonth(11); // december
        jstestdriver.console.log("berlin: " + berlin.toUTCString());
        assertEquals(-60, berlin.getTimezoneOffset());
        jstestdriver.console.log("berlin: " + berlin.toUTCString());
        berlin.setMonth(9); // october => berlin: Sun Oct 25 2009 01:00:00 GMT+0200
        jstestdriver.console.log("berlin: " + berlin.toUTCString());
        assertEquals(-120, berlin.getTimezoneOffset());
        */
    },
    
    test_setTime: function() {
        var chicago = new Date().shift("America/Chicago");
        var time = 150000000;
        chicago.setTime(time);
        assertEquals(time, chicago.getTime());
        
        time = Date.UTC(2009, 8, 4, 10);
        d = new Date(time);
        chicago = d.shift("America/Chicago");
        chicago.setTime(time);
        assertUTCEquals(d, chicago);
    },
    
    test_getUTC: function() {
        // fixed date: 1.7.2009, 10:15:30.500 UTC
        var d = new Date(Date.UTC(2009, 8, 1, 10, 15, 30, 500));
        var chicago = d.shift("America/Chicago");
        
        assertUTCEquals(d, chicago);
        
        // test multi-shift
        var berlin = chicago.shift("Europe/Berlin");
        assertUTCEquals(d, berlin);
        
        chicago = berlin.shift("America/Chicago");
        assertUTCEquals(d, chicago);
    },
    
    test_setUTC: function() {
        // fixed date: 1.7.2009, 10:15:30.500 UTC
        var d = new Date(Date.UTC(2009, 8, 1, 10, 15, 30, 500));
        var chicago = d.shift("America/Chicago");
        
        // test single-arg setter
        
        // change to: 4.7.2010, 08:30:20.100 UTC
        chicago.setUTCFullYear(2010);
        chicago.setUTCMonth(7);
        chicago.setUTCDate(4);
        chicago.setUTCHours(8);
        chicago.setUTCMinutes(30);
        chicago.setUTCSeconds(20);
        chicago.setUTCMilliseconds(100);
        assertUTCEquals(new Date(Date.UTC(2010, 7, 4, 8, 30, 20, 100)), chicago);
        
        // test multi-arg setter
        
        chicago = d.shift("America/Chicago");
        // change to: 4.7.2010, 08:30:20.100 UTC
        chicago.setUTCFullYear(2010, 7, 4);
        chicago.setUTCHours(8, 30, 20, 100);
        assertUTCEquals(new Date(Date.UTC(2010, 7, 4, 8, 30, 20, 100)), chicago);

        chicago = d.shift("America/Chicago");
        // change to: 4.7.2010, 08:30:20.100 UTC
        chicago.setUTCFullYear(2010);
        chicago.setUTCMonth(7, 4);
        chicago.setUTCHours(8);
        chicago.setUTCMinutes(30, 20 , 100);
        assertUTCEquals(new Date(Date.UTC(2010, 7, 4, 8, 30, 20, 100)), chicago);

        chicago = d.shift("America/Chicago");
        // change to: 4.7.2010, 08:30:20.100 UTC
        chicago.setUTCFullYear(2010, 7, 4);
        chicago.setUTCHours(8);
        chicago.setUTCMinutes(30);
        chicago.setUTCSeconds(20, 100);
        assertUTCEquals(new Date(Date.UTC(2010, 7, 4, 8, 30, 20, 100)), chicago);
    },
    
    test_toString: function() {
        // fixed date: 1.7.2009, 10:15:30.500 UTC
        var d = new Date(Date.UTC(2009, 8, 1, 10, 15, 30, 500));
        var chicago = d.shift("America/Chicago");
        // => 1.7.2009, 5:15:30.500 GMT-0500 (CDT)
        
        // Note: the actual format is not clearly defined by Javascript
        assertEquals("Tue Sep 01 2009 10:15:30 GMT", chicago.toUTCString());
        assertEquals("Tue Sep 01 2009 10:15:30 GMT", chicago.toGMTString());
        
        // methods that are not overwritten, depending on browser locale
        //assertEquals("Tue Sep 01 2009", chicago.toDateString());
        //assertEquals("09/01/2009", chicago.toLocaleDateString());
        //assertEquals("Tue Sep  1 05:15:30 2009", chicago.toLocaleString());
        //assertEquals("05:15:30", chicago.toLocaleTimeString());
        
        assertEquals("05:15:30 GMT-0500 (CDT)", chicago.toTimeString());
    },
    
    test_clone: function() {
        // fixed date: 1.7.2009, 10:15:30.500 UTC
        var d = new Date(Date.UTC(2009, 8, 1, 10, 15, 30, 500));
        var chicago = d.shift("America/Chicago");
        
        var chicago2 = chicago.clone();
        
        assertMillisEqual(chicago.getTime(), chicago2.getTime());
        assertEquals(chicago.getTimezoneInfo().getID(), chicago2.getTimezoneInfo().getID());
        assertEquals(chicago.getTimezoneOffset(), chicago2.getTimezoneOffset());
    }
});
    
TestCase("Date", {
    setUp: function() {
        TimeZone.load(TestTimeZones);
    },
    
    test_toUTC: function() {
        var date = new Date(Date.UTC(2009, 8, 1, 10, 15, 30, 500)).toUTC();
        assertUTCAndLocalEquals(date);
        assertUTCEquals(new Date(Date.UTC(2009, 8, 1, 10, 15, 30, 500)), date);

        // test that parsed UTC dates work as well
        var date = new Date(Date.parse("Wed Aug 27 2008 00:00:00 GMT+0000")).toUTC();
        assertUTCAndLocalEquals(date);
        assertUTCEquals(new Date(Date.UTC(2008, 7, 27)), date);
    },
    
    test_utcDateOnly: function() {
        var date = new Date(2009, 8, 1, 10, 15, 30, 500).utcDateOnly();
        assertUTCAndLocalEquals(date);
        assertUTCEquals(new Date(Date.UTC(2009, 8, 1)), date);
    },
    
    test_isSameDateAs: function() {
        // using plain Date
        
        // date equal (but not time)
        var a = new Date(Date.UTC(2009, 8, 1, 10, 15, 30, 500));
        var b = new Date(Date.UTC(2009, 8, 1, 10, 15, 30, 500));
        assertTrue(a.isSameDateAs(b));

        // date different
        a = new Date(Date.UTC(2009, 8, 7, 10, 15, 30, 500));
        b = new Date(Date.UTC(2009, 8, 1, 3, 20, 15, 200));
        assertFalse(a.isSameDateAs(b));

        // month different
        a = new Date(Date.UTC(2009, 9, 1, 10, 15, 30, 500));
        b = new Date(Date.UTC(2009, 8, 1, 3, 20, 15, 200));
        assertFalse(a.isSameDateAs(b));

        // year different
        a = new Date(Date.UTC(2011, 8, 1, 10, 15, 30, 500));
        b = new Date(Date.UTC(2009, 8, 1, 3, 20, 15, 200));
        assertFalse(a.isSameDateAs(b));
        
        // using CQ.collab.cal.Date
        // date equal (but not time)
        var a = new Date(Date.UTC(2009, 8, 1, 10, 15, 30, 500)).shift("Europe/Berlin");
        var b = new Date(Date.UTC(2009, 8, 1, 3, 20, 15, 200)).shift("Europe/Berlin");
        assertTrue(a.isSameDateAs(b));

        // date different
        a = new Date(Date.UTC(2009, 8, 2, 10, 15, 30, 500)).shift("Europe/Berlin");
        b = new Date(Date.UTC(2009, 8, 1, 3, 20, 15, 200)).shift("Europe/Berlin");
        assertFalse(a.isSameDateAs(b));

        // month different
        a = new Date(Date.UTC(2009, 9, 1, 10, 15, 30, 500)).shift("Europe/Berlin");
        b = new Date(Date.UTC(2009, 8, 1, 3, 20, 15, 200)).shift("Europe/Berlin");
        assertFalse(a.isSameDateAs(b));

        // year different
        a = new Date(Date.UTC(2011, 8, 1, 10, 15, 30, 500)).shift("Europe/Berlin");
        b = new Date(Date.UTC(2009, 8, 1, 3, 20, 15, 200)).shift("Europe/Berlin");
        assertFalse(a.isSameDateAs(b));
    },
    
    test_getGMTOffset: function() {
        // test fix for buggy ExtJS Date.getGMTOffset() which rounds up values like 9:30 to 10:30
        // see http://www.extjs.com/forum/showthread.php?p=412985
        
        // always +9:30, no DST (since 1945)
        var d = new Date().shift("Australia/Darwin");
        assertEquals("+0930", d.getGMTOffset());
    }
});
