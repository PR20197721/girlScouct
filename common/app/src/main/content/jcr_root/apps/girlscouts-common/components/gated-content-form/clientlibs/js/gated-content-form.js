$(document).ready(function () {
    var ageLimit = $("#ageLimit").data("config");
    var remember = $("#remember").data("config");
    var cookieExpirationPeriod = $("#cookieExpirationPeriod").data("config");
    var salesforceCampaignId = $("#salesforceCampaignId").data("config");
    var extensions = $("#extensionsList").data("config");
    var filesList = $("#filesList").data("config");
    var gatedFormPage = window.location.href;
    var gatedFormDownload = "";
    var selector = generateSelector(extensions, filesList);
    var action = $("#gated-content-form-action").attr("action");
    $(selector).click(function (e) {
        if (!e.target.hasAttribute("target")) {
            e.target.setAttribute("target", "_blank");
        }
        var gsathomeDataFound = false;
        var gsathomeCookie = getCookie('gsathome');
        if (gsathomeCookie) {
            var gsathomeData = atob(gsathomeCookie).split("|")
            if (gsathomeData.length == 6) {
                gsathomeDataFound = true;
            }
        }
        gatedFormDownload = $(this).attr('href');

        if (gsathomeDataFound) {
            if (gsathomeData[0] != 'age12andunder') {
                $.post(action, {
                    Email: gsathomeData[0],
                    FirstName: gsathomeData[1],
                    LastName: gsathomeData[2],
                    Phone: gsathomeData[3],
                    ZipCode: gsathomeData[4],
                    IsMember: gsathomeData[5],
                    PageURL: gatedFormPage,
                    DownloadURL: gatedFormDownload,
                    CampaignID: salesforceCampaignId
                })
                    .success(function (response, status, xhr) {
                        if (response != null && response.length > 0) {
                            if (response.toUpperCase() == "SUCCESS") {
                                $('#gsathome-main').hide();
                                $('#gsathome-download').show();
                                if (remember) {
                                    setCookie("gsathome", btoa(gatedFormEmail + "|" + gatedFormFirstName + "|" + gatedFormLastName + "|" + gatedFormPhone + "|" + gatedFormZIPCode + "|" + gatedFormIsMember), cookieExpirationPeriod);
                                }
                            } else {
                                if (response.contains("ERROR")) {
                                    var errorDetails = response.trim().split("|");
                                    if (errorDetails != null && errorDetails.length >= 2) {
                                        alert(errorDetails[1]);
                                    } else {
                                        alert('Sorry, we are having a difficulty processing your request.\nPlease try again later.');
                                    }
                                }
                            }
                        }
                    })
                    .fail(function () {
                        if (response.contains("ERROR")) {
                            alert('Sorry, we are having a difficulty processing your request.\nPlease try again later.');
                        }
                    });
            }
        } else {
            $('#gsathome-age-check').show();
            $('#gsathome-main').hide();
            $('#gsathome-download').hide();
            $('#gsathome-download-link').attr('href', gatedFormDownload);
            $.fancybox("#gsathome-age-check-popup");
            return false;
        }

    })

    $("#gsathome-age-gate-month").on("change keyup paste", function (e) {
        var output, $this = $(this),
            input = $this.val();
        if (e.keyCode != 8) {
            output = input.replace(/[^0-9]/g, '').trim();
            if (output.length > this.maxLength) output = output.slice(0, this.maxLength);
            if (output > 12) output = '';
            $this.val(output);
        } else {
            $this.val(input);
        }
    });

    $("#gsathome-age-gate-day").on("change keyup paste", function (e) {
        var output, $this = $(this),
            input = $this.val();
        if (e.keyCode != 8) {
            output = input.replace(/[^0-9]/g, '').trim();
            if (output.length > this.maxLength) output = output.slice(0, this.maxLength);
            if (output > 31) output = '';
            $this.val(output);
        } else {
            $this.val(input);
        }
    });

    $("#gsathome-age-gate-year").on("change keyup paste", function (e) {
        var output, $this = $(this),
            input = $this.val();
        if (e.keyCode != 8) {
            output = input.replace(/[^0-9]/g, '').trim();
            if (output.length > this.maxLength) output = output.slice(0, this.maxLength);
            if (output.length == this.maxLength)
                if (output < 1930 || output > (new Date().getFullYear())) output = '';
            $this.val(output);
        } else {
            $this.val(input);
        }
    });

    $("#gsathome-age-form-continue-button").click(function () {
        var month = $("#gsathome-age-gate-month").val();
        var day = $("#gsathome-age-gate-day").val();
        var year = $("#gsathome-age-gate-year").val();
        if (isValidDate(month + "/" + day + "/" + year) == false) {
            $('#gsathome-age-form-error').text('Please enter a valid birthdate.');
        } else {
            $('#gsathome-age-form-error').html('');
            var birthday = new Date(month + "/" + day + "/" + year);
            var age = getAge(birthday);
            if (age < ageLimit) {
                //alert("12 and below detected");
                // let them download without filling out the form
                $('#gsathome-age-check').hide();
                $('#gsathome-download').show();
                // set cookie
                if (remember)
                    setCookie("gsathome", btoa("age12andunder" + "|" + "|" + "|" + "|" + "|"), cookieExpirationPeriod);
            } else {
                //alert("13 and above detected");
                // show form
                $('#gsathome-age-check').hide();
                $('#gsathome-main').show();
            }
        }
    });


    $("#gsathome-main-form #zipcode").on("change keyup paste", function (e) {
        var output, $this = $(this),
            input = $this.val();
        if (e.keyCode != 8) {
            output = input.replace(/[^0-9]/g, '').trim();
            if (output.length > this.maxLength) output = output.slice(0, this.maxLength);
            $this.val(output);
        } else {
            $this.val(input);
        }
    });

    $("#gsathome-main-form #phone").each(function () {
        $(this).on("change keyup paste", function (e) {
            var output,
                $this = $(this),
                input = $this.val();

            if (e.keyCode != 8) {
                input = input.replace(/[^0-9]/g, '');
                var area = input.substr(0, 3);
                var pre = input.substr(3, 3);
                var tel = input.substr(6, 4);
                if (input.length > 0) {
                    if (area.length < 3) {
                        output = "(" + area;
                    } else if (area.length == 3 && pre.length < 3) {
                        output = "(" + area + ")" + " " + pre;
                    } else if (area.length == 3 && pre.length == 3) {
                        output = "(" + area + ")" + " " + pre + "-" + tel;
                    }
                    $this.val(output);
                } else {
                    $this.val(input);
                }
            }
        });
    });


    $('#gsathome-main-form').submit(function (event) {
        event.preventDefault();
        var me = event.target;
        var gatedFormEmail = $(me).find("[name='email']").val();
        var gatedFormFirstName = $(me).find("[name='first-name']").val();
        var gatedFormLastName = $(me).find("[name='last-name']").val();
        var gatedFormPhone = $(me).find("[name='phone']").val();
        var gatedFormZIPCode = $(me).find("[name='zipcode']").val();
        var gatedFormIsMember = $(me).find("[name='is-member']:checked").val();
        gatedFormIsMember = gatedFormIsMember === 'Yes' ? 'Yes' : 'No';

        $.post(action, {
            Email: gatedFormEmail,
            FirstName: gatedFormFirstName,
            LastName: gatedFormLastName,
            Phone: gatedFormPhone,
            ZipCode: gatedFormZIPCode,
            IsMember: gatedFormIsMember,
            PageURL: gatedFormPage,
            DownloadURL: gatedFormDownload,
            CampaignID: salesforceCampaignId
        })
            .success(function (response, status, xhr) {
                if (response != null && response.length > 0) {
                    if (response.toUpperCase() == "SUCCESS") {
                        $('#gsathome-main').hide();
                        $('#gsathome-download').show();
                        if (remember) {
                            setCookie("gsathome", btoa(gatedFormEmail + "|" + gatedFormFirstName + "|" + gatedFormLastName + "|" + gatedFormPhone + "|" + gatedFormZIPCode + "|" + gatedFormIsMember), cookieExpirationPeriod);
                        }
                    } else {
                        if (response.contains("ERROR")) {
                            var errorDetails = response.trim().split("|");
                            if (errorDetails != null && errorDetails.length >= 2) {
                                alert(errorDetails[1]);
                            } else {
                                alert('Sorry, we are having a difficulty processing your request.\nPlease try again later.');
                            }
                        }
                    }
                }
            })
            .fail(function () {
                if (response.contains("ERROR")) {
                    alert('Sorry, we are having a difficulty processing your request.\nPlease try again later.');
                }
            });

    })

    function isUndefined(param) {
        return param === null || param === undefined || param === "";
    }

    function getCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    function getAge(birthDateString) {
        var today = new Date();
        var birthDate = new Date(birthDateString);
        var age = today.getFullYear() - birthDate.getFullYear();
        var m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    }

    function isValidDate(dateString) {
        // First check for the pattern
        if (!/^\d{1,2}\/\d{1,2}\/\d{4}$/.test(dateString))
            return false;

        // Parse the date parts to integers
        var parts = dateString.split("/");
        var day = parseInt(parts[1], 10);
        var month = parseInt(parts[0], 10);
        var year = parseInt(parts[2], 10);

        // Check the ranges of month and year
        if (year < 1000 || year > 3000 || month == 0 || month > 12)
            return false;

        var monthLength = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

        // Adjust for leap years
        if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
            monthLength[1] = 29;

        // Check the range of the day
        return day > 0 && day <= monthLength[month - 1];
    };

    function setCookie(name, value, days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + (value || "") + expires + "; path=/";
    }

    function generateSelector(param1, param2) {

        if (!isUndefined(param1) && !isUndefined(param2)) {
            param1 = param1.split(",");
            param2 = param2.split(",");
            param1 = $.merge(param1, param2);
        } else if (!isUndefined(param1)) {
            param1 = param1.split(",");
        } else if (!isUndefined(param2)) {
            param1 = param2.split(",");
        }
        var ext = [];
        $.each(param1, function (i, val) {
            ext.push("a[href$='" + val.replace(/'/g, "\\'") + "'i]");
        });
        ext = ext.toString();

        return ext;
    }
});