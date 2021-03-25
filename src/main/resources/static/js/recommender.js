requirejs( ["jquery"], function() {
    $(function () {
        function showLoading() {
            $('.overlay-loading').removeClass('d-none');
        }

        function hideLoading() {
            $('.overlay-loading').addClass('d-none');
        }

        function showRecommendedSuccess() {
            $('#open-modal-info').click();
        }

        $('#recommendFileBtn').on('click', function () {
            showLoading();
            let svdAlgorithm = $('input[name=selectedAlgorithm]:checked').val();
            submitRecommend(svdAlgorithm);
        });

        function submitRecommend(svdAlgorithm) {
            let baseurl = '';
            let data = {
                svdAlgorithm: svdAlgorithm
            };

            $.ajax({
                type: "POST",
                url: baseurl + "/dakgalbi/recommend/file",
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(data),
                beforeSend: function () {
                },
                success: function (data) {
                    hideLoading();
                    showRecommendedSuccess();
                },
                error: function (xhr) {
                    hideLoading();
                },
                complete: function () {
                },
            });
        }

        $('input[name=selectedAlgorithm]').on('change', function () {
            let svdAlgorithmValue = $('input[name=selectedAlgorithm]:checked').val();
            $('#svdAlgorithm').val(svdAlgorithmValue);
        });

        $('#uploadRecommendFileFrom').submit(function () {
            if (!$('#recommendFile').val()) {
                return false;
            }
            showLoading();
        })

    });
});

