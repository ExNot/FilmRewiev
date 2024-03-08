/*
$(document).ready(function () {
    var filmSearchInput = $("#filmSearch");
    var autocompleteSuggestions = $("#autocompleteSuggestions");

    filmSearchInput.on("keyup", function () {
        var query = $(this).val();
        if (query.length >= 3) {
            $.ajax({
                url: "/films/search?query=" + query,
                type: "GET",
                success: function (data) {
                    updateSuggestions(data);
                },
                error: function () {
                    console.error("Autocomplete request failed");
                }
            });
        } else {
            clearSuggestions();
        }
    });

    function updateSuggestions(suggestions) {
        autocompleteSuggestions.empty(); // Önceki önerileri temizle

        if (suggestions.length > 0) {
            // Önerileri göster
            var suggestionsList = $("<ul>");
            for (var i = 0; i < suggestions.length; i++) {
                var suggestionItem = $("<li>").text(suggestions[i]);
                suggestionsList.append(suggestionItem);
            }
            autocompleteSuggestions.append(suggestionsList);
            autocompleteSuggestions.show(); // Önerileri göster
        } else {
            clearSuggestions();
        }
    }

    function clearSuggestions() {
        autocompleteSuggestions.empty(); // Önerileri temizle
        autocompleteSuggestions.hide(); // Önerileri gizle
    }
});
*/



$(document).ready(function () {
    var filmSearchInput = $("#filmSearch");
    var autocompleteSuggestions = $("#autocompleteSuggestions");

    filmSearchInput.on("input", function () {
        var query = $(this).val();
        if (query.length >= 3) {
            $.ajax({
                url: "/films/search?query=" + query,
                type: "GET",
                success: function (data) {
                    updateSuggestions(data);
                },
                error: function () {
                    console.error("Autocomplete request failed");
                }
            });
        } else {
            clearSuggestions();
        }
    });

    function updateSuggestions(suggestions) {
        autocompleteSuggestions.empty(); // Önceki önerileri temizle

        if (suggestions.length > 0) {
            // Önerileri göster
            var suggestionsList = $("<ul>").addClass("list-group");

            for (var i = 0; i < suggestions.length; i++) {
                var suggestionItem = $("<li>")
                    .addClass("list-group-item")
                    .text(suggestions[i]);

                suggestionsList.append(suggestionItem);
            }

            autocompleteSuggestions.html(suggestionsList);
            autocompleteSuggestions.show(); // Önerileri göster
        } else {
            clearSuggestions();
        }
    }

    function clearSuggestions() {
        autocompleteSuggestions.empty(); // Önerileri temizle
        autocompleteSuggestions.hide(); // Önerileri gizle
    }
});
