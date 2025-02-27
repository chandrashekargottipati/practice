/**
 * JavaScript for AEM Registration component
 * Client library category: practice.register
 */
(function($, window) {
    "use strict";
    
    $(document).ready(function() {
        initRegisterForm();
    });
    
    function initRegisterForm() {
        var $form = $("#registerForm");
        var $submitBtn = $form.find("button[type='submit']");
        var $message = $("#registerMessage");
        
        // Form validation
        $form.on("submit", function(e) {
            e.preventDefault();
            
            // Reset previous error states
            resetFormErrors();
            
            // Get form values
            var username = $("#username").val().trim();
            var email = $("#email").val().trim();
            var password = $("#password").val();
            var confirmPassword = $("#confirm-password").val();
            
            // Validate input fields
            var isValid = true;
            
            if (!username) {
                showFieldError("username", "Username is required");
                isValid = false;
            } else if (username.length < 3) {
                showFieldError("username", "Username must be at least 3 characters");
                isValid = false;
            }
            
            if (!email) {
                showFieldError("email", "Email is required");
                isValid = false;
            } else if (!isValidEmail(email)) {
                showFieldError("email", "Please enter a valid email address");
                isValid = false;
            }
            
            if (!password) {
                showFieldError("password", "Password is required");
                isValid = false;
            } else if (password.length < 6) {
                showFieldError("password", "Password must be at least 6 characters");
                isValid = false;
            }
            
            if (!confirmPassword) {
                showFieldError("confirm-password", "Please confirm your password");
                isValid = false;
            } else if (password !== confirmPassword) {
                showFieldError("confirm-password", "Passwords do not match");
                isValid = false;
            }
            
            if (!isValid) {
                return;
            }
            
            // Show loading state
            var originalBtnText = $submitBtn.text();
            $submitBtn.prop("disabled", true).text("Creating Account...");
            $message.hide();
            
            // Submit registration request
            $.ajax({
                type: "POST",
                url: "/bin/register",
                data: {
                    username: username,
                    email: email,
                    password: password
                },
                success: function(response) {
                    showMessage("Account created successfully! Redirecting to login page...", "success");
                    
                    // Reset the form
                    $form[0].reset();
                    
                    // Redirect to login page after 2 seconds
                    setTimeout(function() {
					window.location.href = "/content/practice/us/en/login.html";
                    }, 2000);
                },
                error: function(xhr) {
                    var errorMessage = "Registration failed";
                    
                    // Handle specific error messages
                    if (xhr.status === 409) {
                        errorMessage = "Username or email already exists";
                    } else if (xhr.responseJSON && xhr.responseJSON.error) {
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
    
    function resetFormErrors() {
        $(".error-message").text("");
        $(".input-error").removeClass("input-error");
    }
    
    function showFieldError(fieldId, message) {
        $("#" + fieldId).addClass("input-error");
        $("#" + fieldId + "-error").text(message);
    }
    
    function showMessage(text, type) {
        var $message = $("#registerMessage");
        $message.text(text);
        $message.removeClass("error success").addClass(type).show();
    }
    
    function isValidEmail(email) {
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    
})(jQuery, window);