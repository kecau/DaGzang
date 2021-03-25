requirejs(["jquery"], function () {
    requirejs( ["toastify"], function() {
        $('#saveParameterConfig').on('click', function () {
            let userAmount = getUserAmount();
            let itemAmount = getItemAmount();
            let itemPerUserAmount = getItemPerUserAmount();
            let selectedDistribution = getSelectedDistribution();
            let ratingSparsity = getRatingSparsity();
            let isUsingUserCommon = true;
            let selectedFileName = getSelectedFileName();
            let numberOfCommonUser = getNumberOfCommonUser();
            saveParameterConfig(userAmount, itemAmount, itemPerUserAmount, selectedDistribution, ratingSparsity,
                isUsingUserCommon, selectedFileName, numberOfCommonUser);
        });

        function getUserAmount() {
            return $('#userAmount').val();
        }

        function getItemAmount() {
            return $('#itemAmount').val();
        }

        function getItemPerUserAmount() {
            return $('#itemPerUserAmount').val();
        }

        function getSelectedDistribution() {
            return $('#selectedDistribution').val();
        }

        function getRatingSparsity() {
            return $('#ratingSparsity').val();
        }

        function getUsingUserCommon() {
            let isUsingUserCommonValue = $('#isUsingUserCommon').prop('checked');
            if (isUsingUserCommonValue) {
                return true;
            } else {
                return false;
            }
        }

        function getSelectedFileName() {
            return $('#selectedFileName').val();
        }

        function getNumberOfCommonUser() {
            return $('input[name=numberOfCommonUser]:checked').val();
        }

        function saveParameterConfig(userAmount, itemAmount, itemPerUserAmount, selectedDistribution, ratingSparsity,
                                     isUsingUserCommon, selectedFileName, numberOfCommonUser) {
            let baseUrl = '';
            let parameterConfigData = {
                userAmount: userAmount,
                itemAmount: itemAmount,
                itemPerUserAmount: itemPerUserAmount,
                selectedDistribution: selectedDistribution,
                ratingSparsity: ratingSparsity,
                isUsingUserCommon: isUsingUserCommon,
                selectedFileName: selectedFileName,
                numberOfCommonUser: numberOfCommonUser
            };

            $.ajax({
                type: "POST",
                url: baseUrl + "/dakgalbi/parameter-config/save",
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(parameterConfigData),
                beforeSend: function () {
                },
                success: function (data) {
                    Toastify({
                        text: "Saving data was successful",
                        duration: 5000
                    }).showToast();
                },
                error: function (xhr) {
                    Toastify({
                        text: "Saving data was failed",
                        duration: 5000
                    }).showToast();
                },
                complete: function () {
                },
            });

        }

        $('#isUsingUserCommon').on('click', function () {
            debugger
            let isUsingUserCommonValue = $('#isUsingUserCommon').prop('checked');
            if (isUsingUserCommonValue) {
                $('#using-user-common').removeClass('d-none');
            } else {
                $('#using-user-common').addClass('d-none');
            }
        });
    });

});

