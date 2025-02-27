/**
 * JavaScript for AEM login component
 * Client library category: practice.login
 */
(function($, window) {
    "use strict";
    
    $(document).ready(function() {
        // Initialize the login form
        initLoginForm();
    });
    
    function initLoginForm() {
        var $form = $("#loginForm");
        var $message = $("#loginMessage");
        
        $form.on("submit", function(e) {
            e.preventDefault();
            
            // Get form data
            var username = $("#username").val();
            var email = $("#email").val();
            var password = $("#password").val();
            
            // Validation
            if ((!username && !email) || !password) {
                showMessage("Please provide either username or email, and password is required.", "error");
                return;
            }
            
            // Clear previous messages
            showMessage("", "");
            
            // Disable submit button and show loading state
            var $submitBtn = $form.find("button[type='submit']");
            var originalBtnText = $submitBtn.text();
            $submitBtn.prop("disabled", true).text("Logging in...");
            
            // Create form data
            var formData = new FormData();
            if (username) formData.append("username", username);
            if (email) formData.append("email", email);
            formData.append("password", password);
            
            // Send AJAX request
            $.ajax({
                type: "POST",
                url: "/bin/user/authenticate",
                data: {
                    username: username || "",
                    email: email || "",
                    password: password
                },
                success: function(response) {
                    showMessage("Login successful! Redirecting...", "success");
                    
                    // Redirect to the page specified in the response
                    if (response.redirect) {
                        setTimeout(function() {
                            window.location.href = response.redirect;
                        }, 1000);
                    }
                },
                error: function(xhr) {
                    var errorMessage = "Login failed";
                    
                    // Try to get error message from response
                    if (xhr.responseJSON && xhr.responseJSON.error) {
                        errorMessage = xhr.responseJSON.error;
                    }
                    
                    showMessage(errorMessage, "error");
                },
                complete: function() {
                    // Restore button state
                    $submitBtn.prop("disabled", false).text(originalBtnText);
                }
            });
        });
    }
    
    function showMessage(text, type) {
        var $message = $("#loginMessage");
        $message.text(text);
        
        // Reset classes
        $message.removeClass("error success");
        
        // Add appropriate class
        if (type) {
            $message.addClass(type);
        }
    }
    
})(jQuery, window);