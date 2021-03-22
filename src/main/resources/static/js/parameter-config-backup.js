$("#item .add-new").click(function () {
    addRowItemTab();
});

$("#user .add-new").click(function () {
    addRowUserTab();
});

$("#rating .add-new").click(function () {
    addRowRatingTab();
});

$("#sendData").click(function () {
    sendData();
});

$(document).on("click", ".delete", function () {
    var $objRow = $(this).parents("tr");
    var id = $objRow.data("id");

    if (id != '') {
        $objRow.css('display', "none");
    } else {
        delRow($objRow);
    }
});

function addRowItemTab(p_data = null) {
    var strNewRow = getNewRow(p_data);

    $('#item table>tbody').append(strNewRow);
}

function addRowUserTab(p_data = null) {
    var strNewRow = getNewRow(p_data);

    $('#user table>tbody').append(strNewRow);
}

function addRowRatingTab(p_data = null) {
    var strNewRow = getNewRow(p_data);

    $('#rating table>tbody').append(strNewRow);
}

function getNewRow(p_data = null) {
    var id = '';
    var numFrequency = '';
    var numValue = '';
    var strRow = '';

    if (p_data != null) {
        id = p_data.id;
        numFrequency = p_data.frequency;
        numValue = p_data.value;
    }
    strRow += '<tr data-id="' + id + '">';
    strRow += '    <td><input type="text" name="frequency" class="form-control" placeholder="frequency" value="' + numFrequency + '"></td>';
    strRow += '    <td><input type="text" name="value" class="form-control" placeholder="value" value="' + numValue + '"></td>';
    strRow += '    <td>';
    strRow += '      <a class="delete" title="Delete" data-toggle="tooltip">';
    strRow += '          <i class="material-icons">&#xE872;</i></a>';
    strRow += '      </a>';
    strRow += '    </td>';
    strRow += '</tr>';

    return strRow;
}

function delRow($p_ctr) {
    $p_ctr.remove();
}

function getDataFrequency(p_tab) {
    var arrFrequency = [];
    var objFrequency = {};

    $('#' + p_tab + ' table>tbody>tr').each(function (index) {
        var id = $(this).data('id');
        var frequency = $(this).find('input[name=frequency]').val();
        var value = $(this).find('input[name=value]').val();

        objFrequency = {};

        if (id != '') {
            objFrequency.id = id;
            objFrequency.frequency = frequency;
            objFrequency.value = value;
            arrFrequency.push(objFrequency);
        } else {
            if (frequency != '' && value != '') {
                objFrequency.id = id;
                objFrequency.frequency = frequency;
                objFrequency.value = value;
                arrFrequency.push(objFrequency);
            }
        }
    });

    return arrFrequency;
}

function sendData() {
    var arrFrequencyItemTab = getDataFrequency('item');
    var arrFrequencyUserTab = getDataFrequency('user');
    var arrFrequencyRatingTab = getDataFrequency('rating');
    var objFrequency = {};
    var baseurl = '';

    objFrequency.item = arrFrequencyItemTab;
    objFrequency.user = arrFrequencyUserTab;
    objFrequency.rating = arrFrequencyRatingTab;

    $.ajax({
        type: "POST",
        url: baseurl + "/dakgalbi/parameter-config",
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(objFrequency),
        beforeSend: function () {
            $('#spinnerSendData').addClass('spinner-border');
        },
        success: function (data) {
            $('#spinnerSendData').removeClass('spinner-border');
            toastr.success("data was saved successfully");
        },
        error: function (xhr) {
            $('#spinnerSendData').removeClass('spinner-border');
            toastr.error("saving data was failed");
        },
        complete: function () {
            $('#spinnerSendData').removeClass('spinner-border');
        },
    });
}
