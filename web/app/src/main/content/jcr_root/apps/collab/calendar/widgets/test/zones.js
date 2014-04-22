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
 */
TestTimeZones = [];

// ----------------------------------------------< Etc/UTC >----

TestTimeZones.push({
    "tzID":"Etc/UTC",
    "observances":
    [
        {
            "tzName":"UTC",
            "offsetFrom":0,
            "offsetTo":0,
            "start":"Thu Jan 01 1970 00:00:00 GMT+0000"
        }
    ]
});

// ----------------------------------------------< Europe/Berlin +1 >----

TestTimeZones.push({
    "tzID":"Europe/Berlin",
    "tzURL":"http://tzurl.org/zoneinfo/Europe/Berlin",
    "observances":
    [
        {
            "tzName":"CET",
            "offsetFrom":3208000,
            "offsetTo":3600000,
            "start":"Sat Apr 01 1893 00:00:00 GMT+0000",
            "dates":
            [
                "Sat Apr 01 1893 00:00:00 GMT+0000"
            ]
        },
        {
            "daylight":true,
            "tzName":"CEST",
            "offsetFrom":3600000,
            "offsetTo":7200000,
            "start":"Sun Apr 30 1916 23:00:00 GMT+0000",
            "dates":
            [
                "Sun Apr 30 1916 23:00:00 GMT+0000",
                "Mon Apr 16 1917 02:00:00 GMT+0000",
                "Mon Apr 15 1918 02:00:00 GMT+0000",
                "Mon Apr 01 1940 02:00:00 GMT+0000",
                "Mon Mar 29 1943 02:00:00 GMT+0000",
                "Mon Apr 03 1944 02:00:00 GMT+0000",
                "Mon Apr 02 1945 02:00:00 GMT+0000",
                "Sun Apr 14 1946 02:00:00 GMT+0000",
                "Sun Apr 06 1947 03:00:00 GMT+0000",
                "Sun Apr 18 1948 02:00:00 GMT+0000",
                "Sun Apr 10 1949 02:00:00 GMT+0000",
                "Sun Apr 06 1980 02:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"CET",
            "offsetFrom":7200000,
            "offsetTo":3600000,
            "start":"Sun Oct 01 1916 01:00:00 GMT+0000",
            "dates":
            [
                "Sun Oct 01 1916 01:00:00 GMT+0000",
                "Mon Sep 17 1917 03:00:00 GMT+0000",
                "Mon Sep 16 1918 03:00:00 GMT+0000",
                "Mon Nov 02 1942 03:00:00 GMT+0000",
                "Mon Oct 04 1943 03:00:00 GMT+0000",
                "Mon Oct 02 1944 03:00:00 GMT+0000",
                "Sun Nov 18 1945 03:00:00 GMT+0000",
                "Mon Oct 07 1946 03:00:00 GMT+0000",
                "Sun Oct 05 1947 03:00:00 GMT+0000",
                "Sun Oct 03 1948 03:00:00 GMT+0000",
                "Sun Oct 02 1949 03:00:00 GMT+0000",
                "Sun Sep 28 1980 03:00:00 GMT+0000",
                "Sun Sep 27 1981 03:00:00 GMT+0000",
                "Sun Sep 26 1982 03:00:00 GMT+0000",
                "Sun Sep 25 1983 03:00:00 GMT+0000",
                "Sun Sep 30 1984 03:00:00 GMT+0000",
                "Sun Sep 29 1985 03:00:00 GMT+0000",
                "Sun Sep 28 1986 03:00:00 GMT+0000",
                "Sun Sep 27 1987 03:00:00 GMT+0000",
                "Sun Sep 25 1988 03:00:00 GMT+0000",
                "Sun Sep 24 1989 03:00:00 GMT+0000",
                "Sun Sep 30 1990 03:00:00 GMT+0000",
                "Sun Sep 29 1991 03:00:00 GMT+0000",
                "Sun Sep 27 1992 03:00:00 GMT+0000",
                "Sun Sep 26 1993 03:00:00 GMT+0000",
                "Sun Sep 25 1994 03:00:00 GMT+0000",
                "Sun Sep 24 1995 03:00:00 GMT+0000"
            ]
        },
        {
            "daylight":true,
            "tzName":"CEMT",
            "offsetFrom":7200000,
            "offsetTo":10800000,
            "start":"Thu May 24 1945 02:00:00 GMT+0000",
            "dates":
            [
                "Thu May 24 1945 02:00:00 GMT+0000",
                "Sun May 11 1947 03:00:00 GMT+0000"
            ]
        },
        {
            "daylight":true,
            "tzName":"CEST",
            "offsetFrom":10800000,
            "offsetTo":7200000,
            "start":"Mon Sep 24 1945 03:00:00 GMT+0000",
            "dates":
            [
                "Mon Sep 24 1945 03:00:00 GMT+0000",
                "Sun Jun 29 1947 03:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"CET",
            "offsetFrom":3600000,
            "offsetTo":3600000,
            "start":"Tue Jan 01 1946 00:00:00 GMT+0000",
            "dates":
            [
                "Tue Jan 01 1946 00:00:00 GMT+0000",
                "Tue Jan 01 1980 00:00:00 GMT+0000"
            ]
        },
        {
            "daylight":true,
            "tzName":"CEST",
            "offsetFrom":3600000,
            "offsetTo":7200000,
            "start":"Sun Mar 29 1981 02:00:00 GMT+0000",
            "rules":
            [
                {
                    "frequency":"yearly",
                    "byWeekDay":
                    [
                        "SU"
                    ],
                    "byWeekDayOffset":
                    [
                        -1
                    ],
                    "byMonth":
                    [
                        3
                    ]
                }
            ]
        },
        {
            "tzName":"CET",
            "offsetFrom":7200000,
            "offsetTo":3600000,
            "start":"Sun Oct 27 1996 03:00:00 GMT+0000",
            "rules":
            [
                {
                    "frequency":"yearly",
                    "byWeekDay":
                    [
                        "SU"
                    ],
                    "byWeekDayOffset":
                    [
                        -1
                    ],
                    "byMonth":
                    [
                        10
                    ]
                }
            ]
        }
    ]
});

// ----------------------------------------------< America/Chicago -6 >----

TestTimeZones.push({
    "tzID":"America/Chicago",
    "tzURL":"http://tzurl.org/zoneinfo/America/Chicago",
    "observances":
    [
        {
            "tzName":"CST",
            "offsetFrom":-21036000,
            "offsetTo":-21600000,
            "start":"Sun Nov 18 1883 12:09:24 GMT+0000",
            "dates":
            [
                "Sun Nov 18 1883 12:09:24 GMT+0000"
            ]
        },
        {
            "daylight":true,
            "tzName":"CDT",
            "offsetFrom":-21600000,
            "offsetTo":-18000000,
            "start":"Sun Mar 31 1918 02:00:00 GMT+0000",
            "dates":
            [
                "Sun Mar 31 1918 02:00:00 GMT+0000",
                "Sun Mar 30 1919 02:00:00 GMT+0000",
                "Sun Jun 13 1920 02:00:00 GMT+0000",
                "Sun Mar 27 1921 02:00:00 GMT+0000",
                "Sun Apr 30 1922 02:00:00 GMT+0000",
                "Sun Apr 29 1923 02:00:00 GMT+0000",
                "Sun Apr 27 1924 02:00:00 GMT+0000",
                "Sun Apr 26 1925 02:00:00 GMT+0000",
                "Sun Apr 25 1926 02:00:00 GMT+0000",
                "Sun Apr 24 1927 02:00:00 GMT+0000",
                "Sun Apr 29 1928 02:00:00 GMT+0000",
                "Sun Apr 28 1929 02:00:00 GMT+0000",
                "Sun Apr 27 1930 02:00:00 GMT+0000",
                "Sun Apr 26 1931 02:00:00 GMT+0000",
                "Sun Apr 24 1932 02:00:00 GMT+0000",
                "Sun Apr 30 1933 02:00:00 GMT+0000",
                "Sun Apr 29 1934 02:00:00 GMT+0000",
                "Sun Apr 28 1935 02:00:00 GMT+0000",
                "Sun Apr 25 1937 02:00:00 GMT+0000",
                "Sun Apr 24 1938 02:00:00 GMT+0000",
                "Sun Apr 30 1939 02:00:00 GMT+0000",
                "Sun Apr 28 1940 02:00:00 GMT+0000",
                "Sun Apr 27 1941 02:00:00 GMT+0000",
                "Sun Apr 28 1946 02:00:00 GMT+0000",
                "Sun Apr 27 1947 02:00:00 GMT+0000",
                "Sun Apr 25 1948 02:00:00 GMT+0000",
                "Sun Apr 24 1949 02:00:00 GMT+0000",
                "Sun Apr 30 1950 02:00:00 GMT+0000",
                "Sun Apr 29 1951 02:00:00 GMT+0000",
                "Sun Apr 27 1952 02:00:00 GMT+0000",
                "Sun Apr 26 1953 02:00:00 GMT+0000",
                "Sun Apr 25 1954 02:00:00 GMT+0000",
                "Sun Apr 24 1955 02:00:00 GMT+0000",
                "Sun Apr 29 1956 02:00:00 GMT+0000",
                "Sun Apr 28 1957 02:00:00 GMT+0000",
                "Sun Apr 27 1958 02:00:00 GMT+0000",
                "Sun Apr 26 1959 02:00:00 GMT+0000",
                "Sun Apr 24 1960 02:00:00 GMT+0000",
                "Sun Apr 30 1961 02:00:00 GMT+0000",
                "Sun Apr 29 1962 02:00:00 GMT+0000",
                "Sun Apr 28 1963 02:00:00 GMT+0000",
                "Sun Apr 26 1964 02:00:00 GMT+0000",
                "Sun Apr 25 1965 02:00:00 GMT+0000",
                "Sun Apr 24 1966 02:00:00 GMT+0000",
                "Sun Apr 30 1967 02:00:00 GMT+0000",
                "Sun Apr 28 1968 02:00:00 GMT+0000",
                "Sun Apr 27 1969 02:00:00 GMT+0000",
                "Sun Apr 26 1970 02:00:00 GMT+0000",
                "Sun Apr 25 1971 02:00:00 GMT+0000",
                "Sun Apr 30 1972 02:00:00 GMT+0000",
                "Sun Apr 29 1973 02:00:00 GMT+0000",
                "Sun Jan 06 1974 02:00:00 GMT+0000",
                "Sun Feb 23 1975 02:00:00 GMT+0000",
                "Sun Apr 25 1976 02:00:00 GMT+0000",
                "Sun Apr 24 1977 02:00:00 GMT+0000",
                "Sun Apr 30 1978 02:00:00 GMT+0000",
                "Sun Apr 29 1979 02:00:00 GMT+0000",
                "Sun Apr 27 1980 02:00:00 GMT+0000",
                "Sun Apr 26 1981 02:00:00 GMT+0000",
                "Sun Apr 25 1982 02:00:00 GMT+0000",
                "Sun Apr 24 1983 02:00:00 GMT+0000",
                "Sun Apr 29 1984 02:00:00 GMT+0000",
                "Sun Apr 28 1985 02:00:00 GMT+0000",
                "Sun Apr 27 1986 02:00:00 GMT+0000",
                "Sun Apr 05 1987 02:00:00 GMT+0000",
                "Sun Apr 03 1988 02:00:00 GMT+0000",
                "Sun Apr 02 1989 02:00:00 GMT+0000",
                "Sun Apr 01 1990 02:00:00 GMT+0000",
                "Sun Apr 07 1991 02:00:00 GMT+0000",
                "Sun Apr 05 1992 02:00:00 GMT+0000",
                "Sun Apr 04 1993 02:00:00 GMT+0000",
                "Sun Apr 03 1994 02:00:00 GMT+0000",
                "Sun Apr 02 1995 02:00:00 GMT+0000",
                "Sun Apr 07 1996 02:00:00 GMT+0000",
                "Sun Apr 06 1997 02:00:00 GMT+0000",
                "Sun Apr 05 1998 02:00:00 GMT+0000",
                "Sun Apr 04 1999 02:00:00 GMT+0000",
                "Sun Apr 02 2000 02:00:00 GMT+0000",
                "Sun Apr 01 2001 02:00:00 GMT+0000",
                "Sun Apr 07 2002 02:00:00 GMT+0000",
                "Sun Apr 06 2003 02:00:00 GMT+0000",
                "Sun Apr 04 2004 02:00:00 GMT+0000",
                "Sun Apr 03 2005 02:00:00 GMT+0000",
                "Sun Apr 02 2006 02:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"CST",
            "offsetFrom":-18000000,
            "offsetTo":-21600000,
            "start":"Sun Oct 27 1918 02:00:00 GMT+0000",
            "dates":
            [
                "Sun Oct 27 1918 02:00:00 GMT+0000",
                "Sun Oct 26 1919 02:00:00 GMT+0000",
                "Sun Oct 31 1920 02:00:00 GMT+0000",
                "Sun Oct 30 1921 02:00:00 GMT+0000",
                "Sun Sep 24 1922 02:00:00 GMT+0000",
                "Sun Sep 30 1923 02:00:00 GMT+0000",
                "Sun Sep 28 1924 02:00:00 GMT+0000",
                "Sun Sep 27 1925 02:00:00 GMT+0000",
                "Sun Sep 26 1926 02:00:00 GMT+0000",
                "Sun Sep 25 1927 02:00:00 GMT+0000",
                "Sun Sep 30 1928 02:00:00 GMT+0000",
                "Sun Sep 29 1929 02:00:00 GMT+0000",
                "Sun Sep 28 1930 02:00:00 GMT+0000",
                "Sun Sep 27 1931 02:00:00 GMT+0000",
                "Sun Sep 25 1932 02:00:00 GMT+0000",
                "Sun Sep 24 1933 02:00:00 GMT+0000",
                "Sun Sep 30 1934 02:00:00 GMT+0000",
                "Sun Sep 29 1935 02:00:00 GMT+0000",
                "Sun Nov 15 1936 02:00:00 GMT+0000",
                "Sun Sep 26 1937 02:00:00 GMT+0000",
                "Sun Sep 25 1938 02:00:00 GMT+0000",
                "Sun Sep 24 1939 02:00:00 GMT+0000",
                "Sun Sep 29 1940 02:00:00 GMT+0000",
                "Sun Sep 28 1941 02:00:00 GMT+0000",
                "Sun Sep 30 1945 02:00:00 GMT+0000",
                "Sun Sep 29 1946 02:00:00 GMT+0000",
                "Sun Sep 28 1947 02:00:00 GMT+0000",
                "Sun Sep 26 1948 02:00:00 GMT+0000",
                "Sun Sep 25 1949 02:00:00 GMT+0000",
                "Sun Sep 24 1950 02:00:00 GMT+0000",
                "Sun Sep 30 1951 02:00:00 GMT+0000",
                "Sun Sep 28 1952 02:00:00 GMT+0000",
                "Sun Sep 27 1953 02:00:00 GMT+0000",
                "Sun Sep 26 1954 02:00:00 GMT+0000",
                "Sun Oct 30 1955 02:00:00 GMT+0000",
                "Sun Oct 28 1956 02:00:00 GMT+0000",
                "Sun Oct 27 1957 02:00:00 GMT+0000",
                "Sun Oct 26 1958 02:00:00 GMT+0000",
                "Sun Oct 25 1959 02:00:00 GMT+0000",
                "Sun Oct 30 1960 02:00:00 GMT+0000",
                "Sun Oct 29 1961 02:00:00 GMT+0000",
                "Sun Oct 28 1962 02:00:00 GMT+0000",
                "Sun Oct 27 1963 02:00:00 GMT+0000",
                "Sun Oct 25 1964 02:00:00 GMT+0000",
                "Sun Oct 31 1965 02:00:00 GMT+0000",
                "Sun Oct 30 1966 02:00:00 GMT+0000",
                "Sun Oct 29 1967 02:00:00 GMT+0000",
                "Sun Oct 27 1968 02:00:00 GMT+0000",
                "Sun Oct 26 1969 02:00:00 GMT+0000",
                "Sun Oct 25 1970 02:00:00 GMT+0000",
                "Sun Oct 31 1971 02:00:00 GMT+0000",
                "Sun Oct 29 1972 02:00:00 GMT+0000",
                "Sun Oct 28 1973 02:00:00 GMT+0000",
                "Sun Oct 27 1974 02:00:00 GMT+0000",
                "Sun Oct 26 1975 02:00:00 GMT+0000",
                "Sun Oct 31 1976 02:00:00 GMT+0000",
                "Sun Oct 30 1977 02:00:00 GMT+0000",
                "Sun Oct 29 1978 02:00:00 GMT+0000",
                "Sun Oct 28 1979 02:00:00 GMT+0000",
                "Sun Oct 26 1980 02:00:00 GMT+0000",
                "Sun Oct 25 1981 02:00:00 GMT+0000",
                "Sun Oct 31 1982 02:00:00 GMT+0000",
                "Sun Oct 30 1983 02:00:00 GMT+0000",
                "Sun Oct 28 1984 02:00:00 GMT+0000",
                "Sun Oct 27 1985 02:00:00 GMT+0000",
                "Sun Oct 26 1986 02:00:00 GMT+0000",
                "Sun Oct 25 1987 02:00:00 GMT+0000",
                "Sun Oct 30 1988 02:00:00 GMT+0000",
                "Sun Oct 29 1989 02:00:00 GMT+0000",
                "Sun Oct 28 1990 02:00:00 GMT+0000",
                "Sun Oct 27 1991 02:00:00 GMT+0000",
                "Sun Oct 25 1992 02:00:00 GMT+0000",
                "Sun Oct 31 1993 02:00:00 GMT+0000",
                "Sun Oct 30 1994 02:00:00 GMT+0000",
                "Sun Oct 29 1995 02:00:00 GMT+0000",
                "Sun Oct 27 1996 02:00:00 GMT+0000",
                "Sun Oct 26 1997 02:00:00 GMT+0000",
                "Sun Oct 25 1998 02:00:00 GMT+0000",
                "Sun Oct 31 1999 02:00:00 GMT+0000",
                "Sun Oct 29 2000 02:00:00 GMT+0000",
                "Sun Oct 28 2001 02:00:00 GMT+0000",
                "Sun Oct 27 2002 02:00:00 GMT+0000",
                "Sun Oct 26 2003 02:00:00 GMT+0000",
                "Sun Oct 31 2004 02:00:00 GMT+0000",
                "Sun Oct 30 2005 02:00:00 GMT+0000",
                "Sun Oct 29 2006 02:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"CST",
            "offsetFrom":-21600000,
            "offsetTo":-21600000,
            "start":"Thu Jan 01 1920 00:00:00 GMT+0000",
            "dates":
            [
                "Thu Jan 01 1920 00:00:00 GMT+0000",
                "Thu Jan 01 1942 00:00:00 GMT+0000",
                "Tue Jan 01 1946 00:00:00 GMT+0000",
                "Sun Jan 01 1967 00:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"EST",
            "offsetFrom":-21600000,
            "offsetTo":-18000000,
            "start":"Sun Mar 01 1936 02:00:00 GMT+0000",
            "dates":
            [
                "Sun Mar 01 1936 02:00:00 GMT+0000"
            ]
        },
        {
            "daylight":true,
            "tzName":"CWT",
            "offsetFrom":-21600000,
            "offsetTo":-18000000,
            "start":"Mon Feb 09 1942 02:00:00 GMT+0000",
            "dates":
            [
                "Mon Feb 09 1942 02:00:00 GMT+0000"
            ]
        },
        {
            "daylight":true,
            "tzName":"CPT",
            "offsetFrom":-18000000,
            "offsetTo":-18000000,
            "start":"Tue Aug 14 1945 17:00:00 GMT+0000",
            "dates":
            [
                "Tue Aug 14 1945 17:00:00 GMT+0000"
            ]
        },
        {
            "daylight":true,
            "tzName":"CDT",
            "offsetFrom":-21600000,
            "offsetTo":-18000000,
            "start":"Sun Mar 11 2007 02:00:00 GMT+0000",
            "rules":
            [
                {
                    "frequency":"yearly",
                    "byWeekDay":
                    [
                        "SU"
                    ],
                    "byWeekDayOffset":
                    [
                        2
                    ],
                    "byMonth":
                    [
                        3
                    ]
                }
            ]
        },
        {
            "tzName":"CST",
            "offsetFrom":-18000000,
            "offsetTo":-21600000,
            "start":"Sun Nov 04 2007 02:00:00 GMT+0000",
            "rules":
            [
                {
                    "frequency":"yearly",
                    "byWeekDay":
                    [
                        "SU"
                    ],
                    "byWeekDayOffset":
                    [
                        1
                    ],
                    "byMonth":
                    [
                        11
                    ]
                }
            ]
        }
    ]
});

// ----------------------------------------------< Pacific/Kwajalein once -12, now +12 >----

TestTimeZones.push({
    "tzID":"Pacific/Kwajalein",
    "tzURL":"http://tzurl.org/zoneinfo/Pacific/Kwajalein",
    "observances":
    [
        {
            "tzName":"MHT",
            "offsetFrom":40160000,
            "offsetTo":39600000,
            "start":"Tue Jan 01 1901 00:00:00 GMT+0000",
            "dates":
            [
                "Tue Jan 01 1901 00:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"KWAT",
            "offsetFrom":39600000,
            "offsetTo":-43200000,
            "start":"Wed Oct 01 1969 00:00:00 GMT+0000",
            "dates":
            [
                "Wed Oct 01 1969 00:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"MHT",
            "offsetFrom":-43200000,
            "offsetTo":43200000,
            "start":"Fri Aug 20 1993 00:00:00 GMT+0000",
            "dates":
            [
                "Fri Aug 20 1993 00:00:00 GMT+0000"
            ]
        }
    ]
});


// ----------------------------------------------< Pacific/Kiritimati +14 >----

TestTimeZones.push({
    "tzID":"Pacific/Kiritimati",
    "tzURL":"http://tzurl.org/zoneinfo/Pacific/Kiritimati",
    "observances":
    [
        {
            "tzName":"LINT",
            "offsetFrom":-37760000,
            "offsetTo":-38400000,
            "start":"Tue Jan 01 1901 00:00:00 GMT+0000",
            "dates":
            [
                "Tue Jan 01 1901 00:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"LINT",
            "offsetFrom":-38400000,
            "offsetTo":-36000000,
            "start":"Mon Oct 01 1979 00:00:00 GMT+0000",
            "dates":
            [
                "Mon Oct 01 1979 00:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"LINT",
            "offsetFrom":-36000000,
            "offsetTo":50400000,
            "start":"Sun Jan 01 1995 00:00:00 GMT+0000",
            "dates":
            [
                "Sun Jan 01 1995 00:00:00 GMT+0000"
            ]
        }
    ]
});


// ----------------------------------------------< Australia/Darwin +  >----

TestTimeZones.push({
    "tzID":"Australia/Darwin",
    "tzURL":"http://tzurl.org/zoneinfo/Australia/Darwin",
    "observances":
    [
        {
            "tzName":"CST",
            "offsetFrom":31400000,
            "offsetTo":32400000,
            "start":"Fri Feb 01 1895 00:00:00 GMT+0000",
            "dates":
            [
                "Fri Feb 01 1895 00:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"CST",
            "offsetFrom":32400000,
            "offsetTo":34200000,
            "start":"Mon May 01 1899 00:00:00 GMT+0000",
            "dates":
            [
                "Mon May 01 1899 00:00:00 GMT+0000"
            ]
        },
        {
            "daylight":true,
            "tzName":"CST",
            "offsetFrom":34200000,
            "offsetTo":37800000,
            "start":"Mon Jan 01 1917 00:01:00 GMT+0000",
            "dates":
            [
                "Mon Jan 01 1917 00:01:00 GMT+0000",
                "Thu Jan 01 1942 02:00:00 GMT+0000",
                "Sun Sep 27 1942 02:00:00 GMT+0000",
                "Sun Oct 03 1943 02:00:00 GMT+0000"
            ]
        },
        {
            "tzName":"CST",
            "offsetFrom":37800000,
            "offsetTo":34200000,
            "start":"Sun Mar 25 1917 02:00:00 GMT+0000",
            "dates":
            [
                "Sun Mar 25 1917 02:00:00 GMT+0000",
                "Sun Mar 29 1942 02:00:00 GMT+0000",
                "Sun Mar 28 1943 02:00:00 GMT+0000",
                "Sun Mar 26 1944 02:00:00 GMT+0000"
            ]
        }
    ]
});
