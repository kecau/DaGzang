requirejs( ["jquery"], function() {
    $(function () {

        $('#generate').on('click', function () {
            showLoading();
            generateData();
        });

        function showLoading() {
            $('.overlay-loading').removeClass('d-none');
        }

        function hideLoading() {
            $('.overlay-loading').addClass('d-none');
        }

        function showGeneratedMessage() {
            $('#open-modal-info').click();
        }

        function generateData() {
            let selectedVal = '';
            let selectedRadio = $("input[type='radio'][name='isUsingCustomData']:checked");
            if (selectedRadio.length > 0) {
                selectedVal = selectedRadio.val();
            }
            let selectedFileName = getSelectedFileName();
            let baseurl = '';
            let data = {
                isUsingCustomData: selectedVal,
                selectedFileName: selectedFileName
            };
            $.ajax({
                type: "POST",
                url: baseurl + "/dakgalbi/generate",
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(data),
                beforeSend: function () {
                },
                success: function (data) {
                    hideLoading();
                    showGeneratedMessage();
                },
                error: function (xhr) {
                    hideLoading();
                },
                complete: function () {
                },
            });
        }

        function getSelectedFileName() {
            return $('#selectedFileName').val();
        }

    })
});


